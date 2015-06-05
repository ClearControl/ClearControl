package rtlib.sensors;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;

public interface TemperatureSensorDeviceInterface	extends
																									OpenCloseDeviceInterface
{

	DoubleVariable getTemperatureInCelciusVariable();

	double getTemperatureInCelcius();

}
