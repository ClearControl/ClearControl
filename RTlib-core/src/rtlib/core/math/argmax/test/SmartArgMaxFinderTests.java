package rtlib.core.math.argmax.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Test;

import rtlib.core.math.argmax.SmartArgMaxFinder;
import rtlib.core.units.Magnitudes;

public class SmartArgMaxFinderTests
{

	@Test
	public void basicTest()
	{
		SmartArgMaxFinder lSmartArgMaxFinder = new SmartArgMaxFinder();

		{
			double[] lX = new double[]
			{ 0, 1, 2, 3, 4 };
			double[] lY = new double[]
			{ 0.11, 0.21, 0.3, 0.19, 0.09 };
			double[] lFittedY = null;
			Double lArgmax = null;

			int lNumberOfIterations = 100;
			long lStart = System.nanoTime();
			for (int i = 0; i < lNumberOfIterations; i++)
			{
				lArgmax = lSmartArgMaxFinder.argmax(lX, lY);
				lFittedY = lSmartArgMaxFinder.fit(lX, lY);
			}
			long lStop = System.nanoTime();
			double lElapsed = Magnitudes.nano2milli((1.0 * lStop - lStart) / lNumberOfIterations);

			System.out.format("%g ms per estimation. \n", lElapsed);

			System.out.println(Arrays.toString(lX));
			System.out.println(Arrays.toString(lY));
			System.out.println(Arrays.toString(lFittedY));

			System.out.println(lArgmax);

			assertEquals(2, lArgmax, 0.15);
		}

		{
			double[] lX = new double[]
			{ 0, 1, 2, 3, 4 };
			double[] lY = new double[]
			{ 1, 2, 3, 4, 5 };

			Double lArgmax = lSmartArgMaxFinder.argmax(lX, lY);

			double[] lFittedY = lSmartArgMaxFinder.fit(lX, lY);

			System.out.println(Arrays.toString(lX));
			System.out.println(Arrays.toString(lY));
			System.out.println(Arrays.toString(lFittedY));

			System.out.println(lArgmax);

			assertEquals(4, lArgmax, 0.01);
		}

	}

	@Test
	public void benchmark() throws IOException, URISyntaxException
	{

		SmartArgMaxFinder lSmartArgMaxFinder = new SmartArgMaxFinder();
		double lMaxError = ArgMaxTester.test(lSmartArgMaxFinder, 15);
		assertEquals(0, lMaxError, 1);

	}

	@Test
	public void benchmarkWithFitEstimation() throws IOException,
																					URISyntaxException
	{
		SmartArgMaxFinder lSmartArgMaxFinder = new SmartArgMaxFinder();
		double lMaxError = ArgMaxTester.test(lSmartArgMaxFinder, 8);
		assertEquals(0, lMaxError, 0.5);

	}

}
