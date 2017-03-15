package clearcontrol.microscope.lightsheet.interactive.gui.jfx;

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

  public InteractiveAcquisitionPanel(InteractiveAcquisition pInteractiveAcquisition)
  {
    super();
    mInteractiveAcquisition = pInteractiveAcquisition;

    addTab("DOFs");

    addSliderForVariable("Z :",
                         mInteractiveAcquisition.get2DAcquisitionZVariable(),
                         10.0).setUpdateIfChanging(true);

  }

}
