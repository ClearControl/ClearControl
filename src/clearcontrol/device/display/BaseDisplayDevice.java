package clearcontrol.device.display;

import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.signal.SignalStartableDevice;

public abstract class BaseDisplayDevice extends SignalStartableDevice	implements
																																			OpenCloseDeviceInterface,
																																			DisplayableInterface
{

	public BaseDisplayDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

}
