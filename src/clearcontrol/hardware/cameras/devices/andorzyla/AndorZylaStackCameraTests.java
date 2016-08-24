package clearcontrol.hardware.cameras.devices.andorzyla;


import org.junit.*;

import andorsdkj.AndorSdkJException;
import andorsdkj.enums.TriggerMode;

public class AndorZylaStackCameraTests
{
	@Test
	public void BasicOpenAndCloseTest(){
		
		int lCameraIndex = 0;
		
		try
		{
			AndorZylaStackCamera lAndorZylaStackInstance = new AndorZylaStackCamera(lCameraIndex, TriggerMode.SOFTWARE);
			lAndorZylaStackInstance.start();
			lAndorZylaStackInstance.stop();
		//	lAndorZylaStackInstance.open();
		}
		catch (AndorSdkJException e)
		{
			System.out.println("Failsed to instanciate AndorStackCamera " + lCameraIndex);
			e.printStackTrace();
		}
	}
	
	@Test
	public void SequenceAcquisitionTest(){
		int lCameraIndex = 0;
		try
		{
			AndorZylaStackCamera lAndorZylaStackInstance = new AndorZylaStackCamera(lCameraIndex, TriggerMode.SOFTWARE);
			lAndorZylaStackInstance.start();
			
		}
		catch (AndorSdkJException e)
		{
			System.out.println("Failsed to instanciate AndorStackCamera " + lCameraIndex);
			e.printStackTrace();
		}
	}
}
