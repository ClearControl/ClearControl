//package clearcontrol.hardware.cameras.devices.andorzyla;
//
//import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
//import clearcontrol.device.openclose.OpenCloseDeviceInterface;
//import clearcontrol.hardware.cameras.StackCameraDeviceBase;
//import andorsdkj.*;
//
//public class AndorZylaStackCamera extends StackCameraDeviceBase implements
//																																OpenCloseDeviceInterface,
//																																AsynchronousExecutorServiceAccess
//{
//	// instance variables
//	AndorCamera lAndorCamera;
//	int lCameraIndex;
//
//
//
//	//Constructor with the device name generated as "AndorZyla " + the camera index
//	public AndorZylaStackCamera(int pCameraIndex) throws AndorSdkJException
//	{
//		super("AndorZyla " + pCameraIndex);
//		lAndorCamera = new AndorCamera(pCameraIndex);
//		lCameraIndex = pCameraIndex;
//
//	}
//
//	@Override
//	public void trigger()
//	{
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void reopen()
//	{
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public boolean start()
//	{
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean stop()
//	{
//		boolean exitFlag = true;
//		try
//		{
//			lAndorCamera.close();
//		}
//		catch (Exception e)
//		{
//			exitFlag = false;
//			System.out.println("Cannot close the AndorZylaStackCamera " + lCameraIndex);
//			e.printStackTrace();
//		}
//		return exitFlag;
//	}
//
//}
