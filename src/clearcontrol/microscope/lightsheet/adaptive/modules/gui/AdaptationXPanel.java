package clearcontrol.microscope.lightsheet.adaptive.modules.gui;

import clearcontrol.microscope.lightsheet.adaptive.modules.AdaptationX;

/**
 *
 *
 * @author royer
 */
public class AdaptationXPanel extends StandardAdaptationModulePanel
{

  /**
   * Instantiates an adaptation X panel
   * 
   * @param AdaptationX
   *          adaptation X module
   */
  public AdaptationXPanel(AdaptationX AdaptationX)
  {
    super(AdaptationX);

    addNumberTextFieldForVariable("Delta X: ",
                                  AdaptationX.getDeltaXVariable(),
                                  0.0,
                                  Double.POSITIVE_INFINITY,
                                  0.001);

  }

}
