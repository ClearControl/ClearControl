package clearcontrol.devices.sensors;

import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.variable.Variable;

public interface TemperatureSensorDeviceInterface extends
                                                  OpenCloseDeviceInterface
{

  Variable<Double> getTemperatureInCelciusVariable();

  double getTemperatureInCelcius();

}
