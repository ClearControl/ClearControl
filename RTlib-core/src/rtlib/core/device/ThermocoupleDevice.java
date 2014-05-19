package rtlib.core.device;

import rtlib.core.variable.doublev.DoubleVariable;

public abstract class ThermocoupleDevice extends
																				SignalStartableLoopTaskDevice
{

	DoubleVariable mTemperatureVariable;

	public ThermocoupleDevice(final String pDeviceName)
	{
		super(pDeviceName, false);
		mTemperatureVariable = new DoubleVariable(pDeviceName + "TemperatureInCelcius");
	}

	public DoubleVariable getTemperatureInCelciusVariable()
	{
		return mTemperatureVariable;
	}

	public double getTemperatureInCelcius()
	{
		return mTemperatureVariable.getValue();
	}

}
