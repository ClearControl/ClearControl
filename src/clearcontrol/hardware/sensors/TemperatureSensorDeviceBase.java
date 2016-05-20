package clearcontrol.hardware.sensors;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.startstop.StartableLoopDevice;

public abstract class TemperatureSensorDeviceBase	extends
																									StartableLoopDevice	implements
																																								TemperatureSensorDeviceInterface
{

	Variable<Double> mTemperatureVariable;

	public TemperatureSensorDeviceBase(final String pDeviceName)
	{
		super(pDeviceName, 500.0 ,TimeUnit.MILLISECONDS);
		mTemperatureVariable = new Variable<Double>(pDeviceName + "TemperatureInCelcius");
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
