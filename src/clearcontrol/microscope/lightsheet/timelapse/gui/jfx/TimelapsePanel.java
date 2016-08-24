package clearcontrol.microscope.lightsheet.timelapse.gui.jfx;

import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.microscope.lightsheet.timelapse.TimelapseInterface;

public class TimelapsePanel extends StandardGridPane
{



	private TimelapseInterface mTimelapseInterface;

	public TimelapsePanel(TimelapseInterface pTimelapseInterface)
	{
		super();
		mTimelapseInterface = pTimelapseInterface;


	}
}
