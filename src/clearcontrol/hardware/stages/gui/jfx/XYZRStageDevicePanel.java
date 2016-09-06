package clearcontrol.hardware.stages.gui.jfx;

import clearcontrol.core.variable.Variable;

import clearcontrol.gui.jfx.var.slider.VariableSlider;
import clearcontrol.gui.jfx.var.slider.customslider.Slider;
import clearcontrol.hardware.stages.StageDeviceInterface;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.CubeScene;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.SnapshotView;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.View3D;
import clearcontrol.hardware.stages.gui.jfx.xyzr3d.controls.CircleIndicator;
import javafx.animation.AnimationTimer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.SubScene;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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

	VariableSlider< Double > rotateVariableSlider, xStageVariableSlider, yStageVariableSlider, zStageVariableSlider;

	VariableSlider< Double > rotateCurSlider, xStageCurSlider, yStageCurSlider, zStageCurSlider;

	public XYZRStageDevicePanel(StageDeviceInterface pStageDeviceInterface)
	{
		mStageDeviceInterface = pStageDeviceInterface;

		for ( int i = 0; i < mStageDeviceInterface.getNumberOfDOFs(); i++ )
		{
			String dof = mStageDeviceInterface.getDOFNameByIndex( i );
			VariableSlider< Double > variableSlider = new VariableSlider<>( dof,
					mStageDeviceInterface.getTargetPositionVariable( i ),
					mStageDeviceInterface.getMinPositionVariable( i ),
					mStageDeviceInterface.getMaxPositionVariable( i ),
					new Variable<>( "granularity", 1d ), 10d
			);

			VariableSlider< Double > variableCurSlider = new VariableSlider<>( "",
					mStageDeviceInterface.getCurrentPositionVariable( i ),
					mStageDeviceInterface.getMinPositionVariable( i ),
					mStageDeviceInterface.getMaxPositionVariable( i ),
					new Variable<>( "granularity", 1d ), 10d
			);
			variableCurSlider.getSlider().setDisable( true );
			variableCurSlider.getTextField().setDisable( true );

			if ( dof.equals( "R" ) )
			{
				rotateVariableSlider = variableSlider;
				rotateCurSlider = variableCurSlider;
			}
			else if ( dof.equals( "X" ) )
			{
				xStageVariableSlider = variableSlider;
				xStageCurSlider = variableCurSlider;
			}
			else if ( dof.equals( "Y" ) )
			{
				yStageVariableSlider = variableSlider;
				yStageCurSlider = variableCurSlider;
			}
			else if ( dof.equals( "Z" ) )
			{
				zStageVariableSlider = variableSlider;
				zStageCurSlider = variableCurSlider;
			}

			//			// Data -> GUI
			//			final Slider finalSlider = slider;
			//			mStageDeviceInterface.getCurrentPositionVariable( i ).addSetListener( ( pCurrentValue, pNewValue ) -> {
			//				if ( !pNewValue.equals( finalSlider.getValue() ) )
			//					Platform.runLater( new Runnable()
			//					{
			//						@Override public void run()
			//						{
			//							finalSlider.setValue( pNewValue );
			//						}
			//					} );
			//			} );
			//
			//			// GUI -> Data
			//			final Variable< Double > curPos = mStageDeviceInterface.getTargetPositionVariable( i );
			//			slider.setOnMouseReleased( event -> curPos.set( finalSlider.getValue() ) );
		}

		// same scheme as for the others...
		// TODO: don't forget to check the other issues related to the stage...

		init();

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
			public void handle( long now )
			{
				secondView.drawView( layeredPane.getWidth() / 2,
						layeredPane.getHeight() / 2 );
				thirdView.drawView( layeredPane.getWidth() / 2,
						layeredPane.getHeight() / 2 );
				fourthView.drawView( layeredPane.getWidth() / 2,
						layeredPane.getHeight() / 2 );
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

		Slider rotateSlider = rotateVariableSlider.getSlider();
		rotateSlider.setMin( 0 );
		rotateSlider.setMax( 360 );
		rotateSlider.setMajorTickUnit( 90 );
		rotateSlider.setShowTickMarks( true );
		rotateSlider.setShowTickLabels( true );

		rotateSlider = rotateCurSlider.getSlider();
		rotateSlider.setMin( 0 );
		rotateSlider.setMax( 360 );
		rotateSlider.setMajorTickUnit( 90 );
		rotateSlider.setShowTickMarks( true );
		rotateSlider.setShowTickLabels( true );

		final CircleIndicator pi = new CircleIndicator(0);

		rotateCurSlider.getSlider().valueProperty()
					.addListener((ObservableValue<? extends Number> ov,
												Number old_val,
												Number new_val) -> pi.setProgress(new_val.doubleValue()));

		rotateCurSlider.getSlider().valueProperty()
					.bindBidirectional(cubeScene.getCubeCenterGroup()
																			.rotateProperty());

		final HBox rStage = new HBox(5);
		rStage.getChildren().addAll( caption, rotateVariableSlider, rotateCurSlider, pi );
		HBox.setHgrow(rStage, Priority.ALWAYS);

		BoundingBox cubeBB = cubeScene.getCubeBoundingBox();

		// X-Stage
		final Label xStageLabel = new Label("Stage X (microns)");
		double xMin = cubeScene.getCubeCenterGroup().getTranslateX();
		double xMax = cubeScene.getCubeCenterGroup().getTranslateX() + CubeScene.VIEWPORT_SIZE
									- cubeBB.getMaxX()
									* 2.2;

		double xOffset = xStageCurSlider.getSlider().getMin() * Math.signum( xStageCurSlider.getSlider().getMin() );

		xStageCurSlider.getSlider().valueProperty()
								.addListener(new ChangeListener<Number>()
								{
									@Override
									public void changed(ObservableValue<? extends Number> observable,
																			Number oldValue,
																			Number newValue)
									{
										cubeScene.getCubeCenterGroup()
												.setTranslateX(
														( newValue.doubleValue() + xOffset ) * ( xMax - xMin )
																/
																( xStageCurSlider.getSlider().getMax() + xOffset )
																							+ xMin);
									}
								});

		cubeScene.getCubeCenterGroup()
				.setTranslateX(
						( xStageCurSlider.getSlider().getValue() + xOffset ) * ( xMax - xMin )
								/
								( xStageCurSlider.getSlider().getMax() + xOffset )
								+ xMin );

		final HBox xStage = new HBox(5);
		xStage.getChildren().addAll(xStageLabel,
				xStageVariableSlider,
				xStageCurSlider );
		HBox.setHgrow(xStage, Priority.ALWAYS);

		// Y-Stage
		final Label yStageLabel = new Label("Stage Y (microns)");

		double yMin = cubeScene.getCubeCenterGroup().getTranslateY();
		double yMax = cubeScene.getCubeCenterGroup().getTranslateY() + CubeScene.VIEWPORT_SIZE
									- cubeBB.getMaxY()
									* 2.2;

		double yOffset = yStageCurSlider.getSlider().getMin() * Math.signum( yStageCurSlider.getSlider().getMin() );

		yStageCurSlider.getSlider().valueProperty()
								.addListener( new ChangeListener< Number >()
								{
									@Override
									public void changed( ObservableValue< ? extends Number > observable,
											Number oldValue,
											Number newValue )
									{
										cubeScene.getCubeCenterGroup()
												.setTranslateY(
														( newValue.doubleValue() + yOffset ) * ( yMax - yMin )
																/
																( yStageCurSlider.getSlider().getMax() + yOffset )
																+ yMin );
									}
								} );

		cubeScene.getCubeCenterGroup()
				.setTranslateY(
						( yStageCurSlider.getSlider().getValue() + yOffset ) * ( yMax - yMin )
								/
								( yStageCurSlider.getSlider().getMax() + yOffset )
								+ yMin );

		final HBox yStage = new HBox(5);
		yStage.getChildren().addAll(yStageLabel,
				yStageVariableSlider,
				yStageCurSlider );
		HBox.setHgrow(yStage, Priority.ALWAYS);

		// Z-Stage
		final Label zStageLabel = new Label("Stage Z (microns)");

		double zMin = cubeScene.getCubeCenterGroup().getTranslateZ();
		double zMax = cubeScene.getCubeCenterGroup().getTranslateZ() + CubeScene.VIEWPORT_SIZE
									- cubeBB.getMaxZ()
									* 2.2;

		double zOffset = zStageCurSlider.getSlider().getMin() * Math.signum( zStageCurSlider.getSlider().getMin() );

		zStageCurSlider.getSlider().valueProperty()
								.addListener(new ChangeListener<Number>()
								{
									@Override
									public void changed(ObservableValue<? extends Number> observable,
																			Number oldValue,
																			Number newValue)
									{
										cubeScene.getCubeCenterGroup()
												.setTranslateZ(
														( newValue.doubleValue() + zOffset ) * ( zMax - zMin )
																/
																( zStageCurSlider.getSlider().getMax() + zOffset )
																							+ zMin);
									}
								});

		cubeScene.getCubeCenterGroup()
				.setTranslateZ(
						( zStageCurSlider.getSlider().getValue() + zOffset ) * ( zMax - zMin )
								/
								( zStageCurSlider.getSlider().getMax() + zOffset )
								+ zMin );

		final HBox zStage = new HBox(5);
		zStage.getChildren().addAll(zStageLabel,
				zStageVariableSlider,
				zStageCurSlider );
		HBox.setHgrow(zStage, Priority.ALWAYS);

		//		CheckBox rotate = new CheckBox("Rotate");
		//		rotate.selectedProperty().addListener(observable -> {
		//			if (rotate.isSelected())
		//			{
		//				cubeScene.setRotationSpeed(20);
		//			}
		//			else
		//			{
		//				cubeScene.stopCubeRotation();
		//			}
		//		});

		VBox controls = new VBox(	10,
				//rotate,
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
