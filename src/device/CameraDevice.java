package device;

public abstract class CameraDevice extends SignalStartableDevice implements VirtualDevice
{

	public CameraDevice(String pDeviceName)
	{
		super(pDeviceName);
	}

}
