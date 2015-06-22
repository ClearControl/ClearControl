package rtlib.sensors.devices.tc01.demo;

import org.junit.Test;

import rtlib.core.variable.VariableGetListener;
import rtlib.sensors.devices.tc01.NIThermoCoupleType;
import rtlib.sensors.devices.tc01.TC01;

public class TC01Demo
{

	@Test
	public void test() throws InterruptedException
	{
		final TC01 lTC01 = new TC01("Dev2/ai0", NIThermoCoupleType.J, 0);

		lTC01.start();

		lTC01.getTemperatureInCelciusVariable()
					.addGetListener(new VariableGetListener<Double>()
					{
						@Override
						public void getEvent(Double pCurrentValue)
						{
							System.out.format("Temp = %g deg C \n", pCurrentValue);
						}
					});

		Thread.sleep(10 * 1000);

		lTC01.stop();

	}

}
