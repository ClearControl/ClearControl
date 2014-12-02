package rtlib.core.math.argmax.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import rtlib.core.math.argmax.methods.SmartArgMaxFinder;

public class SmartArgMaxFinderTests
{

	@Test
	public void basicTest()
	{
		SmartArgMaxFinder lSmartArgMaxFinder = new SmartArgMaxFinder();

		double[] lX = new double[]
		{ 0, 1, 2, 3, 4 };
		double[] lY = new double[]
		{ 0.1, 0.2, 0.3, 0.2, 0.1 };

		double lArgmax = lSmartArgMaxFinder.argmax(lX, lY);

		System.out.println(lArgmax);

		assertEquals(2, lArgmax, 0);

	}

	@Test
	public void benchmark() throws IOException, URISyntaxException
	{
		SmartArgMaxFinder lSmartArgMaxFinder = new SmartArgMaxFinder();

		ArgMaxTester.test(lSmartArgMaxFinder);

	}


}
