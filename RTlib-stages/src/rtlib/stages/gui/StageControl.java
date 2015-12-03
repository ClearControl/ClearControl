package rtlib.stages.gui;

import javafx.application.Application;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import rtlib.stages.gui.controls.RadianIndicator;

/**
 * Created by moon on 12/1/15.
 */
public class StageControl extends Application
{
	private GridPane grid;
	public static void main( String[] args )
	{
		launch( args );
	}

	@Override public void init()
	{
		grid = new GridPane();
		grid.setPadding( new Insets( 10, 10, 10, 10 ) );
		grid.setVgap( 10 );
		grid.setHgap( 70 );

		// never size the gridpane larger than its preferred size:
		grid.setMaxSize( Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE );

		// R-Stage
		final Label caption = new Label( "Sample Stage R (micro-degree)" );
		GridPane.setConstraints(caption, 0, 0);
		grid.getChildren().add( caption );

		final Slider slider = new Slider();
		slider.setMin( 0 );
		slider.setMax( 360 );
		slider.setMajorTickUnit( 90 );
		slider.setShowTickMarks( true );
		slider.setShowTickLabels( true );

		final RadianIndicator pi = new RadianIndicator( 0 );

		slider.valueProperty().addListener(
				( ObservableValue<? extends Number> ov, Number old_val, Number new_val ) ->
						pi.setProgress( new_val.doubleValue() ) );

		GridPane.setConstraints( slider, 1, 0 );
		grid.getChildren().add( slider );

		GridPane.setConstraints( pi, 2, 0 );
		grid.getChildren().add( pi );


		// X-Stage
		final Label xStageLabel = new Label("Sample Stage X (microns)");
		GridPane.setConstraints( xStageLabel, 0, 1 );
		grid.getChildren().add( xStageLabel );

		final Slider xStageSlider = createSlider(10000, "X-axis stage control");
		GridPane.setConstraints( xStageSlider, 1, 1 );
		grid.getChildren().add( xStageSlider );

		final Label xStageValue = new Label( "-10000" );
		GridPane.setConstraints( xStageValue, 2, 1 );
		grid.getChildren().add( xStageValue );

		xStageSlider.valueProperty().addListener(
				( ov, old_val, new_val ) -> xStageValue.setText(String.format("%d", new_val.intValue())) );
		xStageValue.setText( Double.toString( xStageSlider.getValue() ) );

		// Y-Stage
		final Label yStageLabel = new Label("Sample Stage Y (microns)");
		GridPane.setConstraints( yStageLabel, 0, 2 );
		grid.getChildren().add( yStageLabel );

		final Slider yStageSlider = createSlider(10000, "Y-axis stage control");
		GridPane.setConstraints( yStageSlider, 1, 2 );
		grid.getChildren().add( yStageSlider );

		final Label yStageValue = new Label( Double.toString( yStageSlider.getValue() ) );
		GridPane.setConstraints( yStageValue, 2, 2 );
		grid.getChildren().add( yStageValue );

		yStageSlider.valueProperty().addListener(
				( ov, old_val, new_val ) -> yStageValue.setText(String.format("%d", new_val.intValue())) );

		// Z-Stage
		final Label zStageLabel = new Label("Sample Stage Z (microns)");
		GridPane.setConstraints( zStageLabel, 0, 3 );
		grid.getChildren().add( zStageLabel );

		final Slider zStageSlider = createSlider(10000, "Z-axis stage control");
		GridPane.setConstraints( zStageSlider, 1, 3 );
		grid.getChildren().add( zStageSlider );

		final Label zStageValue = new Label( Double.toString( zStageSlider.getValue() ) );
		zStageValue.setMaxWidth( 500 );
		GridPane.setConstraints( zStageValue, 2, 3 );
		grid.getChildren().add( zStageValue );

		zStageSlider.valueProperty().addListener(
				( ov, old_val, new_val ) -> zStageValue.setText(String.format("%d", new_val.intValue())) );

		// display a crosshair to mark the current pivot point.
		final Line verticalLine   = new Line(0, -10, 0, 10); verticalLine.setStroke(Color.FIREBRICK);   verticalLine.setStrokeWidth(3);   verticalLine.setStrokeLineCap(StrokeLineCap.ROUND);
		final Line horizontalLine = new Line(-10, 0, 10, 0); horizontalLine.setStroke(Color.FIREBRICK); horizontalLine.setStrokeWidth(3); verticalLine.setStrokeLineCap(StrokeLineCap.ROUND);
		Group pivotMarker = new Group(verticalLine, horizontalLine);
		pivotMarker.translateXProperty().bind(xStageSlider.valueProperty().divide( 500 ));
		pivotMarker.translateYProperty().bind(yStageSlider.valueProperty().divide( 500 ) );

		// display a dashed square border outline to mark the original location of the square.
		final Rectangle squareOutline = new Rectangle(100, 100);
		squareOutline.setFill(Color.TRANSPARENT);
		squareOutline.setOpacity(0.7);
		squareOutline.setMouseTransparent(true);
		squareOutline.setStrokeType( StrokeType.INSIDE);
		squareOutline.setStrokeWidth(1);
		squareOutline.setStrokeLineCap( StrokeLineCap.BUTT);
		squareOutline.setStroke(Color.DARKGRAY);
		squareOutline.setStrokeDashOffset(5);
		squareOutline.getStrokeDashArray().add(10.0);

		final StackPane displayPane = new StackPane();
		displayPane.prefHeight( 600 );
		displayPane.getChildren().addAll(pivotMarker, squareOutline);
		//		displayPane.setTranslateY( 50 );
		displayPane.setMouseTransparent(true);
		GridPane.setConstraints( displayPane, 0, 4 );
		GridPane.setColumnSpan( displayPane, 2 );
		grid.getChildren().add( displayPane );


		final Line verticalLineZ   = new Line(0, -10, 0, 10); verticalLineZ.setStroke(Color.FIREBRICK);   verticalLineZ.setStrokeWidth(3);   verticalLineZ.setStrokeLineCap(StrokeLineCap.ROUND);
		final Line horizontalLineZ = new Line(-10, 0, 10, 0); horizontalLineZ.setStroke(Color.FIREBRICK); horizontalLineZ.setStrokeWidth(3); horizontalLineZ.setStrokeLineCap(StrokeLineCap.ROUND);
		// display a crosshair to mark the current pivot point.
		Group pivotMarkerZ = new Group(verticalLineZ, horizontalLineZ);
		pivotMarkerZ.translateYProperty().bind(zStageSlider.valueProperty().divide( 500 ) );

		final Rectangle squareOutlineZ = new Rectangle(60, 100);
		squareOutlineZ.setFill(Color.TRANSPARENT);
		squareOutlineZ.setOpacity(0.7);
		squareOutlineZ.setMouseTransparent(true);
		squareOutlineZ.setStrokeType( StrokeType.INSIDE);
		squareOutlineZ.setStrokeWidth(1);
		squareOutlineZ.setStrokeLineCap( StrokeLineCap.BUTT);
		squareOutlineZ.setStroke(Color.DARKGRAY);
		squareOutlineZ.setStrokeDashOffset(5);
		squareOutlineZ.getStrokeDashArray().add(10.0);

		final StackPane displayPaneZ = new StackPane();
		displayPaneZ.prefHeight( 600 );
		displayPaneZ.getChildren().addAll(pivotMarkerZ, squareOutlineZ);
		displayPaneZ.setMouseTransparent(true);
		GridPane.setConstraints( displayPaneZ, 2, 4 );
		grid.getChildren().add( displayPaneZ );
	}

	@Override
	public void start( Stage stage )
	{
		Scene scene = new Scene( grid );
		stage.setScene( scene );
		stage.setTitle( "Stage Control" );
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

	public GridPane getPanel()
	{
		return grid;
	}
}
