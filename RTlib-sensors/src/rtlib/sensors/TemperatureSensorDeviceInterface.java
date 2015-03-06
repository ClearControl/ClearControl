package rtlib.sensors;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;

public interface TemperatureSensorDeviceInterface	extends
																									VirtualDeviceInterface
{

	DoubleVariable getTemperatureInCelciusVariable();

	double getTemperatureInCelcius();

}
