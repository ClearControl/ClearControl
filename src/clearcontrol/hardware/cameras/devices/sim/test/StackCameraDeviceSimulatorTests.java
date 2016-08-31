package clearcontrol.hardware.cameras.devices.sim.test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import clearcontrol.core.variable.Variable;
import clearcontrol.hardware.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.stack.StackInterface;

public class StackCameraDeviceSimulatorTests
{

	@Test
	public void test() throws IOException,
										InterruptedException,
										ExecutionException,
										TimeoutException
	{

		Variable<Boolean> lTrigger = new Variable<Boolean>(	"CameraTrigger",
																												false);

		StackCameraDeviceSimulator lStackCameraDeviceSimulator = new StackCameraDeviceSimulator("StackCamera",
																																														lTrigger);

		Variable<StackInterface> lStackVariable = lStackCameraDeviceSimulator.getStackVariable();

		lStackVariable.addSetListener((	StackInterface pOldStack,
																		StackInterface pNewStack) -> {
			System.out.println("Arrived: " + pNewStack);
			pNewStack.release();
		});

		lStackCameraDeviceSimulator.getExposureInMicrosecondsVariable()
																.set((double) 10);

		lStackCameraDeviceSimulator.open();

		lStackCameraDeviceSimulator.start();

		lStackCameraDeviceSimulator.clearQueue();

		for (int i = 0; i < 20; i++)
		{
			lStackCameraDeviceSimulator.addCurrentStateToQueue();
		}

		lStackCameraDeviceSimulator.finalizeQueue();

		for (int j = 0; j < 10; j++)
		{
			Future<Boolean> lPlayQueue = lStackCameraDeviceSimulator.playQueue();

			for (int i = 0; i < lStackCameraDeviceSimulator.getQueueLength(); i++)
			{
				lTrigger.setEdge(false, true);
			}

			System.out.println("waiting...");
			lPlayQueue.get(20L, TimeUnit.SECONDS);
			System.out.println(" ...done waiting.");
		}

		lStackCameraDeviceSimulator.stop();

		lStackCameraDeviceSimulator.close();

	}

}
