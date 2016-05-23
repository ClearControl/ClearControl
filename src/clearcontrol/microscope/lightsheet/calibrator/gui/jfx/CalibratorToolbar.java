package clearcontrol.microscope.lightsheet.calibrator.gui.jfx;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import org.dockfx.DockNode;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.gui.variable.JFXPropertyVariable;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;

public class CalibratorToolbar extends DockNode
{
	private GridPane mGridPane;

	public CalibratorToolbar(Calibrator pCalibrator)
	{
		super(new StandardGridPane());
		mGridPane = (GridPane) getContents();

		setTitle("Calibrator");

		Button lResetCalibration = new Button("Reset");
		lResetCalibration.setAlignment(Pos.CENTER);
		lResetCalibration.setMaxWidth(Double.MAX_VALUE);
		lResetCalibration.setOnAction((e) -> {
			pCalibrator.reset();
		});
		GridPane.setColumnSpan(lResetCalibration, 2);
		mGridPane.add(lResetCalibration, 0, 0);

		Button lStartCalibration = new Button("Calibrate");
		lStartCalibration.setAlignment(Pos.CENTER);
		lStartCalibration.setMaxWidth(Double.MAX_VALUE);
		lStartCalibration.setOnAction((e) -> {
			pCalibrator.startTask();
		});
		GridPane.setColumnSpan(lStartCalibration, 2);
		mGridPane.add(lStartCalibration, 0, 1);

		Button lStart3D = new Button("Stop");
		lStart3D.setAlignment(Pos.CENTER);
		lStart3D.setMaxWidth(Double.MAX_VALUE);
		lStart3D.setOnAction((e) -> {
			pCalibrator.stopTask();
		});
		GridPane.setColumnSpan(lStart3D, 2);
		mGridPane.add(lStart3D, 0, 2);

		addCheckBoxForCalibrationModule("Calibrate Z",
																		pCalibrator.getCalibrateZVariable(),
																		0,
																		3);
		addCheckBoxForCalibrationModule("Calibrate XY",
																		pCalibrator.getCalibrateXYVariable(),
																		0,
																		4);
		addCheckBoxForCalibrationModule("Calibrate A",
																		pCalibrator.getCalibrateAVariable(),
																		2,
																		3);
		addCheckBoxForCalibrationModule("Calibrate P",
																		pCalibrator.getCalibratePVariable(),
																		2,
																		4);

		TextField lCalibrationDataNameTextField = new TextField();

		GridPane.setColumnSpan(lCalibrationDataNameTextField, 2);
		mGridPane.add(lCalibrationDataNameTextField, 2, 0);

		Button lSaveCalibration = new Button("Save");
		lSaveCalibration.setAlignment(Pos.CENTER);
		lSaveCalibration.setMaxWidth(Double.MAX_VALUE);

		lSaveCalibration.setOnAction((e) -> {
			try
			{
				String lName = lCalibrationDataNameTextField.textProperty()
																										.get()
																										.trim();
				if (lName != null && !lName.isEmpty())
					pCalibrator.save(lName);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		});
		GridPane.setColumnSpan(lSaveCalibration, 2);
		mGridPane.add(lSaveCalibration, 2, 1);

		Button lLoadCalibration = new Button("Load");
		lLoadCalibration.setAlignment(Pos.CENTER);
		lLoadCalibration.setMaxWidth(Double.MAX_VALUE);
		lLoadCalibration.setOnAction((e) -> {
			try
			{
				String lName = lCalibrationDataNameTextField.textProperty()
																										.get()
																										.trim();
				if (lName != null && !lName.isEmpty())
					pCalibrator.load(lName);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		});
		GridPane.setColumnSpan(lLoadCalibration, 2);
		mGridPane.add(lLoadCalibration, 2, 2);

		ProgressIndicator lProgressIndicator = new ProgressIndicator(0.0);
		lProgressIndicator.setStyle(".percentage { visibility: hidden; }");
		mGridPane.add(lProgressIndicator, 4, 0);

		pCalibrator.getProgressVariable()
								.addEdgeListener((n) -> {
									Platform.runLater(() -> {
										lProgressIndicator.setProgress(pCalibrator.getProgressVariable()
																															.get());
									});
								});

	}

	private void addCheckBoxForCalibrationModule(	String pName,
																								Variable<Boolean> lCalibrateVariable,
																								int pColumn,
																								int pRow)
	{
		Label lLabel = new Label(pName);
		mGridPane.add(lLabel, pColumn + 0, pRow);

		CheckBox lCheckBox = new CheckBox();
		lCheckBox.setAlignment(Pos.CENTER);
		lCheckBox.setMaxWidth(Double.MAX_VALUE);
		mGridPane.add(lCheckBox, pColumn + 1, pRow);

		JFXPropertyVariable<Boolean> lCheckBoxPropertyVariable = new JFXPropertyVariable<>(	lCheckBox.selectedProperty(),
																																												lCalibrateVariable.getName() + "Property",
																																												false);

		lCheckBoxPropertyVariable.syncWith(lCalibrateVariable);

		lCalibrateVariable.setCurrent();
	}
}
