package clearcontrol.device.task.test;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.device.task.PeriodicLoopTaskDevice;

public class PeriodicLoopTaskDeviceTests
{

	private volatile long mCounter = 0;

	class TestLoopTaskDevice extends PeriodicLoopTaskDevice
	{
		public TestLoopTaskDevice()
		{
			super("TestDevice", 100, TimeUnit.MILLISECONDS);
		}

		@Override
		public boolean loop()
		{
			System.out.println("counter: " + mCounter);
			mCounter++;
			return true;
		}

	}

	@Test
	public void test() throws ExecutionException
	{
		TestLoopTaskDevice lTestLoopTaskDevice = new TestLoopTaskDevice();

		mCounter = 0;
		lTestLoopTaskDevice.getStartSignalBooleanVariable().set(true);
		System.out.println("Waiting to start");
		assertTrue(lTestLoopTaskDevice.waitForStarted(1, TimeUnit.SECONDS));
		ThreadUtils.sleep(1, TimeUnit.SECONDS);
		lTestLoopTaskDevice.getStopSignalBooleanVariable().set(true);

		assertTrue(lTestLoopTaskDevice.waitForStopped(10,
																									TimeUnit.SECONDS));
		long lCounter = mCounter;
		System.out.println("lCounter=" + mCounter);

		ThreadUtils.sleep(200, TimeUnit.MILLISECONDS);

		assertTrue(lCounter >= mCounter);
	}

}
