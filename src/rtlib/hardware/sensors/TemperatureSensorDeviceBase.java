package rtlib.hardware.sensors;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.SignalStartableLoopTaskDevice;
import rtlib.core.variable.Variable;

public abstract class TemperatureSensorDeviceBase	extends
																									SignalStartableLoopTaskDevice	implements
																																								TemperatureSensorDeviceInterface
{

	Variable<Double> mTemperatureVariable;

	public TemperatureSensorDeviceBase(final String pDeviceName)
	{
		super(pDeviceName, false, TimeUnit.MILLISECONDS);
		mTemperatureVariable = new Variable<Double>(pDeviceName + "TemperatureInCelcius");
		getLoopPeriodVariable().set(500L);
	}

	@Override
	public Variable<Double> getTemperatureInCelciusVariable()
	{
		return mTemperatureVariable;
	}

	@Override
	public double getTemperatureInCelcius()
	{
		return mTemperatureVariable.get();
	}

}
