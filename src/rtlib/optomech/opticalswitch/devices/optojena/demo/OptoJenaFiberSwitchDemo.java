package rtlib.optomech.opticalswitch.devices.optojena.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.optomech.opticalswitch.devices.optojena.OptoJenaFiberSwitchDevice;

public class OptoJenaFiberSwitchDemo
{

	@Test
	public void test() throws InterruptedException
	{
		final OptoJenaFiberSwitchDevice lOptoJenaFiberSwitchDevice = new OptoJenaFiberSwitchDevice("COM10");

		assertTrue(lOptoJenaFiberSwitchDevice.open());

		final ObjectVariable<Integer> lPositionVariable = lOptoJenaFiberSwitchDevice.getPositionVariable();

		for (int i = 0; i < 500; i++)
		{
			int lTargetPosition = 0; // i % 4;
			lPositionVariable.set(lTargetPosition);
			Thread.sleep(10);
			System.out.format("i=%d, tp=%d\n", i, lTargetPosition);
		}

		assertTrue(lOptoJenaFiberSwitchDevice.close());

	}

}
