package clearcontrol.hardware.sensors;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.startstop.StartStopDeviceInterface;

public interface TemperatureSensorDeviceInterface	extends
																									OpenCloseDeviceInterface
{

	Variable<Double> getTemperatureInCelciusVariable();

	double getTemperatureInCelcius();

}
