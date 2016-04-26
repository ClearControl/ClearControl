package rtlib.microscope.lightsheet.component.detection.gui.jfx;

import rtlib.gui.jfx.sliderpanel.SliderPanel;
import rtlib.microscope.lightsheet.component.detection.DetectionArmInterface;

public class DetectionArmPanel extends SliderPanel
{

	public DetectionArmPanel(DetectionArmInterface pDetectionArmInterface)
	{
		super();

		double lMinOffset = pDetectionArmInterface.getZFunction().get().getMin();
		double lMaxOffset = pDetectionArmInterface.getZFunction().get().getMax();

		addSliderForVariable(	"D.Z :",
		                     	pDetectionArmInterface.getZVariable(),
													lMinOffset,
													lMaxOffset,
													0,
													0.1 * (lMaxOffset - lMinOffset));/**/
	}

}
