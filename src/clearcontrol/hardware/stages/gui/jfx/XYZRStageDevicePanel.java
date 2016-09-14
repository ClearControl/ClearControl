package clearcontrol.hardware.stages.gui.jfx;

import clearcontrol.core.variable.Variable;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.custom.iconswitch.IconSwitch;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import clearcontrol.hardware.stages.StageDeviceInterface;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.CubeScene;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.SnapshotView;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.View3D;
import eu.hansolo.enzo.common.SymbolType;

import eu.hansolo.enzo.simpleindicator.SimpleIndicator;
import javafx.animation.AnimationTimer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.geometry.BoundingBox;
import javafx.geometry.HPos;
import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.SubScene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;

/**
 * Stage 3D Control
 */
public class XYZRStageDevicePanel extends BorderPane
{

	private StageDeviceInterface mStageDeviceInterface;

	private Pane background1Pane = null;
	private CubeScene cubeScene = null;
	private SubScene subScene = null;
	private SnapshotView secondView = null;
	private SnapshotView thirdView = null;
	private SnapshotView fourthView = null;
	private AnimationTimer viewTimer = null;

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

	private VariableSlider<Double> createCurrentSlider(Stage pStage)
	{
		int lIndex = mStageDeviceInterface.getDOFIndexByName(pStage.name());
		VariableSlider<Double> variableCurSlider = new VariableSlider<>("",
																																		mStageDeviceInterface.getCurrentPositionVariable(lIndex),
																																		mStageDeviceInterface.getMinPositionVariable(lIndex),
																																		mStageDeviceInterface.getMaxPositionVariable(lIndex),
																																		mStageDeviceInterface.getGranularityPositionVariable(lIndex),
																																		10d);
		variableCurSlider.getSlider().setDisable(true);
		variableCurSlider.getSlider().setStyle( "-fx-opacity: 1;" );
		variableCurSlider.getTextField().setDisable(true);
		variableCurSlider.getTextField().setStyle( "-fx-opacity: 1;" );

		variableCurSlider.setPadding( new Insets( 5, 25, 25, 25 ) );
		return variableCurSlider;
	}

	private VariableSlider<Double> createTargetSlider(Stage pStage)
	{
		int lIndex = mStageDeviceInterface.getDOFIndexByName(pStage.name());
		VariableSlider<Double> variableSlider = new VariableSlider<>("",
																																	mStageDeviceInterface.getTargetPositionVariable(lIndex),
																																	mStageDeviceInterface.getMinPositionVariable(lIndex),
																																	mStageDeviceInterface.getMaxPositionVariable(lIndex),
																																	mStageDeviceInterface.getGranularityPositionVariable(lIndex),
																																	10d);
		variableSlider.getSlider().setShowTickLabels( false );
		variableSlider.setPadding( new Insets( 25, 25, 5, 25 ) );
		return variableSlider;
	}

	private Variable<Boolean> getStageAttribute(Stage pStage,
																							Attribute pAttribute)
	{
		Variable<Boolean> variable = null;
		int lIndex = mStageDeviceInterface.getDOFIndexByName(pStage.name());

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

	public void init()
	{
		// System.out.println("3D supported? " +
		// Platform.isSupported(ConditionalFeature.SCENE3D));

		cubeScene = new CubeScene();
		subScene = cubeScene.getSubScene();

		secondView = new SnapshotView(cubeScene.getRoot3D());
		final ImageView secondViewPane = secondView.getViewPane();
		secondView.setSceneDiameter(cubeScene.getSceneDiameter());

		thirdView = new SnapshotView(cubeScene.getRoot3D());
		final ImageView thirdViewPane = thirdView.getViewPane();
		thirdView.setSceneDiameter(cubeScene.getSceneDiameter());

		fourthView = new SnapshotView(cubeScene.getRoot3D());
		final ImageView fourthViewPane = fourthView.getViewPane();
		fourthView.setSceneDiameter(cubeScene.getSceneDiameter());

		cubeScene.setVantagePoint(View3D.ViewPort.CORNER);
		secondView.setVantagePoint(View3D.ViewPort.BOTTOM);
		thirdView.setVantagePoint(View3D.ViewPort.RIGHT);
		fourthView.setVantagePoint(View3D.ViewPort.FRONT);

		final Rectangle2D screenRect = Screen.getPrimary().getBounds();
		final double screenWidth = screenRect.getWidth();
		final double screenHeight = screenRect.getHeight();

		final double startWidth = screenWidth * 0.7;
		final double startHeight = screenHeight * 0.7;
		subScene.setWidth(startWidth / 2);
		subScene.setHeight(startHeight / 2);

		background1Pane = new Pane();
		background1Pane.setPrefSize(startWidth / 2, startHeight / 2);

		final Pane layeredPane = new Pane()
		{
			@Override
			protected void layoutChildren()
			{

				final double sceneWidth = getWidth();

				// Main view
				final double width = sceneWidth / 2;
				final double height = getHeight() / 2;

				background1Pane.setPrefSize(width, height);
				background1Pane.autosize();
				background1Pane.relocate(0, 0);

				// Second view
				secondViewPane.relocate(width, 0);
				thirdViewPane.relocate(0, height);
				fourthViewPane.relocate(width, height);
			}
		};

		final ChangeListener sceneBoundsListener = new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable,
													Object oldXY,
													Object newXY)
			{
				subScene.setWidth(layeredPane.getWidth() / 2);
				subScene.setHeight(layeredPane.getHeight() / 2);
			}
		};
		layeredPane.widthProperty().addListener(sceneBoundsListener);
		layeredPane.heightProperty().addListener(sceneBoundsListener);

