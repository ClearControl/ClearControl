package rtlib.core.device;

public class VirtualDeviceAdapter	implements
																	OpenCloseDeviceInterface,
																	StartStopDeviceInterface
{
	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		return true;
	}
}
