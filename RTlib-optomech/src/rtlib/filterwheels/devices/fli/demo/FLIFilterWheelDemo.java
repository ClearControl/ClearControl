package rtlib.filterwheels.devices.fli.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.filterwheels.devices.fli.FLIFilterWheelDevice;

public class FLIFilterWheelDemo
{

	@Test
	public void test() throws InterruptedException
	{
		final FLIFilterWheelDevice lFLIFilterWheelDevice = new FLIFilterWheelDevice("COM25");

		assertTrue(lFLIFilterWheelDevice.open());

		final DoubleVariable lPositionVariable = lFLIFilterWheelDevice.getPositionVariable();
		final DoubleVariable lSpeedVariable = lFLIFilterWheelDevice.getSpeedVariable();

		for (int i = 0; i < 10; i++)
		{
			int lTargetPosition = i % 10;
			lPositionVariable.set((double) lTargetPosition);
			lSpeedVariable.set((double) (i / 30));
			Thread.sleep(30);
			int lCurrentPosition = (int) lPositionVariable.getValue();
			System.out.format("i=%d, tp=%d, cp=%d\n",
												i,
												lTargetPosition,
												lCurrentPosition);
		}

		assertTrue(lFLIFilterWheelDevice.close());

	}

}
