package rtlib.cameras.devices.sim.test;

import java.io.IOException;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.junit.Test;

import rtlib.cameras.devices.sim.StackCameraDeviceSimulator;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.sourcesink.RandomStackSource;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclerInterface;

public class StackCameraDeviceSimulatorTests
{

	@Test
	public void test() throws IOException
	{
		final ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess>();

		final RecyclerInterface<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> lRecycler = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(lOffHeapPlanarStackFactory,
																																																																																																																					10);
		RandomStackSource lRandomStackSource = new RandomStackSource(	100L,
																																	101L,
																																	103L,
																																	lRecycler);

		BooleanVariable lTrigger = new BooleanVariable(	"CameraTrigger",
																										false);

		StackCameraDeviceSimulator lStackCameraDeviceSimulator = new StackCameraDeviceSimulator(lRandomStackSource,
																																														new UnsignedShortType(),
																																														lTrigger);

		ObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>> lStackVariable = lStackCameraDeviceSimulator.getStackVariable();

		lStackVariable.addSetListener((	StackInterface<UnsignedShortType, ShortOffHeapAccess> pOldStack,
																		StackInterface<UnsignedShortType, ShortOffHeapAccess> pNewStack) -> {
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
			lTrigger.setEdge(true);
		}

		lStackCameraDeviceSimulator.stop();

		lStackCameraDeviceSimulator.close();

	}

}
