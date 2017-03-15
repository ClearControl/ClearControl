package clearcontrol.hardware.sensors;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

public interface TemperatureSensorDeviceInterface extends
                                                  OpenCloseDeviceInterface
{

  Variable<Double> getTemperatureInCelciusVariable();

  double getTemperatureInCelcius();

}
