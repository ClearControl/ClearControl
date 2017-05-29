package clearcontrol.microscope.lightsheet;

import clearcl.ClearCLContext;
import clearcontrol.core.concurrent.future.FutureBooleanList;
import clearcontrol.core.device.switches.SwitchingDeviceInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.MicroscopeBase;
import clearcontrol.microscope.adaptive.AdaptiveEngine;
import clearcontrol.microscope.lightsheet.calibrator.CalibrationEngine;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import clearcontrol.microscope.lightsheet.processor.LightSheetFastFusionProcessor;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.timelapse.LightSheetTimelapse;
import clearcontrol.microscope.timelapse.TimelapseInterface;

/**
 * Lightsheet microscope class
 *
 * @author royer
 */
public class LightSheetMicroscope extends
                                  MicroscopeBase<LightSheetMicroscope, LightSheetMicroscopeQueue>
                                  implements
                                  LightSheetMicroscopeInterface
{
  private LightSheetFastFusionProcessor mStackFusionProcessor;

  /**
   * Instantiates a lightsheet microscope with a given name.
   * 
   * @param pDeviceName
   *          device name
   * @param pStackFusionContext
   *          ClearCL context for stack fusion
   * @param pMaxStackProcessingQueueLength
   *          max stack processing queue length
   * @param pThreadPoolSize
   *          thread pool size for stack processing pipeline
   */
  public LightSheetMicroscope(String pDeviceName,
                              ClearCLContext pStackFusionContext,
                              int pMaxStackProcessingQueueLength,
                              int pThreadPoolSize)
  {
    super(pDeviceName,
          pMaxStackProcessingQueueLength,
          pThreadPoolSize);

    mStackFusionProcessor =
                          new LightSheetFastFusionProcessor("Fusion Stack Processor",
                                                            this,
                                                            pStackFusionContext);

    addDevice(0, mStackFusionProcessor);

    mStackProcessingPipeline.addStackProcessor(mStackFusionProcessor,
                                               "StackFusion",
                                               32,
                                               32);

  }

  @Override
  public int getNumberOfDetectionArms()
  {
    return getNumberOfDevices(DetectionArmInterface.class);
  }

  @Override
  public int getNumberOfLightSheets()
  {
    return getNumberOfDevices(LightSheetInterface.class);
  }

  @Override
  public int getNumberOfLaserLines()
  {
    return getNumberOfDevices(LaserDeviceInterface.class);
  }

  @Override
  public DetectionArmInterface getDetectionArm(int pDeviceIndex)
  {
    return getDevice(DetectionArmInterface.class, pDeviceIndex);
  }

  @Override
  public LightSheetInterface getLightSheet(int pDeviceIndex)
  {
    return getDevice(LightSheetInterface.class, pDeviceIndex);
  }

  @Override
  public <T> void addDevice(int pDeviceIndex, T pDevice)
  {
    super.addDevice(pDeviceIndex, pDevice);

    if (pDevice instanceof StackCameraDeviceInterface)
    {
      StackCameraDeviceInterface<?> lStackCameraDevice =
                                                       (StackCameraDeviceInterface<?>) pDevice;
      lStackCameraDevice.getStackVariable()
                        .sendUpdatesTo(getStackProcesssingPipeline().getInputVariable());
    }
  }

  /**
   * Adds an interactive acquisition device for a given acquisition state
   * manager.
   * 
   * @return interactive acquisition
   */
  public InteractiveAcquisition addInteractiveAcquisition()
  {
    InteractiveAcquisition lInteractiveAcquisition =
                                                   new InteractiveAcquisition("Interactive",
                                                                              this);
    addDevice(0, lInteractiveAcquisition);
    return lInteractiveAcquisition;
  }

  /**
   * Add calibrator
   * 
   * @return calibrator
   */
  public CalibrationEngine addCalibrator()
  {
    CalibrationEngine lCalibrator = new CalibrationEngine(this);
    addDevice(0, lCalibrator);
    return lCalibrator;
  }

  /**
   * Adds timelapse
   * 
   * @return timelapse
   */
  public TimelapseInterface addTimelapse()
  {
    TimelapseInterface lTimelapseInterface =
                                           new LightSheetTimelapse(this);
    addDevice(0, lTimelapseInterface);
    return lTimelapseInterface;
  }

  /**
   * Adds the adaptive engine
   * 
   * @param pAcquisitionState
   *          acquisition state
   * 
   * @return adaptive engine
   */
  public AdaptiveEngine<InterpolatedAcquisitionState> addAdaptiveEngine(InterpolatedAcquisitionState pAcquisitionState)
  {
    AdaptiveEngine<InterpolatedAcquisitionState> lAdaptiveEngine =
                                                                 new AdaptiveEngine<InterpolatedAcquisitionState>(this,
                                                                                                                  pAcquisitionState);
    addDevice(0, lAdaptiveEngine);
    return lAdaptiveEngine;
  }/**/

  /**
   * Returns lightsheet switching device
   * 
   * @return lightsheet switching device
   */
  public SwitchingDeviceInterface getLightSheetSwitchingDevice()
  {
    return getDevice(LightSheetOpticalSwitch.class, 0);
  }

  /**
   * Sends stacks to null.
   */
  public void sendPipelineStacksToNull()
  {
    getPipelineStackVariable().addSetListener((pCurrentValue,
                                               pNewValue) -> {
      pNewValue.release();
    });
  }

  @Override
  public void setCameraWidthHeight(long pWidth, long pHeight)
  {
    for (int i =
               0; i < getDeviceLists().getNumberOfDevices(StackCameraDeviceInterface.class); i++)
    {
      StackCameraDeviceInterface<?> lDevice =
                                            getDeviceLists().getDevice(StackCameraDeviceInterface.class,
                                                                       i);
      lDevice.getStackWidthVariable().set(pWidth);
      lDevice.getStackHeightVariable().set(pHeight);
    }

    for (int i =
               0; i < getDeviceLists().getNumberOfDevices(LightSheetInterface.class); i++)
    {
      getDeviceLists().getDevice(LightSheetInterface.class, i)
                      .getImageHeightVariable()
                      .set(pHeight);
    }
  };

  @Override
  public int getCameraWidth(int pCameraDeviceIndex)
  {
    @SuppressWarnings("unchecked")
    Variable<Long> lStackWidthVariable =
                                       getDeviceLists().getDevice(StackCameraDeviceInterface.class,
                                                                  pCameraDeviceIndex)
                                                       .getStackWidthVariable();

    return lStackWidthVariable.get().intValue();
  };

  @Override
  public int getCameraHeight(int pCameraDeviceIndex)
  {
    @SuppressWarnings("unchecked")
    Variable<Long> lStackHeightVariable =
                                        getDeviceLists().getDevice(StackCameraDeviceInterface.class,
                                                                   pCameraDeviceIndex)
                                                        .getStackHeightVariable();

    return lStackHeightVariable.get().intValue();
  };

  @Override
  public void setExposure(double pExposureInSeconds)
  {

    for (StackCameraDeviceInterface<?> lStackCamera : getDeviceLists().getDevices(StackCameraDeviceInterface.class))
      lStackCamera.getExposureInSecondsVariable()
                  .set(pExposureInSeconds);

    for (LightSheetInterface lLightSheet : getDeviceLists().getDevices(LightSheetInterface.class))
      lLightSheet.getEffectiveExposureInSecondsVariable()
                 .set(pExposureInSeconds);
  };

  @Override
  public double getExposure(int pCameraDeviceIndex)
  {

    double lExposureInSeconds = getDeviceLists()
                                                .getDevice(StackCameraDeviceInterface.class,
                                                           pCameraDeviceIndex)
                                                .getExposureInSecondsVariable()
                                                .get()
                                                .doubleValue();

    return lExposureInSeconds;
  };

  @Override
  public void setLO(int pLaserIndex, boolean pLaserOnOff)
  {
    getDevice(LaserDeviceInterface.class,
              pLaserIndex).getLaserOnVariable().set(pLaserOnOff);
  };

  @Override
  public boolean getLO(int pLaserIndex)
  {
    return getDevice(LaserDeviceInterface.class, pLaserIndex)
                                                             .getLaserOnVariable()
                                                             .get();
  }

  @Override
  public void setLP(int pLaserIndex, double pLaserPowerInmW)
  {
    getDevice(LaserDeviceInterface.class,
              pLaserIndex).getTargetPowerInMilliWattVariable()
                          .set(pLaserPowerInmW);
  };

  @Override
  public double getLP(int pLaserIndex)
  {
    return getDevice(LaserDeviceInterface.class,
                     pLaserIndex).getTargetPowerInMilliWattVariable()
                                 .get()
                                 .doubleValue();
  }

  /**
   * Returns the number of degrees of freedom of this lightsheet microscope.
   * 
   * @return numebr of DOFs
   */
  public int getNumberOfDOF()
  {
    final int lNumberOfLightSheetsDOFs =
                                       getDeviceLists().getNumberOfDevices(LightSheetInterface.class)
                                         * 7;
    final int lNumberOfDetectionArmDOFs =
                                        getDeviceLists().getNumberOfDevices(DetectionArmInterface.class)
                                          * 1;

    return lNumberOfLightSheetsDOFs + lNumberOfDetectionArmDOFs;
  }

  @Override
  public String toString()
  {
    return String.format("LightSheetMicroscope: \n%s\n",
                         mDeviceLists.toString());
  }

  @Override
  public LightSheetMicroscopeQueue requestQueue()
  {
    LightSheetMicroscopeQueue lLightSheetMicroscopeQueue =
                                                         new LightSheetMicroscopeQueue(this);

    return lLightSheetMicroscopeQueue;
  }

  @Override
  public FutureBooleanList playQueue(LightSheetMicroscopeQueue pQueue)
  {
    return super.playQueue(pQueue);
  }

}
