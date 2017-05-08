package clearcontrol.microscope.lightsheet.calibrator.modules;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;

/**
 * Base class providing common fields and methods for all calibration modules
 *
 * @author royer
 */
public abstract class CalibrationBase implements
                             CalibrationModuleInterface,
                             LoggingInterface
{
  protected final Calibrator mCalibrator;
  protected final LightSheetMicroscope mLightSheetMicroscope;

  /**
   * Instantiates a calibration module given a parent calibrator and lightsheet
   * microscope.
   * 
   * @param pCalibrator
   *          parent calibrator
   */
  public CalibrationBase(Calibrator pCalibrator)
  {
    super();
    mCalibrator = pCalibrator;
    mLightSheetMicroscope = pCalibrator.getLightSheetMicroscope();
  }

  /**
   * Returns the number of lightsheets
   * 
   * @return number of lightsheets
   */
  public int getNumberOfLightSheets()
  {
    return mLightSheetMicroscope.getDeviceLists()
                                .getNumberOfDevices(LightSheetInterface.class);
  }

  /**
   * Returns the number of detection arms
   * 
   * @return number of detection arms
   */
  public int getNumberOfDetectionArms()
  {
    return mLightSheetMicroscope.getDeviceLists()
                                .getNumberOfDevices(DetectionArmInterface.class);
  }

}
