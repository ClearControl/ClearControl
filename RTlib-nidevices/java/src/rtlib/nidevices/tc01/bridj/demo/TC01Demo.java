package rtlib.nidevices.tc01.bridj.demo;

import org.junit.Test;

import rtlib.nidevices.tc01.NIThermoCoupleType;
import rtlib.nidevices.tc01.bridj.TC01libLibrary;

public class TC01Demo
{

	@Test
	public void test() throws InterruptedException
	{
		
		for(int i=0; i<100; i++)
		{
			double lTemp = TC01libLibrary.tC01lib(NIThermoCoupleType.J.getValue());
			System.out.format("Temp = %g deg C \n",lTemp);
		}

	}

}
