package clearcontrol.hardware.stages.devices.sim.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.hardware.stages.StageType;
import clearcontrol.hardware.stages.devices.sim.StageDeviceSimulator;

public class StageDeviceSimulatorDemo
{

	@Test
	public void test() throws InterruptedException
	{
		StageDeviceSimulator lStageDeviceSimulator = new StageDeviceSimulator("demostage",
																																					StageType.Single);

		lStageDeviceSimulator.setSimLogging(true);

		lStageDeviceSimulator.addDOF("X", -1, 1);
		lStageDeviceSimulator.addDOF("Y", -1, 1);

		lStageDeviceSimulator.setTargetPosition(0, 1);

		while (Math.abs(lStageDeviceSimulator.getCurrentPosition(0) - 1) > 0.01)
		{
			System.out.println(lStageDeviceSimulator.getCurrentPosition(0));
			ThreadUtils.sleep(200, TimeUnit.MILLISECONDS);
		}

		lStageDeviceSimulator.setTargetPosition(0, -1);

		lStageDeviceSimulator.waitToArrive(0.001, 10, TimeUnit.SECONDS);

		assertTrue(true);

	}
}
