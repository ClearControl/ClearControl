package clearcontrol.hardware.cameras.devices.andorzyla;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.hardware.cameras.StackCameraDeviceBase;

public class AndorZylaStackCamera extends StackCameraDeviceBase implements
																																OpenCloseDeviceInterface,
																																AsynchronousExecutorServiceAccess
{

	public AndorZylaStackCamera(String pDeviceName)
	{
		super(pDeviceName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void trigger()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reopen()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean start()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
