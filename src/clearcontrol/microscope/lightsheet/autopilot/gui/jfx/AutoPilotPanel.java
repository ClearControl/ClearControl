package clearcontrol.microscope.lightsheet.autopilot.gui.jfx;

import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.microscope.lightsheet.autopilot.AutoPilotInterface;

public class AutoPilotPanel extends StandardGridPane
{

	private AutoPilotInterface mAutoPilotInterface;

	public AutoPilotPanel(AutoPilotInterface pAutoPilotInterface)
	{
		super();
		mAutoPilotInterface = pAutoPilotInterface;

	}
}
