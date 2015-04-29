package rtlib.microscope.lightsheetmicroscope.lightsheet.demo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.junit.Test;

import rtlib.microscope.lightsheetmicroscope.lightsheet.LightSheet;
import rtlib.symphony.devices.nirio.NIRIOSignalGenerator;

public class LightSheetDemo
{

	@Test
	public void demo() throws InterruptedException, ExecutionException
	{

		final PolynomialFunction lPolynomialFunction = new PolynomialFunction(new double[]
		{ 0.0, 1.0 });
		final NIRIOSignalGenerator lNIRIOSignalGenerator = new NIRIOSignalGenerator();
		final LightSheet<PolynomialFunction> lLightSheetSignalGenerator = new LightSheet<PolynomialFunction>(	"Lightsheet",
																																																																				lNIRIOSignalGenerator,
																																																																				lPolynomialFunction,
																																																																				9.74,
																																																																				512,
																																																																				6);

		assertTrue(lLightSheetSignalGenerator.open());
		assertTrue(lLightSheetSignalGenerator.start());

		lLightSheetSignalGenerator.prepareQueueFor2DContinuousAcquisition();

		for (int i = 0; i < 10000; i++)
		{
			final Future<Boolean> lPlayQueue = lLightSheetSignalGenerator.playQueue();
			// System.out.println("waiting...");
			final Boolean lBoolean = lPlayQueue.get();
			assertTrue(lBoolean);
			// System.out.println("done");
		}

		assertTrue(lLightSheetSignalGenerator.stop());
		assertTrue(lLightSheetSignalGenerator.close());
	}
}
