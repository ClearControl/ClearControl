package rtlib.microscope.lightsheet.component.lightsheet.gui.jfx;

import rtlib.gui.jfx.sliderpanel.SliderPanel;
import rtlib.microscope.lightsheet.component.lightsheet.LightSheetInterface;

public class LightSheetPanel extends SliderPanel
{

	public LightSheetPanel(LightSheetInterface pLightSheetInterface)
	{
		super();

		addSliderForVariable(	"X :",
													pLightSheetInterface.getXVariable(),
													5.0);

		addSliderForVariable(	"Y :",
													pLightSheetInterface.getYVariable(),
													5.0);

		addSliderForVariable(	"Z :",
													pLightSheetInterface.getZVariable(),
													5.0);

		addSliderForVariable(	"Alpha :",
													pLightSheetInterface.getAlphaInDegreesVariable(),
													0.2);

		addSliderForVariable(	"Beta :",
													pLightSheetInterface.getBetaInDegreesVariable(),
													0.2);

		addSliderForVariable(	"Width :",
													pLightSheetInterface.getWidthVariable(),
													5.0);

		addSliderForVariable(	"Height :",
													pLightSheetInterface.getHeightVariable(),
													5.0);

		addSliderForVariable(	"Overscan :",
													pLightSheetInterface.getOverScanVariable(),
													1.0);

		addSliderForVariable(	"Power :",
													pLightSheetInterface.getPowerVariable(),
													0.1);

	}

}
