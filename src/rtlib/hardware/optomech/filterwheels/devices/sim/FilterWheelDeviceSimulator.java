package rtlib.hardware.optomech.filterwheels.devices.sim;

import rtlib.hardware.optomech.filterwheels.FilterWheelDeviceBase;
import rtlib.hardware.optomech.filterwheels.FilterWheelDeviceInterface;

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

	@Override
	public int[] getValidPositions()
	{
		return new int[]
		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	}

}
