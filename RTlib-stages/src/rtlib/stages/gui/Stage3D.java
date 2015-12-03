package rtlib.stages.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * Created by moon on 12/3/15.
 */
public class Stage3D  extends Application
{

	public static void main( String[] args )
	{
		launch( args );
	}

	double mousePosX;
	double mousePosY;
	double mouseOldX;
	double mouseOldY;
	double mouseDeltaX;
	double mouseDeltaY;


	final Cam camOffset = new Cam();
	final Cam cam = new Cam();

	final Shear shear = new Shear();

	class Cam extends Group
	{
		Translate t  = new Translate();
		Translate p  = new Translate();
		Translate ip = new Translate();
		Rotate rx = new Rotate();
		{ rx.setAxis(Rotate.X_AXIS); }
		Rotate ry = new Rotate();
		{ ry.setAxis(Rotate.Y_AXIS); }
		Rotate rz = new Rotate();
		{ rz.setAxis(Rotate.Z_AXIS); }
		Scale s = new Scale();
		public Cam() { super(); getTransforms().addAll(t, p, rx, rz, ry, s, ip); }
	}

	@Override
	public void start( Stage stage )
	{
		stage.setTitle( "Stage 3D View" );

		camOffset.getChildren().add(cam);
		resetCam();

		final Scene scene = new Scene(camOffset, 800, 600, true);
		scene.setFill(new RadialGradient(225, 0.85, 300, 300, 500, false,
				CycleMethod.NO_CYCLE, new Stop[]
				{ new Stop(0f, Color.BLUE),
						new Stop(1f, Color.LIGHTBLUE) }));
		scene.setCamera(new PerspectiveCamera());

		Group rectangleGroup = new Group();
		rectangleGroup.getTransforms().add(shear);
		rectangleGroup.setDepthTest( DepthTest.ENABLE);

		double xStart = 260.0;
		double xOffset = 30.0;
		double yPos = 300.0;
		double zPos = 0.0;
		double barWidth = 30.0;
		double barDepth = 30.0;

		// Chamber
		Cube chamber = new Cube(1.0, Color.RED, 1.0);
		chamber.setTranslateX(xStart + 8*xOffset);
		chamber.setTranslateZ(yPos);
		chamber.setScaleX(barWidth);
		chamber.setScaleZ(30.0);
		chamber.setScaleY(barDepth);
		rectangleGroup.getChildren().add( chamber );

		rectangleGroup.setScaleX(2.5);
		rectangleGroup.setScaleY(2.5);
		rectangleGroup.setScaleZ(2.5);
		cam.getChildren().add(rectangleGroup);

		double halfSceneWidth = 375;  // scene.getWidth()/2.0;
		double halfSceneHeight = 275;  // scene.getHeight()/2.0;
		cam.p.setX(halfSceneWidth);
		cam.ip.setX(-halfSceneWidth);
		cam.p.setY(halfSceneHeight);
		cam.ip.setY(-halfSceneHeight);

		frameCam(stage, scene);

		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				mousePosX = me.getX();
				mousePosY = me.getY();
				mouseOldX = me.getX();
				mouseOldY = me.getY();
				//System.out.println("scene.setOnMousePressed " + me);
			}
		});
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getX();
				mousePosY = me.getY();
				mouseDeltaX = mousePosX - mouseOldX;
				mouseDeltaY = mousePosY - mouseOldY;
				if (me.isAltDown() && me.isShiftDown() && me.isPrimaryButtonDown()) {
					double rzAngle = cam.rz.getAngle();
					cam.rz.setAngle(rzAngle - mouseDeltaX);
				}
				else if (me.isAltDown() && me.isPrimaryButtonDown()) {
					double ryAngle = cam.ry.getAngle();
					cam.ry.setAngle(ryAngle - mouseDeltaX);
					double rxAngle = cam.rx.getAngle();
					cam.rx.setAngle(rxAngle + mouseDeltaY);
				}
				else if (me.isShiftDown() && me.isPrimaryButtonDown()) {
					double yShear = shear.getY();
					shear.setY(yShear + mouseDeltaY/1000.0);
					double xShear = shear.getX();
					shear.setX(xShear + mouseDeltaX/1000.0);
				}
				else if (me.isAltDown() && me.isSecondaryButtonDown()) {
					double scale = cam.s.getX();
					double newScale = scale + mouseDeltaX*0.01;
					cam.s.setX(newScale);
					cam.s.setY(newScale);
					cam.s.setZ(newScale);
				}
				else if (me.isAltDown() && me.isMiddleButtonDown()) {
					double tx = cam.t.getX();
					double ty = cam.t.getY();
					cam.t.setX(tx + mouseDeltaX);
					cam.t.setY(ty + mouseDeltaY);
				}
			}
		});
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if (KeyCode.A.equals(ke.getCode())) {
					resetCam();
					shear.setX(0.0);
					shear.setY(0.0);
				}
				if (KeyCode.F.equals(ke.getCode())) {
					frameCam(stage, scene);
					shear.setX(0.0);
					shear.setY(0.0);
				}
				if (KeyCode.SPACE.equals(ke.getCode())) {
					if (stage.isFullScreen()) {
						stage.setFullScreen(false);
						frameCam(stage, scene);
					} else {
						stage.setFullScreen(true);
						frameCam(stage, scene);
					}
				}
			}
		});

		stage.setScene( scene );
		stage.show();
	}

	private Slider createSlider(final double value, final String helpText) {
		final Slider slider = new Slider(value * -1, value, 0);
		slider.setMajorTickUnit(2000);
		slider.setMinorTickCount(0);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.setStyle("-fx-text-fill: white");
		slider.setTooltip(new Tooltip(helpText));
		return slider;
	}

	//=========================================================================
	// CubeSystem.frameCam
	//=========================================================================
	public void frameCam(final Stage stage, final Scene scene) {
		setCamOffset(camOffset, scene);
		// cam.resetTSP();
		setCamPivot(cam);
		setCamTranslate(cam);
		setCamScale(cam, scene);
	}

	//=========================================================================
	// CubeSystem.setCamOffset
	//=========================================================================
	public void setCamOffset(final Cam camOffset, final Scene scene) {
		double width = scene.getWidth();
		double height = scene.getHeight();
		camOffset.t.setX(width/2.0);
		camOffset.t.setY(height/2.0);
	}

	//=========================================================================
	// setCamScale
	//=========================================================================
	public void setCamScale(final Cam cam, final Scene scene) {
		final Bounds bounds = cam.getBoundsInLocal();
		final double pivotX = bounds.getMinX() + bounds.getWidth()/2;
		final double pivotY = bounds.getMinY() + bounds.getHeight()/2;
		final double pivotZ = bounds.getMinZ() + bounds.getDepth()/2;

		double width = scene.getWidth();
		double height = scene.getHeight();

		double scaleFactor = 1.0;
		double scaleFactorY = 1.0;
		double scaleFactorX = 1.0;
		if (bounds.getWidth() > 0.0001) {
			scaleFactorX = width / bounds.getWidth(); // / 2.0;
		}
		if (bounds.getHeight() > 0.0001) {
			scaleFactorY = height / bounds.getHeight(); //  / 1.5;
		}
		if (scaleFactorX > scaleFactorY) {
			scaleFactor = scaleFactorY;
		} else {
			scaleFactor = scaleFactorX;
		}
		cam.s.setX(scaleFactor);
		cam.s.setY(scaleFactor);
		cam.s.setZ(scaleFactor);
	}

	//=========================================================================
	// setCamPivot
	//=========================================================================
	public void setCamPivot(final Cam cam) {
		final Bounds bounds = cam.getBoundsInLocal();
		final double pivotX = bounds.getMinX() + bounds.getWidth()/2;
		final double pivotY = bounds.getMinY() + bounds.getHeight()/2;
		final double pivotZ = bounds.getMinZ() + bounds.getDepth()/2;
		cam.p.setX(pivotX);
		cam.p.setY(pivotY);
		cam.p.setZ(pivotZ);
		cam.ip.setX(-pivotX);
		cam.ip.setY(-pivotY);
		cam.ip.setZ(-pivotZ);
	}

	//=========================================================================
	// setCamTranslate
	//=========================================================================
	public void setCamTranslate(final Cam cam) {
		final Bounds bounds = cam.getBoundsInLocal();
		final double pivotX = bounds.getMinX() + bounds.getWidth()/2;
		final double pivotY = bounds.getMinY() + bounds.getHeight()/2;
		cam.t.setX(-pivotX);
		cam.t.setY(-pivotY);
	}

	public void resetCam() {
		cam.t.setX(0.0);
		cam.t.setY(0.0);
		cam.t.setZ(0.0);
		cam.rx.setAngle(45.0);
		cam.ry.setAngle(-7.0);
		cam.rz.setAngle(0.0);
		cam.s.setX(1.25);
		cam.s.setY(1.25);
		cam.s.setZ(1.25);


		cam.p.setX(0.0);
		cam.p.setY(0.0);
		cam.p.setZ(0.0);

		cam.ip.setX(0.0);
		cam.ip.setY(0.0);
		cam.ip.setZ(0.0);

		final Bounds bounds = cam.getBoundsInLocal();
		final double pivotX = bounds.getMinX() + bounds.getWidth() / 2;
		final double pivotY = bounds.getMinY() + bounds.getHeight() / 2;
		final double pivotZ = bounds.getMinZ() + bounds.getDepth() / 2;

		cam.p.setX(pivotX);
		cam.p.setY(pivotY);
		cam.p.setZ(pivotZ);

		cam.ip.setX(-pivotX);
		cam.ip.setY(-pivotY);
		cam.ip.setZ(-pivotZ);
	}

	public class Cube extends Group {
		final Rotate rx = new Rotate(0, Rotate.X_AXIS);
		final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
		final Rotate rz = new Rotate(0, Rotate.Z_AXIS);
		public Cube(double size, Color color, double shade) {
			getTransforms().addAll(rz, ry, rx);
			getChildren().addAll(
					RectangleBuilder.create() // back face
							.width(size).height(size)
							.fill(color.deriveColor(0.0, 1.0, (1 - 0.5*shade), 1.0))
							.translateX(-0.5*size)
							.translateY(-0.5*size)
							.translateZ(0.5*size)
							.build(),
					RectangleBuilder.create() // bottom face
							.width(size).height(size)
							.fill(color.deriveColor(0.0, 1.0, (1 - 0.4*shade), 1.0))
							.translateX(-0.5*size)
							.translateY(0)
							.rotationAxis(Rotate.X_AXIS)
							.rotate(90)
							.build(),
					RectangleBuilder.create() // right face
							.width(size).height(size)
							.fill(color.deriveColor(0.0, 1.0, (1 - 0.3*shade), 1.0))
							.translateX(-1*size)
							.translateY(-0.5*size)
							.rotationAxis(Rotate.Y_AXIS)
							.rotate(90)
							.build(),
					RectangleBuilder.create() // left face
							.width(size).height(size)
							.fill(color.deriveColor(0.0, 1.0, (1 - 0.2*shade), 1.0))
							.translateX(0)
							.translateY(-0.5*size)
							.rotationAxis(Rotate.Y_AXIS)
							.rotate(90)
							.build(),
					RectangleBuilder.create() // top face
							.width(size).height(size)
							.fill(color.deriveColor(0.0, 1.0, (1 - 0.1*shade), 1.0))
							.translateX(-0.5*size)
							.translateY(-1*size)
							.rotationAxis(Rotate.X_AXIS)
							.rotate(90)
							.build(),
					RectangleBuilder.create() // top face
							.width(size).height(size)
							.fill(color)
							.translateX(-0.5*size)
							.translateY(-0.5*size)
							.translateZ(-0.5*size)
							.build()
			);
		}
	}

}
