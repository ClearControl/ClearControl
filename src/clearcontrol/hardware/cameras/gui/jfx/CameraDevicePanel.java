package clearcontrol.hardware.cameras.gui.jfx;

import java.util.Arrays;

import clearcontrol.gui.variable.JFXSimpleLongPropertyVariable;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * CameraDeviceGUI
 */
public class CameraDevicePanel extends AnchorPane
{
	final int mMainRectangleSize = 300;

	float mMaxCameraWidth = 2048;
	float mMaxCameraHeight = 2048;

	// String properties hold the actual dimension size for the capture resolution
	private StringProperty mCameraWidthStringProperty,
			mCameraHeightStringProperty;

	// Double properties hold pixel based values for the rectangle's width and
	// height
	private DoubleProperty mRectangleWidthProperty,
			mRectangleHeightProperty;

	private SimpleLongProperty mCameraWidthProperty,
			mCameraHeightProperty;

	private GridPane mGridPane;

	Rectangle mRect = createDraggableRectangle(37.5, 37.5);
	Line mHLine, mVLine;
	Text mHText, mVText;

	public CameraDevicePanel(StackCameraDeviceInterface pCameraDeviceInterface)
	{
		init();

		Bindings.bindBidirectional(	mCameraWidthStringProperty,
				mCameraWidthProperty,
				new StringConverter<Number>()
				{
					@Override
					public String toString(Number object)
					{
						return Long.toString(object.longValue());
					}

					@Override
					public Number fromString(String string)
					{
						return Long.parseLong(string);
					}
				});

		Bindings.bindBidirectional(	mCameraHeightStringProperty,
				mCameraHeightProperty,
				new StringConverter<Number>()
				{
					@Override
					public String toString(Number object)
					{
						return Long.toString(object.longValue());
					}

					@Override
					public Number fromString(String string)
					{
						return Long.parseLong(string);
					}
				});

		JFXSimpleLongPropertyVariable lWidthPropertyVariable = new JFXSimpleLongPropertyVariable(	mCameraWidthProperty,
																																															"WidthPropertyVariable",
																																															0L);

		pCameraDeviceInterface.getStackWidthVariable()
													.syncWith(lWidthPropertyVariable);

		JFXSimpleLongPropertyVariable lHeightWidthPropertyVariable = new JFXSimpleLongPropertyVariable(	mCameraHeightProperty,
																																																		"HeightPropertyVariable",
																																																		0L);

		pCameraDeviceInterface.getStackHeightVariable()
													.syncWith(lHeightWidthPropertyVariable);

	}

