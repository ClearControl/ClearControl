package clearcontrol.devices.stages.gui.jfx;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.custom.iconswitch.IconSwitch;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator;

/**
 * Stage 3D Control
 */
public class XYZRStageDevicePanel extends BorderPane
{

  private StageDeviceInterface mStageDeviceInterface;

  private Pane background1Pane = null;

  enum Stage
  {
   R, X, Y, Z
  }

  enum Attribute
  {
   Enable, Ready, Homing, Stop, Reset
  }

  public XYZRStageDevicePanel(StageDeviceInterface pStageDeviceInterface)
  {
    mStageDeviceInterface = pStageDeviceInterface;
    init();
  }

  public void init()
  {
    setTop(createControls());
  }

  private VBox createControls()
  {

    VBox controls =
                  new VBox(10,
                           // rotate,
                           createStageControl("Stage R (micro-degree)",
                                              Stage.R),
                           createStageControl("Stage X (microns)",
                                              Stage.X),
                           createStageControl("Stage Y (microns)",
                                              Stage.Y),
                           createStageControl("Stage Z (microns)",
                                              Stage.Z));
    controls.setPadding(new Insets(10));
    return controls;
  }

  private HBox createStageControl(String pLabelString, Stage pStage)
  {

    final Label lStageLabel = new Label(pLabelString);

    final VariableSlider<Double> lTargetSlider =
                                               createTargetSlider(pStage);
    final VariableSlider<Double> lCurrentSlider =
                                                createCurrentSlider(pStage);

    double lOffset = lCurrentSlider.getSlider().getMin()
                     * Math.signum(lCurrentSlider.getSlider()
                                                 .getMin());

    final HBox lStageBox = new HBox(5);
    final VBox lSliderBox = new VBox(lTargetSlider, lCurrentSlider);

    lStageBox.getChildren().addAll(
                                   new VBox(lStageLabel,
                                            createFrontControls(pStage,
                                                                lTargetSlider)),
                                   lSliderBox);
    HBox.setHgrow(lStageBox, Priority.ALWAYS);

    return lStageBox;
  }

  private VariableSlider<Double> createCurrentSlider(Stage pStage)
  {
    int lIndex =
               mStageDeviceInterface.getDOFIndexByName(pStage.name());
    VariableSlider<Double> variableCurSlider =
                                             new VariableSlider<>("",
                                                                  mStageDeviceInterface.getCurrentPositionVariable(lIndex),
                                                                  mStageDeviceInterface.getMinPositionVariable(lIndex),
                                                                  mStageDeviceInterface.getMaxPositionVariable(lIndex),
                                                                  mStageDeviceInterface.getGranularityPositionVariable(lIndex),
                                                                  10d);
    variableCurSlider.getSlider().setDisable(true);
    variableCurSlider.getSlider().setStyle("-fx-opacity: 1;");
    variableCurSlider.getTextField().setDisable(true);
    variableCurSlider.getTextField().setStyle("-fx-opacity: 1;");

    variableCurSlider.setPadding(new Insets(5, 25, 25, 25));
    return variableCurSlider;
  }

  private VariableSlider<Double> createTargetSlider(Stage pStage)
  {
    int lIndex =
               mStageDeviceInterface.getDOFIndexByName(pStage.name());
    VariableSlider<Double> variableSlider = new VariableSlider<>("",
                                                                 mStageDeviceInterface.getTargetPositionVariable(lIndex),
                                                                 mStageDeviceInterface.getMinPositionVariable(lIndex),
                                                                 mStageDeviceInterface.getMaxPositionVariable(lIndex),
                                                                 mStageDeviceInterface.getGranularityPositionVariable(lIndex),
                                                                 10d);
    variableSlider.getSlider().setShowTickLabels(false);
    variableSlider.setPadding(new Insets(25, 25, 5, 25));
    return variableSlider;
  }

  private Variable<Boolean> getStageAttribute(Stage pStage,
                                              Attribute pAttribute)
  {
    Variable<Boolean> variable = null;
    int lIndex =
               mStageDeviceInterface.getDOFIndexByName(pStage.name());

    switch (pAttribute)
    {
    case Enable:
      variable = mStageDeviceInterface.getEnableVariable(lIndex);
      break;
    case Ready:
      variable = mStageDeviceInterface.getReadyVariable(lIndex);
      break;
    case Homing:
      variable = mStageDeviceInterface.getHomingVariable(lIndex);
      break;
    case Stop:
      variable = mStageDeviceInterface.getStopVariable(lIndex);
      break;
    case Reset:
      variable = mStageDeviceInterface.getResetVariable(lIndex);
      break;
    }

    return variable;
  }

