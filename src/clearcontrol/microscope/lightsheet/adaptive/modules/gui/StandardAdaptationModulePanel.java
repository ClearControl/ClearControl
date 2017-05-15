package clearcontrol.microscope.lightsheet.adaptive.modules.gui;

import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.microscope.lightsheet.adaptive.modules.StandardAdaptationModule;

/**
 * Standard adaptation module panel
 *
 * @author royer
 */
public class StandardAdaptationModulePanel extends CustomVariablePane
{

  /**
   * Instantiates a standard adaptation module panel
   * 
   * @param pAdaptationModule
   *          standard adaptation module
   */
  public StandardAdaptationModulePanel(StandardAdaptationModule<?> pAdaptationModule)
  {
    super();

    addTab("");

    addNumberTextFieldForVariable("Number of samples: ",
                                  pAdaptationModule.getNumberOfSamplesVariable(),
                                  0,
                                  257,
                                  1);

    addNumberTextFieldForVariable("image metric threshold: ",
                                  pAdaptationModule.getImageMetricThresholdVariable(),
                                  0.0,
                                  1.0,
                                  0.00001);

    addNumberTextFieldForVariable("probability threshold: ",
                                  pAdaptationModule.getProbabilityThresholdVariable(),
                                  0.0,
                                  1.0,
                                  0.001);
  }

}
