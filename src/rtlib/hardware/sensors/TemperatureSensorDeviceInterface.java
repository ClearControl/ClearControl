package rtlib.hardware.sensors;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.Variable;

public interface TemperatureSensorDeviceInterface	extends
																									OpenCloseDeviceInterface
{

	Variable<Double> getTemperatureInCelciusVariable();

	double getTemperatureInCelcius();

}
