package clearcontrol.hardware.sensors.gui.jfx;

import clearcontrol.hardware.sensors.TemperatureSensorDeviceInterface;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class TemperatureSensorDevicePanel extends Label
{

	public TemperatureSensorDevicePanel(TemperatureSensorDeviceInterface pTemperatureSensorDeviceInterface)
	{

		setTemperatureText(pTemperatureSensorDeviceInterface.getTemperatureInCelcius());

		pTemperatureSensorDeviceInterface.getTemperatureInCelciusVariable()
																			.addSetListener((o, n) -> {
																				if (o != n)
																					setTemperatureText(n);
																			});

	}

	private String setTemperatureText(double pTemperatureInCelcius)
	{
		String lString = String.format("%g ºC", pTemperatureInCelcius);
		Platform.runLater(() -> {
			setText(lString);
		});
		return lString;
	}

}
