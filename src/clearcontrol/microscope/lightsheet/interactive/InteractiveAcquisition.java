package clearcontrol.microscope.lightsheet.interactive;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.change.ChangeListener;
import clearcontrol.core.device.task.PeriodicLoopTaskDevice;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeQueue;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.state.AcquisitionType;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.state.LightSheetAcquisitionStateInterface;
import clearcontrol.microscope.stacks.metadata.MetaDataAcquisitionType;
import clearcontrol.microscope.stacks.metadata.MetaDataView;
import clearcontrol.microscope.stacks.metadata.MetaDataViewFlags;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.stack.metadata.StackMetaData;

/**
 * Interactive acquisition for lightseet microscope
 *
 * @author royer
 */
public class InteractiveAcquisition extends PeriodicLoopTaskDevice
                                    implements LoggingInterface
{

  private static final int cRecyclerMinimumNumberOfAvailableStacks =
                                                                   60;
  private static final int cRecyclerMaximumNumberOfAvailableStacks =
                                                                   60;
  private static final int cRecyclerMaximumNumberOfLiveStacks = 60;

  private final LightSheetMicroscopeInterface mLightSheetMicroscope;
  private final AcquisitionStateManager<LightSheetAcquisitionStateInterface<?>> mAcquisitionStateManager;

  private volatile InteractiveAcquisitionModes mCurrentAcquisitionMode =
                                                                       InteractiveAcquisitionModes.None;

  private final BoundedVariable<Double> mExposureVariableInSeconds;
  private final Variable<Boolean> mTriggerOnChangeVariable,
      mUseCurrentAcquisitionStateVariable;
  private final Variable<Boolean> mControlIlluminationZVariable,
      mControlDetectionZVariable;
  private final BoundedVariable<Number> m2DAcquisitionZVariable;

  private final Variable<Long> mAcquisitionCounterVariable;

  private volatile boolean mUpdate = true;

  private ChangeListener<VirtualDevice> mMicroscopeChangeListener;
  private ChangeListener<AcquisitionStateInterface<LightSheetMicroscopeInterface, LightSheetMicroscopeQueue>> mAcquisitionStateChangeListener;
  private LightSheetMicroscopeQueue mQueue;

  /**
   * Instantiates an interactive acquisition for lightsheet microscope
   * 
   * @param pDeviceName
   *          device name
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   * @param pAcquisitionStateManager
   *          acquisition state manager
   */
  @SuppressWarnings("unchecked")
  public InteractiveAcquisition(String pDeviceName,
                                LightSheetMicroscope pLightSheetMicroscope,
                                AcquisitionStateManager<LightSheetAcquisitionStateInterface<?>> pAcquisitionStateManager)
  {
    super(pDeviceName, 1, TimeUnit.SECONDS);
    mLightSheetMicroscope = pLightSheetMicroscope;
    mAcquisitionStateManager = pAcquisitionStateManager;

    @SuppressWarnings("rawtypes")
    VariableSetListener lListener = (o, n) -> {
      if (o != n || (o == null && n != null) || !o.equals(n))
        mUpdate = true;
    };

    mExposureVariableInSeconds =
                               new BoundedVariable<Double>(pDeviceName
                                                           + "Exposure",
                                                           0.0,
                                                           0.0,
                                                           Double.POSITIVE_INFINITY,
                                                           0.0);

    mTriggerOnChangeVariable = new Variable<Boolean>(pDeviceName
                                                     + "TriggerOnChange",
                                                     false);

    mUseCurrentAcquisitionStateVariable =
                                        new Variable<Boolean>(pDeviceName
                                                              + "UseCurrentAcquisitionState",
                                                              false);

    Variable<Number> lMinVariable =
                                  mLightSheetMicroscope.getDevice(DetectionArmInterface.class,
                                                                  0)
                                                       .getZVariable()
                                                       .getMinVariable();
    Variable<Number> lMaxVariable =
                                  mLightSheetMicroscope.getDevice(DetectionArmInterface.class,
                                                                  0)
                                                       .getZVariable()
                                                       .getMaxVariable();

    mControlDetectionZVariable =
                               new Variable<Boolean>("Control Illumination",
                                                     false);
    mControlIlluminationZVariable =
                                  new Variable<Boolean>("Control Detection",
                                                        false);

    m2DAcquisitionZVariable =
                            new BoundedVariable<Number>("2DAcquisitionZ",
                                                        0.0,
                                                        lMinVariable.get(),
                                                        lMaxVariable.get());

    mAcquisitionCounterVariable =
                                new Variable<Long>("AcquisitionCounter",
                                                   0L);

    lMinVariable.sendUpdatesTo(m2DAcquisitionZVariable.getMinVariable());
    lMaxVariable.sendUpdatesTo(m2DAcquisitionZVariable.getMaxVariable());

    getExposureVariable().addSetListener(lListener);
    getTriggerOnChangeVariable().addSetListener(lListener);
    getLoopPeriodVariable().addSetListener(lListener);
    get2DAcquisitionZVariable().addSetListener(lListener);
    getControlDetectionZVariable().addSetListener(lListener);
    getControlIlluminationZVariable().addSetListener(lListener);

    getLoopPeriodVariable().set(1.0);
    getExposureVariable().set(0.010);

    mMicroscopeChangeListener = (o) -> {
      // info("Received request to update queue from:" + o.toString());

      mUpdate = true;
    };
    mAcquisitionStateChangeListener = (o) -> {
      // info("Received request to update queue from:" + o.toString());
      mUpdate = true;
    };
  }

  @Override
  public boolean open()
  {
    getLightSheetMicroscope().addChangeListener(mMicroscopeChangeListener);
    return super.open();
  }

  @Override
  public boolean close()
  {
    getLightSheetMicroscope().removeChangeListener(mMicroscopeChangeListener);
    return super.close();
  }

  @Override
  public void run()
  {
    try
    {
      super.run();
    }
    finally
    {
      getLightSheetMicroscope().getCurrentTask().set(null);
    }
  }

  @Override
  public boolean loop()
  {
    try
    {
      // info("begin of loop");
      final boolean lCachedUpdate = mUpdate;

      InterpolatedAcquisitionState lCurrentState =
                                                 (InterpolatedAcquisitionState) mAcquisitionStateManager.getCurrentState();

      if (getCurrentAcquisitionMode() == InteractiveAcquisitionModes.Acquisition3D
          || getUseCurrentAcquisitionStateVariable().get())
      {
        if (!lCurrentState.isChangeListener(mAcquisitionStateChangeListener))
          lCurrentState.addChangeListener(mAcquisitionStateChangeListener);
      }

      if (lCachedUpdate || mQueue == null
          || mQueue.getQueueLength() == 0)
      {

        double lCurrentZ = get2DAcquisitionZVariable().get()
                                                      .doubleValue();

        if (getCurrentAcquisitionMode() == InteractiveAcquisitionModes.Acquisition2D)
        {
          // info("Building 2D Acquisition queue");
          if (getUseCurrentAcquisitionStateVariable().get())
          {
            // info("Building 2D Acquisition queue using the current acquisition
            // state");

            mQueue = getLightSheetMicroscope().requestQueue();

            mQueue.clearQueue();
            mQueue.addVoxelDimMetaData(getLightSheetMicroscope(), 1);

            lCurrentState.applyAcquisitionStateAtZ(mQueue, lCurrentZ);
            mQueue.addCurrentStateToQueue();
            mQueue.finalizeQueue();

          }
          else
          {
            // info("Building 2D Acquisition queue using current devices
            // state");
            getLightSheetMicroscope().useRecycler("2DInteractive",
                                                  cRecyclerMinimumNumberOfAvailableStacks,
                                                  cRecyclerMaximumNumberOfAvailableStacks,
                                                  cRecyclerMaximumNumberOfLiveStacks);

            mQueue = getLightSheetMicroscope().requestQueue();
            mQueue.clearQueue();
            mQueue.addVoxelDimMetaData(getLightSheetMicroscope(), 1);
            for (int c = 0; c < getNumberOfCameras(); c++)
            {
              mQueue.setC(c, true);
              if (mControlDetectionZVariable.get())
                mQueue.setDZ(c, lCurrentZ);
            }
            mQueue.setExp(mExposureVariableInSeconds.get()
                                                    .doubleValue());

            for (int l = 0; l < getNumberOfLightsSheets(); l++)
            {
              boolean lIsOn = mQueue.getI(l);
              if (lIsOn && mControlIlluminationZVariable.get())
                mQueue.setIZ(l, lCurrentZ);
            }

            mQueue.addCurrentStateToQueue();

            mQueue.finalizeQueue();

          }
        }
        else if (getCurrentAcquisitionMode() == InteractiveAcquisitionModes.Acquisition3D)
        {
          // info("Building Acquisition3D queue");
          getLightSheetMicroscope().useRecycler("3DInteractive",
                                                cRecyclerMinimumNumberOfAvailableStacks,
                                                cRecyclerMaximumNumberOfAvailableStacks,
                                                cRecyclerMaximumNumberOfLiveStacks);

          if (lCurrentState != null)
          {
            lCurrentState.updateQueue(true);
            mQueue = lCurrentState.getQueue();
          }
        }

        if (lCachedUpdate)
          mUpdate = false;
      }

      if (mQueue.getQueueLength() == 0)
      {
        // this leads to a call to stop() which stops the loop
        warning("Queue empty stopping interactive acquisition loop");
        return false;
      }

      // Setting meta-data:
      for (int c = 0; c < getNumberOfCameras(); c++)
      {
        StackMetaData lMetaData = mQueue.getCameraDeviceQueue(c)
                                        .getMetaDataVariable()
                                        .get();

        lMetaData.addEntry(MetaDataView.Camera, c);

        for (int l = 0; l < getNumberOfLightsSheets(); l++)
          lMetaData.addEntry(MetaDataViewFlags.getLightSheet(l),
                             mQueue.getI(l));

        lMetaData.addEntry(MetaDataAcquisitionType.AcquisitionType,
                           AcquisitionType.Interactive);
      }

      if (getCurrentAcquisitionMode() != InteractiveAcquisitionModes.None)
      {
        if (getTriggerOnChangeVariable().get() && !lCachedUpdate)
          return true;

        if (getUseCurrentAcquisitionStateVariable().get()
            || getCurrentAcquisitionMode() == InteractiveAcquisitionModes.Acquisition3D)
          lCurrentState.prepareAcquisition(100, TimeUnit.SECONDS);

        // info("play queue");
        // play queue
        // info("Playing LightSheetMicroscope Queue...");
        boolean lSuccess =
                         getLightSheetMicroscope().playQueueAndWaitForStacks(mQueue,
                                                                             30,
                                                                             TimeUnit.SECONDS);

        if (lSuccess)
        {
          // info("play queue success");
          mAcquisitionCounterVariable.increment();
        }

        // info("... done waiting!");
      }

    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

    // info("end of loop");

    return true;
  }

  /**
   * Starts 2D acquisition
   */
  public void start2DAcquisition()
  {
    if (getLightSheetMicroscope().getCurrentTask().get() != null)
    {
      warning("Another task (%s) is already running, please stop it first.",
              getLightSheetMicroscope().getCurrentTask());
      return;
    }
    
    getLightSheetMicroscope().getCurrentTask().set(this);

    if (getIsRunningVariable().get()
        && getCurrentAcquisitionMode() == InteractiveAcquisitionModes.Acquisition3D)
    {
      warning("Please stop 3D acquisition first!");
      return;
    }
    if (getCurrentAcquisitionMode() == InteractiveAcquisitionModes.Acquisition2D)
    {
      warning("Already doing 2D acquisition!");
      return;
    }

    info("Starting 2D Acquisition...");
    setCurrentAcquisitionMode(InteractiveAcquisitionModes.Acquisition2D);
    mAcquisitionCounterVariable.set(0L);
    mUpdate = true;
    startTask();
  }

  /**
   * Starts 3D acquisition
   */
  public void start3DAcquisition()
  {
    if (getLightSheetMicroscope().getCurrentTask().get() != null)
    {
      warning("Another task (%s) is already running, please stop it first.",
              getLightSheetMicroscope().getCurrentTask());
      return;
    }

    if (getIsRunningVariable().get()
        && getCurrentAcquisitionMode() == InteractiveAcquisitionModes.Acquisition2D)
    {
      warning("Please stop 2D acquisition first!");
      return;
    }
    if (getCurrentAcquisitionMode() == InteractiveAcquisitionModes.Acquisition3D)
    {
      warning("Already doing 3D acquisition!");
      return;
    }

    info("Starting 3D Acquisition...");
    setCurrentAcquisitionMode(InteractiveAcquisitionModes.Acquisition3D);
    mAcquisitionCounterVariable.set(0L);
    mUpdate = true;

    startTask();
  }

  /**
   * Stops acquisition
   */
  public void stopAcquisition()
  {
    info("Stopping Acquisition...");
    setCurrentAcquisitionMode(InteractiveAcquisitionModes.None);
    stopTask();

  }

  /**
   * Returns the exposure variable
   * 
   * @return exposure variable (unit: seconds)
   */
  public BoundedVariable<Double> getExposureVariable()
  {
    return mExposureVariableInSeconds;
  }

  /**
   * Returns the trigger-on-change variable
   * 
   * @return trigger-on-change variable
   */
  public Variable<Boolean> getTriggerOnChangeVariable()
  {
    return mTriggerOnChangeVariable;
  }

  /**
   * Returns the use-current-acquisition-state variable
   * 
   * @return use-current-acquisition-state variable
   */
  public Variable<Boolean> getUseCurrentAcquisitionStateVariable()
  {
    return mUseCurrentAcquisitionStateVariable;
  }

  /**
   * Returns lightsheet microscope
   * 
   * @return lightsheet microscope
   */
  public LightSheetMicroscopeInterface getLightSheetMicroscope()
  {
    return mLightSheetMicroscope;
  }

  /**
   * Returns the number of cameras
   * 
   * @return number of cameras
   */
  public int getNumberOfCameras()
  {
    int lNumberOfCameras =
                         mLightSheetMicroscope.getNumberOfDevices(StackCameraDeviceInterface.class);
    return lNumberOfCameras;
  }

  private int getNumberOfLightsSheets()
  {
    int lNumberOfLightsSheets =
                              mLightSheetMicroscope.getNumberOfDevices(LightSheet.class);
    return lNumberOfLightsSheets;
  }

  /**
   * Returns the variable that holds the flag that decides whether to control
   * the detection z focus using the z value from this interactive acquisition
   * device.
   * 
   * @return control illumination variable
   */
  public Variable<Boolean> getControlDetectionZVariable()
  {
    return mControlDetectionZVariable;
  }

  /**
   * Returns the variable that holds the flag that decides whether to control
   * the illumination z focus using the z value from this interactive
   * acquisition device, or not :-)
   * 
   * @return control illumination variable
   */
  public Variable<Boolean> getControlIlluminationZVariable()
  {
    return mControlIlluminationZVariable;
  }

  /**
   * Returns the 2D acquisition Z variable
   * 
   * @return 2D acquisition Z variable
   */
  public BoundedVariable<Number> get2DAcquisitionZVariable()
  {
    return m2DAcquisitionZVariable;
  }

  /**
   * Returns the acquisition counter variable
   * 
   * @return acquisition counter variable
   */
  public Variable<Long> getAcquisitionCounterVariable()
  {
    return mAcquisitionCounterVariable;

  }

  /**
   * Returns current acquisition mode
   * 
   * @return current acquisition mode
   */
  public InteractiveAcquisitionModes getCurrentAcquisitionMode()
  {
    return mCurrentAcquisitionMode;
  }

  /**
   * Sets current acquisition mode
   * 
   * @param pNewAcquisitionMode
   *          new acquisition mode
   */
  public void setCurrentAcquisitionMode(InteractiveAcquisitionModes pNewAcquisitionMode)
  {
    mCurrentAcquisitionMode = pNewAcquisitionMode;
  }

}
