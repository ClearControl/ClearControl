package clearcontrol.devices.cameras.devices.hamamatsu;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.StackCameraDeviceBase;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.cameras.StandardTriggerType;
import clearcontrol.devices.cameras.TriggerTypeInterface;
import dcamj2.DcamDevice;
import dcamj2.DcamLibrary;

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

  private final DcamDevice mDcamDevice;

  private Object mLock = new Object();

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
          null);

    mTemplateQueue = new HamStackCameraQueue(this);
    mDcamDevice = pDcamDevice;
  }

  private HamStackCamera(final int pCameraDeviceIndex,
                         final TriggerTypeInterface pTriggerType)
  {
    this(new DcamDevice(pCameraDeviceIndex));

    if (pTriggerType == StandardTriggerType.Software)
      getDcamDevice().setInputTriggerToSoftware();
    else if (pTriggerType == StandardTriggerType.Internal)
      getDcamDevice().setInputTriggerToInternal();
    else if (pTriggerType == StandardTriggerType.ExternalEdge)
      getDcamDevice().setInputTriggerToExternalFastEdge();
    else if (pTriggerType == StandardTriggerType.ExternalLevel)
      getDcamDevice().setInputTriggerToExternalLevel();

    getDcamDevice().setOutputTriggerToExposure();
    getDcamDevice().setDefectCorectionMode(false);

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
    super.playQueue(pQueue);

    ArrayList<Boolean> lKeepPlaneList =
                                      pQueue.getVariableQueue(pQueue.getKeepPlaneVariable());

    final Future<Boolean> lFuture =
                                  executeAsynchronously(new Callable<Boolean>()
                                  {
                                    @Override
                                    public Boolean call() throws Exception
                                    {
                                      return acquisition();
                                    }
                                  });

    return lFuture;
  }

  protected Boolean acquisition()
  {
    // TODO Auto-generated method stub
    return null;
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

}
