package clearcontrol.devices.stages.gui.jfx;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.stages.StageDeviceInterface;
import clearcontrol.devices.stages.gui.jfx.XYZRStageDevicePanel.Attribute;
import clearcontrol.devices.stages.gui.jfx.XYZRStageDevicePanel.Stage;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.custom.iconswitch.IconSwitch;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator;

public class StageDOFPanel extends CustomGridPane
{

  private StageDeviceInterface mStageDeviceInterface;
  private int mDOFIndex;

  public StageDOFPanel(StageDeviceInterface pStageDeviceInterface,
                       int pDOFIndex)
  {
    mStageDeviceInterface = pStageDeviceInterface;
    mDOFIndex = pDOFIndex;

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
