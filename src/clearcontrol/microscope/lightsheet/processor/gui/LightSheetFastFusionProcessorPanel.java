package clearcontrol.microscope.lightsheet.processor.gui;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsolePanel;
import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.microscope.lightsheet.processor.LightSheetFastFusionProcessor;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Lightsheet fast fusion processor panel
 *
 * @author royer
 */
public class LightSheetFastFusionProcessorPanel extends TabPane
{

  private final VisualConsolePanel mVisualConsolePanel;

  /**
   * Instantiates a lightsheet fast fusion processor panel
   * 
   * @param pLightSheetFastFusionProcessor
   *          lightsheet fast fusion processor
   */
  public LightSheetFastFusionProcessorPanel(LightSheetFastFusionProcessor pLightSheetFastFusionProcessor)
  {
    super();

    Tab lParametersTab = new Tab("Parameters");

    lParametersTab.setContent(getParametersPanel(pLightSheetFastFusionProcessor));

    mVisualConsolePanel =
                        new VisualConsolePanel((VisualConsoleInterface) pLightSheetFastFusionProcessor);

    Tab lVisualLogTab = new Tab("Visual Log");
    lVisualLogTab.setContent(mVisualConsolePanel);

    getTabs().addAll(lParametersTab, lVisualLogTab);
  }

  private Node getParametersPanel(LightSheetFastFusionProcessor pLightSheetFastFusionProcessor)
  {
    Variable<Integer> lNumberOfRestartsVariable =
                                                pLightSheetFastFusionProcessor.getNumberOfRestartsVariable();

    Variable<Integer> lMaxNumberOfEvaluationsVariable =
                                                      pLightSheetFastFusionProcessor.getMaxNumberOfEvaluationsVariable();
    
    Variable<Double> lTranslationSearchRadiusVariable =
                                                pLightSheetFastFusionProcessor.getTranslationSearchRadiusVariable();
    
    Variable<Double> lRotationSearchRadiusVariable =
                                             pLightSheetFastFusionProcessor.getRotationSearchRadiusVariable();
    
    CustomVariablePane lCustomVariablePane = new CustomVariablePane();

    lCustomVariablePane.addTab("");

    lCustomVariablePane.addNumberTextFieldForVariable("Number of restarts",
                                                      lNumberOfRestartsVariable);

    lCustomVariablePane.addNumberTextFieldForVariable("Maximum number of evaluations",
                                                      lMaxNumberOfEvaluationsVariable);

    lCustomVariablePane.addNumberTextFieldForVariable("Translation radius",
                                                      lTranslationSearchRadiusVariable);

    lCustomVariablePane.addNumberTextFieldForVariable("Rotation radius",
                                                      lRotationSearchRadiusVariable);

    return lCustomVariablePane;
  }


}
