package rtlib.lasers.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.hansolo.enzo.common.Marker;
import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.gauge.RadialBargraph;
import eu.hansolo.enzo.gauge.RadialBargraphBuilder;
import eu.hansolo.enzo.onoffswitch.IconSwitch;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import window.util.WavelengthColors;

/**
 * Laser Gauge Controls
 */
public class LaserGauge extends Application
{
	private static final Random RND = new Random();
	private IconSwitch powerSwitch;
	private IconSwitch laserSwitch;
	private RadialBargraph targetGauge;
	private RadialBargraph actualGauge;

	private VBox properties;
	private VBox pane;

	private long lastTimerCall;
	private AnimationTimer timer;
	private final String waveLength;

	public LaserGauge()
	{
		this.waveLength = "488";
	}

	public LaserGauge( final String waveLength )
	{
		this.waveLength = waveLength;
	}

	@Override public void init() {
		// Power on/off
		powerSwitch = new IconSwitch();
		powerSwitch.setSymbolType( SymbolType.POWER );
		powerSwitch.setSymbolColor( Color.web("#ffffff") );
		powerSwitch.setSwitchColor( Color.web("#34495e") );
		powerSwitch.setThumbColor( Color.web("#ff495e") );

		// Laser on/off
		laserSwitch = new IconSwitch();
		laserSwitch.setSymbolType( SymbolType.BRIGHTNESS );
		laserSwitch.setSymbolColor( Color.web("#ffffff") );
		laserSwitch.setSwitchColor( Color.web("#34495e") );
		laserSwitch.setThumbColor( Color.web("#ff495e") );

		// Gauge bar gradient
		List<Stop> stops = new ArrayList<>();
		stops.add(new Stop(0.0, Color.BLUE));
		stops.add(new Stop(0.31, Color.CYAN));
		stops.add(new Stop(0.5, Color.LIME));
		stops.add(new Stop(0.69, Color.YELLOW));
		stops.add( new Stop( 1.0, Color.RED ) );

		// Target gauge build

		// Marker for user input
		Marker mwMarker = new Marker(0, "mW");
		targetGauge = RadialBargraphBuilder.create()
				.title("Target")
				.unit("mW")
				.markers(mwMarker)
				.maxValue( 50 )
				.build();
		targetGauge.setBarGradientEnabled( true );
		targetGauge.setBarGradient( stops );

		// Actual gauge build
		actualGauge = RadialBargraphBuilder.create()
				.title( "Actual" )
				.unit( "mW" )
				.maxValue( 50 )
				.build();
		actualGauge.setBarGradientEnabled( true );
		actualGauge.setBarGradient( stops );
		actualGauge.setDisable( true );

		// Laser name with Wavelength
		properties = new VBox();
		properties.setPadding( new Insets( 10 ) );
		properties.setSpacing( 3 );

		Label laserLabel = new Label();
		laserLabel.setText( waveLength + " nm" );
		laserLabel.setFont( new Font( "Arial Black", 22 ) );

		properties.getChildren().add( laserLabel );

		pane = new VBox();
		pane.setPadding( new Insets( 10, 10, 10, 10 ) );

		pane.setBackground(new Background(new BackgroundFill( Color.web( WavelengthColors.getWebColorString( waveLength ) ), CornerRadii.EMPTY, Insets.EMPTY)));
		pane.setSpacing( 8 );
		pane.setAlignment( Pos.CENTER );
		pane.getChildren().addAll( properties, powerSwitch, laserSwitch );

		// As soon as user changes the target value, it updates gauge value
		targetGauge.interactiveProperty().addListener( ( observable, oldValue, newValue ) -> {
			if(!newValue.booleanValue())
			{
				targetGauge.setValue( mwMarker.getValue() );
			}
		} );

		lastTimerCall = System.nanoTime() + 2_000_000_000l;
		timer = new AnimationTimer() {
			@Override public void handle(long now) {
				if (now > lastTimerCall + 5_000_000_000l) {

					double v = RND.nextDouble();
					v = (v > 0.5)? v * 0.05 + 1.0d : v * -0.05 + 1.0d;

					actualGauge.setValue( mwMarker.getValue() * v );
					lastTimerCall = now;
				}
			}
		};
	}

	public HBox getPanel()
	{
		HBox hBox = new HBox();
		hBox.setBackground( null );
		hBox.setPadding( new Insets( 15, 15, 15, 15 ) );
		hBox.setSpacing( 10 );
		hBox.getChildren().addAll( pane, targetGauge, actualGauge );
		hBox.setStyle( "-fx-border-style: solid;"
				+ "-fx-border-width: 1;"
				+ "-fx-border-color: black" );

		timer.start();

		return hBox;
	}

	@Override public void start(Stage stage) throws Exception {
		HBox pane = getPanel();

		Scene scene = new Scene(pane, Color.WHITE);

		//scene.setFullScreen(true);

		stage.setTitle("Laser-1");
		stage.setScene(scene);
		stage.show();

		timer.start();
	}

	@Override public void stop() {

	}

	public static void main(final String[] args) {
		Application.launch(args);
	}
}
