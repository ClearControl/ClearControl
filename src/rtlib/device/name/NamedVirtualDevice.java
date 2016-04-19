package rtlib.device.name;

import rtlib.device.openclose.OpenCloseDeviceAdapter;
import rtlib.device.openclose.OpenCloseDeviceInterface;

public class NamedVirtualDevice extends OpenCloseDeviceAdapter implements
																															OpenCloseDeviceInterface,
																															NameableInterface
{

	private String mDeviceName;

	public NamedVirtualDevice(final String pDeviceName)
	{
		super();
		mDeviceName = pDeviceName;
	}

	@Override
	public void setName(String pName)
	{
		mDeviceName = pName;
	}

	@Override
	public String getName()
	{
		return mDeviceName;
	}

	@Override
	public String toString()
	{
		return String.format("NamedDevice [mDeviceName=%s]", mDeviceName);
	}

}
