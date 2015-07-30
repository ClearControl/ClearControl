package rtlib.optomech.filterwheels.devices.sim;

import rtlib.optomech.filterwheels.FilterWheelDeviceBase;
import rtlib.optomech.filterwheels.FilterWheelDeviceInterface;

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
	public boolean close()
	{
		return true;
	}

}
