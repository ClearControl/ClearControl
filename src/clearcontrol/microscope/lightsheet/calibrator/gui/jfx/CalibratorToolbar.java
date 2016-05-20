package clearcontrol.microscope.lightsheet.calibrator.gui.jfx;

import org.dockfx.DockNode;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.gui.variable.JFXPropertyVariable;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class CalibratorToolbar extends DockNode
{
	private GridPane mGridPane;

	public CalibratorToolbar(Calibrator pCalibrator)
	{
		super(new StandardGridPane());
		mGridPane = (GridPane) getContents();

		Button lStartCalibration = new Button("Calibrate");
		lStartCalibration.setAlignment(Pos.CENTER);
		lStartCalibration.setMaxWidth(Double.MAX_VALUE);
		lStartCalibration.setOnAction((e) -> {
			pCalibrator.startTask();
		});
		GridPane.setColumnSpan(lStartCalibration, 3);
		mGridPane.add(lStartCalibration, 0, 0);

		Button lStart3D = new Button("Stop");
		lStart3D.setAlignment(Pos.CENTER);
		lStart3D.setMaxWidth(Double.MAX_VALUE);
		lStart3D.setOnAction((e) -> {
			pCalibrator.stopTask();
		});
		GridPane.setColumnSpan(lStart3D, 3);
		mGridPane.add(lStart3D, 0, 1);
		
		addCheckBoxForCalibrationModule(pCalibrator.getCalibrateZVariable(), 2);
		addCheckBoxForCalibrationModule(pCalibrator.getCalibrateXYVariable(), 3);
		addCheckBoxForCalibrationModule(pCalibrator.getCalibrateAVariable(), 4);
		addCheckBoxForCalibrationModule(pCalibrator.getCalibratePVariable(), 5);


	}

	private void addCheckBoxForCalibrationModule(	Variable<Boolean> lCalibrateVariable,
																								int lPosition)
	{
		Label lLabel = new Label();
		mGridPane.add(lLabel, 0, lPosition);
		
		CheckBox lCheckBox = new CheckBox();
		lCheckBox.setAlignment(Pos.CENTER);
		lCheckBox.setMaxWidth(Double.MAX_VALUE);
		mGridPane.add(lCheckBox, 1, lPosition);

		JFXPropertyVariable<Boolean> lCheckBoxPropertyVariable = new JFXPropertyVariable<>(	lCheckBox.selectedProperty(),
																																												lCalibrateVariable.getName() + "Property",
																																												false);

		lCheckBoxPropertyVariable.sendUpdatesTo(lCalibrateVariable);
	}
}
