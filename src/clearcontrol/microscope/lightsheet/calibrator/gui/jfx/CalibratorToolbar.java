package clearcontrol.microscope.lightsheet.calibrator.gui.jfx;

import org.dockfx.DockNode;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.checkbox.VariableCheckBox;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class CalibratorToolbar extends DockNode
{
  private GridPane mGridPane;

  public CalibratorToolbar(Calibrator pCalibrator)
  {
    super(new CustomGridPane());
    mGridPane = (GridPane) getContents();

    // this.setStyle("-fx-background-color: yellow;");
    // mGridPane.setStyle("-fx-border-color: blue;");

    setTitle("Calibrator");

    for (int i = 0; i < 3; i++)
    {
      ColumnConstraints lColumnConstraints = new ColumnConstraints();
      lColumnConstraints.setPercentWidth(33);
      mGridPane.getColumnConstraints().add(lColumnConstraints);
    }

    Button lStartCalibration = new Button("Calibrate");
    lStartCalibration.setAlignment(Pos.CENTER);
    lStartCalibration.setMaxWidth(Double.MAX_VALUE);
    lStartCalibration.setOnAction((e) -> {
      pCalibrator.startTask();
    });
    GridPane.setColumnSpan(lStartCalibration, 2);
    GridPane.setHgrow(lStartCalibration, Priority.ALWAYS);
    mGridPane.add(lStartCalibration, 0, 0);

    Button lStopCalibration = new Button("Stop");
    lStopCalibration.setAlignment(Pos.CENTER);
    lStopCalibration.setMaxWidth(Double.MAX_VALUE);
    lStopCalibration.setOnAction((e) -> {
      pCalibrator.stopTask();
    });
    GridPane.setColumnSpan(lStopCalibration, 2);
    GridPane.setHgrow(lStopCalibration, Priority.ALWAYS);
    mGridPane.add(lStopCalibration, 0, 1);

    ProgressIndicator lCalibrationProgressIndicator =
                                                    new ProgressIndicator(0.0);
    lCalibrationProgressIndicator.setMaxWidth(Double.MAX_VALUE);
    lCalibrationProgressIndicator.setStyle(".percentage { visibility: hidden; }");
    GridPane.setRowSpan(lCalibrationProgressIndicator, 2);
    mGridPane.add(lCalibrationProgressIndicator, 2, 0);

    pCalibrator.getProgressVariable().addEdgeListener((n) -> {
      Platform.runLater(() -> {
        lCalibrationProgressIndicator.setProgress(pCalibrator.getProgressVariable()
                                                             .get());
      });
    });

    addCheckBoxForCalibrationModule("Z ",
                                    pCalibrator.getCalibrateZVariable(),
                                    0,
                                    2);
    addCheckBoxForCalibrationModule("XY",
                                    pCalibrator.getCalibrateXYVariable(),
                                    0,
                                    3);
    addCheckBoxForCalibrationModule("A ",
                                    pCalibrator.getCalibrateAVariable(),
                                    1,
                                    2);
    addCheckBoxForCalibrationModule("P ",
                                    pCalibrator.getCalibratePVariable(),
                                    1,
                                    3);

    TextField lCalibrationDataNameTextField =
                                            new TextField(pCalibrator.getCalibrationDataNameVariable()
                                                                     .get());
    lCalibrationDataNameTextField.setMaxWidth(Double.MAX_VALUE);
    lCalibrationDataNameTextField.textProperty()
                                 .addListener((obs, o, n) -> {
                                   String lName = n.trim();
                                   if (!lName.isEmpty())
                                     pCalibrator.getCalibrationDataNameVariable()
                                                .set(lName);

                                 });
    GridPane.setColumnSpan(lCalibrationDataNameTextField, 3);
    GridPane.setFillWidth(lCalibrationDataNameTextField, true);
    GridPane.setHgrow(lCalibrationDataNameTextField, Priority.ALWAYS);
    mGridPane.add(lCalibrationDataNameTextField, 0, 4);

    Button lSaveCalibration = new Button("Save");
    lSaveCalibration.setAlignment(Pos.CENTER);
    lSaveCalibration.setMaxWidth(Double.MAX_VALUE);
    lSaveCalibration.setOnAction((e) -> {
      try
      {
        pCalibrator.save();
      }
      catch (Exception e1)
      {
        e1.printStackTrace();
      }
    });
    GridPane.setColumnSpan(lSaveCalibration, 1);
    mGridPane.add(lSaveCalibration, 0, 5);

    Button lLoadCalibration = new Button("Load");
    lLoadCalibration.setAlignment(Pos.CENTER);
    lLoadCalibration.setMaxWidth(Double.MAX_VALUE);
    lLoadCalibration.setOnAction((e) -> {
      try
      {
        pCalibrator.load();
      }
      catch (Exception e1)
      {
        e1.printStackTrace();
      }
    });
    GridPane.setColumnSpan(lLoadCalibration, 1);
    mGridPane.add(lLoadCalibration, 1, 5);

    Button lResetCalibration = new Button("Reset");
    lResetCalibration.setAlignment(Pos.CENTER);
    lResetCalibration.setMaxWidth(Double.MAX_VALUE);
    lResetCalibration.setOnAction((e) -> {
      pCalibrator.reset();
    });
    GridPane.setColumnSpan(lResetCalibration, 1);
    mGridPane.add(lResetCalibration, 2, 5);

  }

  private void addCheckBoxForCalibrationModule(String pName,
                                               Variable<Boolean> lCalibrateVariable,
                                               int pColumn,
                                               int pRow)
  {
    CustomGridPane lGroupGridPane = new CustomGridPane(0, 3);

    VariableCheckBox lCheckBox =
                               new VariableCheckBox(pName,
                                                    lCalibrateVariable);

    lCheckBox.getLabel().setAlignment(Pos.CENTER_LEFT);
    lCheckBox.getLabel().setMaxWidth(Double.MAX_VALUE);

    lCheckBox.getCheckBox().setAlignment(Pos.CENTER_RIGHT);
    lCheckBox.getCheckBox().setMaxWidth(Double.MAX_VALUE);

    lGroupGridPane.add(lCheckBox.getLabel(), 0, 0);
    lGroupGridPane.add(lCheckBox.getCheckBox(), 1, 0);

    lGroupGridPane.setMaxWidth(Double.MAX_VALUE);

    mGridPane.add(lGroupGridPane, pColumn, pRow);

    lCalibrateVariable.setCurrent();
  }
}
