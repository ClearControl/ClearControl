package rtlib.sensors;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.ObjectVariable;

public interface TemperatureSensorDeviceInterface	extends
																									OpenCloseDeviceInterface
{

	ObjectVariable<Double> getTemperatureInCelciusVariable();

	double getTemperatureInCelcius();

}
