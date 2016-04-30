package clearcontrol.hardware.cameras.devices.sim.test;

import java.io.IOException;

import org.junit.Test;

import clearcontrol.core.variable.Variable;
import clearcontrol.hardware.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.stack.ContiguousOffHeapPlanarStackFactory;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.RandomStackSource;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

public class StackCameraDeviceSimulatorTests
{

	@Test
	public void test() throws IOException
	{
		final ContiguousOffHeapPlanarStackFactory lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory();

		final RecyclerInterface<StackInterface, StackRequest> lRecycler = new BasicRecycler<StackInterface, StackRequest>(lOffHeapPlanarStackFactory,
																																																											10);
		RandomStackSource lRandomStackSource = new RandomStackSource(	100L,
																																	101L,
																																	103L,
																																	lRecycler);

		Variable<Boolean> lTrigger = new Variable<Boolean>(	"CameraTrigger",
																												false);

		StackCameraDeviceSimulator lStackCameraDeviceSimulator = new StackCameraDeviceSimulator("StackCamera",
																																														lRandomStackSource,
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

		for (int i = 0; i < 20; i++)
		{
			lStackCameraDeviceSimulator.addCurrentStateToQueue();
		}

		lStackCameraDeviceSimulator.finalizeQueue();

		lStackCameraDeviceSimulator.playQueue();

		for (int i = 0; i < 20; i++)
		{
			lTrigger.setEdge(false, true);
		}

		lStackCameraDeviceSimulator.stop();

		lStackCameraDeviceSimulator.close();

	}

}
