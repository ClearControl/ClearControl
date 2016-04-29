package rtlib.hardware.lasers.gui.jfx.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rtlib.hardware.lasers.LaserDeviceInterface;
import rtlib.hardware.lasers.devices.sim.LaserDeviceSimulator;
import rtlib.hardware.lasers.gui.jfx.LaserGauge;

public class AllLaserDevicesGUIDemo extends Application
{

	// public final JSliderDouble mLaser405TargetPower,
	// mLaser488TargetPower, mLaser515TargetPower,
	// mLaser561TargetPower, mLaser594TargetPower;
	//
	// public final JSliderDouble mLaser405CurrentPower,
	// mLaser488CurrentPower, mLaser515CurrentPower,
	// mLaser561CurrentPower, mLaser594CurrentPower;

	// public final JSliderIndexedStrings mFilterWheelPosition;
	// public final JSliderDouble mFilterWheelSpeed;

	// public final JCheckBoxBoolean m405OnOffSwitch, m488OnOffSwitch,
	// m515OnOffSwitch, m561OnOffSwitch, m594OnOffSwitch;
	// private final JLabel mLaserSetPowerLabel;
	// private final JLabel mLaserCurrentPowerLabel;
	// private final JLabel mDetectionFilterWheelLabel;

	public final ArrayList<LaserGauge> mLaserGauges = new ArrayList<LaserGauge>();

	public AllLaserDevicesGUIDemo()
	{
		ArrayList<LaserDeviceInterface> list = new ArrayList<>();

		list.add(new LaserDeviceSimulator("405", 0, 405, 100));
		list.add(new LaserDeviceSimulator("488", 0, 488, 100));
		list.add(new LaserDeviceSimulator("515", 0, 515, 100));
		list.add(new LaserDeviceSimulator("561", 0, 561, 100));
		list.add(new LaserDeviceSimulator("594", 0, 594, 100));

		for (LaserDeviceInterface laser : list)
		{
			mLaserGauges.add(new LaserGauge(""	+ laser.getWavelengthInNanoMeter(),
																			"mW",
																			100));
		}
	}

	public AllLaserDevicesGUIDemo(List<LaserDeviceInterface> pLaserDeviceList)
	{
		for (LaserDeviceInterface laser : pLaserDeviceList)
		{
			mLaserGauges.add(new LaserGauge(""	+ laser.getWavelengthInNanoMeter(),
																			"mW",
																			100));
		}

	}

	@Override
	public void start(Stage stage)
	{
		VBox pane = new VBox();

		pane.getChildren()
				.addAll(mLaserGauges);

		Scene scene = new Scene(pane, javafx.scene.paint.Color.WHITE);

		stage.setTitle("Lasers");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args)
	{
		Application.launch(AllLaserDevicesGUIDemo.class);
	}

}
