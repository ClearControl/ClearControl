package clearcontrol.devices.sensors.gui.jfx;

import javafx.application.Platform;
import javafx.scene.control.Label;

import clearcontrol.devices.sensors.TemperatureSensorDeviceInterface;

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
