package clearcontrol.microscope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import clearcontrol.core.concurrent.future.FutureBooleanList;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.active.ActivableDeviceInterface;
import clearcontrol.core.device.change.ChangeListener;
import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.queue.StateQueueDeviceInterface;
import clearcontrol.core.device.startstop.StartStopDeviceInterface;
import clearcontrol.core.gc.GarbageCollector;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.processor.StackProcessingPipeline;
import coremem.recycling.RecyclerInterface;

/**
 * Microscope base class providing common fields and methods for all microscopes
 *
 * @author royer
 */
public abstract class MicroscopeBase extends VirtualDevice implements
                                     MicroscopeInterface,
                                     StartStopDeviceInterface,
                                     AsynchronousSchedulerServiceAccess,
                                     LoggingInterface
{

  protected final StackRecyclerManager mStackRecyclerManager;
  protected final MicroscopeDeviceLists mDeviceLists;
  protected StageDeviceInterface mMainXYZRStage;
  protected volatile int mNumberOfEnqueuedStates;
  protected volatile long mAverageTimeInNS;
  private volatile boolean mSimulation;

  final ArrayList<Variable<Double>> mCameraPixelSizeInNanometerVariableList =
                                                                            new ArrayList<>();

  // Lock:
  protected Object mAcquisitionLock = new Object();

  private final HashMap<Integer, StackProcessingPipeline> mStackPipelines =
                                                                          new HashMap<>();

  /**
   * Instanciates the micorsocope base class.
   * 
   * @param pDeviceName
   *          device name
   */
  public MicroscopeBase(String pDeviceName)
  {
    super(pDeviceName);

    mDeviceLists = new MicroscopeDeviceLists();

    mStackRecyclerManager = new StackRecyclerManager();
    mDeviceLists.addDevice(0, mStackRecyclerManager);

    for (int i = 0; i < 128; i++)
    {
      Double lPixelSizeInNanometers =
                                    MachineConfiguration.getCurrentMachineConfiguration()
                                                        .getDoubleProperty("device.camera"
                                                                           + i
                                                                           + ".pixelsizenm",
                                                                           null);

      if (lPixelSizeInNanometers == null)
        break;

      Variable<Double> lPixelSizeInNanometersVariable =
                                                      new Variable<Double>("Camera"
                                                                           + i
                                                                           + "PixelSizeNm",
                                                                           lPixelSizeInNanometers);
      mCameraPixelSizeInNanometerVariableList.add(lPixelSizeInNanometersVariable);
    }

  }

  @Override
  public boolean isSimulation()
  {
    return mSimulation;
  }

  @Override
  public void setSimulation(boolean pSimulation)
  {
    mSimulation = pSimulation;
  }

  @Override
  public Variable<Double> getCameraPixelSizeInNanometerVariable(int pCameraIndex)
  {
    return mCameraPixelSizeInNanometerVariableList.get(pCameraIndex);
  }

  @Override
  public MicroscopeDeviceLists getDeviceLists()
  {
    return mDeviceLists;
  }

  @Override
  public <T> void addDevice(int pDeviceIndex, T pDevice)
  {
    mDeviceLists.addDevice(pDeviceIndex, pDevice);
  }

  @Override
  public <T> int getNumberOfDevices(Class<T> pClass)
  {
    return mDeviceLists.getNumberOfDevices(pClass);
  }

  @Override
  public <T> T getDevice(Class<T> pClass, int pIndex)
  {
    return mDeviceLists.getDevice(pClass, pIndex);
  }

  @Override
  public <T> ArrayList<T> getDevices(Class<T> pClass)
  {
    return mDeviceLists.getDevices(pClass);
  }

  @Override
  public void addChangeListener(ChangeListener<VirtualDevice> pChangeListener)
  {
    super.addChangeListener(pChangeListener);
    for (final Object lDevice : mDeviceLists.getAllDeviceList())
    {
      if (lDevice instanceof HasChangeListenerInterface)
      {
        @SuppressWarnings("unchecked")
        final HasChangeListenerInterface<VirtualDevice> lHasChangeListenersInterface =
                                                                                     (HasChangeListenerInterface<VirtualDevice>) lDevice;
        lHasChangeListenersInterface.addChangeListener(pChangeListener);
      }
    }
  }/**/

  @Override
  public void removeChangeListener(ChangeListener<VirtualDevice> pChangeListener)
  {
    super.removeChangeListener(pChangeListener);
    for (final Object lDevice : mDeviceLists.getAllDeviceList())
    {

      if (lDevice instanceof HasChangeListenerInterface)
      {
        @SuppressWarnings("unchecked")
        final HasChangeListenerInterface<VirtualDevice> lHasChangeListenersInterface =
                                                                                     (HasChangeListenerInterface<VirtualDevice>) lDevice;
        lHasChangeListenersInterface.removeChangeListener(pChangeListener);
      }
    }
  }

  /**
   * Sets stack processing pipeline for a given stack camera index.
   * 
   * @param pIndex
   *          stack camera index
   * @param pStackPipeline
   *          stack processing pipeline
   */
  public void setStackProcessingPipeline(int pIndex,
                                         StackProcessingPipeline pStackPipeline)
  {
    StackCameraDeviceInterface lDevice =
                                       mDeviceLists.getDevice(StackCameraDeviceInterface.class,
                                                              pIndex);
    StackProcessingPipeline lStackProcessingPipeline =
                                                     mStackPipelines.get(pIndex);

    if (lStackProcessingPipeline != null)
    {
      lDevice.getStackVariable()
             .doNotSendUpdatesTo(lStackProcessingPipeline.getInputVariable());
    }

    lDevice.getStackVariable()
           .sendUpdatesTo(pStackPipeline.getInputVariable());

    mStackPipelines.put(pIndex, pStackPipeline);
  }

  @Override
  public boolean open()
  {
    synchronized (mAcquisitionLock)
    {
      boolean lIsOpen = true;

      for (final Object lDevice : mDeviceLists.getAllDeviceList())
      {

        if (lDevice instanceof OpenCloseDeviceInterface)
        {
          final OpenCloseDeviceInterface lOpenCloseDevice =
                                                          (OpenCloseDeviceInterface) lDevice;

          info("Opening device: " + lDevice);
          final boolean lResult = lOpenCloseDevice.open();
          if (lResult)
            info("Successfully opened device: " + lDevice);
          else
            warning("Failed to open device: " + lDevice);

          lIsOpen &= lResult;
        }
      }

      return lIsOpen;
    }
  }

  @Override
  public boolean close()
  {
    synchronized (mAcquisitionLock)
    {
      boolean lIsClosed = true;
      for (final Object lDevice : mDeviceLists.getAllDeviceList())
      {
        if (lDevice instanceof OpenCloseDeviceInterface)
        {
          final OpenCloseDeviceInterface lOpenCloseDevice =
                                                          (OpenCloseDeviceInterface) lDevice;

          info("Closing device: " + lDevice);
          boolean lResult = lOpenCloseDevice.close();

          if (lResult)
            info("Successfully closed device: " + lDevice);
          else
            warning("Failed to close device: " + lDevice);

          lIsClosed &= lResult;
        }
      }

      mStackRecyclerManager.clearAll();

      return lIsClosed;
    }
  }

  @Override
  public boolean start()
  {
    synchronized (mAcquisitionLock)
    {
      boolean lIsStarted = true;
      for (final Object lDevice : mDeviceLists.getAllDeviceList())
      {
        if (lDevice instanceof StartStopDeviceInterface)
        {
          final StartStopDeviceInterface lStartStopDevice =
                                                          (StartStopDeviceInterface) lDevice;

          info("Starting device: " + lDevice);
          boolean lResult = lStartStopDevice.start();

          if (lResult)
            info("Successfully started device: " + lDevice);
          else
            warning("Failed to start device: " + lDevice);

          lIsStarted &= lResult;
        }
      }

      return lIsStarted;
    }
  }

  @Override
  public boolean stop()
  {
    synchronized (mAcquisitionLock)
    {
      boolean lIsStopped = true;
      for (final Object lDevice : mDeviceLists.getAllDeviceList())
      {
        if (lDevice instanceof StartStopDeviceInterface)
        {
          final StartStopDeviceInterface lStartStopDevice =
                                                          (StartStopDeviceInterface) lDevice;

          info("Stoping device: " + lDevice);
          boolean lResult = lStartStopDevice.stop();

          if (lResult)
            info("Successfully stopped device: " + lDevice);
          else
            warning("Failed to stop device: " + lDevice);

          lIsStopped &= lResult;
        }
      }

      return lIsStopped;
    }
  }

  @Override
  public void clearQueue()
  {
    synchronized (mAcquisitionLock)
    {
      for (final Object lDevice : mDeviceLists.getAllDeviceList())
      {
        if (lDevice instanceof StateQueueDeviceInterface)
          if (isActiveDevice(lDevice))
          {
            final StateQueueDeviceInterface lStateQueueDeviceInterface =
                                                                       (StateQueueDeviceInterface) lDevice;
            lStateQueueDeviceInterface.clearQueue();
          }
      }
      mNumberOfEnqueuedStates = 0;
    }
  }

  @Override
  public void addCurrentStateToQueue()
  {
    synchronized (mAcquisitionLock)
    {
      for (final Object lDevice : mDeviceLists.getAllDeviceList())
      {
        if (lDevice instanceof StateQueueDeviceInterface)
          if (isActiveDevice(lDevice))
          {
            final StateQueueDeviceInterface lStateQueueDeviceInterface =
                                                                       (StateQueueDeviceInterface) lDevice;
            lStateQueueDeviceInterface.addCurrentStateToQueue();
          }

      }
      mNumberOfEnqueuedStates++;
    }
  }

  @Override
  public void finalizeQueue()
  {
    synchronized (mAcquisitionLock)
    {
      // TODO: this should be put in a subclass specific to the way that we
      // trigger cameras...
      mDeviceLists.getDevice(SignalGeneratorInterface.class, 0)
                  .addCurrentStateToQueue();

      for (final Object lDevice : mDeviceLists.getAllDeviceList())
      {
        if (lDevice instanceof StateQueueDeviceInterface)
          if (isActiveDevice(lDevice))
          {
            final StateQueueDeviceInterface lStateQueueDeviceInterface =
                                                                       (StateQueueDeviceInterface) lDevice;
            lStateQueueDeviceInterface.finalizeQueue();
          }
      }
    }
  }

  private boolean isActiveDevice(final Object lDevice)
  {
    boolean lIsActive = true;
    if (lDevice instanceof ActivableDeviceInterface)
    {
      ActivableDeviceInterface lActivableDeviceInterface =
                                                         (ActivableDeviceInterface) lDevice;
      lIsActive = lActivableDeviceInterface.isActive();
    }
    return lIsActive;
  }

  @Override
  public long lastAcquiredStacksTimeStampInNS()
  {
    return mAverageTimeInNS;
  }

  @Override
  public Variable<StackInterface> getStackVariable(int pIndex)
  {
    StackProcessingPipeline lStackProcessingPipeline =
                                                     mStackPipelines.get(pIndex);
    if (lStackProcessingPipeline != null)
      return lStackProcessingPipeline.getOutputVariable();
    else
      return mDeviceLists.getDevice(StackCameraDeviceInterface.class,
                                    pIndex)
                         .getStackVariable();
  }

  @Override
  public void useRecycler(final String pName,
                          final int pMinimumNumberOfAvailableStacks,
                          final int pMaximumNumberOfAvailableObjects,
                          final int pMaximumNumberOfLiveObjects)
  {
    int lNumberOfStackCameraDevices =
                                    getNumberOfDevices(StackCameraDeviceInterface.class);
    RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                              mStackRecyclerManager.getRecycler(pName,
                                                                                                lNumberOfStackCameraDevices
                                                                                                       * pMaximumNumberOfAvailableObjects,
                                                                                                lNumberOfStackCameraDevices * pMaximumNumberOfLiveObjects);

    for (int i = 0; i < lNumberOfStackCameraDevices; i++)
      getDevice(StackCameraDeviceInterface.class,
                i).setMinimalNumberOfAvailableStacks(pMinimumNumberOfAvailableStacks);

    setRecycler(lRecycler);
  }

  @Override
  public void setRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler)
  {
    int lNumberOfStackCameraDevices =
                                    getDeviceLists().getNumberOfDevices(StackCameraDeviceInterface.class);
    for (int i = 0; i < lNumberOfStackCameraDevices; i++)
      setRecycler(i, pRecycler);
  }

  @Override
  public void setRecycler(int pStackCameraDeviceIndex,
                          RecyclerInterface<StackInterface, StackRequest> pRecycler)
  {
    getDeviceLists().getDevice(StackCameraDeviceInterface.class,
                               pStackCameraDeviceIndex)
                    .setStackRecycler(pRecycler);
  }

  @Override
  public RecyclerInterface<StackInterface, StackRequest> getRecycler(int pStackCameraDeviceIndex)
  {
    return getDeviceLists().getDevice(StackCameraDeviceInterface.class,
                                      pStackCameraDeviceIndex)
                           .getStackRecycler();
  }

  @Override
  public void clearRecycler(String pName)
  {
    mStackRecyclerManager.clear(pName);
  }

  @Override
  public void clearAllRecyclers()
  {
    mStackRecyclerManager.clearAll();
  }

  @Override
  public int getQueueLength()
  {
    return mNumberOfEnqueuedStates;
  }

  @Override
  public FutureBooleanList playQueue()
  {
    synchronized (mAcquisitionLock)
    {
      GarbageCollector.trigger();
      final FutureBooleanList lFutureBooleanList =
                                                 new FutureBooleanList();

      for (final Object lDevice : mDeviceLists.getAllDeviceList())
      {

        if (lDevice instanceof StateQueueDeviceInterface)
        {
          /*info("playQueue() on device: %s \n",
          									lDevice);/**/
          final StateQueueDeviceInterface lStateQueueDeviceInterface =
                                                                     (StateQueueDeviceInterface) lDevice;
          final Future<Boolean> lPlayQueueFuture =
                                                 lStateQueueDeviceInterface.playQueue();
          lFutureBooleanList.addFuture(lDevice.toString(),
                                       lPlayQueueFuture);
        }
      }

      return lFutureBooleanList;
    }
  }

  @Override
  public Boolean playQueueAndWait(long pTimeOut,
                                  TimeUnit pTimeUnit) throws InterruptedException,
                                                      ExecutionException,
                                                      TimeoutException
  {
    synchronized (mAcquisitionLock)
    {
      final FutureBooleanList lPlayQueue = playQueue();
      return lPlayQueue.get(pTimeOut, pTimeUnit);
    }
  }

  @Override
  public Boolean playQueueAndWaitForStacks(long pTimeOut,
                                           TimeUnit pTimeUnit) throws InterruptedException,
                                                               ExecutionException,
                                                               TimeoutException
  {
    synchronized (mAcquisitionLock)
    {
      int lNumberOfDetectionArmDevices =
                                       getDeviceLists().getNumberOfDevices(DetectionArmInterface.class);
      CountDownLatch[] lStacksReceivedLatches =
                                              new CountDownLatch[lNumberOfDetectionArmDevices];

      mAverageTimeInNS = 0;

      ArrayList<VariableSetListener<StackInterface>> lListenerList =
                                                                   new ArrayList<>();
      for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
      {
        lStacksReceivedLatches[i] = new CountDownLatch(1);

        final int fi = i;

        VariableSetListener<StackInterface> lVariableSetListener =
                                                                 new VariableSetListener<StackInterface>()
                                                                 {

                                                                   @Override
                                                                   public void setEvent(StackInterface pCurrentValue,
                                                                                        StackInterface pNewValue)
                                                                   {
                                                                     lStacksReceivedLatches[fi].countDown();
                                                                     mAverageTimeInNS +=
                                                                                      pNewValue.getTimeStampInNanoseconds()
                                                                                         / lNumberOfDetectionArmDevices;
                                                                   }
                                                                 };

        lListenerList.add(lVariableSetListener);

        getStackVariable(i).addSetListener(lVariableSetListener);
      }

      // info("Playing queue of length: " + getQueueLength());
      final FutureBooleanList lPlayQueue = playQueue();

      Boolean lBoolean = lPlayQueue.get(pTimeOut, pTimeUnit);

      if (lBoolean != null && lBoolean)
      {
        for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
        {
          lStacksReceivedLatches[i].await(pTimeOut, pTimeUnit);
        }
      }

      for (VariableSetListener<StackInterface> lVariableSetListener : lListenerList)
        for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
        {
          getStackVariable(i).removeSetListener(lVariableSetListener);
        }

      return lBoolean;
    }
  }

  @Override
  public void setMainXYZRStage(StageDeviceInterface pStageDeviceInterface)
  {
    mMainXYZRStage = pStageDeviceInterface;
  }

  @Override
  public StageDeviceInterface getMainXYZRStage()
  {
    return mMainXYZRStage;
  }

  @Override
  public void setStageX(double pXValue)
  {
    Variable<Double> lTargetPositionVariable =
                                             mMainXYZRStage.getTargetPositionVariable(mMainXYZRStage.getDOFIndexByName("X"));
    if (lTargetPositionVariable != null)
      lTargetPositionVariable.set(pXValue);
  }

  @Override
  public void setStageY(double pYValue)
  {
    Variable<Double> lTargetPositionVariable =
                                             mMainXYZRStage.getTargetPositionVariable(mMainXYZRStage.getDOFIndexByName("Y"));
    if (lTargetPositionVariable != null)
      lTargetPositionVariable.set(pYValue);
  }

  @Override
  public void setStageZ(double pZValue)
  {
    Variable<Double> lTargetPositionVariable =
                                             mMainXYZRStage.getTargetPositionVariable(mMainXYZRStage.getDOFIndexByName("Z"));
    if (lTargetPositionVariable != null)
      lTargetPositionVariable.set(pZValue);
  }

  @Override
  public void setStageR(double pZValue)
  {
    Variable<Double> lTargetPositionVariable =
                                             mMainXYZRStage.getTargetPositionVariable(mMainXYZRStage.getDOFIndexByName("R"));
    if (lTargetPositionVariable != null)
      lTargetPositionVariable.set(pZValue);
  }

  @Override
  public double getStageX()
  {
    Variable<Double> lTargetPositionVariable =
                                             mMainXYZRStage.getTargetPositionVariable(mMainXYZRStage.getDOFIndexByName("X"));
    if (lTargetPositionVariable != null)
      return lTargetPositionVariable.get();
    return 0;
  }

  @Override
  public double getStageY()
  {
    Variable<Double> lTargetPositionVariable =
                                             mMainXYZRStage.getTargetPositionVariable(mMainXYZRStage.getDOFIndexByName("Y"));
    if (lTargetPositionVariable != null)
      return lTargetPositionVariable.get();
    return 0;
  }

  @Override
  public double getStageZ()
  {
    Variable<Double> lTargetPositionVariable =
                                             mMainXYZRStage.getTargetPositionVariable(mMainXYZRStage.getDOFIndexByName("Z"));
    if (lTargetPositionVariable != null)
      return lTargetPositionVariable.get();
    return 0;
  }

  @Override
  public double getStageR()
  {
    Variable<Double> lTargetPositionVariable =
                                             mMainXYZRStage.getTargetPositionVariable(mMainXYZRStage.getDOFIndexByName("R"));
    if (lTargetPositionVariable != null)
      return lTargetPositionVariable.get();
    return 0;
  }
}
