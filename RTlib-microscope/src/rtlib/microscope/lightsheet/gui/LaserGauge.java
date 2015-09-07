package rtlib.microscope.lightsheet.gui;

import eu.hansolo.enzo.gauge.RadialBargraph;
import eu.hansolo.enzo.gauge.RadialBargraphBuilder;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import eu.hansolo.enzo.common.Marker;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Laser Guage Controls
 */
public class LaserGauge extends Application
{
	private static final Random RND       = new Random();
	private static int          noOfNodes = 0;
	private RadialBargraph mwControl;
	private RadialBargraph percentControl;

	private VBox properties;

	private long                lastTimerCall;
	private AnimationTimer timer;


	@Override public void init() {
		Marker mwMarker = new Marker(0, "Target Power");
		Marker percentMarker = new Marker(0, "Target Power");

		mwControl = RadialBargraphBuilder.create()
				.title("Laser(mW)")
				.unit("mW")
				.markers(mwMarker)
				.maxValue( 50 )
				.build();
		mwControl.setBarGradientEnabled(true);

		List<Stop> stops = new ArrayList<>();
		stops.add(new Stop(0.0, Color.BLUE));
		stops.add(new Stop(0.31, Color.CYAN));
		stops.add(new Stop(0.5, Color.LIME));
		stops.add(new Stop(0.69, Color.YELLOW));
		stops.add(new Stop(1.0, Color.RED));
		mwControl.setBarGradient(stops);

		percentControl = RadialBargraphBuilder.create()
				.title( "Laser(%)" )
				.unit( "%" )
				.markers( percentMarker )
				.build();
		percentControl.setBarGradientEnabled(true);

		percentControl.setBarGradient(stops);

		// Bind bidirectional for interactive property with two controls
		mwControl.interactiveProperty().bindBidirectional( percentControl.interactiveProperty() );

		// Each marker value changes the other control value as well
		// mW --> %
		mwMarker.valueProperty().addListener( ( observable, oldValue, newValue ) -> {
			if(percentMarker.getValue() != newValue.doubleValue() * 2.0)
			{
				percentMarker.setValue( newValue.doubleValue() * 2.0 );
				Rotate rotate = mwControl.getMarkers().get( mwMarker );
				percentControl.getMarkers().get( percentMarker ).setAngle( rotate.getAngle() );
			}
		} );

		// % --> mW
		percentMarker.valueProperty().addListener( ( observable, oldValue, newValue ) -> {
			if(mwMarker.getValue() != newValue.doubleValue() / 2.0)
			{
				mwMarker.setValue( newValue.doubleValue() / 2.0 );
				Rotate rotate = percentControl.getMarkers().get( percentMarker );
				mwControl.getMarkers().get( mwMarker ).setAngle( rotate.getAngle() );
			}
		} );


		properties = new VBox();
		properties.setPadding( new Insets( 10 ) );
		properties.setSpacing( 8 );

		Text text = new Text();
		Bindings.bindBidirectional(text.textProperty(), mwMarker.valueProperty(), new StringConverter<Number>()
		{
			@Override public String toString( Number object )
			{
				return "Target: " + object.doubleValue() + " mW";
			}

			@Override public Number fromString( String string )
			{
				return null;
			}
		});

		properties.getChildren().add(text);

		lastTimerCall = System.nanoTime() + 2_000_000_000l;
		timer = new AnimationTimer() {
			@Override public void handle(long now) {
				if (now > lastTimerCall + 5_000_000_000l) {
					final double v = RND.nextDouble();
					mwControl.setValue(v * 50);
					percentControl.setValue(v * 100);
					lastTimerCall = now;
				}
			}
		};
	}

	public HBox getPanel()
	{
		HBox pane = new HBox();
		pane.setBackground(null);
		pane.setPadding(new Insets(15, 15, 15, 15));
		pane.setSpacing(10);
		pane.getChildren().addAll(mwControl, percentControl, properties);

		timer.start();

		return pane;
	}

	@Override public void start(Stage stage) throws Exception {
		HBox pane = getPanel();

		Scene scene = new Scene(pane, Color.WHITE);

		//scene.setFullScreen(true);

		stage.setTitle("Laser-1");
		stage.setScene(scene);
		stage.show();

		timer.start();

		calcNoOfNodes(scene.getRoot());
		System.out.println(noOfNodes + " Nodes in SceneGraph");
	}

	@Override public void stop() {

	}

	public static void main(final String[] args) {
		Application.launch(args);
	}


	// ******************** Misc **********************************************
	private static void calcNoOfNodes(Node node) {
		if (node instanceof Parent) {
			if (((Parent) node).getChildrenUnmodifiable().size() != 0) {
				ObservableList<Node> tempChildren = ((Parent) node).getChildrenUnmodifiable();
				noOfNodes += tempChildren.size();
				for (Node n : tempChildren) {
					calcNoOfNodes(n);
					//System.out.println(n.getStyleClass().toString());
				}
			}
		}
	}
}
