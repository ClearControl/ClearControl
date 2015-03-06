package rtlib.filterwheels.devices.sim;

import rtlib.filterwheels.FilterWheelDeviceBase;
import rtlib.filterwheels.FilterWheelDeviceInterface;

public class FilterWheelDeviceSimulator extends FilterWheelDeviceBase	implements
																																			FilterWheelDeviceInterface
{

	public FilterWheelDeviceSimulator(String pDeviceName)
	{
		super(pDeviceName);
	}

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		return true;
	}

}
