package rtlib.lasers.gui.jfx;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import rtlib.gui.jfx.rbg.RadialBargraph;
import rtlib.gui.jfx.rbg.RadialBargraphBuilder;
import eu.hansolo.enzo.common.Marker;
import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.onoffswitch.IconSwitch;

/**
 * Laser Gauge Controls
 */
public class LaserGauge
{
	static private Properties sProperties;
	static
	{
		try
		{
			sProperties = new Properties();
			InputStream lResourceAsStream = LaserGauge.class.getResourceAsStream("./WavelengthColors.properties");
			sProperties.load(lResourceAsStream);
			lResourceAsStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private String mPowerUnits;
	private double mMaxPower;

	private IconSwitch mLaserOnSwitch;
	private RadialBargraph mTargetGauge;
	private RadialBargraph mActualGauge;
	Marker mwMarker;

	private VBox properties;
	private HBox pane;

	private final String waveLength;

	public LaserGauge()
	{
		this("488", "mW", 100);
	}

	public LaserGauge(final String waveLength,
										final String pPowerUnits,
										double pMaxPower)
	{
		this.waveLength = waveLength;
		mPowerUnits = pPowerUnits;
		mMaxPower = pMaxPower;
		init();
	}

	public BooleanProperty getLaserOnBooleanProperty()
	{
		return mLaserOnSwitch.selectedProperty();
	}

	public DoubleProperty getTargetPowerProperty()
	{
		return mwMarker.valueProperty();
	}

	public DoubleProperty getCurrentPowerProperty()
	{
		return mActualGauge.valueProperty();
	}


	private void init()
	{
		// Power on/off
		mLaserOnSwitch = new IconSwitch();

		mLaserOnSwitch.setSymbolType(SymbolType.POWER);
		mLaserOnSwitch.setSymbolColor(Color.web("#ffffff"));
		mLaserOnSwitch.setSwitchColor(Color.web("#34495e"));
		mLaserOnSwitch.setThumbColor(Color.web("#ff495e"));

		// Gauge bar gradient
		List<Stop> stops = new ArrayList<>();
		stops.add(new Stop(0.0, Color.BLUE));
		stops.add(new Stop(0.31, Color.CYAN));
		stops.add(new Stop(0.5, Color.LIME));
		stops.add(new Stop(0.69, Color.YELLOW));
		stops.add(new Stop(1.0, Color.RED));

		// Target gauge build

		// Marker for user input
		mwMarker = new Marker(0, mPowerUnits);
		mTargetGauge = RadialBargraphBuilder.create()
																				.title("Target")
																				.unit(mPowerUnits)
																				.markers(mwMarker)
																				.maxValue(mMaxPower)
																				.build();
		mTargetGauge.setBarGradientEnabled(true);
		mTargetGauge.setBarGradient(stops);
		mTargetGauge.setAnimated(false);
		mTargetGauge.setInteractive(true);

		// As soon as user changes the target value, it updates gauge value
		mTargetGauge.valueProperty().bind(mwMarker.valueProperty());

		// Actual gauge build
		mActualGauge = RadialBargraphBuilder.create()
																				.title("Current")
																				.unit(mPowerUnits)
																				.maxValue(mMaxPower)
																				.build();
		mActualGauge.setAnimated(false);
		mActualGauge.setBarGradientEnabled(true);
		mActualGauge.setBarGradient(stops);
		mActualGauge.setDisable(true);

		// Laser name with Wavelength
		properties = new VBox();
		// properties.setPadding(new Insets(10));
		properties.setSpacing(3);

		Label laserLabel = new Label();
		laserLabel.setText(waveLength + " nm");
		laserLabel.setFont(new Font("Arial Black", 22));

		properties.getChildren().add(laserLabel);

		pane = new HBox();

		VBox lVBoxForColoredRectangle = new VBox();
		lVBoxForColoredRectangle.setBackground(new Background(new BackgroundFill(Color.web(getWebColorString(waveLength)),
																												CornerRadii.EMPTY,
																												Insets.EMPTY)));
		Rectangle rectangle = new Rectangle(20, 80, Color.TRANSPARENT);
		lVBoxForColoredRectangle.getChildren().add(rectangle);

		VBox vBox = new VBox();
		// vBox.setPadding(new Insets(10, 10, 10, 10));

		// vBox.setBackground(new Background(new BackgroundFill( Color.web(
		// WavelengthColors.getWebColorString( waveLength ) ), CornerRadii.EMPTY,
		// Insets.EMPTY)));
		vBox.setSpacing(8);
		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().addAll(properties, mLaserOnSwitch);

		pane.getChildren().addAll(lVBoxForColoredRectangle, vBox);


	}

	public HBox getPanel()
	{
		HBox hBox = new HBox();
		hBox.setBackground(null);
		// hBox.setPadding(new Insets(15, 15, 15, 15));
		hBox.setSpacing(10);
		hBox.getChildren().addAll(pane, mTargetGauge, mActualGauge);
		hBox.setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;"
									+ "-fx-border-color: black");


		return hBox;
	}


	public String getWebColorString(String wavelength)
	{
		return sProperties.getProperty(wavelength);
	}

	public java.awt.Color getWavelengthColor(String wavelength)
	{
		return java.awt.Color.decode(sProperties.getProperty(wavelength));
	}
}
