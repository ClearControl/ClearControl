package rtlib.microscope.lsm.component.lightsheet.gui.jfx;

import rtlib.gui.jfx.sliderpanel.SliderPanel;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;

public class LightSheetPanel extends SliderPanel
{

	public LightSheetPanel(LightSheetInterface pLightSheetInterface)
	{
		super();

	
		double lMinOffset = pLightSheetInterface.getZFunction().get().getMin();
		double lMaxOffset = pLightSheetInterface.getZFunction().get().getMax();

		addSliderForVariable(	pLightSheetInterface.getZVariable(),
													lMinOffset,
													lMaxOffset,
													0.1 * (lMaxOffset - lMinOffset));/**/
	}

}
