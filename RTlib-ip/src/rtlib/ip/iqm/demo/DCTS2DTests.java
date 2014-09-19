package rtlib.ip.iqm.demo;

import static java.lang.Math.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.junit.Test;

import rtlib.core.units.Magnitudes;
import rtlib.ip.iqm.DCTS2D;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;
import rtlib.kam.memory.ndarray.NDArrayTyped;

public class DCTS2DTests
{

	@Test
	public void test()
	{
		DCTS2D lDCTS2D = new DCTS2D();

		int lWidth = 513;
		int lHeight = 511;
		int lDepth = 100;

		int repeats = 30;

		NDArrayTyped<Character> lNDArray = NDArrayTypedDirect.allocateTXYZ(	Character.class,
																																				lWidth,
																																				lHeight,
																																				lDepth);
		long lLengthInElements = lNDArray.getLengthInElements();
		long lSizeInBytes = lNDArray.getSizeInBytes();

		assertEquals(lWidth * lHeight * lDepth, lLengthInElements);
		assertEquals(lWidth * lHeight * lDepth * 2, lSizeInBytes);

		for (long i = 0; i < lLengthInElements; i++)
		{
			lNDArray.setCharAligned(i, (char) (random() * (1 << 10)));
		}

		double[] lComputeDCTS = new double[lDepth];

		long lStartTimeInNs = System.nanoTime();
		for (int r = 0; r < repeats; r++)
			lComputeDCTS = lDCTS2D.computeImageQualityMetric(lNDArray);
		long lStopTimeInNs = System.nanoTime();

		double lElapsedTimeInMs = Magnitudes.nano2milli((lStopTimeInNs - lStartTimeInNs) / repeats);
		System.out.println("time per slicewise-dcts computation on a stack: " + lElapsedTimeInMs
												+ " ms");

		System.out.println(Arrays.toString(lComputeDCTS));

		for (double lValue : lComputeDCTS)
		{
			assertFalse(Double.isNaN(lValue));
			assertFalse(Double.isInfinite(lValue));
			assertFalse(lValue == 0);

		}

	}

}
