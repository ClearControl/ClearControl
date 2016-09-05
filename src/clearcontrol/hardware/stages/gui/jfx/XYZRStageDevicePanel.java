package clearcontrol.hardware.stages.gui.jfx;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.hardware.stages.StageDeviceInterface;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.CubeScene;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.SnapshotView;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.View3D;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.controls.CircleIndicator;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
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

	Slider rotateSlider, xStageSlider, yStageSlider, zStageSlider;

	public XYZRStageDevicePanel(StageDeviceInterface pStageDeviceInterface)
	{
		mStageDeviceInterface = pStageDeviceInterface;

		init();

		// TODO: @HongKee: please connect the variables from the stage to the GUI.
		// TODO: @Loic: Could you check the Variable listener? When I change the value, it continues to change the values.
		// TODO:        Maybe double checking needs less granularity.
		for ( int i = 0; i < mStageDeviceInterface.getNumberOfDOFs(); i++ )
		{
			String dof = mStageDeviceInterface.getDOFNameByIndex( i );
			Slider slider = null;

			if ( dof.equals( "R" ) )
				slider = rotateSlider;
			else if ( dof.equals( "X" ) )
				slider = xStageSlider;
			else if ( dof.equals( "Y" ) )
				slider = yStageSlider;
			else if ( dof.equals( "Z" ) )
				slider = zStageSlider;

			System.out.println( mStageDeviceInterface.getMaxPositionVariable( i ).get() );

			slider.setMin( mStageDeviceInterface.getMinPositionVariable( i ).get() );
			slider.setMax( mStageDeviceInterface.getMaxPositionVariable( i ).get() );
			slider.setValue( mStageDeviceInterface.getCurrentPositionVariable( i ).get() );

			// Data -> GUI
			final Slider finalSlider = slider;
			mStageDeviceInterface.getCurrentPositionVariable( i ).addSetListener( ( pCurrentValue, pNewValue ) -> {
				if ( !pNewValue.equals( finalSlider.getValue() ) )
					Platform.runLater( new Runnable()
					{
						@Override public void run()
						{
							finalSlider.setValue( pNewValue );
						}
					} );
			} );

			// GUI -> Data
			final Variable< Double > curPos = mStageDeviceInterface.getTargetPositionVariable( i );
			slider.setOnMouseReleased( event -> curPos.set( finalSlider.getValue() ) );
		}

		// same scheme as for the others...
		// TODO: don't forget to check the other issues related to the stage...
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

		setTop(createControls(cubeScene));
		setCenter(layeredPane);

		viewTimer.start();
	}

	public void stop()
	{
		viewTimer.stop();
	}

	private VBox createControls(CubeScene cubeScene)
	{
		// R-Stage
		final Label caption = new Label("Stage R (micro-degree)");

		rotateSlider = new Slider();
		rotateSlider.setMin( 0 );
		rotateSlider.setMax( 360 );
		rotateSlider.setMajorTickUnit( 90 );
		rotateSlider.setShowTickMarks( true );
		rotateSlider.setShowTickLabels( true );

		final CircleIndicator pi = new CircleIndicator(0);

		rotateSlider.valueProperty()
					.addListener((ObservableValue<? extends Number> ov,
												Number old_val,
												Number new_val) -> pi.setProgress(new_val.doubleValue()));

		rotateSlider.valueProperty()
					.bindBidirectional(cubeScene.getCubeCenterGroup()
																			.rotateProperty());

		final HBox rStage = new HBox(5);
		rStage.getChildren().addAll( caption, rotateSlider, pi );
		HBox.setHgrow(rStage, Priority.ALWAYS);

		BoundingBox cubeBB = cubeScene.getCubeBoundingBox();

		// X-Stage
		final Label xStageLabel = new Label("Stage X (microns)");
		double xMin = cubeScene.getCubeCenterGroup().getTranslateX();
		double xMax = cubeScene.getCubeCenterGroup().getTranslateX() + CubeScene.VIEWPORT_SIZE
									- cubeBB.getMaxX()
									* 2.2;

		xStageSlider = createSlider( 0,
																							100,
																							"X-axis stage control");

		xStageSlider.valueProperty()
								.addListener(new ChangeListener<Number>()
								{
									@Override
									public void changed(ObservableValue<? extends Number> observable,
																			Number oldValue,
																			Number newValue)
									{
										cubeScene.getCubeCenterGroup()
															.setTranslateX(newValue.doubleValue() * (xMax - xMin)
																	/ xStageSlider.getMax()
																							+ xMin);
									}
								});
		// xStageSlider.valueProperty().bindBidirectional(
		// cubeScene.getCubeCenterGroup().translateXProperty() );

		final Label xStageValue = new Label(Double.toString(xStageSlider.getValue()));
		xStageValue.textProperty().bind(xStageSlider.valueProperty()
																								.asString("%.0f"));

		final HBox xStage = new HBox(5);
		xStage.getChildren().addAll(xStageLabel,
																xStageSlider,
																xStageValue);
		HBox.setHgrow(xStage, Priority.ALWAYS);

		// Y-Stage
		final Label yStageLabel = new Label("Stage Y (microns)");

		double yMin = cubeScene.getCubeCenterGroup().getTranslateY();
		double yMax = cubeScene.getCubeCenterGroup().getTranslateY() + CubeScene.VIEWPORT_SIZE
									- cubeBB.getMaxY()
									* 2.2;
		yStageSlider = createSlider( 0,
																							100,
																							"Y-axis stage control");

		yStageSlider.valueProperty()
								.addListener(new ChangeListener<Number>()
								{
									@Override
									public void changed(ObservableValue<? extends Number> observable,
																			Number oldValue,
																			Number newValue)
									{
										cubeScene.getCubeCenterGroup()
															.setTranslateY(newValue.doubleValue() * (yMax - yMin)
																	/ yStageSlider.getMax()
																							+ yMin);
									}
								});
		// yStageSlider.valueProperty().bindBidirectional(
		// cubeScene.getCubeCenterGroup().translateYProperty() );

		final Label yStageValue = new Label(Double.toString(yStageSlider.getValue()));
		yStageValue.textProperty().bind(yStageSlider.valueProperty()
																								.asString("%.0f"));

		final HBox yStage = new HBox(5);
		yStage.getChildren().addAll(yStageLabel,
																yStageSlider,
																yStageValue);
		HBox.setHgrow(yStage, Priority.ALWAYS);

		// Z-Stage
		final Label zStageLabel = new Label("Stage Z (microns)");

		double zMin = cubeScene.getCubeCenterGroup().getTranslateZ();
		double zMax = cubeScene.getCubeCenterGroup().getTranslateZ() + CubeScene.VIEWPORT_SIZE
									- cubeBB.getMaxZ()
									* 2.2;
		zStageSlider = createSlider( 0,
																							100,
																							"Z-axis stage control");

		zStageSlider.valueProperty()
								.addListener(new ChangeListener<Number>()
								{
									@Override
									public void changed(ObservableValue<? extends Number> observable,
																			Number oldValue,
																			Number newValue)
									{
										cubeScene.getCubeCenterGroup()
															.setTranslateZ(newValue.doubleValue() * (zMax - zMin)
																	/ zStageSlider.getMax()
																							+ zMin);
									}
								});
		// zStageSlider.valueProperty().bindBidirectional(
		// cubeScene.getCubeCenterGroup().translateZProperty() );

		final Label zStageValue = new Label(Double.toString(zStageSlider.getValue()));
		zStageValue.textProperty().bind(zStageSlider.valueProperty()
																								.asString("%.0f"));

		final HBox zStage = new HBox(5);
		zStage.getChildren().addAll(zStageLabel,
																zStageSlider,
																zStageValue);
		HBox.setHgrow(zStage, Priority.ALWAYS);

		CheckBox rotate = new CheckBox("Rotate");
		rotate.selectedProperty().addListener(observable -> {
			if (rotate.isSelected())
			{
				cubeScene.setRotationSpeed(20);
			}
			else
			{
				cubeScene.stopCubeRotation();
			}
		});

		VBox controls = new VBox(	10,
															rotate,
															rStage,
															xStage,
															yStage,
															zStage);
		controls.setPadding(new Insets(10));
		return controls;
	}

	private Slider createSlider(final double minValue,
															final double maxValue,
															final String helpText)
	{
		final Slider slider = new Slider(minValue, maxValue, minValue);
		slider.setMajorTickUnit(20);
		slider.setMinorTickCount(0);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.setStyle("-fx-text-fill: white");
		slider.setTooltip(new Tooltip(helpText));
		return slider;
	}
}
