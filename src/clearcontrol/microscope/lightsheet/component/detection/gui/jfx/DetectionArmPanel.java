package clearcontrol.microscope.lightsheet.component.detection.gui.jfx;

import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;

/**
 * Detection arm panel
 *
 * @author royer
 */
public class DetectionArmPanel extends CustomVariablePane
{

  /**
   * Instanciates a detection arm panel
   * 
   * @param pDetectionArmInterface
   *          detection arm device
   */
  public DetectionArmPanel(DetectionArmInterface pDetectionArmInterface)
  {
    super();

    addTab("DOFs");
    addSliderForVariable("Z :",
                         pDetectionArmInterface.getZVariable(),
                         5).setUpdateIfChanging(true);/**/

    addTab("Functions");

    addFunctionPane("Z: ", pDetectionArmInterface.getZFunction());/**/

  }

}
