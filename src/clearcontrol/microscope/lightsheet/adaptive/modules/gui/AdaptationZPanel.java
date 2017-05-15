package clearcontrol.microscope.lightsheet.adaptive.modules.gui;

import clearcontrol.microscope.lightsheet.adaptive.modules.AdaptationZ;

/**
 *
 *
 * @author royer
 */
public class AdaptationZPanel extends StandardAdaptationModulePanel
{

  /**
   * Instantiates an adaptation Z panel
   * 
   * @param pAdaptationZ
   *          adaptation Z module
   */
  public AdaptationZPanel(AdaptationZ pAdaptationZ)
  {
    super(pAdaptationZ);

    addNumberTextFieldForVariable("Delta Z: ",
                                  pAdaptationZ.getDeltaZVariable(),
                                  0.0,
                                  1.0,
                                  0.001);
  }

}
