package rtlib.device.display;

import rtlib.device.openclose.OpenCloseDeviceInterface;
import rtlib.device.signal.SignalStartableDevice;

public abstract class BaseDisplayDevice extends SignalStartableDevice	implements
																																			OpenCloseDeviceInterface,
																																			DisplayableInterface
{

	public BaseDisplayDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

}
