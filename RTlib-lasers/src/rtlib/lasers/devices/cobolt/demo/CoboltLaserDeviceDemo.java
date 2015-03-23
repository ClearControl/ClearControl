package rtlib.lasers.devices.cobolt.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.lasers.devices.cobolt.CoboltLaserDevice;

public class CoboltLaserDeviceDemo
{

	@Test
	public void test() throws InterruptedException
	{
		final CoboltLaserDevice lCoboltLaserDevice = new CoboltLaserDevice(	"Jive",
																																				100,
																																				"COM22");

		assertTrue(lCoboltLaserDevice.open());

		System.out.println("device id: " + lCoboltLaserDevice.getDeviceId());
		System.out.println("working hours: " + lCoboltLaserDevice.getWorkingHours());
		System.out.println("wavelength: " + lCoboltLaserDevice.getWavelengthInNanoMeter());
		System.out.println("spec power (mW): " + lCoboltLaserDevice.getSpecPowerInMilliWatt());
		System.out.println("max power (mW): " + lCoboltLaserDevice.getMaxPowerInMilliWatt());/**/

		lCoboltLaserDevice.getPowerOnVariable().setValue(true);
		lCoboltLaserDevice.getLaserOnVariable().setValue(true);

		for (int i = 0; i < 200; i++)
		{
			System.out.format("       current power at: \t%g mW \n",
												lCoboltLaserDevice.getCurrentPowerInMilliWatt());
			Thread.sleep(100);
		}

		assertTrue(lCoboltLaserDevice.start());

		System.out.println("setting target power to 0mW ");
		lCoboltLaserDevice.setTargetPowerInMilliWatt(0);
		System.out.println("target power (mW): " + lCoboltLaserDevice.getTargetPowerInMilliWatt());
		System.out.println("target power (%): " + lCoboltLaserDevice.getTargetPowerInPercent());
		System.out.println("current power (mW): " + lCoboltLaserDevice.getCurrentPowerInMilliWatt());
		System.out.println("current power (%): " + lCoboltLaserDevice.getCurrentPowerInPercent());

		System.out.println("setting target power to 10mW ");
		lCoboltLaserDevice.setTargetPowerInMilliWatt(10);
		System.out.println("target power (mW): " + lCoboltLaserDevice.getTargetPowerInMilliWatt());
		System.out.println("target power (%): " + lCoboltLaserDevice.getTargetPowerInPercent());
		System.out.println("current power (mW): " + lCoboltLaserDevice.getCurrentPowerInMilliWatt());
		System.out.println("current power (%): " + lCoboltLaserDevice.getCurrentPowerInPercent());

		System.out.println("setting target power to 20mW ");
		lCoboltLaserDevice.setTargetPowerInMilliWatt(20);
		System.out.println("target power (mW): " + lCoboltLaserDevice.getTargetPowerInMilliWatt());
		System.out.println("target power (%): " + lCoboltLaserDevice.getTargetPowerInPercent());
		System.out.println("current power (mW): " + lCoboltLaserDevice.getCurrentPowerInMilliWatt());
		System.out.println("current power (%): " + lCoboltLaserDevice.getCurrentPowerInPercent());

		for (int r = 0; r < 3; r++)
		{
			for (int i = 0; i < 100; i++)
			{
				final int lTargetPower = i;
				System.out.format("setting target power to: \t%d mW \n",
													lTargetPower);
				lCoboltLaserDevice.setTargetPowerInMilliWatt(lTargetPower);
				System.out.format("       current power at: \t%g mW \n",
													lCoboltLaserDevice.getCurrentPowerInMilliWatt());
				Thread.sleep(10);
			}
		}

		assertTrue(lCoboltLaserDevice.stop());

		lCoboltLaserDevice.setTargetPowerInMilliWatt(0);

		assertTrue(lCoboltLaserDevice.close());

	}

}
