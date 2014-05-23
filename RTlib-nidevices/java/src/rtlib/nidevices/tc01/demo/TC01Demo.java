package rtlib.nidevices.tc01.demo;

import org.junit.Test;

import rtlib.core.variable.VariableListenerAdapter;
import rtlib.nidevices.tc01.NIThermoCoupleType;
import rtlib.nidevices.tc01.TC01;

public class TC01Demo
{

	@Test
	public void test() throws InterruptedException
	{
		TC01 lTC01  = new TC01(NIThermoCoupleType.J,0);
		
		lTC01.start();
				
		
		lTC01.getTemperatureInCelciusVariable().addListener(new VariableListenerAdapter<Double>()
		{
			@Override
			public void getEvent(Double pCurrentValue)
			{
				System.out.format("Temp = %g deg C \n",pCurrentValue);
			}
		});
		
		Thread.sleep(10*1000);
		
		lTC01.stop();


	}

}
