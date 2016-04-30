package clearcontrol.hardware.sensors.devices.sim;

import java.util.concurrent.ThreadLocalRandom;

import clearcontrol.core.variable.Variable;
import clearcontrol.hardware.sensors.TemperatureSensorDeviceBase;
import clearcontrol.hardware.sensors.TemperatureSensorDeviceInterface;

public class TemperatureSensorDeviceSimulator	extends
																							TemperatureSensorDeviceBase	implements
																																					TemperatureSensorDeviceInterface
{

	public TemperatureSensorDeviceSimulator(String pDeviceName)
	{
		super(pDeviceName);
		getLoopPeriodVariable().set(15000L);
	}

	@Override
	protected boolean loop()
	{
		final Variable<Double> lTemperatureInCelciusVariable = getTemperatureInCelciusVariable();
		final ThreadLocalRandom lThreadLocalRandom = ThreadLocalRandom.current();
		final double lTemperatureInCelcius = 24 + lThreadLocalRandom.nextDouble();
		lTemperatureInCelciusVariable.set(lTemperatureInCelcius);

		return true;
	}

}
