package clearcontrol.microscope.adaptive.gui;

import clearcontrol.gui.jfx.custom.visualconsole.VisualConsolePanel;
import clearcontrol.microscope.adaptive.AdaptiveEngine;

/**
 * Adaptor Panel
 *
 * @author royer
 */
public class AdaptiveEnginePanel extends VisualConsolePanel
{

  /**
   * Instantiates a panel for displaying information about the adaptive engine.
   * 
   * @param pAdaptiveEngine
   *          adaptive engine
   */
  public AdaptiveEnginePanel(AdaptiveEngine<?> pAdaptiveEngine)
  {
    super(pAdaptiveEngine);
  }

}
