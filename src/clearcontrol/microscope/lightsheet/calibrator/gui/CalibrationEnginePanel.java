package clearcontrol.microscope.lightsheet.calibrator.gui;

import clearcontrol.gui.jfx.custom.visualconsole.VisualConsolePanel;
import clearcontrol.microscope.lightsheet.calibrator.CalibrationEngine;

/**
 * Calibration Engine Panel
 *
 * @author royer
 */
public class CalibrationEnginePanel extends VisualConsolePanel
{

  /**
   * Instantiates a panel for displaying information about the calibration
   * engine.
   * 
   * @param pCalibrationEngine
   *          calibration engine
   */
  public CalibrationEnginePanel(CalibrationEngine pCalibrationEngine)
  {
    super(pCalibrationEngine);
  }

}
