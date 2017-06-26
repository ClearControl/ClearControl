package clearcontrol.microscope.lightsheet.processor.gui;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsolePanel;
import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.microscope.lightsheet.processor.LightSheetFastFusionProcessor;

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

    Variable<Double> lSmoothingConstantVariable =
                                                pLightSheetFastFusionProcessor.getSmoothingConstantVariable();

    Variable<Boolean> lTransformLockSwitchVariable =
                                                   pLightSheetFastFusionProcessor.getTransformLockSwitchVariable();

    Variable<Integer> lTransformLockThresholdVariable =
                                                      pLightSheetFastFusionProcessor.getTransformLockThresholdVariable();

    CustomVariablePane lCustomVariablePane = new CustomVariablePane();

    lCustomVariablePane.addTab("");

    lCustomVariablePane.addNumberTextFieldForVariable("Number of restarts",
                                                      lNumberOfRestartsVariable,
                                                      0,
                                                      Integer.MAX_VALUE,
                                                      1);

    lCustomVariablePane.addNumberTextFieldForVariable("Maximum number of evaluations",
                                                      lMaxNumberOfEvaluationsVariable,
                                                      0,
                                                      Integer.MAX_VALUE,
                                                      1);

    lCustomVariablePane.addNumberTextFieldForVariable("Translation search radius",
                                                      lTranslationSearchRadiusVariable);

    lCustomVariablePane.addNumberTextFieldForVariable("Rotation search radius",
                                                      lRotationSearchRadiusVariable);

    lCustomVariablePane.addNumberTextFieldForVariable("Smoothing constant",
                                                      lSmoothingConstantVariable);

    lCustomVariablePane.addCheckBoxForVariable("Transform lock switch",
                                               lTransformLockSwitchVariable);

    lCustomVariablePane.addNumberTextFieldForVariable("Transform lock timepoint threshold",
                                                      lTransformLockThresholdVariable,
                                                      0,
                                                      Integer.MAX_VALUE,
                                                      1);

    return lCustomVariablePane;
  }

}
