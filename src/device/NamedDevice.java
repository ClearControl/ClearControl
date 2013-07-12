package device;

public abstract class NamedDevice implements VirtualDevice
{

	private final String mDeviceName;

	public NamedDevice(String pDeviceName)
	{
		super();
		mDeviceName = pDeviceName;
	}

	public String getDeviceName()
	{
		return mDeviceName;
	}
	
}
