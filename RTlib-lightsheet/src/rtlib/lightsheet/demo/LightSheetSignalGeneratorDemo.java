package rtlib.lightsheet.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

import rtlib.lightsheet.LightSheetSignalGenerator;
import rtlib.symphony.devices.nirio.NIRIOSignalGenerator;

public class LightSheetSignalGeneratorDemo
{

	@Test
	public void demo() throws InterruptedException, ExecutionException
	{

		NIRIOSignalGenerator lNIRIOSignalGenerator = new NIRIOSignalGenerator();
		LightSheetSignalGenerator lLightSheetSignalGenerator = new LightSheetSignalGenerator(	lNIRIOSignalGenerator,
																																													9.74,
																																													512);

		assertTrue(lLightSheetSignalGenerator.open());
		assertTrue(lLightSheetSignalGenerator.start());

		lLightSheetSignalGenerator.prepareQueueFor2DContinuousAcquisition();

		for (int i = 0; i < 10000; i++)
		{
			Future<Boolean> lPlayQueue = lLightSheetSignalGenerator.playQueue();
			// System.out.println("waiting...");
			Boolean lBoolean = lPlayQueue.get();
			assertTrue(lBoolean);
			// System.out.println("done");
		}

		assertTrue(lLightSheetSignalGenerator.stop());
		assertTrue(lLightSheetSignalGenerator.close());
	}

}
