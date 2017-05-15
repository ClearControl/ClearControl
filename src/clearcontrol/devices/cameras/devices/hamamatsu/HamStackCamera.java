package clearcontrol.devices.cameras.devices.hamamatsu;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.StackCameraDeviceBase;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.cameras.StandardTriggerType;
import clearcontrol.devices.cameras.TriggerTypeInterface;
import clearcontrol.stack.EmptyStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import dcamj2.DcamDevice;
import dcamj2.DcamLibrary;
import dcamj2.DcamSequenceAcquisition;
import dcamj2.imgseq.DcamImageSequence;

/**
 * 
 * @author royer
 */
public class HamStackCamera extends
                            StackCameraDeviceBase<HamStackCameraQueue>
                            implements
                            StackCameraDeviceInterface<HamStackCameraQueue>,
                            OpenCloseDeviceInterface,
                            LoggingInterface,
                            AsynchronousExecutorServiceAccess
{

  private static final long cWaitTime = 1000;

  private final DcamDevice mDcamDevice;

  private final DcamSequenceAcquisition mDcamSequenceAcquisition;

  private Object mLock = new Object();

  private DcamImageSequence mSequence;

  static
  {
    if (!DcamLibrary.initialize())
      LoggingInterface.getLoggerStatic()
                      .severe("Could not initialize Dcam library.");
  }

  /**
   * Instantiates a Hamamatsu camera with external triggering.
   * 
   * @param pCameraDeviceIndex
   *          camera index
   * @return stack camera
   */
  public static final HamStackCamera buildWithExternalTriggering(final int pCameraDeviceIndex)
  {
    return new HamStackCamera(pCameraDeviceIndex,
                              StandardTriggerType.ExternalEdge);
  }

  /**
   * Instantiates a Hamamatsu camera with internal triggering.
   * 
   * @param pCameraDeviceIndex
   *          camera index
   * @return stack camera
   */
  public static final HamStackCamera buildWithInternalTriggering(final int pCameraDeviceIndex)
  {
    return new HamStackCamera(pCameraDeviceIndex,
                              StandardTriggerType.Internal);
  }

  /**
   * Instantiates a Hamamatsu camera with software triggering.
   * 
   * @param pCameraDeviceIndex
   *          camera index
   * @return stack camera
   */
  public static final HamStackCamera buildWithSoftwareTriggering(final int pCameraDeviceIndex)
  {
    return new HamStackCamera(pCameraDeviceIndex,
                              StandardTriggerType.Software);
  }

  private HamStackCamera(DcamDevice pDcamDevice)
  {
    super(pDcamDevice.getCameraName(),
          new Variable<Boolean>("CameraTrigger"),
          new HamStackCameraQueue());
    mTemplateQueue.setStackCamera(this);

    mDcamDevice = pDcamDevice;
    mDcamSequenceAcquisition =
                             new DcamSequenceAcquisition(mDcamDevice);
  }

  private HamStackCamera(final int pCameraDeviceIndex,
                         final TriggerTypeInterface pTriggerType)
  {
    this(new DcamDevice(pCameraDeviceIndex, true));

    if (pTriggerType == StandardTriggerType.Software)
      getDcamDevice().setInputTriggerToSoftware();
    else if (pTriggerType == StandardTriggerType.Internal)
      getDcamDevice().setInputTriggerToInternal();
    else if (pTriggerType == StandardTriggerType.ExternalEdge)
      getDcamDevice().setInputTriggerToExternalFastEdge();
    else if (pTriggerType == StandardTriggerType.ExternalLevel)
      getDcamDevice().setInputTriggerToExternalLevel();

    getDcamDevice().setOutputTriggerToExposure();
    getDcamDevice().setDefectCorectionMode(true);

    // ----------------------- done with the listener -------- //

    // for OrcaFlash 4.0:
    getLineReadOutTimeInMicrosecondsVariable().set(9.74);
    getBytesPerPixelVariable().set(2L);

    getMaxWidthVariable().set(2048L);
    getMaxHeightVariable().set(2048L);

    getPixelSizeInMicrometersVariable().set(260.0);

  }

  /**
   * Returns Dcam device.
   * 
   * @return Dcam device
   */
  public DcamDevice getDcamDevice()
  {
    return mDcamDevice;
  }

  /**
   * Sets binning.
   * 
   * @param pBinSize
   *          binning (1, 2, or 4)
   */
  public void setBinning(int pBinSize)
  {
    mDcamDevice.setBinning(pBinSize);
  }

  @Override
  public boolean open()
  {
    return true;
  }

  @Override
  public HamStackCameraQueue requestQueue()
  {
    return new HamStackCameraQueue(mTemplateQueue);
  }

  @Override
  public Future<Boolean> playQueue(HamStackCameraQueue pQueue)
  {
    synchronized (mLock)
    {
      super.playQueue(pQueue);

      double lExposureInSeconds =
                                pQueue.getExposureInSecondsVariable()
                                      .get()
                                      .doubleValue();
      long lWidth =
                  mDcamDevice.adjustWidthHeight(pQueue.getStackWidthVariable()
                                                      .get(),
                                                4);
      long lHeight =
                   mDcamDevice.adjustWidthHeight(pQueue.getStackHeightVariable()
                                                       .get(),
                                                 4);

      long lAcquiredPlanesDepth = pQueue.getQueueLength();

      long lKeptPlanesDepth =
                            countKeptPlanes(pQueue.getVariableQueue(pQueue.getKeepPlaneVariable()));

      if (mSequence == null || mSequence.getWidth() != lWidth
          || mSequence.getHeight() != lHeight
          || mSequence.getDepth() != lAcquiredPlanesDepth)
        mSequence = new DcamImageSequence(mDcamDevice,
                                          2,
                                          lWidth,
                                          lHeight,
                                          lAcquiredPlanesDepth);

      final Future<Boolean> lFuture = acquisition(pQueue,
                                                  lExposureInSeconds,
                                                  lWidth,
                                                  lHeight,
                                                  lAcquiredPlanesDepth,
                                                  lKeptPlanesDepth);

      return lFuture;
    }

  }

  private Future<Boolean> acquisition(HamStackCameraQueue pQueue,
                                      double lExposureInSeconds,
                                      long pWidth,
                                      long pHeight,
                                      long pAcquiredPlanesDepth,
                                      long pKeptPlanesDepth)
  {

    Future<Boolean> lAcquisitionResult =
                                       mDcamSequenceAcquisition.acquireSequenceAsync(lExposureInSeconds,
                                                                                     mSequence);

    Callable<Boolean> lCallable = () -> {

      StackInterface lAcquiredStack;

      if (lAcquisitionResult == null && pAcquiredPlanesDepth == 0)
      {
        lAcquiredStack = new EmptyStack();
        return true;
      }
      else
      {
        Boolean lResult = lAcquisitionResult.get();
        if (!lResult)
          return false;

        StackRequest lRecyclerRequest =
                                      StackRequest.build(pWidth,
                                                         pHeight,
                                                         pKeptPlanesDepth);
        lAcquiredStack =
                       getStackRecycler().getOrWait(cWaitTime,
                                                    TimeUnit.MILLISECONDS,
                                                    lRecyclerRequest);

        if (lAcquiredStack == null)
          return false;

        ArrayList<Boolean> lKeepPlaneList =
                                          pQueue.getVariableQueue(pQueue.getKeepPlaneVariable());

        mSequence.consolidateTo(lKeepPlaneList,
                                lAcquiredStack.getContiguousMemory());
      }

      lAcquiredStack.setMetaData(pQueue.getMetaDataVariable()
                                       .get()
                                       .clone());

      lAcquiredStack.getMetaData()
                    .setTimeStampInNanoseconds(System.nanoTime());
      lAcquiredStack.getMetaData()
                    .setIndex(getCurrentIndexVariable().get());

      getStackVariable().setAsync(lAcquiredStack);
      return true;
    };

    final Future<Boolean> lFuture = executeAsynchronously(lCallable);

    return lFuture;
  }

  @Override
  public void reopen()
  {

  }

  @Override
  public boolean close()
  {
    synchronized (mLock)
    {
      try
      {
        mDcamDevice.close();
        return true;
      }
      catch (final Throwable e)
      {
        e.printStackTrace();
        return false;
      }
    }
  }

  @Override
  public Variable<Double> getLineReadOutTimeInMicrosecondsVariable()
  {
    return mLineReadOutTimeInMicrosecondsVariable;
  }

  private long countKeptPlanes(ArrayList<Boolean> pKeptPlanesList)
  {
    long lKeptPlanes = 0;
    for (Boolean lKeptPlane : pKeptPlanesList)
      if (lKeptPlane)
        lKeptPlanes++;
    return lKeptPlanes;
  }

}
