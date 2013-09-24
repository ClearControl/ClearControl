package device;


public abstract class BaseDisplayDevice extends SignalStartableDevice	implements
																																			VirtualDeviceInterface,
																																			Displayable
{

	public BaseDisplayDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

}
