package rtlib.microscope.lightsheet.component.detection.gui.jfx;

import rtlib.gui.jfx.sliderpanel.CustomVariablePane;
import rtlib.microscope.lightsheet.component.detection.DetectionArmInterface;

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
