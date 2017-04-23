package clearcontrol.microscope.timelapse.gui;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.microscope.timelapse.TimelapseInterface;

/**
 * Timelapse panel
 *
 * @author royer
 */
public class TimelapsePanel extends CustomGridPane
{

  @SuppressWarnings("unused")
  private TimelapseInterface mTimelapseInterface;

  /**
   * Instanciates a timelapse panel.
   * 
   * @param pTimelapseInterface
   *          timelapse device
   */
  public TimelapsePanel(TimelapseInterface pTimelapseInterface)
  {
    super();
    mTimelapseInterface = pTimelapseInterface;

  }
}
