package device;

import variable.doublev.DoubleVariable;

public abstract class ThermocoupleDevice extends SignalStartableLoopTaskDevice
{

	DoubleVariable mTemperatureVariable;
	
	public ThermocoupleDevice(String pDeviceName)
	{
		super(pDeviceName, false);
		mTemperatureVariable = new DoubleVariable(pDeviceName+"TemperatureInCelcius");
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
