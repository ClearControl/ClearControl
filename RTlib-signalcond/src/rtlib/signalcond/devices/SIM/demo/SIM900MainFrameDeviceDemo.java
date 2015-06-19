package rtlib.signalcond.devices.SIM.demo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rtlib.signalcond.devices.SIM.SIM900MainframeDevice;
import rtlib.signalcond.devices.SIM.SIM983ScalingAmplifierDevice;

public class SIM900MainFrameDeviceDemo
{

	@Test
	public void test() throws InterruptedException
	{
		final SIM900MainframeDevice lSIM900MainframeDevice = new SIM900MainframeDevice("COM4");

		final SIM983ScalingAmplifierDevice lScalingAmp = new SIM983ScalingAmplifierDevice(lSIM900MainframeDevice,
																																											4);


		lScalingAmp.setGain(1);
		lScalingAmp.setGain(4.3);

		assertEquals(4.3, lScalingAmp.getGain(), 0.01);

		lScalingAmp.setOffset(0);
		lScalingAmp.setOffset(2.3);
		
		assertEquals(2.3, lScalingAmp.getOffset(), 0.01);

	}

}
