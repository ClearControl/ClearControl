package clearcontrol.microscope.lightsheet.state.gui;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.combo.EnumComboBoxVariable;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;
import clearcontrol.gui.jfx.var.rangeslider.VariableRangeSlider;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;
import clearcontrol.microscope.lightsheet.state.ControlPlaneLayout;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;

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

    BoundedVariable<Number> lExposureInSecondsVariable =
                                                       pAcquisitionState.getExposureInSecondsVariable();

    BoundedVariable<Number> lImageWidthVariable =
                                                pAcquisitionState.getImageWidthVariable();
    BoundedVariable<Number> lImageHeightVariable =
                                                 pAcquisitionState.getImageHeightVariable();

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

    // Collecting state variables:

    BoundedVariable<Number> lZLow =
                                  pAcquisitionState.getStackZLowVariable();
    BoundedVariable<Number> lZHigh =
                                   pAcquisitionState.getStackZHighVariable();

    Variable<Number> lZStep =
                            pAcquisitionState.getStackZStepVariable();

    Variable<Number> lNumberOfPlanes =
                                     pAcquisitionState.getNumberOfZPlanesVariable();

    // Isotropic ratio:

    Variable<Integer> lAnisotropyFactor =
                                        new Variable<Integer>("AnisotropyFactor",
                                                              4);

    // Control planes Z range:

    BoundedVariable<Double> lControlPlanesZLow =
                                               new BoundedVariable<Double>("ControlPlanesZLow",
                                                                           lZLow.get()
                                                                                .doubleValue(),
                                                                           lZLow.get()
                                                                                .doubleValue(),
                                                                           lZHigh.get()
                                                                                 .doubleValue());

    BoundedVariable<Double> lControlPlanesZHigh =
                                                new BoundedVariable<Double>("ControlPlanesZHigh",
                                                                            lZHigh.get()
                                                                                  .doubleValue(),
                                                                            lZLow.get()
                                                                                 .doubleValue(),
                                                                            lZHigh.get()
                                                                                  .doubleValue());

    // Control planes layout mode:

    Variable<ControlPlaneLayout> lControlPlaneLayoutModeVariable =
                                                                 new Variable<>("ControlPlaneLayoutMode",
                                                                                ControlPlaneLayout.Circular);

    // Creating elements:

    Button lCopyCurrentSettingsButton =
                                      new Button("Copy current microscope settings");
    lCopyCurrentSettingsButton.setOnAction((e) -> pAcquisitionState.copyCurrentMicroscopeSettings());

    NumberVariableTextField<Number> lExposureField =
                                                   new NumberVariableTextField<Number>("Exp(s):",
                                                                                       lExposureInSecondsVariable,
                                                                                       0.0,
                                                                                       Double.POSITIVE_INFINITY,
                                                                                       0.0);

    NumberVariableTextField<Number> lImageWidthField =
                                                     new NumberVariableTextField<Number>("Image width:",
                                                                                         lImageWidthVariable,
                                                                                         0.0,
                                                                                         Double.POSITIVE_INFINITY,
                                                                                         0.0);

    NumberVariableTextField<Number> lImageHeightField =
                                                      new NumberVariableTextField<Number>("Image Height:",
                                                                                          lImageHeightVariable,
                                                                                          0.0,
                                                                                          Double.POSITIVE_INFINITY,
                                                                                          0.0);

    VariableRangeSlider<Number> lZRangeSlider =
                                              new VariableRangeSlider<>("Z-range",
                                                                        lZLow,
                                                                        lZHigh,
                                                                        lZLow.getMinVariable(),
                                                                        lZHigh.getMaxVariable(),
                                                                        0.01d,
                                                                        null);

    VariableRangeSlider<Double> lControlPlaneZRangeSlider =
                                                          new VariableRangeSlider<Double>("CP-range",
                                                                                          lControlPlanesZLow,
                                                                                          lControlPlanesZHigh,
                                                                                          lZLow.getMinVariable()
                                                                                               .get()
                                                                                               .doubleValue(),
                                                                                          lZHigh.getMaxVariable()
                                                                                                .get()
                                                                                                .doubleValue(),
                                                                                          0.01d,
                                                                                          null);

    lControlPlaneZRangeSlider.getRangeSlider()
                             .setStyle("-fx-background-color: lightgray");

    NumberVariableTextField<Number> lZStepTextField =
                                                    new NumberVariableTextField<Number>("Z-step:",
                                                                                        lZStep,
                                                                                        0d,
                                                                                        Double.POSITIVE_INFINITY,
                                                                                        0d);
    lZStepTextField.getTextField().setPrefWidth(90);

    NumberVariableTextField<Number> lNumberOfPlanesTextField =
                                                             new NumberVariableTextField<Number>("Number of planes:",
                                                                                                 lNumberOfPlanes,
                                                                                                 0,
                                                                                                 Double.POSITIVE_INFINITY,
                                                                                                 0);
    lNumberOfPlanesTextField.getTextField().setPrefWidth(50);

    NumberVariableTextField<Integer> lAnisotropyFactorTextField =
                                                                new NumberVariableTextField<Integer>("Anisotropy factor:",
                                                                                                     lAnisotropyFactor,
                                                                                                     1,
                                                                                                     32,
                                                                                                     1);
    lAnisotropyFactorTextField.getTextField().setPrefWidth(50);

    Button lSetAnisotropyFactorButton = new Button("Set");
    lSetAnisotropyFactorButton.setOnAction((e) -> pAcquisitionState.setNumberOfPlanesForGivenAnisotropy(lAnisotropyFactor.get()
                                                                                                                         .doubleValue()));

    EnumComboBoxVariable<ControlPlaneLayout> lControlPlaneLayoutModeComboBox =
                                                                             new EnumComboBoxVariable<ControlPlaneLayout>(lControlPlaneLayoutModeVariable,
                                                                                                                          ControlPlaneLayout.values());

    Button lSetupControlPlanesButton =
                                     new Button("Distribute control planes");
    lSetupControlPlanesButton.setOnAction((e) -> pAcquisitionState.setupControlPlanes(pAcquisitionState.getNumberOfControlPlanes(),
                                                                                      lControlPlanesZLow.get()
                                                                                                        .doubleValue(),
                                                                                      lControlPlanesZHigh.get()
                                                                                                         .doubleValue(),
                                                                                      lControlPlaneLayoutModeVariable.get()));

    OnOffArrayPane lCameraOnOffArray = new OnOffArrayPane();
    for (int i =
               0; i < pAcquisitionState.getNumberOfDetectionArms(); i++)
    {
      lCameraOnOffArray.addSwitch("C" + i,
                                  pAcquisitionState.getCameraOnOffVariable(i));
    }

    OnOffArrayPane lLightSheetOnOffArray = new OnOffArrayPane();
    for (int i =
               0; i < pAcquisitionState.getNumberOfLightSheets(); i++)
    {
      lLightSheetOnOffArray.addSwitch("LS" + i,
                                      pAcquisitionState.getLightSheetOnOffVariable(i));
    }

    OnOffArrayPane lLaserOnOffArray = new OnOffArrayPane();

    for (int i =
               0; i < pAcquisitionState.getNumberOfLaserLines(); i++)
    {
      lLaserOnOffArray.addSwitch("La" + i,
                                 pAcquisitionState.getLaserOnOffVariable(i));
    }

    AcquistionStateMultiChart lMultiChart =
                                          new AcquistionStateMultiChart(pAcquisitionState);

    AcquistionStateTableView lTableView =
                                        new AcquistionStateTableView(pAcquisitionState);

    // Laying out components:

    int lRow = 0;

    {
      lCopyCurrentSettingsButton.setMaxWidth(Double.MAX_VALUE);
      GridPane.setHgrow(lCopyCurrentSettingsButton, Priority.ALWAYS);
      GridPane.setColumnSpan(lCopyCurrentSettingsButton, 8);
      add(lCopyCurrentSettingsButton, 0, lRow);
      lRow++;
    }

    {
      HBox lHBox = new HBox(new Label("    "),
                            lImageWidthField.getLabel(),
                            lImageWidthField.getTextField(),
                            new Label("    "),
                            lImageHeightField.getLabel(),
                            lImageHeightField.getTextField());
      lHBox.setAlignment(Pos.CENTER_LEFT);
      GridPane.setColumnSpan(lHBox, 6);
      add(lExposureField.getLabel(), 0, lRow);
      add(lExposureField.getTextField(), 1, lRow);
      add(lHBox, 2, lRow);
      lRow++;
    }

    lRow =

         insertSeparator(lRow);

    {
      add(lStageXSlider.getLabel(), 0, lRow);
      add(lStageXSlider.getTextField(), 1, lRow);
      add(lStageXSlider.getSlider(), 2, lRow);
      lRow++;
    }

    {
      add(lStageYSlider.getLabel(), 0, lRow);
      add(lStageYSlider.getTextField(), 1, lRow);
      add(lStageYSlider.getSlider(), 2, lRow);
      lRow++;
    }

    {
      add(lStageZSlider.getLabel(), 0, lRow);
      add(lStageZSlider.getTextField(), 1, lRow);
      add(lStageZSlider.getSlider(), 2, lRow);
      lRow++;
    }

    lRow = insertSeparator(lRow);

    {
      add(lZRangeSlider.getLabel(), 0, lRow);
      add(lZRangeSlider.getLowTextField(), 1, lRow);
      add(lZRangeSlider.getRangeSlider(), 2, lRow);
      add(lZRangeSlider.getHighTextField(), 3, lRow);
      lRow++;
    }

    {
      add(lControlPlaneZRangeSlider.getLabel(), 0, lRow);
      add(lControlPlaneZRangeSlider.getLowTextField(), 1, lRow);
      add(lControlPlaneZRangeSlider.getRangeSlider(), 2, lRow);
      add(lControlPlaneZRangeSlider.getHighTextField(), 3, lRow);
      lRow++;
    }

    lRow = insertSeparator(lRow);

    {
      HBox lHBox = new HBox(new Label("    "),
                            lNumberOfPlanesTextField.getLabel(),
                            lNumberOfPlanesTextField.getTextField(),
                            new Label("    "),
                            lAnisotropyFactorTextField.getLabel(),
                            lAnisotropyFactorTextField.getTextField(),
                            lSetAnisotropyFactorButton);
      lHBox.setAlignment(Pos.CENTER_LEFT);
      GridPane.setColumnSpan(lHBox, 6);
      add(lZStepTextField.getLabel(), 0, lRow);
      add(lZStepTextField.getTextField(), 1, lRow);
      add(lHBox, 2, lRow);
      lRow++;
    }

    {
      HBox lHBox = new HBox(new Label("Control plane layout mode: "),
                            lControlPlaneLayoutModeComboBox,
                            new Label("      "),
                            lSetupControlPlanesButton);
      lHBox.setAlignment(Pos.CENTER_LEFT);
      GridPane.setColumnSpan(lHBox, 8);
      add(lHBox, 0, lRow);
      lRow++;
    }

    lRow = insertSeparator(lRow);

    {
      HBox lHBox = new HBox(new Label("      Cameras: "),
                            lCameraOnOffArray,
                            new Label("      Lightsheets: "),
                            lLightSheetOnOffArray,
                            new Label("      Lasers: "),
                            lLaserOnOffArray);
      lHBox.setAlignment(Pos.CENTER_LEFT);
      GridPane.setColumnSpan(lHBox, 6);
      add(lHBox, 1, lRow);
      lRow++;
    }

    {
      TabPane lTabPane = new TabPane();
      Tab lChartTab = new Tab("Chart");
      Tab lTableTab = new Tab("Table");
      lChartTab.setClosable(false);
      lTableTab.setClosable(false);
      lTabPane.getTabs().addAll(lChartTab, lTableTab);

      lChartTab.setContent(lMultiChart);
      lTableTab.setContent(lTableView);

      GridPane.setVgrow(lTabPane, Priority.ALWAYS);
      GridPane.setHgrow(lTabPane, Priority.ALWAYS);
      GridPane.setColumnSpan(lTabPane, 8);
      add(lTabPane, 0, lRow);
      lRow++;
    }

    // Update events:

    pAcquisitionState.addChangeListener((e) -> {
      if (isVisible())
      {
        Platform.runLater(() -> {
          try
          {
            lMultiChart.updateChart(pAcquisitionState);
          }
          catch (Throwable e1)
          {
            e1.printStackTrace();
          }
        });
        Platform.runLater(() -> {
          try
          {
            lTableView.updateTable(pAcquisitionState);
          }
          catch (Throwable e1)
          {
            e1.printStackTrace();
          }
        });
      }

    });
  }

  protected int insertSeparator(int lRow)
  {
    {
      Separator lSeparator = new Separator();
      lSeparator.setOrientation(Orientation.HORIZONTAL);
      GridPane.setColumnSpan(lSeparator, 8);
      add(lSeparator, 0, lRow);
      lRow++;
    }
    return lRow;
  }

}
