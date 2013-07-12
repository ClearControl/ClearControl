package device;


public abstract class BaseDisplayDevice extends SignalStartableDevice	implements
																																			VirtualDevice,
																																			Displayable
{

	public BaseDisplayDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

}
