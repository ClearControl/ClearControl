package clearcontrol.microscope.lightsheet;

import static java.lang.Math.toIntExact;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.future.FutureBooleanList;
import clearcontrol.core.device.switches.SwitchingDeviceInterface;
import clearcontrol.devices.cameras.StackCameraDeviceInterface;
import clearcontrol.devices.lasers.LaserDeviceInterface;
import clearcontrol.microscope.MicroscopeBase;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.timelapse.Timelapse;
import clearcontrol.microscope.timelapse.TimelapseInterface;

/**
 * Lightsheet microscope class
 *
 * @author royer
 */
public class LightSheetMicroscope extends
                                  MicroscopeBase<LightSheetMicroscopeQueue>
                                  implements
                                  LightSheetMicroscopeInterface
{
  private SwitchingDeviceInterface mLightSheetOpticalSwitch;
  private AcquisitionStateManager mAcquisitionStateManager;

  /**
   * Instanciates a lightsheet microscope with a given name.
   * 
   * @param pDeviceName
   *          device name
   */
  public LightSheetMicroscope(String pDeviceName)
  {
    super(pDeviceName);
  }

  /**
   * Adds an interactive acquisition device for a given acquisition state
   * manager.
   * 
   * @param pAcquisitionStateManager
   *          acquisition state manager
   * @return interactive acquisition
   */
  public InteractiveAcquisition addInteractiveAcquisition(AcquisitionStateManager pAcquisitionStateManager)
  {
    InteractiveAcquisition lInteractiveAcquisition =
                                                   new InteractiveAcquisition(getName()
                                                                              + "InteractiveAcquisition",
                                                                              this,
                                                                              pAcquisitionStateManager);
    addDevice(0, lInteractiveAcquisition);
    return lInteractiveAcquisition;
  }

  /**
   * Add calibrator
   * 
   * @return calibrator
   */
  public Calibrator addCalibrator()
  {
    Calibrator lCalibrator = new Calibrator(this);
    addDevice(0, lCalibrator);
    return lCalibrator;
  }

  /**
   * Adds acquisition state manager
   * 
   * @return acquisition manager
   */
  public AcquisitionStateManager addAcquisitionStateManager()
  {
    mAcquisitionStateManager = new AcquisitionStateManager(this);
    addDevice(0, mAcquisitionStateManager);
    return mAcquisitionStateManager;
  }

  /**
   * Adds timelapse
   * 
   * @return timelapse
   */
  public TimelapseInterface addTimelapse()
  {
    TimelapseInterface lTimelapseInterface = new Timelapse();
    addDevice(0, lTimelapseInterface);
    return lTimelapseInterface;
  }

  /*public AutoPilotInterface addAutoPilot()
  {
  	return null;
  	AutoPilotInterface lAutoPilot = new AutoPilot(this,
  	                                              mAcquisitionStateManager);
  	addDevice(0, lAutoPilot);
  	return lAutoPilot;
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
  public void sendStacksToNull()
  {
    for (int i =
               0; i < getDeviceLists().getNumberOfDevices(StackCameraDeviceInterface.class); i++)
    {
      getStackVariable(i).addSetListener((pCurrentValue,
                                          pNewValue) -> {
        pNewValue.release();
      });

    }
  }

  @Override
  public void setCameraWidthHeight(long pWidth, long pHeight)
  {
    for (int i =
               0; i < getDeviceLists().getNumberOfDevices(StackCameraDeviceInterface.class); i++)
    {
      StackCameraDeviceInterface lDevice =
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
    return getDeviceLists().getDevice(StackCameraDeviceInterface.class,
                                      pCameraDeviceIndex)
                           .getStackWidthVariable()
                           .get()
                           .intValue();
  };

  @Override
  public int getCameraHeight(int pCameraDeviceIndex)
  {
    return getDeviceLists().getDevice(StackCameraDeviceInterface.class,
                                      pCameraDeviceIndex)
                           .getStackHeightVariable()
                           .get()
                           .intValue();
  };

  @Override
  public void setExposure(long pValue, TimeUnit pTimeUnit)
  {
    final double lExposureTimeInMicroseconds =
                                             TimeUnit.MICROSECONDS.convert(pValue,
                                                                           pTimeUnit);

    for (StackCameraDeviceInterface lStackCamera : getDeviceLists().getDevices(StackCameraDeviceInterface.class))
      lStackCamera.getExposureInMicrosecondsVariable()
                  .set(lExposureTimeInMicroseconds);

    for (LightSheetInterface lLightSheet : getDeviceLists().getDevices(LightSheetInterface.class))
      lLightSheet.getEffectiveExposureInMicrosecondsVariable()
                 .set(lExposureTimeInMicroseconds);
  };

  @Override
  public long getExposure(int pCameraDeviceIndex, TimeUnit pTimeUnit)
  {

    long lExposureInMicroseconds = getDeviceLists()
                                                   .getDevice(StackCameraDeviceInterface.class,
                                                              pCameraDeviceIndex)
                                                   .getExposureInMicrosecondsVariable()
                                                   .get()
                                                   .longValue();

    long lExposureInProvidedUnit =
                                 pTimeUnit.convert(lExposureInMicroseconds,
                                                   TimeUnit.MICROSECONDS);

    return toIntExact(lExposureInProvidedUnit);
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
