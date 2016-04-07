package rtlib.sensors;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.SignalStartableLoopTaskDevice;
import rtlib.core.variable.types.objectv.ObjectVariable;

public abstract class TemperatureSensorDeviceBase	extends
													SignalStartableLoopTaskDevice	implements
																					TemperatureSensorDeviceInterface
{

	 ObjectVariable<Double> mTemperatureVariable;

	public TemperatureSensorDeviceBase(final String pDeviceName)
	{
		super(pDeviceName, false, TimeUnit.MILLISECONDS);
		mTemperatureVariable =   new ObjectVariable<Double>  (pDeviceName + "TemperatureInCelcius");
		getLoopPeriodVariable().set(500L);
	}

	@Override
	public  ObjectVariable<Double> getTemperatureInCelciusVariable()
	{
		return mTemperatureVariable;
	}

	@Override
	public double getTemperatureInCelcius()
	{
		return mTemperatureVariable.get();
	}

}
