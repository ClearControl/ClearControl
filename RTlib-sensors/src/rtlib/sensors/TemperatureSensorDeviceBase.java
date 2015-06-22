package rtlib.sensors;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.SignalStartableLoopTaskDevice;
import rtlib.core.variable.types.doublev.DoubleVariable;

public abstract class TemperatureSensorDeviceBase extends
																				SignalStartableLoopTaskDevice	implements
																																			TemperatureSensorDeviceInterface
{

	DoubleVariable mTemperatureVariable;

	public TemperatureSensorDeviceBase(final String pDeviceName)
	{
		super(pDeviceName, false, TimeUnit.MILLISECONDS);
		mTemperatureVariable = new DoubleVariable(pDeviceName + "TemperatureInCelcius");
		getLoopPeriodVariable().setValue(500);
	}

	@Override
	public DoubleVariable getTemperatureInCelciusVariable()
	{
		return mTemperatureVariable;
	}

	@Override
	public double getTemperatureInCelcius()
	{
		return mTemperatureVariable.getValue();
	}

}
