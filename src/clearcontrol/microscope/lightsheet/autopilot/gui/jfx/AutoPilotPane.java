package clearcontrol.microscope.lightsheet.autopilot.gui.jfx;

import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.microscope.lightsheet.autopilot.AutoPilotInterface;

public class AutoPilotPane extends StandardGridPane
{

	private AutoPilotInterface mAutoPilotInterface;

	public AutoPilotPane(AutoPilotInterface pAutoPilotInterface)
	{
		super();
		mAutoPilotInterface = pAutoPilotInterface;

	}
}
