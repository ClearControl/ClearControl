package rtlib.hardware.stages.gui.jfx.demo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Screen;
import javafx.stage.Stage;
import rtlib.hardware.stages.gui.jfx.CubeScene;
import rtlib.hardware.stages.gui.jfx.SnapshotView;
import rtlib.hardware.stages.gui.jfx.View3D;
import rtlib.hardware.stages.gui.jfx.controls.CircleIndicator;

/**
 * CubeViewer for Test Purpose
 */
public class CubeViewer extends Application
{
	private SnapshotView secondView = null;
	private SnapshotView thirdView = null;
	private SnapshotView fourthView = null;

	public static void main(String[] args)
	{
		launch(args);
	}

	private Pane background1Pane = null;

	CubeScene cubeScene = null;
	private SubScene subScene = null;
	private AnimationTimer viewTimer = null;

	@Override
	public void start(Stage stage)
	{
		System.out.println("3D supported? " + Platform.isSupported(ConditionalFeature.SCENE3D));

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

		final BorderPane rootGroup = new BorderPane();
		final Scene scene = new Scene(rootGroup,
																	startWidth,
																	startHeight,
																	true);

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
		scene.widthProperty().addListener(sceneBoundsListener);
		scene.heightProperty().addListener(sceneBoundsListener);

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

		// Backgrounds

		final Stop[] stopsRG = new Stop[]
		{ new Stop(0.0, Color.LIGHTGRAY),
			new Stop(0.2, Color.BLACK),
			new Stop(1.0, Color.BLACK) };
		RadialGradient blackRG = new RadialGradient(0,
																								0,
																								0.5,
																								0.5,
																								1,
																								true,
																								CycleMethod.NO_CYCLE,
																								stopsRG);
		Background blackBG = new Background(new BackgroundFill(	blackRG,
																														null,
																														null));

		final Stop[] stopsLG = new Stop[]
		{ new Stop(0.0, Color.rgb(0, 73, 255)),
			new Stop(0.7, Color.rgb(127, 164, 255)),
			new Stop(1.0, Color.rgb(0, 73, 255)) };
		LinearGradient blueLG = new LinearGradient(	0,
																								0,
																								0,
																								1,
																								true,
																								CycleMethod.NO_CYCLE,
																								stopsLG);
		Background blueBG = new Background(new BackgroundFill(blueLG,
																													null,
																													null));

		Background greenBG = new Background(new BackgroundFill(	Color.MEDIUMTURQUOISE,
																														null,
																														null));

		Background black = new Background(new BackgroundFill(	Color.BLACK,
																													null,
																													null));

		background1Pane.setBackground(black); // initial background
		secondView.setBackground(Paint.valueOf("#000000"));
		thirdView.setBackground(Paint.valueOf("#000000"));
		fourthView.setBackground(Paint.valueOf("#000000"));

		rootGroup.setTop(createControls(cubeScene));
		rootGroup.setCenter(layeredPane);

		stage.setTitle("Model Viewer");

		// Scene scene = new Scene(layout, Color.CORNSILK);
		stage.setScene(scene);
		stage.show();

		viewTimer.start();
	}

	private VBox createControls(CubeScene cubeScene)
	{
		// CheckBox cull = new CheckBox("Cull Back");
		// meshView.cullFaceProperty().bind(
		// Bindings.when(
		// cull.selectedProperty())
		// .then(CullFace.BACK)
		// .otherwise(CullFace.NONE)
		// );
		// meshViewXY.cullFaceProperty().bind(
		// Bindings.when(
		// cull.selectedProperty())
		// .then(CullFace.BACK)
		// .otherwise(CullFace.NONE)
		// );
		// CheckBox wireframe = new CheckBox("Wireframe");
		// meshView.drawModeProperty().bind(
		// Bindings.when(
		// wireframe.selectedProperty())
		// .then(DrawMode.LINE)
		// .otherwise(DrawMode.FILL)
		// );
		// meshViewXY.drawModeProperty().bind(
		// Bindings.when(
		// wireframe.selectedProperty())
		// .then(DrawMode.LINE)
		// .otherwise(DrawMode.FILL)
		// );

		// R-Stage
		final Label caption = new Label("Sample Stage R (micro-degree)");

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
		final Label xStageLabel = new Label("Sample Stage X (microns)");
		final Slider xStageSlider = createSlider(	cubeScene.getCubeCenterGroup()
																												.getTranslateX(),
																							cubeScene.getCubeCenterGroup()
																												.getTranslateX() + CubeScene.VIEWPORT_SIZE
																									- cubeBB.getMaxX()
																									* 2.2,
																							"X-axis stage control");
		xStageSlider.valueProperty()
								.bindBidirectional(cubeScene.getCubeCenterGroup()
																						.translateXProperty());
		final Label xStageValue = new Label(Double.toString(xStageSlider.getValue()));
		xStageValue.textProperty().bind(xStageSlider.valueProperty()
																								.asString("%.0f"));

		final HBox xStage = new HBox(5);
		xStage.getChildren().addAll(xStageLabel,
																xStageSlider,
																xStageValue);
		HBox.setHgrow(xStage, Priority.ALWAYS);

		// Y-Stage
		final Label yStageLabel = new Label("Sample Stage Y (microns)");
		final Slider yStageSlider = createSlider(	cubeScene.getCubeCenterGroup()
																												.getTranslateY(),
																							cubeScene.getCubeCenterGroup()
																												.getTranslateY() + CubeScene.VIEWPORT_SIZE
																									- cubeBB.getMaxY()
																									* 2.2,
																							"Y-axis stage control");
		yStageSlider.valueProperty()
								.bindBidirectional(cubeScene.getCubeCenterGroup()
																						.translateYProperty());
		final Label yStageValue = new Label(Double.toString(yStageSlider.getValue()));
		yStageValue.textProperty().bind(yStageSlider.valueProperty()
																								.asString("%.0f"));

		final HBox yStage = new HBox(5);
		yStage.getChildren().addAll(yStageLabel,
																yStageSlider,
																yStageValue);
		HBox.setHgrow(yStage, Priority.ALWAYS);

		// Z-Stage
		final Label zStageLabel = new Label("Sample Stage Z (microns)");
		final Slider zStageSlider = createSlider(	cubeScene.getCubeCenterGroup()
																												.getTranslateZ(),
																							cubeScene.getCubeCenterGroup()
																												.getTranslateZ() + CubeScene.VIEWPORT_SIZE
																									- cubeBB.getMaxZ()
																									* 2.2,
																							"Z-axis stage control");
		zStageSlider.valueProperty()
								.bindBidirectional(cubeScene.getCubeCenterGroup()
																						.translateZProperty());

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
				cubeScene.setRotationSpeed(30);
			}
			else
			{
				cubeScene.stopCubeRotation();
			}
		});

		// CheckBox texture = new CheckBox("Texture");
		// meshView.materialProperty().bind(
		// Bindings.when(
		// texture.selectedProperty())
		// .then(texturedMaterial)
		// .otherwise((PhongMaterial) null)
		// );
		// meshViewXY.materialProperty().bind(
		// Bindings.when(
		// texture.selectedProperty())
		// .then(texturedMaterial)
		// .otherwise((PhongMaterial) null)
		// );

		// VBox controls = new VBox(10, rotate, texture, cull, wireframe);
		// VBox controls = new VBox(10, rotate, cull, wireframe);
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