	private void init()
	{
		// Setting up the double properties with 256x256
		mRectangleWidthProperty = new SimpleDoubleProperty(37.5);
		mRectangleHeightProperty = new SimpleDoubleProperty(37.5);

		mCameraWidthProperty = new SimpleLongProperty(256L);
		mCameraHeightProperty = new SimpleLongProperty(256L);

		mGridPane = new GridPane();

		for (int x = 7; x < 11; x++)
		{
			for (int y = 7; y < 11; y++)
			{
				int width = 2 << x;
				int height = 2 << y;

				Button button = new Button(height + "\n" + width);
				button.setMaxWidth(Double.MAX_VALUE);
				button.setOnAction(event -> {
					mRectangleWidthProperty.set(width * mMainRectangleSize
																			/ mMaxCameraWidth);
					mRectangleHeightProperty.set(height * mMainRectangleSize
																				/ mMaxCameraHeight);

					mCameraWidthStringProperty.set(Integer.toString(width));
					mCameraHeightStringProperty.set(Integer.toString(height));

					// System.out.println( "Set width/height: " + width + "/" + height );
				});

				// Place the button on the GridPane
				mGridPane.add( button, x, y );
			}
		}

		Pane canvas = new Pane();
		canvas.setStyle("-fx-background-color: green;");
		canvas.setPrefSize(mMainRectangleSize, mMainRectangleSize);

		Line line = new Line(	mMainRectangleSize / 2,
				0,
				mMainRectangleSize / 2,
				mMainRectangleSize);
		canvas.getChildren().add(line);

		line = new Line(0,
				mMainRectangleSize / 2,
				mMainRectangleSize,
				mMainRectangleSize / 2);
		canvas.getChildren().add(line);

		canvas.getChildren().addAll( mRect );

		HBox widthBox = new HBox(5);
		widthBox.setPadding(new Insets(30, 10, 10, 10));
		widthBox.setAlignment(Pos.CENTER);
		widthBox.getChildren().add(new Label("Width: "));
		TextField width = new TextField(){
			@Override public void replaceText(int start, int end, String text) {
				// If the replaced text would end up being invalid, then simply
				// ignore this call!
				if (text.matches("[0-9]*")) {

					String replaced = checkNewString( getText(), start, end, text );
					if(isLessThanMaxValue( mMaxCameraWidth, replaced ))
						super.replaceText(start, end, text);
				}
			}

			@Override public void replaceSelection(String text) {
				if (text.matches("[0-9]*")) {
					if(isLessThanMaxValue( mMaxCameraWidth, text ))
						super.replaceSelection( text );
				}
			}
		};

		width.setPrefWidth(80);
		mCameraWidthStringProperty = width.textProperty();
		widthBox.getChildren().add(width);

		HBox heightBox = new HBox(5);
		heightBox.setPadding(new Insets(10, 10, 10, 10));
		heightBox.setAlignment(Pos.CENTER);
		heightBox.getChildren().add(new Label("Height: "));
		TextField height = new TextField(){
			@Override public void replaceText(int start, int end, String text) {
				// If the replaced text would end up being invalid, then simply
				// ignore this call!
				if (text.matches("[0-9]*")) {

					String replaced = checkNewString( getText(), start, end, text );
					if(isLessThanMaxValue( mMaxCameraHeight, replaced ))
						super.replaceText( start, end, text );
				}
			}

			@Override public void replaceSelection(String text) {
				if (text.matches("[0-9]*")) {
					if(isLessThanMaxValue( mMaxCameraHeight, text ))
						super.replaceSelection( text );
				}
			}
		};

		height.setPrefWidth(80);
		mCameraHeightStringProperty = height.textProperty();
		heightBox.getChildren().add(height);

		VBox vBox = new VBox( mGridPane, widthBox, heightBox);

		setBackground( null );
		setPadding( new Insets( 15, 15, 15, 15 ) );
		getChildren().addAll( vBox, canvas );

		AnchorPane.setLeftAnchor( vBox, 3d );
		AnchorPane.setTopAnchor( vBox, 10d );

		AnchorPane.setLeftAnchor( canvas, 220d );
		AnchorPane.setTopAnchor(canvas, 10d);

		setStyle( "-fx-border-style: solid;" + "-fx-border-width: 1;"
				+ "-fx-border-color: grey" );

		mRectangleWidthProperty.addListener( new ChangeListener< Number >()
		{
			@Override public void changed( ObservableValue< ? extends Number > observable, Number oldValue, Number newValue )
			{
				mRect.widthProperty().set( newValue.doubleValue() );
			}
		} );

		mRectangleHeightProperty.addListener( new ChangeListener< Number >()
		{
			@Override public void changed( ObservableValue< ? extends Number > observable, Number oldValue, Number newValue )
			{
				mRect.heightProperty().set( newValue.doubleValue() );
			}
		} );
//		mRectangleWidthProperty.bind( mRect.widthProperty() );
//		mRectangleHeightProperty.bind( mRect.heightProperty() );

		Bindings.bindBidirectional(	mCameraWidthStringProperty,
				mRectangleWidthProperty,
				new StringConverter<Number>()
				{
					@Override
					public String toString(Number object)
					{
						return Integer.toString((int) Math.round(object.doubleValue() * mMaxCameraWidth
								/ mMainRectangleSize));
					}

					@Override
					public Number fromString(String string)
					{
						return Double.parseDouble(string) * mMainRectangleSize
								/ mMaxCameraWidth;
					}
				});

		Bindings.bindBidirectional(	mCameraHeightStringProperty,
				mRectangleHeightProperty,
				new StringConverter<Number>()
				{
					@Override
					public String toString(Number object)
					{
						return Integer.toString((int) Math.round(object.doubleValue() * mMaxCameraHeight
								/ mMainRectangleSize));
					}

					@Override
					public Number fromString(String string)
					{
						return Double.parseDouble(string) * mMainRectangleSize
								/ mMaxCameraHeight;
					}
				});
	}

	private static boolean isLessThanMaxValue(final float max, final String text)
	{
		Float lValue = Float.parseFloat( text );
		return lValue <= max;
	}

	private String checkNewString(String oldText, int start, int end, String text)
	{
		String newString = oldText.substring( 0, start );
		newString += text;
		newString += oldText.substring( start, oldText.length() );
		return newString;
	}

	private void setDragHandlers(	final Line line,
																final Rectangle rect,
																final Cursor cursor,
																Wrapper<Point2D> mouseLocation)
	{
		line.setOnMouseEntered(mouseEvent -> line.setCursor(cursor));

		line.setOnMouseDragged(event -> {
			if (cursor == Cursor.V_RESIZE)
			{
				System.out.println(event.getSceneY());
			}
			else if (cursor == Cursor.H_RESIZE)
			{
				System.out.println(event.getSceneX());
			}
		});

		line.setOnMousePressed( event ->
				mouseLocation.value = new Point2D(event.getSceneX(),
						event.getSceneY())
		);

		line.setOnMouseReleased(event -> {
			mRectangleHeightProperty.set( mRect.heightProperty().get() );
			mRectangleWidthProperty.set( mRect.widthProperty().get() );
			mouseLocation.value = null;
			line.setCursor(Cursor.NONE);
		});
	}

