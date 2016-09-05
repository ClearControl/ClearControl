package clearcontrol.hardware.sensors.gui.jfx.demo;

import clearcontrol.hardware.sensors.devices.sim.TemperatureSensorDeviceSimulator;
import clearcontrol.hardware.sensors.gui.jfx.TemperatureSensorDevicePanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TemperatureSensorDevicePanelDemo extends Application
{

	@Override
	public void start(Stage pPrimaryStage) throws Exception
	{

		TemperatureSensorDeviceSimulator lTemperatureSensorDeviceSimulator = new TemperatureSensorDeviceSimulator("demotempsesor");
		lTemperatureSensorDeviceSimulator.setSimLogging(true);

		TemperatureSensorDevicePanel lTemperatureSensorDevicePanel = new TemperatureSensorDevicePanel(lTemperatureSensorDeviceSimulator);

		Scene scene = new Scene(lTemperatureSensorDevicePanel,
														javafx.scene.paint.Color.WHITE);

		pPrimaryStage.setTitle(this.getClass().getSimpleName());
		pPrimaryStage.setScene(scene);
		pPrimaryStage.setWidth(100);
		pPrimaryStage.setHeight(100);/**/

		lTemperatureSensorDeviceSimulator.open();
		pPrimaryStage.show();

	}

	public static void main(String[] args)
	{
		Application.launch(TemperatureSensorDevicePanelDemo.class);
	}

}
