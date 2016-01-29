package rtlib.cameras.gui;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.component.RunnableFX;
import utils.RunFX;

import java.util.Arrays;

/**
 * CameraDeviceGUI
 */
public class CameraDeviceGUI implements RunnableFX
{
	final private int resUnit = 256;
	private int expectedWidth, expectedHeight;
	private GridPane gridPane;
	private AnimationTimer timer;
	Rectangle rect = createDraggableRectangle(10, 10, 20, 30);

	public CameraDeviceGUI()
	{

	}

	@Override public void init()
	{
		gridPane = new GridPane();

		for(int x = 8; x < 12; x++)
		{
			for(int y = 8; y < 12; y++)
			{
				int width = (int) Math.pow( 2, x );
				int height = (int) Math.pow( 2, y );

				Button button = new Button( height + "\n" + width );
				button.setMaxWidth( Double.MAX_VALUE );
				button.setOnAction( event -> {
					expectedWidth = width;
					expectedHeight = height;
					System.out.println( "Set width/height: " + width + "/" + height );
				} );

				// Place the button on the GridPane
				gridPane.add( button, x, y );
			}
		}

		timer = new AnimationTimer() {
			@Override public void handle(long now) {
			}
		};
	}

	@Override public void start( Stage stage )
	{
		HBox pane = getPanel();

		Scene scene = new Scene(pane, Color.WHITE);

		//scene.setFullScreen(true);

		stage.setTitle("Camera-1");
		stage.setScene(scene);
		stage.show();
	}

	@Override public void stop()
	{

	}

	public HBox getPanel()
	{
		Pane canvas = new Pane();
		canvas.setStyle( "-fx-background-color: blue;" );
		canvas.setPrefSize( 300, 300 );
		canvas.getChildren().add( rect );

		HBox hBox = new HBox();
		hBox.setBackground( null );
		hBox.setPadding( new Insets( 15, 15, 15, 15 ) );
		hBox.setSpacing( 10 );
		hBox.getChildren().addAll( gridPane, canvas );
		hBox.setStyle( "-fx-border-style: solid;"
				+ "-fx-border-width: 1;"
				+ "-fx-border-color: black" );

		timer.start();

		return hBox;
	}

	private Rectangle createDraggableRectangle(double x, double y, double width, double height) {
		final double handleRadius = 10 ;

		Rectangle rect = new Rectangle(x, y, width, height);

		// top left resize handle:
		Circle resizeHandleNW = new Circle(handleRadius, Color.BLUEVIOLET);
		// bind to top left corner of Rectangle:
		resizeHandleNW.centerXProperty().bind(rect.xProperty().add(20));
		resizeHandleNW.centerYProperty().bind(rect.yProperty());

		// bottom right resize handle:
		Circle resizeHandleSE = new Circle(handleRadius, Color.GOLDENROD);
		// bind to bottom right corner of Rectangle:
		resizeHandleSE.centerXProperty().bind(rect.xProperty().add(rect.widthProperty()));
		resizeHandleSE.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

		// move handle:
		Circle moveHandle = new Circle(handleRadius, Color.CRIMSON);
		// bind to bottom center of Rectangle:
		moveHandle.centerXProperty().bind(rect.xProperty());
		moveHandle.centerYProperty().bind(rect.yProperty());

		// force circles to live in same parent as rectangle:
		rect.parentProperty().addListener((obs, oldParent, newParent) -> {
			for (Circle c : Arrays.asList( resizeHandleNW, resizeHandleSE, moveHandle )) {
				Pane currentParent = (Pane)c.getParent();
				if (currentParent != null) {
					currentParent.getChildren().remove(c);
				}
				((Pane)newParent).getChildren().add(c);
			}
		});

		Wrapper<Point2D > mouseLocation = new Wrapper<>();

		setUpDragging(resizeHandleNW, mouseLocation) ;
		setUpDragging(resizeHandleSE, mouseLocation) ;
		setUpDragging(moveHandle, mouseLocation) ;

		resizeHandleNW.setOnMouseDragged(event -> {
			if (mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newX = rect.getX() + deltaX ;
				if (newX >= handleRadius
						&& newX <= rect.getX() + rect.getWidth() - handleRadius) {
					rect.setX(newX);
					rect.setWidth(rect.getWidth() - deltaX);
				}
				double newY = rect.getY() + deltaY ;
				if (newY >= handleRadius
						&& newY <= rect.getY() + rect.getHeight() - handleRadius) {
					rect.setY(newY);
					rect.setHeight(rect.getHeight() - deltaY);
				}
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});

		resizeHandleSE.setOnMouseDragged(event -> {
			if (mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newMaxX = rect.getX() + rect.getWidth() + deltaX ;
				if (newMaxX >= rect.getX()
						&& newMaxX <= rect.getParent().getBoundsInLocal().getWidth() - handleRadius) {
					rect.setWidth(rect.getWidth() + deltaX);
				}
				double newMaxY = rect.getY() + rect.getHeight() + deltaY ;
				if (newMaxY >= rect.getY()
						&& newMaxY <= rect.getParent().getBoundsInLocal().getHeight() - handleRadius) {
					rect.setHeight(rect.getHeight() + deltaY);
				}
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});

		moveHandle.setOnMouseDragged(event -> {
			if (mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newX = rect.getX() + deltaX ;
				double newMaxX = newX + rect.getWidth();
				if (newX >= handleRadius
						&& newMaxX <= rect.getParent().getBoundsInLocal().getWidth() - handleRadius) {
					rect.setX(newX);
				}
				double newY = rect.getY() + deltaY ;
				double newMaxY = newY + rect.getHeight();
				if (newY >= handleRadius
						&& newMaxY <= rect.getParent().getBoundsInLocal().getHeight() - handleRadius) {
					rect.setY(newY);
				}
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}

		});

		return rect ;
	}

	private void setUpDragging(Circle circle, Wrapper<Point2D> mouseLocation) {

		circle.setOnDragDetected(event -> {
			circle.getParent().setCursor( Cursor.CLOSED_HAND);
			mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
		});

		circle.setOnMouseReleased(event -> {
			circle.getParent().setCursor(Cursor.DEFAULT);
			mouseLocation.value = null ;
		});
	}

	static class Wrapper<T> { T value ; }

	public static void main( final String[] args )
	{
		RunFX.start( new CameraDeviceGUI() );
	}
}
