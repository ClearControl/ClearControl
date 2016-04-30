package clearcontrol.microscope.lightsheet.component.detection.gui.jfx;

import clearcontrol.gui.jfx.sliderpanel.CustomVariablePane;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;

public class DetectionArmPanel extends CustomVariablePane
{

	public DetectionArmPanel(DetectionArmInterface pDetectionArmInterface)
	{
		super();

		addTab("DOFs");
		addSliderForVariable(	"Z :",
		                     	pDetectionArmInterface.getZVariable(),1);/**/
		
		addTab("Functions");
		
		addFunctionPane("Z: ", pDetectionArmInterface.getZFunction());/**/
	}

}
