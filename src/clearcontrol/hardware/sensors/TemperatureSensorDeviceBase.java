package clearcontrol.hardware.sensors;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.signal.SignalStartableLoopTaskDevice;

public abstract class TemperatureSensorDeviceBase	extends
																									SignalStartableLoopTaskDevice	implements
																																								TemperatureSensorDeviceInterface
{

	Variable<Double> mTemperatureVariable;

	public TemperatureSensorDeviceBase(final String pDeviceName)
	{
		super(pDeviceName, false, TimeUnit.MILLISECONDS);
		mTemperatureVariable = new Variable<Double>(pDeviceName + "TemperatureInCelcius");
		getLoopPeriodVariable().set(500.0);
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
