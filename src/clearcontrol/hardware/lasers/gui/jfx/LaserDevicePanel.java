package clearcontrol.hardware.lasers.gui.jfx;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import clearcontrol.gui.jfx.custom.rbg.RadialBargraph;
import clearcontrol.gui.jfx.custom.rbg.RadialBargraphBuilder;
import clearcontrol.hardware.lasers.LaserDeviceInterface;
import eu.hansolo.enzo.common.Marker;
import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.onoffswitch.IconSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class LaserDevicePanel extends HBox
{
	static private Properties sProperties;
	static
	{
		try
		{
			sProperties = new Properties();
			InputStream lResourceAsStream = LaserDevicePanel.class.getResourceAsStream("./WavelengthColors.properties");
			sProperties.load(lResourceAsStream);
			lResourceAsStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private LaserDeviceInterface mLaserDeviceInterface;

	private String mPowerUnits;
	private double mMaxPower;

	private IconSwitch mLaserOnSwitch;
	private RadialBargraph mTargetPowerGauge;
	private RadialBargraph mCurrentPowerGauge;
	private Marker mTargetPowerMarker;

	private VBox properties;
	private HBox pane;

	private final int mWaveLength;

	public LaserDevicePanel(LaserDeviceInterface pLaserDeviceInterface)
	{
		mLaserDeviceInterface = pLaserDeviceInterface;
		mWaveLength = mLaserDeviceInterface.getWavelengthInNanoMeter();
		mPowerUnits = "mW";
		mMaxPower = mLaserDeviceInterface.getMaxPowerInMilliWatt();
		init();

		// TODO: @HongKee: please complete the code below to establish the link:

		mLaserDeviceInterface.getCurrentPowerInMilliWattVariable()
													.addSetListener((o, n) -> {
														// o is old value, n is new value
														// here you change the laser gauge GUI to indicate a
														// new current power
													});

		mLaserDeviceInterface.getLaserOnVariable()
													.addSetListener((o, n) -> {
														// o is old value, n is new value
														// here you change the laser gauge GUI to indicate a
														// new on/off state
													});

		mLaserDeviceInterface.getWavelengthInNanoMeterVariable()
													.addSetListener((o, n) -> {
														// o is old value, n is new value
														// here you change the laser wavelength displayed
														// (can happen if the laser has variable wavelength
														// capability)
													});

		// TODO: you also need to change these variables values in response to GUI
		// events...
		// Use the demo to check if everything works out, you should get console
		// output telling you that it works.

		setBackground(null);
		// hBox.setPadding(new Insets(15, 15, 15, 15));
		setSpacing(10);
		getChildren().addAll(pane, mTargetPowerGauge, mCurrentPowerGauge);
		setStyle("-fx-border-style: solid;" + "-fx-border-width: 1;"
							+ "-fx-border-color: black");
	}

	public BooleanProperty getLaserOnBooleanProperty()
	{
		return mLaserOnSwitch.selectedProperty();
	}

	public Property<Number> getTargetPowerProperty()
	{
		return mTargetPowerMarker.valueProperty();
	}

	public Property<Number> getCurrentPowerProperty()
	{
		return mCurrentPowerGauge.valueProperty();
	}

	private DoubleProperty fontSize = new SimpleDoubleProperty(22);

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
		mTargetPowerMarker = new Marker(0, mPowerUnits);
		mTargetPowerGauge = RadialBargraphBuilder.create()
																							.title("Target")
																							.unit(mPowerUnits)
																							.markers(mTargetPowerMarker)
																							.maxValue(mMaxPower)
																							.build();
		mTargetPowerGauge.setBarGradientEnabled(true);
		mTargetPowerGauge.setBarGradient(stops);
		mTargetPowerGauge.setAnimated(false);
		mTargetPowerGauge.setInteractive(true);

		// As soon as user changes the target value, it updates gauge value
		mTargetPowerMarker.valueProperty()
											.bindBidirectional(mTargetPowerGauge.valueProperty());

		// Actual gauge build
		mCurrentPowerGauge = RadialBargraphBuilder.create()
																							.title("Current")
																							.unit(mPowerUnits)
																							.maxValue(mMaxPower)
																							.build();
		mCurrentPowerGauge.setAnimated(false);
		mCurrentPowerGauge.setBarGradientEnabled(true);
		mCurrentPowerGauge.setBarGradient(stops);
		mCurrentPowerGauge.setDisable(true);

		// Laser name with Wavelength
		properties = new VBox();
		// properties.setPadding(new Insets(10));
		properties.setPrefWidth(100);
		properties.setSpacing(3);

		Label laserLabel = new Label();
		String fontFamily = "Arial Black";
		laserLabel.setText(mWaveLength + " nm");
		laserLabel.setFont(new Font(fontFamily, 24));

		VBox lVBoxForColoredRectangle = new VBox();
		lVBoxForColoredRectangle.setBackground(new Background(new BackgroundFill(	Color.web(getWebColorString("" + mWaveLength)),
																																							CornerRadii.EMPTY,
																																							Insets.EMPTY)));
		Rectangle rectangle = new Rectangle(33, 80, Color.TRANSPARENT);

		properties.widthProperty()
							.addListener(new ChangeListener<Number>()
							{
								@Override
								public void changed(ObservableValue<? extends Number> observable,
																		Number oldValue,
																		Number newValue)
								{
									laserLabel.fontProperty()
														.set(Font.font(	fontFamily,
																						newValue.doubleValue() / 4.1));
								}
							});

		properties.getChildren().add(laserLabel);

		pane = new HBox();

		pane.widthProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> observable,
													Number oldValue,
													Number newValue)
			{
				rectangle.setWidth(newValue.doubleValue() / 4.5);
			}
		});

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

	public String getWebColorString(String wavelength)
	{
		return sProperties.getProperty(wavelength);
	}

	public java.awt.Color getWavelengthColor(String wavelength)
	{
		return java.awt.Color.decode(sProperties.getProperty(wavelength));
	}
}
