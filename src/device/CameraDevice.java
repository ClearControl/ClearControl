package device;

public abstract class CameraDevice extends SignalStartableDevice implements
																																VirtualDeviceInterface
{

	public CameraDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

}
