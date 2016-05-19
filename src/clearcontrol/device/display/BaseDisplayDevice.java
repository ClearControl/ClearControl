package clearcontrol.device.display;

import clearcontrol.device.VirtualDevice;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

public abstract class BaseDisplayDevice extends VirtualDevice	implements
																															OpenCloseDeviceInterface,
																															DisplayableInterface
{

	public BaseDisplayDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

}
