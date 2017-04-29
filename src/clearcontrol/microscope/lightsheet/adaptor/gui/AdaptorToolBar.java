package clearcontrol.microscope.lightsheet.adaptor.gui;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.checkbox.VariableCheckBox;
import clearcontrol.microscope.lightsheet.adaptor.Adaptator;
import clearcontrol.microscope.lightsheet.adaptor.modules.AdaptationModuleInterface;
import clearcontrol.microscope.lightsheet.state.LightSheetAcquisitionStateInterface;

/**
 * Adaptor Panel
 *
 * @author royer
 * @param <S>
 *          state type
 */
public class AdaptorToolBar<S extends LightSheetAcquisitionStateInterface<S>>
                           extends CustomGridPane
{

  /**
   * Instanciates a panel gievn and adaptor
   * 
   * @param pAdaptator
   *          adaptor
   */
  public AdaptorToolBar(Adaptator<S> pAdaptator)
  {
    super();

    int lRow = 0;
    // this.setStyle("-fx-background-color: yellow;");
    // mGridPane.setStyle("-fx-border-color: blue;");

    for (int i = 0; i < 3; i++)
    {
      ColumnConstraints lColumnConstraints = new ColumnConstraints();
      lColumnConstraints.setPercentWidth(33);
      getColumnConstraints().add(lColumnConstraints);
    }

    Button lStart = new Button("Start");
    lStart.setAlignment(Pos.CENTER);
    lStart.setMaxWidth(Double.MAX_VALUE);
    lStart.setOnAction((e) -> {
      pAdaptator.startTask();
    });
    GridPane.setColumnSpan(lStart, 2);
    GridPane.setHgrow(lStart, Priority.ALWAYS);
    add(lStart, 0, lRow++);

    Button lStop = new Button("Stop");
    lStop.setAlignment(Pos.CENTER);
    lStop.setMaxWidth(Double.MAX_VALUE);
    lStop.setOnAction((e) -> {
      pAdaptator.stopTask();
    });
    GridPane.setColumnSpan(lStop, 2);
    GridPane.setHgrow(lStop, Priority.ALWAYS);
    add(lStop, 0, lRow++);

    Button lReset = new Button("Reset");
    lReset.setAlignment(Pos.CENTER);
    lReset.setMaxWidth(Double.MAX_VALUE);
    lReset.setOnAction((e) -> {
      pAdaptator.reset();
    });
    GridPane.setColumnSpan(lReset, 2);
    GridPane.setHgrow(lReset, Priority.ALWAYS);
    add(lReset, 0, lRow++);

    ProgressIndicator lCalibrationProgressIndicator =
                                                    new ProgressIndicator(0.0);
    lCalibrationProgressIndicator.setMaxWidth(Double.MAX_VALUE);
    lCalibrationProgressIndicator.setStyle(".percentage { visibility: hidden; }");
    GridPane.setRowSpan(lCalibrationProgressIndicator, 3);
    GridPane.setColumnSpan(lCalibrationProgressIndicator, 2);
    add(lCalibrationProgressIndicator, 2, 0);

    pAdaptator.getProgressVariable().addEdgeListener((n) -> {
      Platform.runLater(() -> {
        lCalibrationProgressIndicator.setProgress(pAdaptator.getProgressVariable()
                                                            .get());
      });
    });

    VariableCheckBox lCheckBox =
                               new VariableCheckBox("run until ready",
                                                    pAdaptator.getRunUntilAllModulesReadyVariable());
    GridPane.setColumnSpan(lCheckBox, 3);
    add(lCheckBox, 0, lRow++);

    ArrayList<AdaptationModuleInterface<S>> lModuleList =
                                                        pAdaptator.getModuleList();

    for (AdaptationModuleInterface<S> lAdaptationModuleInterface : lModuleList)
    {
      addCalibrationModule(lAdaptationModuleInterface.getName(),
                           lAdaptationModuleInterface.getIsActiveVariable(),
                           lAdaptationModuleInterface.getStatusStringVariable(),
                           0,
                           lRow++);
    }

  }

  private void addCalibrationModule(String pName,
                                    Variable<Boolean> lCalibrateVariable,
                                    Variable<String> pStatusStringVariable,
                                    int pColumn,
                                    int pRow)
  {
    CustomGridPane lGroupGridPane = new CustomGridPane(0, 0);
    lGroupGridPane.setAlignment(Pos.CENTER_LEFT);

    VariableCheckBox lCheckBox =
                               new VariableCheckBox(pName,
                                                    lCalibrateVariable);

    lCheckBox.getLabel().setAlignment(Pos.CENTER_LEFT);
    lCheckBox.getLabel().setMaxWidth(Double.MAX_VALUE);

    lCheckBox.getCheckBox().setAlignment(Pos.CENTER_RIGHT);
    lCheckBox.getCheckBox().setMaxWidth(Double.MAX_VALUE);

    Label lStatusLabel = new Label();
    lStatusLabel.setPrefWidth(50);
    pStatusStringVariable.addSetListener((o,
                                          n) -> Platform.runLater(() -> lStatusLabel.setText(" -> "
                                                                                             + n)));

    lGroupGridPane.add(lCheckBox.getCheckBox(), 0, 0);
    lGroupGridPane.add(lCheckBox.getLabel(), 1, 0);
    lGroupGridPane.add(lStatusLabel, 2, 0);

    lGroupGridPane.setMaxWidth(Double.MAX_VALUE);

    add(lGroupGridPane, pColumn, pRow);

    lCalibrateVariable.setCurrent();
  }
}
