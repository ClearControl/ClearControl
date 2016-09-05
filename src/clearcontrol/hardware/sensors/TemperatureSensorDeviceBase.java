package clearcontrol.hardware.sensors;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.task.PeriodicLoopTaskDevice;

public abstract class TemperatureSensorDeviceBase	extends
																									PeriodicLoopTaskDevice implements
																																OpenCloseDeviceInterface,
																																TemperatureSensorDeviceInterface
{

	private Variable<Double> mTemperatureVariable;

	public TemperatureSensorDeviceBase(final String pDeviceName)
	{
		super(pDeviceName, 500.0, TimeUnit.MILLISECONDS);
		mTemperatureVariable = new Variable<Double>(pDeviceName + "TemperatureInCelcius",
																								Double.NaN);
	}

	public boolean open()
	{
		startTask();
		return waitForStarted(100, TimeUnit.MILLISECONDS);
	};

	public boolean close()
	{
		stopTask();
		return waitForStopped(100, TimeUnit.MILLISECONDS);
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
