package clearcontrol.microscope.lightsheet.state.gui;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;
import clearcontrol.gui.jfx.var.rangeslider.VariableRangeSlider;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Acquisition state panel
 *
 * @author royer
 */
public class AcquisitionStatePanel extends CustomGridPane
{

  /**
   * Acquisition state
   * 
   * @param pAcquisitionState
   *          acquisition state
   */
  public AcquisitionStatePanel(InterpolatedAcquisitionState pAcquisitionState)
  {
    super();

    BoundedVariable<Number> lStageXVariable =
                                            pAcquisitionState.getStageXVariable();
    BoundedVariable<Number> lStageYVariable =
                                            pAcquisitionState.getStageYVariable();
    BoundedVariable<Number> lStageZVariable =
                                            pAcquisitionState.getStageZVariable();

    VariableSlider<Number> lStageXSlider =
                                         new VariableSlider<Number>("stageX",
                                                                    lStageXVariable,
                                                                    5);

    VariableSlider<Number> lStageYSlider =
                                         new VariableSlider<Number>("stageY",
                                                                    lStageYVariable,
                                                                    5);

    VariableSlider<Number> lStageZSlider =
                                         new VariableSlider<Number>("stageZ",
                                                                    lStageZVariable,
                                                                    5);

    // Collecting variables:

    BoundedVariable<Number> lZLow =
                                  pAcquisitionState.getStackZLowVariable();
    BoundedVariable<Number> lZHigh =
                                   pAcquisitionState.getStackZHighVariable();

    Variable<Number> lZStep =
                            pAcquisitionState.getStackZStepVariable();

    Variable<Number> lNumberOfPlanes =
                                     pAcquisitionState.getStackDepthInPlanesVariable();

    // Creating elements:

    VariableRangeSlider<Number> lZRangeSlider =
                                              new VariableRangeSlider<>("Z-range",
                                                                        lZLow,
                                                                        lZHigh,
                                                                        lZLow.getMinVariable(),
                                                                        lZHigh.getMaxVariable(),
                                                                        0.01d,
                                                                        null);

    NumberVariableTextField<Number> lZStepTextField =
                                                    new NumberVariableTextField<Number>("Z-step:",
                                                                                        lZStep,
                                                                                        0d,
                                                                                        Double.POSITIVE_INFINITY,
                                                                                        0d);
    lZStepTextField.getTextField().setPrefWidth(100);

    NumberVariableTextField<Number> lNumberOfPlanesTextField =
                                                             new NumberVariableTextField<Number>("Number of planes:",
                                                                                                 lNumberOfPlanes,
                                                                                                 0,
                                                                                                 Double.POSITIVE_INFINITY,
                                                                                                 0);

    lNumberOfPlanesTextField.getTextField().setPrefWidth(100);

    OnOffArrayPane lCameraOnOffArray = new OnOffArrayPane();

    for (int i =
               0; i < pAcquisitionState.getNumberOfDetectionArms(); i++)
    {
      lCameraOnOffArray.addSwitch("Camera " + i,
                                  pAcquisitionState.getCameraOnOffVariable(i));
    }

    OnOffArrayPane lLightSheetOnOffArray = new OnOffArrayPane();

    for (int i =
               0; i < pAcquisitionState.getNumberOfLightSheets(); i++)
    {
      lLightSheetOnOffArray.addSwitch("Lightsheet " + i,
                                      pAcquisitionState.getLightSheetOnOffVariable(i));
    }

    OnOffArrayPane lLaserOnOffArray = new OnOffArrayPane();

    for (int i =
               0; i < pAcquisitionState.getNumberOfLaserLines(); i++)
    {
      lLaserOnOffArray.addSwitch("Laser " + i,
                                 pAcquisitionState.getLaserOnOffVariable(i));
    }

    AcquistionStateMultiChart lMultiChart =
                                          new AcquistionStateMultiChart(pAcquisitionState);

    AcquistionStateTableView lTableView =
                                        new AcquistionStateTableView(pAcquisitionState);

    // Laying out components:

    add(lStageXSlider.getLabel(), 0, 0);
    add(lStageXSlider.getTextField(), 1, 0);
    add(lStageXSlider.getSlider(), 2, 0);

    add(lStageYSlider.getLabel(), 0, 1);
    add(lStageYSlider.getTextField(), 1, 1);
    add(lStageYSlider.getSlider(), 2, 1);

    add(lStageZSlider.getLabel(), 0, 2);
    add(lStageZSlider.getTextField(), 1, 2);
    add(lStageZSlider.getSlider(), 2, 2);

    add(lZRangeSlider.getLabel(), 0, 3);
    add(lZRangeSlider.getLowTextField(), 1, 3);
    add(lZRangeSlider.getRangeSlider(), 2, 3);
    add(lZRangeSlider.getHighTextField(), 3, 3);

    // setGridLinesVisible(true);
    GridPane lGridPane = new GridPane();
    lGridPane.setHgap(10);
    lGridPane.add(lZStepTextField.getLabel(), 0, 0);
    lGridPane.add(lZStepTextField.getTextField(), 1, 0);
    lGridPane.add(lNumberOfPlanesTextField.getLabel(), 2, 0);
    lGridPane.add(lNumberOfPlanesTextField.getTextField(), 3, 0);

    lGridPane.setAlignment(Pos.BASELINE_LEFT);
    GridPane.setColumnSpan(lGridPane, 8);
    add(lGridPane, 0, 4);

    lCameraOnOffArray.setAlignment(Pos.BASELINE_LEFT);
    GridPane.setHalignment(lCameraOnOffArray, HPos.LEFT);
    GridPane.setColumnSpan(lCameraOnOffArray, 7);
    add(new Label("Camera on/off: "), 0, 5);
    add(lCameraOnOffArray, 1, 5);

    lLightSheetOnOffArray.setAlignment(Pos.BASELINE_LEFT);
    GridPane.setHalignment(lLightSheetOnOffArray, HPos.LEFT);
    GridPane.setColumnSpan(lLightSheetOnOffArray, 7);
    add(new Label("LightSheet on/off: "), 0, 6);
    add(lLightSheetOnOffArray, 1, 6);

    lLaserOnOffArray.setAlignment(Pos.BASELINE_LEFT);
    GridPane.setHalignment(lLaserOnOffArray, HPos.LEFT);
    GridPane.setColumnSpan(lLaserOnOffArray, 7);
    add(new Label("Laser on/off: "), 0, 7);
    add(lLaserOnOffArray, 1, 7);

    TabPane lTabPane = new TabPane();
    Tab lChartTab = new Tab("Chart");
    Tab lTableTab = new Tab("Table");
    lTabPane.getTabs().addAll(lChartTab, lTableTab);

    lChartTab.setContent(lMultiChart);
    lTableTab.setContent(lTableView);

    GridPane.setVgrow(lTabPane, Priority.ALWAYS);
    GridPane.setHgrow(lTabPane, Priority.ALWAYS);
    GridPane.setColumnSpan(lTabPane, 8);
    add(lTabPane, 0, 8);

    // Update events:

    pAcquisitionState.addChangeListener((e) -> {
      if (isVisible())
      {
        Platform.runLater(() -> {
          lMultiChart.updateChart(pAcquisitionState);
        });
        Platform.runLater(() -> {
          lTableView.updateTable(pAcquisitionState);
        });
      }

    });
  }

}
