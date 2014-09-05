package rtlib.serialdevice.stage.smc100.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.serialdevice.stage.smc100.SMC100Device;

public class SMC100DeviceDemo
{

	@Test
	public void demo() throws InterruptedException
	{
		final SMC100Device lSMC100Device = new SMC100Device("LightStage",
																												"COM1");

		assertTrue(lSMC100Device.open());
		assertTrue(lSMC100Device.start());

		lSMC100Device.home();

		lSMC100Device.goToPosition(5);

		Thread.sleep(2000);

		assertTrue(lSMC100Device.stop());
		assertTrue(lSMC100Device.close());

	}

}
