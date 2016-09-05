package clearcontrol.hardware.lasers.gui.jfx.demo;

import clearcontrol.hardware.lasers.devices.sim.LaserDeviceSimulator;
import clearcontrol.hardware.lasers.gui.jfx.LaserDevicePanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LaserDevicePanelDemo extends Application
{

	@Override
	public void start(Stage pPrimaryStage) throws Exception
	{

		LaserDeviceSimulator lLaserDeviceSimulator = new LaserDeviceSimulator("demolaser",
																																					0,
																																					594,
																																					100);
		lLaserDeviceSimulator.setSimLogging(true);

		lLaserDeviceSimulator.setLaserOn(true);
		lLaserDeviceSimulator.setTargetPowerInMilliWatt(20);

		LaserDevicePanel lLaserDevicePanel = new LaserDevicePanel(lLaserDeviceSimulator);

		Scene scene = new Scene(lLaserDevicePanel,
														javafx.scene.paint.Color.WHITE);

		pPrimaryStage.setTitle(this.getClass().getSimpleName());
		pPrimaryStage.setScene(scene);
		pPrimaryStage.show();

	}

	public static void main(String[] args)
	{
		Application.launch(LaserDevicePanelDemo.class);
	}

}
