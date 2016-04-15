package rtlib.hardware.sensors.devices.tc01.bridj.demo;

import org.bridj.Pointer;
import org.junit.Test;

import rtlib.hardware.sensors.devices.tc01.NIThermoCoupleType;
import rtlib.hardware.sensors.devices.tc01.bridj.TC01libLibrary;

public class TC01Demo
{

	@Test
	public void test() throws InterruptedException
	{

		String lPhysicalChannel = "Dev2/ai0\0";
		Pointer<Byte> lPhysicalChannelPointer = Pointer.pointerToCString(lPhysicalChannel);

		for (int i = 0; i < 100; i++)
		{
			double lTemp = TC01libLibrary.tC01lib(lPhysicalChannelPointer,
																						NIThermoCoupleType.J.getValue());
			System.out.format("Temp = %g deg C \n", lTemp);
		}

	}

}
