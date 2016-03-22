package rtlib.lasers.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javafx.animation.AnimationTimer;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.component.RunnableFX;
import rtlib.lasers.gui.rbg.RadialBargraph;
import rtlib.lasers.gui.rbg.RadialBargraphBuilder;
import utils.RunFX;
import eu.hansolo.enzo.common.Marker;
import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.onoffswitch.IconSwitch;

/**
 * Laser Gauge Controls
 */
public class LaserGauge implements RunnableFX
{
	private static final Random RND = new Random();

	private Properties mProperties;

	private IconSwitch powerSwitch;
	private IconSwitch laserSwitch;
	private RadialBargraph targetGauge;
	private RadialBargraph actualGauge;

	private VBox properties;
	private HBox pane;

	private long lastTimerCall;
	private AnimationTimer timer;
	private final String waveLength;

	public LaserGauge()
	{
		this("488");
	}

	public LaserGauge(final String waveLength)
	{
		this.waveLength = waveLength;

		try
		{
			mProperties = new Properties();
			InputStream lResourceAsStream = LaserGauge.class.getResourceAsStream("./WavelengthColors.properties");
			mProperties.load(lResourceAsStream);
			lResourceAsStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void init()
	{
		// Power on/off
		powerSwitch = new IconSwitch();
		powerSwitch.setSymbolType(SymbolType.POWER);
		powerSwitch.setSymbolColor(Color.web("#ffffff"));
		powerSwitch.setSwitchColor(Color.web("#34495e"));
		powerSwitch.setThumbColor(Color.web("#ff495e"));

		// Laser on/off
		laserSwitch = new IconSwitch();
		laserSwitch.setSymbolType(SymbolType.BRIGHTNESS);
		laserSwitch.setSymbolColor(Color.web("#ffffff"));
		laserSwitch.setSwitchColor(Color.web("#34495e"));
		laserSwitch.setThumbColor(Color.web("#ff495e"));

		// Gauge bar gradient
		List<Stop> stops = new ArrayList<>();
		stops.add(new Stop(0.0, Color.BLUE));
		stops.add(new Stop(0.31, Color.CYAN));
		stops.add(new Stop(0.5, Color.LIME));
		stops.add(new Stop(0.69, Color.YELLOW));
		stops.add(new Stop(1.0, Color.RED));

		// Target gauge build

		// Marker for user input
		Marker mwMarker = new Marker(0, "mW");
		targetGauge = RadialBargraphBuilder.create()
																				.title("Target")
																				.unit("mW")
																				.markers(mwMarker)
																				.maxValue(50)
																				.build();
		targetGauge.setBarGradientEnabled(true);
		targetGauge.setBarGradient(stops);
		targetGauge.setAnimated(false);
		targetGauge.setInteractive(true);

		// As soon as user changes the target value, it updates gauge value
		targetGauge.valueProperty().bind(mwMarker.valueProperty());

		// Actual gauge build
		actualGauge = RadialBargraphBuilder.create()
																				.title("Actual")
																				.unit("mW")
																				.maxValue(50)
																				.build();
		actualGauge.setAnimated(false);
		actualGauge.setBarGradientEnabled(true);
		actualGauge.setBarGradient(stops);
		actualGauge.setDisable(true);

		// Laser name with Wavelength
		properties = new VBox();
		properties.setPadding(new Insets(10));
		properties.setSpacing(3);

		Label laserLabel = new Label();
		laserLabel.setText(waveLength + " nm");
		laserLabel.setFont(new Font("Arial Black", 22));

		properties.getChildren().add(laserLabel);

		pane = new HBox();

		VBox rec = new VBox();
		rec.setBackground(new Background(new BackgroundFill(Color.web(getWebColorString(waveLength)),
																												CornerRadii.EMPTY,
																												Insets.EMPTY)));
		Rectangle rectangle = new Rectangle(60, 80, Color.TRANSPARENT);
		rec.getChildren().add(rectangle);

		VBox vBox = new VBox();
		vBox.setPadding(new Insets(10, 10, 10, 10));

		// vBox.setBackground(new Background(new BackgroundFill( Color.web(
		// WavelengthColors.getWebColorString( waveLength ) ), CornerRadii.EMPTY,
		// Insets.EMPTY)));
		vBox.setSpacing(8);
		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().addAll(properties, powerSwitch, laserSwitch);

		pane.getChildren().addAll(rec, vBox);

		lastTimerCall = System.nanoTime() + 2_000_000_000l;
		timer = new AnimationTimer()
		{
			@Override
			public void handle(long now)
			{
				if (now > lastTimerCall + 500_000_000l)
				{

					double v = (2 * RND.nextDouble() - 1);
					// v = (v > 0.5)? v * 0.05 + 1.0d : v * -0.05 + 1.0d;

					actualGauge.setValue(mwMarker.getValue() + v);
					lastTimerCall = now;
				}
			}
		};
	}

	public HBox getPanel()
	{
		HBox hBox = new HBox();
		hBox.setBackground(null);
		hBox.setPadding(new Insets(15, 15, 15, 15));
		hBox.setSpacing(10);
		hBox.getChildren().addAll(pane, targetGauge, actualGauge);
		hBox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;"
									+ "-fx-border-color: black");

		timer.start();

		return hBox;
	}

	@Override
	public void start(Stage stage)
	{
		HBox pane = getPanel();

		Scene scene = new Scene(pane, Color.WHITE);

		// scene.setFullScreen(true);

		stage.setTitle("Laser-1");
		stage.setScene(scene);
		stage.show();

		timer.start();
	}

	@Override
	public void stop()
	{

	}

	public static void main(final String[] args)
	{
		RunFX.start(new LaserGauge());
	}

	public String getWebColorString(String wavelength)
	{
		return mProperties.getProperty(wavelength);
	}

	public java.awt.Color getWavelengthColor(String wavelength)
	{
		return java.awt.Color.decode(mProperties.getProperty(wavelength));
	}
}
