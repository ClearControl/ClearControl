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
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;

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
  private final AcquisitionStateManager mAcquisitionStateManager;

  private volatile InteractiveAcquisitionModes mCurrentAcquisitionMode =
                                                                       InteractiveAcquisitionModes.None;

  private final BoundedVariable<Double> mExposureVariableInSeconds;
  private final Variable<Boolean> mTriggerOnChangeVariable,
      mUseCurrentAcquisitionStateVariable;
  private final BoundedVariable<Number> m2DAcquisitionZVariable;
  private final Variable<Boolean>[] mActiveCameraVariableArray;
  private final Variable<Long> mAcquisitionCounterVariable;

  private volatile boolean mUpdate = true;

  private ChangeListener<VirtualDevice> mChangeListener;
  private LightSheetMicroscopeQueue mQueue;

  /**
   * Instanciates an interactive acquisition for lightsheet microscope
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
                                AcquisitionStateManager pAcquisitionStateManager)
  {
    super(pDeviceName, 1, TimeUnit.SECONDS);
    mLightSheetMicroscope = pLightSheetMicroscope;
    mAcquisitionStateManager = pAcquisitionStateManager;

    @SuppressWarnings("rawtypes")
    VariableSetListener lListener = (o, n) -> {
      if (!o.equals(n))
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

    m2DAcquisitionZVariable =
                            new BoundedVariable<Number>("2DAcquisitionZ",
                                                        0,
                                                        lMinVariable.get(),
                                                        lMaxVariable.get());

    mAcquisitionCounterVariable =
                                new Variable<Long>("AcquisitionCounter",
                                                   0L);

    lMinVariable.sendUpdatesTo(m2DAcquisitionZVariable.getMinVariable());
    lMaxVariable.sendUpdatesTo(m2DAcquisitionZVariable.getMaxVariable());

    mExposureVariableInSeconds.addSetListener(lListener);
    mTriggerOnChangeVariable.addSetListener(lListener);
    getLoopPeriodVariable().addSetListener(lListener);
    m2DAcquisitionZVariable.addSetListener(lListener);

    getLoopPeriodVariable().set(1.0);
    getExposureVariable().set(0.010);

    int lNumberOfCameras = getNumberOfCameras();
    mActiveCameraVariableArray = new Variable[lNumberOfCameras];
    for (int c = 0; c < lNumberOfCameras; c++)
    {
      mActiveCameraVariableArray[c] =
                                    new Variable<Boolean>("ActiveCamera"
                                                          + c, true);

      mActiveCameraVariableArray[c].addSetListener(lListener);
    }

    mChangeListener = (o) -> {
      // info("Received request to update queue.");
      mUpdate = true;
    };
  }

  @Override
  public boolean open()
  {
    getLightSheetMicroscope().addChangeListener(mChangeListener);
    return super.open();
  }

  @Override
  public boolean close()
  {
    getLightSheetMicroscope().removeChangeListener(mChangeListener);
    return super.close();
  }

  @Override
  public boolean loop()
  {
    try
    {
      // info("begin of loop");
      final boolean lCachedUpdate = mUpdate;

      if (lCachedUpdate || mQueue == null
          || mQueue.getQueueLength() == 0)
      {

        double lCurrentZ = get2DAcquisitionZVariable().get()
                                                      .doubleValue();

        if (mCurrentAcquisitionMode == InteractiveAcquisitionModes.Acquisition2D)
        {
          // info("Building 2D Acquisition queue");
          if (getUseCurrentAcquisitionStateVariable().get())
          {
            // info("Building 2D Acquisition queue using the current acquisition
            // state");

            InterpolatedAcquisitionState lCurrentState =
                                                       (InterpolatedAcquisitionState) mAcquisitionStateManager.getCurrentState();

            lCurrentState.applyAcquisitionStateAtZ(mQueue, lCurrentZ);
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

            for (int c = 0; c < getNumberOfCameras(); c++)
            {
              mQueue.setC(c, mActiveCameraVariableArray[c].get());

              mQueue.setDZ(c, lCurrentZ);
            }
            getLightSheetMicroscope().setExposure((long) (mExposureVariableInSeconds.get()
                                                          * 1000000L),
                                                  TimeUnit.MICROSECONDS);

            for (int l = 0; l < getNumberOfLightsSheets(); l++)
              if (mQueue.getI(l))
              {

                mQueue.setIZ(l, lCurrentZ);

                break;
              }

            mQueue.addCurrentStateToQueue();

            mQueue.finalizeQueue();
          }
        }
        else if (mCurrentAcquisitionMode == InteractiveAcquisitionModes.Acquisition3D)
        {
          // info("Building Acquisition3D queue");
          getLightSheetMicroscope().useRecycler("3DInteractive",
                                                cRecyclerMinimumNumberOfAvailableStacks,
                                                cRecyclerMaximumNumberOfAvailableStacks,
                                                cRecyclerMaximumNumberOfLiveStacks);

          @SuppressWarnings("unchecked")
          AcquisitionStateInterface<LightSheetMicroscopeInterface> lCurrentState =
                                                                                 (AcquisitionStateInterface<LightSheetMicroscopeInterface>) mAcquisitionStateManager.getCurrentState();

          lCurrentState.applyAcquisitionState(mLightSheetMicroscope);
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

      if (mCurrentAcquisitionMode != InteractiveAcquisitionModes.None)
      {
        if (getTriggerOnChangeVariable().get() && !lCachedUpdate)
          return true;

        // info("play queue");
        // play queue
        // info("Playing LightSheetMicroscope Queue...");
        boolean lSuccess =
                         getLightSheetMicroscope().playQueueAndWaitForStacks(mQueue,
                                                                             100,
                                                                             TimeUnit.SECONDS);

        if (lSuccess)
        {
          // info("play queue success");
          mAcquisitionCounterVariable.set(mAcquisitionCounterVariable.get()
                                          + 1);
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
    info("Starting 2D Acquisition...");
    mCurrentAcquisitionMode =
                            InteractiveAcquisitionModes.Acquisition2D;
    mAcquisitionCounterVariable.set(0L);
    startTask();
  }

  /**
   * Starts 3D acquisition
   */
  public void start3DAcquisition()
  {
    info("Starting 3D Acquisition...");
    mCurrentAcquisitionMode =
                            InteractiveAcquisitionModes.Acquisition3D;
    mAcquisitionCounterVariable.set(0L);
    startTask();
  }

  /**
   * Stops acquisition
   */
  public void stopAcquisition()
  {
    info("Stopping Acquisition...");
    mCurrentAcquisitionMode = InteractiveAcquisitionModes.None;
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
   * Returns the active camera variable for a given camera index
   * 
   * @param pCameraIndex
   *          camera index
   * @return active camera variable
   */
  public Variable<Boolean> getActiveCameraVariable(int pCameraIndex)
  {
    return mActiveCameraVariableArray[pCameraIndex];
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

}
