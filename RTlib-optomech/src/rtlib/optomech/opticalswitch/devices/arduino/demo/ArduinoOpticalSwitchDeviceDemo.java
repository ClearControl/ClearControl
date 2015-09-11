package rtlib.optomech.opticalswitch.devices.arduino.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.optomech.opticalswitch.devices.arduino.ArduinoOpticalSwitchDevice;

public class ArduinoOpticalSwitchDeviceDemo
{

	@Test
	public void test() throws InterruptedException
	{
		final ArduinoOpticalSwitchDevice lArduinoOpticalSwitchDevice = new ArduinoOpticalSwitchDevice("COM14");

		assertTrue(lArduinoOpticalSwitchDevice.open());

		int[] lValidPositions = lArduinoOpticalSwitchDevice.getValidPositions();

		final DoubleVariable lPositionVariable = lArduinoOpticalSwitchDevice.getPositionVariable();

		for (int i = 0; i < 500; i++)
		{
			int lTargetPosition = lValidPositions[i % lValidPositions.length];
			lPositionVariable.set((double) lTargetPosition);
			Thread.sleep(1000);
			System.out.format("i=%d, tp=%d\n", i, lTargetPosition);
		}

		assertTrue(lArduinoOpticalSwitchDevice.close());

	}

}
