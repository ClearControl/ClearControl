package clearcontrol.microscope.lightsheet.timelapse.gui.jfx;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.microscope.lightsheet.timelapse.TimelapseInterface;

public class TimelapsePanel extends CustomGridPane
{

	private TimelapseInterface mTimelapseInterface;

	public TimelapsePanel(TimelapseInterface pTimelapseInterface)
	{
		super();
		mTimelapseInterface = pTimelapseInterface;

	}
}
