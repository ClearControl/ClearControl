package clearcontrol.microscope.lightsheet.component.detection.gui;

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
                         null).setUpdateIfChanging(true);/**/

    addTab("Functions");
    addFunctionPane("Z: ", pDetectionArmInterface.getZFunction());/**/

    addTab("Bounds");
    addBoundedVariable("Z: ",
                       pDetectionArmInterface.getZVariable());/**/

  }

}
