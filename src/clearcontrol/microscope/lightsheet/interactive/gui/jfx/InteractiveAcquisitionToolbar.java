package clearcontrol.microscope.lightsheet.interactive.gui.jfx;

import org.dockfx.DockNode;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.gui.jfx.onoff.OnOffArrayPane;
import clearcontrol.gui.jfx.slider.VariableSlider;
import clearcontrol.gui.jfx.togglebutton.CustomToggleButton;
import clearcontrol.gui.variable.JFXPropertyVariable;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator.IndicatorStyle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class InteractiveAcquisitionToolbar extends DockNode
{
	private GridPane mGridPane;

	public InteractiveAcquisitionToolbar(InteractiveAcquisition pInteractiveAcquisition)
	{
		super(new StandardGridPane());
		mGridPane = (GridPane) getContents();

		mGridPane.setPrefSize(300, 200);

		setTitle("Interactive");

		CustomToggleButton lUseAcqStateToggleButton = new CustomToggleButton(	"Using current Acquisition State",
																																					"Not using current Acquisition State");
		// lUseAcqStateToggleButton.setMinWidth(250);
		lUseAcqStateToggleButton.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(lUseAcqStateToggleButton, Priority.ALWAYS);
		GridPane.setColumnSpan(lUseAcqStateToggleButton, 3);
		mGridPane.add(lUseAcqStateToggleButton, 0, 0);

		BooleanProperty lUseAcqStateSelectedProperty = lUseAcqStateToggleButton.selectedProperty();
		JFXPropertyVariable<Boolean> lUseAcqStateJFXPropertyVariable = new JFXPropertyVariable<Boolean>(lUseAcqStateSelectedProperty,
																																																		"UseAcqState",
																																																		false);

		Button lStart2D = new Button("Start 2D");
		lStart2D.setAlignment(Pos.CENTER);
		lStart2D.setMaxWidth(Double.MAX_VALUE);
		lStart2D.setOnAction((e) -> {
			pInteractiveAcquisition.start2DAcquisition();
		});
		GridPane.setColumnSpan(lStart2D, 2);
		mGridPane.add(lStart2D, 0, 1);

		Button lStart3D = new Button("Start 3D");
		lStart3D.setAlignment(Pos.CENTER);
		lStart3D.setMaxWidth(Double.MAX_VALUE);
		lStart3D.setOnAction((e) -> {
			pInteractiveAcquisition.start3DAcquisition();
		});
		GridPane.setColumnSpan(lStart3D, 2);
		mGridPane.add(lStart3D, 0, 2);

		Button lStop = new Button("Stop");
		lStop.setAlignment(Pos.CENTER);
		lStop.setMaxWidth(Double.MAX_VALUE);
		lStop.setOnAction((e) -> {
			pInteractiveAcquisition.stopAcquisition();
		});
		GridPane.setColumnSpan(lStop, 2);
		mGridPane.add(lStop, 0, 3);

		SimpleIndicator lAcquisitionStateIndicator = new SimpleIndicator();
		lAcquisitionStateIndicator.indicatorStyleProperty()
															.set(IndicatorStyle.RED);
		pInteractiveAcquisition.getIsRunningVariable()
														.addSetListener((o, n) -> {
															lAcquisitionStateIndicator.onProperty()
																												.set(n);
														});

		lAcquisitionStateIndicator.setMinSize(100, 100);
		GridPane.setColumnSpan(lAcquisitionStateIndicator, 1);
		GridPane.setRowSpan(lAcquisitionStateIndicator, 3);
		mGridPane.add(lAcquisitionStateIndicator, 2, 1);

		VariableSlider<Double> lIntervalSlider = new VariableSlider<Double>("Period (s)",
																																				pInteractiveAcquisition.getLoopPeriodVariable(),
																																				0.0,
																																				1000.0,
																																				0.001,
																																				100.0);
		lIntervalSlider.setAlignment(Pos.BASELINE_CENTER);
		GridPane.setHgrow(lIntervalSlider.getSlider(), Priority.ALWAYS);
		mGridPane.add(lIntervalSlider.getLabel(), 0, 4);
		mGridPane.add(lIntervalSlider.getSlider(), 1, 4);
		mGridPane.add(lIntervalSlider.getTextField(), 2, 4);

		VariableSlider<Double> lExposureSlider = new VariableSlider<Double>("Exp (s)",
																																				pInteractiveAcquisition.getExposureVariable(),
																																				0.0,
																																				1.0,
																																				0.001,
																																				0.1);
		lExposureSlider.setAlignment(Pos.BASELINE_CENTER);
		GridPane.setHgrow(lExposureSlider.getSlider(), Priority.ALWAYS);
		mGridPane.add(lExposureSlider.getLabel(), 0, 5);
		mGridPane.add(lExposureSlider.getSlider(), 1, 5);
		mGridPane.add(lExposureSlider.getTextField(), 2, 5);

		Variable<Boolean> lUseAcqStateVariable = pInteractiveAcquisition.getUseCurrentAcquisitionStateVariable();
		lUseAcqStateJFXPropertyVariable.syncWith(lUseAcqStateVariable);
		lUseAcqStateSelectedProperty.set(lUseAcqStateVariable.get());

		Label lActiveCamerasLabel = new Label("Active Cameras");
		mGridPane.add(lActiveCamerasLabel, 0, 6);
		GridPane.setColumnSpan(lActiveCamerasLabel, 2);

		OnOffArrayPane lAddOnOffArray = new OnOffArrayPane();

		for (int c = 0; c < pInteractiveAcquisition.getNumberOfCameras(); c++)
		{
			lAddOnOffArray.addSwitch(	"" + c,
																pInteractiveAcquisition.getActiveCameraVariable(c));
		}

		GridPane.setColumnSpan(lAddOnOffArray, 2);
		mGridPane.add(lAddOnOffArray, 2, 6);

		CustomToggleButton lTriggerOnChangeToggleButton = new CustomToggleButton(	"Trigger-on-change active",
																																							"Trigger-on-change inactive");
		lTriggerOnChangeToggleButton.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(lTriggerOnChangeToggleButton, Priority.ALWAYS);
		GridPane.setColumnSpan(lTriggerOnChangeToggleButton, 3);
		mGridPane.add(lTriggerOnChangeToggleButton, 0, 9);

		BooleanProperty lTriggerOnChangeSelectedProperty = lTriggerOnChangeToggleButton.selectedProperty();
		JFXPropertyVariable<Boolean> lTriggerOnChangeJFXPropertyVariable = new JFXPropertyVariable<Boolean>(lTriggerOnChangeSelectedProperty,
																																																				"TriggerOnChange",
																																																				false);

		Variable<Boolean> lTriggerOnChangeVariable = pInteractiveAcquisition.getTriggerOnChangeVariable();
		lTriggerOnChangeJFXPropertyVariable.syncWith(lTriggerOnChangeVariable);
		lTriggerOnChangeSelectedProperty.set(lTriggerOnChangeVariable.get());

		Label lInteractiveAcquisitionStatusLabel = new Label();
		lInteractiveAcquisitionStatusLabel.setAlignment(Pos.CENTER);
		lInteractiveAcquisitionStatusLabel.setMinWidth(300);
		lInteractiveAcquisitionStatusLabel.setMaxWidth(Double.POSITIVE_INFINITY);
		GridPane.setHgrow(lInteractiveAcquisitionStatusLabel, Priority.ALWAYS);
		GridPane.setColumnSpan(lInteractiveAcquisitionStatusLabel, 3);
		GridPane.setValignment(lInteractiveAcquisitionStatusLabel, VPos.CENTER);
		mGridPane.add(lInteractiveAcquisitionStatusLabel, 0, 11);

		pInteractiveAcquisition.getAcquisitionCounterVariable()
														.addSetListener((o, n) -> {
															Platform.runLater(() -> {

																long lAcquisitionCounter = n;
																long lNumberOfCameras = pInteractiveAcquisition.getNumberOfCameras();
																long lNumberofStacks = lAcquisitionCounter * lNumberOfCameras;
																String lStatus = String.format(	"Number of stacks acquired: %d stacks = %d cam. x %d acqu.",
																																lNumberofStacks,
																																lNumberOfCameras,
																																lAcquisitionCounter);
																lInteractiveAcquisitionStatusLabel.setText(lStatus);
															});

														});

	}

}
