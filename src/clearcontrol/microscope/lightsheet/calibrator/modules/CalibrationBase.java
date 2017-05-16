package clearcontrol.microscope.lightsheet.calibrator.modules;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.calibrator.CalibrationEngine;
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
  private final CalibrationEngine mCalibrationEngine;
  private final LightSheetMicroscope mLightSheetMicroscope;

  private volatile int mIteration = 0;

  /**
   * Instantiates a calibration module given a parent calibrator and lightsheet
   * microscope.
   * 
   * @param pCalibrationEngine
   *          parent calibrator
   */
  public CalibrationBase(CalibrationEngine pCalibrationEngine)
  {
    super();
    mCalibrationEngine = pCalibrationEngine;
    mLightSheetMicroscope =
                          pCalibrationEngine.getLightSheetMicroscope();
  }

  /**
   * Returns this calibrator's parent lightsheet microscope
   * 
   * @return parent lightsheet microscope
   */
  public LightSheetMicroscope getLightSheetMicroscope()
  {
    return mLightSheetMicroscope;
  }

  /**
   * Returns this calibration module parent calibration engine
   * 
   * @return parent calibration engine
   */
  public CalibrationEngine getCalibrationEngine()
  {
    return mCalibrationEngine;
  }

  @Override
  public void reset()
  {
    resetIteration();
  }

  /**
   * Returns the iteration counter
   * 
   * @return iteration counter value
   */
  public int getIteration()
  {
    return mIteration;
  }

  /**
   * increment iteration counter
   */
  public void incrementIteration()
  {
    mIteration++;
  }

  /**
   * resets iteration counter
   */
  public void resetIteration()
  {
    mIteration = 0;
  }

  /**
   * Returns the number of lightsheets
   * 
   * @return number of lightsheets
   */
  public int getNumberOfLightSheets()
  {
    return getLightSheetMicroscope().getDeviceLists()
                                    .getNumberOfDevices(LightSheetInterface.class);
  }

  /**
   * Returns the number of detection arms
   * 
   * @return number of detection arms
   */
  public int getNumberOfDetectionArms()
  {
    return getLightSheetMicroscope().getDeviceLists()
                                    .getNumberOfDevices(DetectionArmInterface.class);
  }

}
