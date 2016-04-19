package rtlib.hardware.sensors;

import rtlib.core.variable.Variable;
import rtlib.device.openclose.OpenCloseDeviceInterface;

public interface TemperatureSensorDeviceInterface	extends
																									OpenCloseDeviceInterface
{

	Variable<Double> getTemperatureInCelciusVariable();

	double getTemperatureInCelcius();

}