  private GridPane createFrontControls(Stage pStage,
                                       VariableSlider<Double> pSlider)
  {
    final IconSwitch lEnableSwitch = new IconSwitch();
    lEnableSwitch.setSymbolType(SymbolType.POWER);
    lEnableSwitch.setSymbolColor(Color.web("#ffffff"));
    lEnableSwitch.setSwitchColor(Color.web("#34495e"));
    lEnableSwitch.setThumbColor(Color.web("#ff495e"));

    lEnableSwitch.setMaxSize(60, 30);

    // Data -> GUI
    getStageAttribute(pStage,
                      Attribute.Enable).addSetListener((pCurrentValue,
                                                        pNewValue) -> {
                        Platform.runLater(() -> {
                          lEnableSwitch.setSelected(pNewValue);
                          pSlider.getSlider().setDisable(!pNewValue);
                          pSlider.getTextField()
                                 .setDisable(!pNewValue);
                        });
                      });

    // Enable, GUI -> Data
    lEnableSwitch.setOnMouseReleased(event -> getStageAttribute(pStage,
                                                                Attribute.Enable).setAsync(!getStageAttribute(pStage,
                                                                                                              Attribute.Enable).get()));

    // Initialize the status at startup
    lEnableSwitch.setSelected(getStageAttribute(pStage,
                                                Attribute.Enable).get());
    pSlider.getSlider()
           .setDisable(!getStageAttribute(pStage,
                                          Attribute.Enable).get());
    pSlider.getTextField()
           .setDisable(!getStageAttribute(pStage,
                                          Attribute.Enable).get());

    final SimpleIndicator lIndicator = new SimpleIndicator();
    lIndicator.setMaxSize(50, 50);
    getStageAttribute(pStage,
                      Attribute.Ready).addSetListener((pCurrentValue,
                                                       pNewValue) -> {
                        Platform.runLater(() -> {
                          if (pNewValue)
                            lIndicator.setIndicatorStyle(SimpleIndicator.IndicatorStyle.GREEN);
                          else
                            lIndicator.setIndicatorStyle(SimpleIndicator.IndicatorStyle.GRAY);
                        });
                      });

    if (getStageAttribute(pStage, Attribute.Ready).get())
      lIndicator.setIndicatorStyle(SimpleIndicator.IndicatorStyle.GREEN);
    else
      lIndicator.setIndicatorStyle(SimpleIndicator.IndicatorStyle.GRAY);

    final Button lHomingButton = new Button("Homing");
    lHomingButton.setAlignment(Pos.BASELINE_LEFT);
    lHomingButton.setPrefWidth(70);
    lHomingButton.setOnAction(event -> getStageAttribute(pStage,
                                                         Attribute.Homing).setEdgeAsync(false,
                                                                                        true));

    final Button lStopButton = new Button("Stop");
    lStopButton.setAlignment(Pos.BASELINE_LEFT);
    lStopButton.setPrefWidth(70);
    lStopButton.setOnAction(event -> getStageAttribute(pStage,
                                                       Attribute.Stop).setEdgeAsync(false,
                                                                                    true));

    final Button lResetButton = new Button("Reset");
    lResetButton.setAlignment(Pos.BASELINE_LEFT);
    lResetButton.setPrefWidth(70);
    lResetButton.setOnAction(event -> getStageAttribute(pStage,
                                                        Attribute.Reset).setEdgeAsync(false,
                                                                                      true));

    GridPane lGridPane = new CustomGridPane();
    lGridPane.add(lIndicator, 0, 0);
    GridPane.setRowSpan(lIndicator, 2);
    lGridPane.add(lEnableSwitch, 0, 2);
    GridPane.setHalignment(lEnableSwitch, HPos.CENTER);

    lGridPane.add(lHomingButton, 1, 0);
    lGridPane.add(lStopButton, 1, 1);
    lGridPane.add(lResetButton, 1, 2);

    return lGridPane;
  }

}
