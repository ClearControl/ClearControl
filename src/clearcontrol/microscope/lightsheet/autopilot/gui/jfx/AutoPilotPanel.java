package clearcontrol.microscope.lightsheet.autopilot.gui.jfx;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.microscope.lightsheet.autopilot.AutoPilotInterface;

public class AutoPilotPanel extends CustomGridPane
{

	private AutoPilotInterface mAutoPilotInterface;

	public AutoPilotPanel(AutoPilotInterface pAutoPilotInterface)
	{
		super();
		mAutoPilotInterface = pAutoPilotInterface;

	}
}
