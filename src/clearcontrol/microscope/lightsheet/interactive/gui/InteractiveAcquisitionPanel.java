package clearcontrol.microscope.lightsheet.interactive.gui;

import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;

/**
 * InteractiveAcquisitionPanel is a GUI element that displays information about
 * the 2D and 3D interactive acquisition.
 * 
 * @author royer
 */
public class InteractiveAcquisitionPanel extends CustomVariablePane

{

  private InteractiveAcquisition mInteractiveAcquisition;

  /**
   * Instanciates an interactive acquisition panel for a given interactive
   * acquisition device
   * 
   * @param pInteractiveAcquisition
   *          interactive acquisition
   */
  public InteractiveAcquisitionPanel(InteractiveAcquisition pInteractiveAcquisition)
  {
    super();
    mInteractiveAcquisition = pInteractiveAcquisition;

    addTab("DOFs");

    addSliderForVariable("Z :",
                         mInteractiveAcquisition.get2DAcquisitionZVariable(),
                         null).setUpdateIfChanging(true);
    addCheckBoxForVariable("Control detection:",
                           pInteractiveAcquisition.getControlDetectionVariable());
    addCheckBoxForVariable("Control illumination:",
                           pInteractiveAcquisition.getControlIlluminationVariable());

  }

}
