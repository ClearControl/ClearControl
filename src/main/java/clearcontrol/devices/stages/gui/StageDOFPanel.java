package clearcontrol.devices.stages.gui;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
 * Stage DOF panel
 *
 * @author royer
 */
public class StageDOFPanel extends HBox
{

  private StageDeviceInterface mStageDevice;
  private int mDOFIndex;

  /**
   * Instantiates a Stage DOF panel given the stage device and DOF index
   * 
   * @param pStageDevice
   *          stage device
   * @param pDOFIndex
   *          DOF index
   * @param pLabelString
   *          label string (if null then the DOF name will be used)
   */
  public StageDOFPanel(StageDeviceInterface pStageDevice,
                       int pDOFIndex,
                       String pLabelString)
  {
    super(5);
    mStageDevice = pStageDevice;
    mDOFIndex = pDOFIndex;
    createStageControl(pLabelString);
    setMaxWidth(Double.MAX_VALUE);
  }

  private void createStageControl(String pLabelString)
  {
    String lDOFName = mStageDevice.getDOFNameByIndex(mDOFIndex);

    final Label lStageLabel =
                            new Label(pLabelString == null ? lDOFName
                                                           : pLabelString);

    final VariableSlider<Double> lTargetSlider = createTargetSlider();
    final VariableSlider<Double> lCurrentSlider =
                                                createCurrentSlider();

    final VBox lSliderBox = new VBox(lTargetSlider, lCurrentSlider);

    lSliderBox.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(lSliderBox, Priority.ALWAYS);

    getChildren().addAll(new VBox(lStageLabel,
                                  createFrontControls(lTargetSlider)), /**/
                         lSliderBox);

  }

  private VariableSlider<Double> createCurrentSlider()
  {

    VariableSlider<Double> variableCurSlider =
                                             new VariableSlider<>("",
                                                                  mStageDevice.getCurrentPositionVariable(mDOFIndex),
                                                                  mStageDevice.getMinPositionVariable(mDOFIndex),
                                                                  mStageDevice.getMaxPositionVariable(mDOFIndex),
                                                                  mStageDevice.getGranularityPositionVariable(mDOFIndex),
                                                                  10d);
    variableCurSlider.getSlider().setDisable(true);
    variableCurSlider.getSlider().setStyle("-fx-opacity: 1;");
    variableCurSlider.getTextField().setDisable(true);
    variableCurSlider.getTextField().setStyle("-fx-opacity: 1;");

    variableCurSlider.setPadding(new Insets(5, 10, 10, 10));
    return variableCurSlider;
  }

  private VariableSlider<Double> createTargetSlider()
  {
    VariableSlider<Double> variableSlider =
                                          new VariableSlider<>("",
                                                               mStageDevice.getTargetPositionVariable(mDOFIndex),
                                                               mStageDevice.getMinPositionVariable(mDOFIndex),
                                                               mStageDevice.getMaxPositionVariable(mDOFIndex),
                                                               mStageDevice.getGranularityPositionVariable(mDOFIndex),
                                                               null);
    variableSlider.getSlider().setShowTickLabels(false);
    variableSlider.setPadding(new Insets(10, 10, 5, 10));
    return variableSlider;
  }

  private GridPane createFrontControls(VariableSlider<Double> pSlider)
  {
    final IconSwitch lEnableSwitch = new IconSwitch();
    lEnableSwitch.setSymbolType(SymbolType.POWER);
    lEnableSwitch.setSymbolColor(Color.web("#ffffff"));
    lEnableSwitch.setSwitchColor(Color.web("#34495e"));
    lEnableSwitch.setThumbColor(Color.web("#ff495e"));

    lEnableSwitch.setMaxSize(60, 30);

    // Data -> GUI
    getStageVariable(StageVariableEnum.Enable).addSetListener((pCurrentValue,
                                                               pNewValue) -> {
      Platform.runLater(() -> {
        lEnableSwitch.setSelected(pNewValue);
        pSlider.getSlider().setDisable(!pNewValue);
        pSlider.getTextField().setDisable(!pNewValue);
      });
    });

    // Enable, GUI -> Data
    lEnableSwitch.setOnMouseReleased(event -> getStageVariable(StageVariableEnum.Enable).setAsync(!getStageVariable(StageVariableEnum.Enable).get()));

    // Initialize the status at startup
    lEnableSwitch.setSelected(getStageVariable(StageVariableEnum.Enable).get());
    pSlider.getSlider()
           .setDisable(!getStageVariable(StageVariableEnum.Enable).get());
    pSlider.getTextField()
           .setDisable(!getStageVariable(StageVariableEnum.Enable).get());

    final SimpleIndicator lIndicator = new SimpleIndicator();
    lIndicator.setMaxSize(50, 50);
    getStageVariable(StageVariableEnum.Ready).addSetListener((pCurrentValue,
                                                              pNewValue) -> {
      Platform.runLater(() -> {
        if (pNewValue)
          lIndicator.setIndicatorStyle(SimpleIndicator.IndicatorStyle.GREEN);
        else
          lIndicator.setIndicatorStyle(SimpleIndicator.IndicatorStyle.GRAY);
      });
    });

    if (getStageVariable(StageVariableEnum.Ready).get())
      lIndicator.setIndicatorStyle(SimpleIndicator.IndicatorStyle.GREEN);
    else
      lIndicator.setIndicatorStyle(SimpleIndicator.IndicatorStyle.GRAY);

    final Button lHomingButton = new Button("Homing");
    lHomingButton.setAlignment(Pos.BASELINE_LEFT);
    lHomingButton.setPrefWidth(70);
    lHomingButton.setOnAction(event -> getStageVariable(StageVariableEnum.Homing).setEdgeAsync(false,
                                                                                               true));

    final Button lStopButton = new Button("Stop");
    lStopButton.setAlignment(Pos.BASELINE_LEFT);
    lStopButton.setPrefWidth(70);
    lStopButton.setOnAction(event -> getStageVariable(StageVariableEnum.Stop).setEdgeAsync(false,
                                                                                           true));

    final Button lResetButton = new Button("Reset");
    lResetButton.setAlignment(Pos.BASELINE_LEFT);
    lResetButton.setPrefWidth(70);
    lResetButton.setOnAction(event -> getStageVariable(StageVariableEnum.Reset).setEdgeAsync(false,
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

  private Variable<Boolean> getStageVariable(StageVariableEnum pStageVariableEnum)
  {
    Variable<Boolean> variable = null;

    switch (pStageVariableEnum)
    {
    case Enable:
      variable = mStageDevice.getEnableVariable(mDOFIndex);
      break;
    case Ready:
      variable = mStageDevice.getReadyVariable(mDOFIndex);
      break;
    case Homing:
      variable = mStageDevice.getHomingVariable(mDOFIndex);
      break;
    case Stop:
      variable = mStageDevice.getStopVariable(mDOFIndex);
      break;
    case Reset:
      variable = mStageDevice.getResetVariable(mDOFIndex);
      break;
    }

    return variable;
  }

}