	private Rectangle createDraggableRectangle(	double width,
																							double height)
	{
		double x = mMainRectangleSize / 2 - width / 2;
		double y = mMainRectangleSize / 2 - height / 2;

		Rectangle rect = new Rectangle(x, y, width, height);

		rect.heightProperty()
				.addListener((observable, oldValue, newValue) -> rect.setY(mMainRectangleSize / 2
																																		- newValue.intValue()
																																		/ 2));

		rect.widthProperty()
				.addListener((observable, oldValue, newValue) -> rect.setX(mMainRectangleSize / 2
																																		- newValue.intValue()
																																		/ 2));

		rect.setFill(Color.color(0, 0, 0, 0.50));

		Wrapper<Point2D> mouseLocation = new Wrapper<>();

		mHText = new Text();
		mHText.setStroke( Color.WHITE );
		mHText.textProperty()
					.bind( rect.widthProperty()
							.multiply( mMaxCameraWidth / mMainRectangleSize )
							.asString( "%.0f px" ) );
		mHText.translateXProperty().bind(rect.xProperty()
																				.add(rect.widthProperty()
																									.divide(2.5)));
		mHText.translateYProperty().bind(rect.yProperty()
																				.add(rect.heightProperty()
																									.subtract(13)));

		mHLine = new Line(x, y, x + width, y);
		mHLine.setStrokeWidth( 5 );
		mHLine.setStroke( Color.WHITE );
		setDragHandlers( mHLine, rect, Cursor.V_RESIZE, mouseLocation );

		mHLine.startXProperty().bind(rect.xProperty());
		mHLine.startYProperty().bind(rect.yProperty()
																		.add( rect.heightProperty() ));
		mHLine.endYProperty().bind(rect.yProperty()
																	.add(rect.heightProperty()));
		mHLine.endXProperty().bind(rect.xProperty()
																	.add(rect.widthProperty()));

		mHLine.setOnMouseDragged( event -> {
			if ( mouseLocation.value != null )
			{
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newMaxY = rect.getY() + rect.getHeight() + deltaY;
				double newValue = rect.getHeight() + deltaY * 2;
				if ( newValue > 0 && newMaxY >= rect.getY() && newMaxY <= mMainRectangleSize )
				{
					rect.setHeight( newValue );
				}
				mouseLocation.value = new Point2D( event.getSceneX(),
						event.getSceneY() );
			}
		} );

		mVText = new Text();
		mVText.setStroke( Color.WHITE );
		mVText.setTranslateX( 7 );
		mVText.textProperty()
					.bind( rect.heightProperty()
							.multiply( mMaxCameraHeight / mMainRectangleSize )
							.asString( "%.0f px" ) );
		mVText.translateXProperty().bind(rect.xProperty()
																				.add(rect.widthProperty()
																									.subtract(55)));
		mVText.translateYProperty().bind(rect.yProperty()
																				.add(rect.heightProperty()
																									.divide(2)));

		mVLine = new Line(x + width, y, x + width, y + height);
		mVLine.setStrokeWidth( 5 );
		mVLine.setStroke( Color.WHITE );
		setDragHandlers( mVLine, rect, Cursor.H_RESIZE, mouseLocation );

		mVLine.startXProperty().bind(rect.xProperty()
																		.add( rect.widthProperty() ));
		mVLine.startYProperty().bind(rect.yProperty());
		mVLine.endXProperty().bind(rect.xProperty()
																	.add(rect.widthProperty()));
		mVLine.endYProperty().bind(rect.yProperty()
																	.add(rect.heightProperty()));

		mVLine.setOnMouseDragged( event -> {
			if ( mouseLocation.value != null )
			{
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double newMaxX = rect.getX() + rect.getWidth() + deltaX;
				double newValue = rect.getWidth() + deltaX * 2;
				if ( newValue > 0 && newMaxX >= rect.getX() && newMaxX <= mMainRectangleSize )
				{
					rect.setWidth( newValue );
				}
				mouseLocation.value = new Point2D( event.getSceneX(),
						event.getSceneY() );
			}
		} );

		// force controls to live in same parent as rectangle:
		rect.parentProperty()
				.addListener( ( obs, oldParent, newParent ) -> {
					for ( Node c : Arrays.asList( mHLine, mVLine, mHText, mVText ) )
					{
						Pane currentParent = ( Pane ) c.getParent();
						if ( currentParent != null )
						{
							currentParent.getChildren().remove( c );
						}
						( ( Pane ) newParent ).getChildren().add( c );
					}
				} );

		return rect;
	}

	static class Wrapper<T>
	{
		T value;
	}

}
