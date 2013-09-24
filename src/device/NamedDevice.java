package device;

public abstract class NamedDevice implements VirtualDeviceInterface
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

	@Override
	public String toString()
	{
		return String.format("NamedDevice [mDeviceName=%s]", mDeviceName);
	}
	
}