		viewTimer = new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				secondView.drawView(layeredPane.getWidth() / 2,
														layeredPane.getHeight() / 2);
				thirdView.drawView(	layeredPane.getWidth() / 2,
														layeredPane.getHeight() / 2);
				fourthView.drawView(layeredPane.getWidth() / 2,
														layeredPane.getHeight() / 2);
			}
		};

		layeredPane.getChildren().addAll(	background1Pane,
																			subScene,
																			secondViewPane,
																			thirdViewPane,
																			fourthViewPane);

		Background black = new Background(new BackgroundFill(	Color.BLACK,
																													null,
																													null));

		background1Pane.setBackground(black); // initial background
		secondView.setBackground(Paint.valueOf("#000000"));
		thirdView.setBackground(Paint.valueOf("#000000"));
		fourthView.setBackground(Paint.valueOf("#000000"));

		setTop(createControls());
		setCenter(layeredPane);

		viewTimer.start();
	}

	public void stop()
	{
		viewTimer.stop();
	}

	private GridPane createFrontControls(	Stage pStage,
																				VariableSlider<Double> pSlider)
	{
		final IconSwitch lEnableSwitch = new IconSwitch();
		lEnableSwitch.setSymbolType(SymbolType.POWER);
		lEnableSwitch.setSymbolColor(Color.web("#ffffff"));
		lEnableSwitch.setSwitchColor(Color.web("#34495e"));
		lEnableSwitch.setThumbColor(Color.web("#ff495e"));

		lEnableSwitch.setMaxSize(60, 30);

		// Data -> GUI
		getStageAttribute(pStage, Attribute.Enable).addSetListener((pCurrentValue,
																																pNewValue) -> {
			Platform.runLater(() -> {
				lEnableSwitch.setSelected(pNewValue);
				pSlider.getSlider().setDisable(!pNewValue);
				pSlider.getTextField().setDisable(!pNewValue);
			});
		});

		// Enable, GUI -> Data
		lEnableSwitch.setOnMouseReleased(event -> getStageAttribute(pStage,
																																Attribute.Enable).setAsync(!getStageAttribute(pStage,
																																																							Attribute.Enable).get()));

		// Initialize the status at startup
		pSlider.getSlider()
				.setDisable( !getStageAttribute( pStage, Attribute.Enable ).get() );
		pSlider.getTextField()
				.setDisable( !getStageAttribute( pStage, Attribute.Enable ).get() );

		final SimpleIndicator lIndicator = new SimpleIndicator();
		lIndicator.setMaxSize(50, 50);
		getStageAttribute(pStage, Attribute.Ready).addSetListener((	pCurrentValue,
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
		lHomingButton.setAlignment( Pos.BASELINE_LEFT );
		lHomingButton.setPrefWidth( 70 );
		lHomingButton.setOnAction(event -> getStageAttribute(	pStage,
				Attribute.Homing ).setEdgeAsync( false,
				true ) );

		final Button lStopButton = new Button("Stop");
		lStopButton.setAlignment( Pos.BASELINE_LEFT );
		lStopButton.setPrefWidth( 70 );
		lStopButton.setOnAction(event -> getStageAttribute(	pStage,
				Attribute.Stop ).setEdgeAsync( false,
				true ) );

		final Button lResetButton = new Button("Reset");
		lResetButton.setAlignment( Pos.BASELINE_LEFT );
		lResetButton.setPrefWidth( 70 );
		lResetButton.setOnAction(event -> getStageAttribute(pStage,
				Attribute.Reset ).setEdgeAsync( false,
				true ) );

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

	private HBox createStageControl(String pLabelString, Stage pStage)
	{
		BoundingBox cubeBB = cubeScene.getCubeBoundingBox();

		final Label lStageLabel = new Label(pLabelString);

		final VariableSlider<Double> lTargetSlider = createTargetSlider(pStage);
		final VariableSlider<Double> lCurrentSlider = createCurrentSlider(pStage);

		double lOffset = lCurrentSlider.getSlider().getMin() * Math.signum(lCurrentSlider.getSlider()
																																											.getMin());

		switch (pStage)
		{
		case R:
		{
			lCurrentSlider.getSlider()
										.valueProperty()
					.bindBidirectional( cubeScene.getCubeCenterGroup()
							.rotateProperty() );
		}
			break;
		case X:
		{
			double min = cubeScene.getCubeCenterGroup().getTranslateX();
			double max = cubeScene.getCubeCenterGroup().getTranslateX() + CubeScene.VIEWPORT_SIZE
										- cubeBB.getMaxX()
										* 2.2;

			lCurrentSlider.getSlider()
										.valueProperty()
					.addListener( ( observable, oldValue, newValue ) -> cubeScene.getCubeCenterGroup()
							.setTranslateX( ( newValue.doubleValue() + lOffset ) * ( max - min )
									/ ( lCurrentSlider.getSlider()
									.getMax() + lOffset )
									+ min ) );

			cubeScene.getCubeCenterGroup()
					.setTranslateX( ( lCurrentSlider.getSlider().getValue() + lOffset ) * ( max - min )
							/ ( lCurrentSlider.getSlider()
							.getMax() + lOffset )
							+ min );
		}
			break;
		case Y:
		{
			double min = cubeScene.getCubeCenterGroup().getTranslateY();
			double max = cubeScene.getCubeCenterGroup().getTranslateY() + CubeScene.VIEWPORT_SIZE
										- cubeBB.getMaxY()
										* 2.2;

			lCurrentSlider.getSlider()
										.valueProperty()
					.addListener( ( observable, oldValue, newValue ) -> cubeScene.getCubeCenterGroup()
							.setTranslateY( ( newValue.doubleValue() + lOffset ) * ( max - min )
									/ ( lCurrentSlider.getSlider()
									.getMax() + lOffset )
									+ min ) );

			cubeScene.getCubeCenterGroup()
					.setTranslateY( ( lCurrentSlider.getSlider().getValue() + lOffset ) * ( max - min )
							/ ( lCurrentSlider.getSlider()
							.getMax() + lOffset )
							+ min );
		}
			break;
		case Z:
		{
			double min = cubeScene.getCubeCenterGroup().getTranslateZ();
			double max = cubeScene.getCubeCenterGroup().getTranslateZ() + CubeScene.VIEWPORT_SIZE
										- cubeBB.getMaxZ()
										* 2.2;

			lCurrentSlider.getSlider()
										.valueProperty()
					.addListener( ( observable, oldValue, newValue ) -> cubeScene.getCubeCenterGroup()
							.setTranslateZ( ( newValue.doubleValue() + lOffset ) * ( max - min )
									/ ( lCurrentSlider.getSlider()
									.getMax() + lOffset )
									+ min ) );

			cubeScene.getCubeCenterGroup()
					.setTranslateZ( ( lCurrentSlider.getSlider().getValue() + lOffset ) * ( max - min )
							/ ( lCurrentSlider.getSlider()
							.getMax() + lOffset )
							+ min );

		}
			break;
		}

		final HBox lStageBox = new HBox(5);
		final VBox lSliderBox = new VBox( lTargetSlider, lCurrentSlider );

		lStageBox.getChildren()
							.addAll(new VBox(	lStageLabel,
																createFrontControls(pStage,
																										lTargetSlider)),
									lSliderBox );
		HBox.setHgrow(lStageBox, Priority.ALWAYS);

		return lStageBox;
	}

	private VBox createControls()
	{
		// CheckBox rotate = new CheckBox("Rotate");
		// rotate.selectedProperty().addListener(observable -> {
		// if (rotate.isSelected())
		// {
		// cubeScene.setRotationSpeed(20);
		// }
		// else
		// {
		// cubeScene.stopCubeRotation();
		// }
		// });

		VBox controls = new VBox(	10,
															// rotate,
															createStageControl(	"Stage R (micro-degree)",
																									Stage.R),
															createStageControl(	"Stage X (microns)",
																									Stage.X),
															createStageControl(	"Stage Y (microns)",
																									Stage.Y),
															createStageControl(	"Stage Z (microns)",
																									Stage.Z));
		controls.setPadding(new Insets(10));
		return controls;
	}
}
