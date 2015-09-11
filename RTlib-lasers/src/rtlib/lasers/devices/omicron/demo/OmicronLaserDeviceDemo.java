package rtlib.lasers.devices.omicron.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.lasers.devices.omicron.OmicronLaserDevice;

public class OmicronLaserDeviceDemo
{

	@Test
	public void testOn() throws InterruptedException
	{
		final OmicronLaserDevice lOmicronLaserDevice = new OmicronLaserDevice("COM4");

		assertTrue(lOmicronLaserDevice.open());

		System.out.println("device id: " + lOmicronLaserDevice.getDeviceId());
		System.out.println("working hours: " + lOmicronLaserDevice.getWorkingHours());
		System.out.println("wavelength: " + lOmicronLaserDevice.getWavelengthInNanoMeter());
		System.out.println("spec power (mW): " + lOmicronLaserDevice.getSpecPowerInMilliWatt());
		System.out.println("max power (mW): " + lOmicronLaserDevice.getMaxPowerInMilliWatt());/**/

		assertTrue(lOmicronLaserDevice.start());

		System.out.println("seting target power to 0mW ");
		lOmicronLaserDevice.setTargetPowerInMilliWatt(50);
		System.out.println("target power (mW): " + lOmicronLaserDevice.getTargetPowerInMilliWatt());
		System.out.println("target power (%): " + lOmicronLaserDevice.getTargetPowerInPercent());
		System.out.println("current power (mW): " + lOmicronLaserDevice.getCurrentPowerInMilliWatt());
		System.out.println("current power (%): " + lOmicronLaserDevice.getCurrentPowerInPercent());

		ThreadUtils.sleep(60, TimeUnit.SECONDS);

		assertTrue(lOmicronLaserDevice.stop());

		lOmicronLaserDevice.setTargetPowerInMilliWatt(0);

		assertTrue(lOmicronLaserDevice.close());

	}

	@Test
	public void testRamp() throws InterruptedException
	{
		final OmicronLaserDevice lOmicronLaserDevice = new OmicronLaserDevice("COM4");

		assertTrue(lOmicronLaserDevice.open());

		System.out.println("device id: " + lOmicronLaserDevice.getDeviceId());
		System.out.println("working hours: " + lOmicronLaserDevice.getWorkingHours());
		System.out.println("wavelength: " + lOmicronLaserDevice.getWavelengthInNanoMeter());
		System.out.println("spec power (mW): " + lOmicronLaserDevice.getSpecPowerInMilliWatt());
		System.out.println("max power (mW): " + lOmicronLaserDevice.getMaxPowerInMilliWatt());/**/

		assertTrue(lOmicronLaserDevice.start());

		System.out.println("seting target power to 0mW ");
		lOmicronLaserDevice.setTargetPowerInMilliWatt(0);
		System.out.println("target power (mW): " + lOmicronLaserDevice.getTargetPowerInMilliWatt());
		System.out.println("target power (%): " + lOmicronLaserDevice.getTargetPowerInPercent());
		System.out.println("current power (mW): " + lOmicronLaserDevice.getCurrentPowerInMilliWatt());
		System.out.println("current power (%): " + lOmicronLaserDevice.getCurrentPowerInPercent());

		System.out.println("seting target power to 10mW ");
		lOmicronLaserDevice.setTargetPowerInMilliWatt(10);
		System.out.println("target power (mW): " + lOmicronLaserDevice.getTargetPowerInMilliWatt());
		System.out.println("target power (%): " + lOmicronLaserDevice.getTargetPowerInPercent());
		System.out.println("current power (mW): " + lOmicronLaserDevice.getCurrentPowerInMilliWatt());
		System.out.println("current power (%): " + lOmicronLaserDevice.getCurrentPowerInPercent());

		System.out.println("seting target power to 20mW ");
		lOmicronLaserDevice.setTargetPowerInMilliWatt(20);
		System.out.println("target power (mW): " + lOmicronLaserDevice.getTargetPowerInMilliWatt());
		System.out.println("target power (%): " + lOmicronLaserDevice.getTargetPowerInPercent());
		System.out.println("current power (mW): " + lOmicronLaserDevice.getCurrentPowerInMilliWatt());
		System.out.println("current power (%): " + lOmicronLaserDevice.getCurrentPowerInPercent());

		for (int r = 0; r < 3; r++)
		{
			for (int i = 0; i < 100; i++)
			{
				final int lTargetPower = i;
				System.out.format(	"setting target power to: \t%d mW \n",
									lTargetPower);
				lOmicronLaserDevice.setTargetPowerInMilliWatt(lTargetPower);
				System.out.format(	"       current power at: \t%g mW \n",
									lOmicronLaserDevice.getCurrentPowerInMilliWatt());
				Thread.sleep(100);
			}
		}

		assertTrue(lOmicronLaserDevice.stop());

		lOmicronLaserDevice.setTargetPowerInMilliWatt(0);

		assertTrue(lOmicronLaserDevice.close());

	}

}
