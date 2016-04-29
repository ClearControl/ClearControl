package rtlib.hardware.stages.gui.jfx;

import javafx.animation.AnimationTimer;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
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
import javafx.stage.Stage;
import rtlib.hardware.stages.gui.jfx.controls.CircleIndicator;

/**
 * Stage 3D Control
 */
public class Stage3DControl
{
	private BorderPane rootGroup = null;

	private Pane background1Pane = null;

	CubeScene cubeScene = null;
	private SubScene subScene = null;
	private SnapshotView secondView = null;
	private SnapshotView thirdView = null;
	private SnapshotView fourthView = null;

	private AnimationTimer viewTimer = null;
	private Scene scene = null;

	public Stage3DControl(final BorderPane rootPane)
	{
		rootGroup = rootPane;
		init();
	}

	public void init()
	{
//		System.out.println("3D supported? " + Platform.isSupported(ConditionalFeature.SCENE3D));

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

		scene = new Scene(rootGroup, startWidth, startHeight, true);

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

		rootGroup.setTop(createControls(cubeScene));
		rootGroup.setCenter(layeredPane);

		viewTimer.start();
	}

	public void start(Stage stage)
	{
		stage.setTitle("Stage Control");
		stage.setScene(scene);
		stage.show();
	}

	public void stop()
	{
		viewTimer.stop();
	}

	private VBox createControls(CubeScene cubeScene)
	{
		// R-Stage
		final Label caption = new Label("Stage R (micro-degree)");

		final Slider slider = new Slider();
		slider.setMin(0);
		slider.setMax(360);
		slider.setMajorTickUnit(90);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);

		final CircleIndicator pi = new CircleIndicator(0);

		slider.valueProperty()
					.addListener((ObservableValue<? extends Number> ov,
												Number old_val,
												Number new_val) -> pi.setProgress(new_val.doubleValue()));

		slider.valueProperty()
					.bindBidirectional(cubeScene.getCubeCenterGroup()
																			.rotateProperty());

		final HBox rStage = new HBox(5);
		rStage.getChildren().addAll(caption, slider, pi);
		HBox.setHgrow(rStage, Priority.ALWAYS);

		BoundingBox cubeBB = cubeScene.getCubeBoundingBox();

		// X-Stage
		final Label xStageLabel = new Label("Stage X (microns)");
		double xMin = cubeScene.getCubeCenterGroup().getTranslateX();
		double xMax = cubeScene.getCubeCenterGroup().getTranslateX() + CubeScene.VIEWPORT_SIZE
									- cubeBB.getMaxX()
									* 2.2;

		final Slider xStageSlider = createSlider(	0,
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
																							/ 100
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
		final Slider yStageSlider = createSlider(	0,
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
																							/ 100
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
		final Slider zStageSlider = createSlider(	0,
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
																							/ 100
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
