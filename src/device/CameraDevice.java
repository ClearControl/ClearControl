package device;

public abstract class CameraDevice extends SignalStartableDevice implements VirtualDeviceInterface
{

	public CameraDevice(String pDeviceName)
	{
		super(pDeviceName);
	}

}
