package clearcontrol.microscope.timelapse.gui.jfx;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.microscope.timelapse.TimelapseInterface;

public class TimelapsePanel extends CustomGridPane
{

  private TimelapseInterface mTimelapseInterface;

  public TimelapsePanel(TimelapseInterface pTimelapseInterface)
  {
    super();
    mTimelapseInterface = pTimelapseInterface;

  }
}
