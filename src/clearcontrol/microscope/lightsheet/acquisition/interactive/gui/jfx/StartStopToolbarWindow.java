package clearcontrol.microscope.lightsheet.acquisition.interactive.gui.jfx;

import org.dockfx.DockNode;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.gui.jfx.slider.VariableSlider;
import clearcontrol.gui.variable.JFXPropertyVariable;
import clearcontrol.microscope.lightsheet.acquisition.interactive.InteractiveAcquisition;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class StartStopToolbarWindow extends DockNode
{
	private GridPane mGridPane;

	public StartStopToolbarWindow(InteractiveAcquisition pInteractiveAcquisition)
	{
		super(new StandardGridPane());
		mGridPane = (GridPane) getContents();

		Button lStart2D = new Button("Start 2D");
		lStart2D.setAlignment(Pos.CENTER);
		lStart2D.setMaxWidth(Double.MAX_VALUE);
		lStart2D.setOnAction((e) -> {
			pInteractiveAcquisition.start2DAcquisition();
		});
		GridPane.setColumnSpan(lStart2D, 3);
		mGridPane.add(lStart2D, 0, 0);

		Button lStart3D = new Button("Start 3D");
		lStart3D.setAlignment(Pos.CENTER);
		lStart3D.setMaxWidth(Double.MAX_VALUE);
		lStart3D.setOnAction((e) -> {
			pInteractiveAcquisition.start3DAcquisition();
		});
		GridPane.setColumnSpan(lStart3D, 3);
		mGridPane.add(lStart3D, 0, 1);

		Button lStop = new Button("Stop");
		lStop.setAlignment(Pos.CENTER);
		lStop.setMaxWidth(Double.MAX_VALUE);
		lStop.setOnAction((e) -> {
			pInteractiveAcquisition.stopAcquisition();
		});
		GridPane.setColumnSpan(lStop, 3);
		mGridPane.add(lStop, 0, 2);

		VariableSlider<Double> lIntervalSlider = new VariableSlider<Double>("Period (s)",
																																		pInteractiveAcquisition.getLoopPeriodVariable(),
																																		0.0,
																																		1000.0,
																																		0.001,
																																		100.0);
		lIntervalSlider.setAlignment(Pos.BASELINE_CENTER);
		GridPane.setHgrow(lIntervalSlider.getSlider(), Priority.ALWAYS);
		mGridPane.add(lIntervalSlider.getLabel(), 0, 3);
		mGridPane.add(lIntervalSlider.getSlider(), 1, 3);
		mGridPane.add(lIntervalSlider.getTextField(), 2, 3);

		VariableSlider<Double> lExposureSlider = new VariableSlider<Double>("Exp (s)",
																																		pInteractiveAcquisition.getExposureVariable(),
																																		0.0,
																																		1.0,
																																		0.001,
																																		0.1);
		lExposureSlider.setAlignment(Pos.BASELINE_CENTER);
		GridPane.setHgrow(lExposureSlider.getSlider(), Priority.ALWAYS);
		mGridPane.add(lExposureSlider.getLabel(), 0, 4);
		mGridPane.add(lExposureSlider.getSlider(), 1, 4);
		mGridPane.add(lExposureSlider.getTextField(), 2, 4);
		
		
		Label lTriggerOnChangeLabel = new Label("Trigger-on-change");
		CheckBox lTriggerOnChangeLabelCheckBox = new CheckBox();
		GridPane.setColumnSpan(lTriggerOnChangeLabel, 2);
		mGridPane.add(lTriggerOnChangeLabel, 0, 5);
		mGridPane.add(lTriggerOnChangeLabelCheckBox, 2, 5);
		
		BooleanProperty lSelectedProperty = lTriggerOnChangeLabelCheckBox.selectedProperty();
		JFXPropertyVariable<Boolean> lJFXPropertyVariable = new JFXPropertyVariable<Boolean>(	lSelectedProperty,
				"TriggerOnChange",
				false);

		Variable<Boolean> lTriggerOnChangeVariable = pInteractiveAcquisition.getTriggerOnChangeVariable();
		lJFXPropertyVariable.syncWith(lTriggerOnChangeVariable);
		lSelectedProperty.set(lTriggerOnChangeVariable.get());
		
	}

}
