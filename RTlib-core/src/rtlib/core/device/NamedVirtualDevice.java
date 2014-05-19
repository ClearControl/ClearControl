package rtlib.core.device;

public class NamedVirtualDevice extends VirtualDeviceAdapter implements
																														NamedDeviceInterface,
																														VirtualDeviceInterface
{

	private final String mDeviceName;

	public NamedVirtualDevice(final String pDeviceName)
	{
		super();
		mDeviceName = pDeviceName;
	}

	@Override
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
