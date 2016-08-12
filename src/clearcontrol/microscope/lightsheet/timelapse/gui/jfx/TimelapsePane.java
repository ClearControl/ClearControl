package clearcontrol.microscope.lightsheet.timelapse.gui.jfx;

import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.microscope.lightsheet.timelapse.TimelapseInterface;

public class TimelapsePane extends StandardGridPane
{



	private TimelapseInterface mTimelapseInterface;

	public TimelapsePane(TimelapseInterface pTimelapseInterface)
	{
		super();
		mTimelapseInterface = pTimelapseInterface;


	}
}
