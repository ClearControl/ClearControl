package rtlib.ip.iqm.test;

import static org.junit.Assert.assertFalse;
import io.scif.FormatException;
import io.scif.Plane;
import io.scif.Reader;
import io.scif.SCIFIO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import rtlib.core.units.Magnitude;
import rtlib.ip.iqm.DCTS2D;
import coremem.ContiguousMemoryInterface;

public class DCTS2DTests
{

	@Test
	public void test()	throws IOException,
						FormatException,
						InterruptedException
	{
		final File lTempFile = File.createTempFile(	DCTS2DTests.class.getSimpleName(),
													"test.tif");
		FileUtils.copyInputStreamToFile(DCTS2DTests.class.getResourceAsStream("./stacks/example.tif"),
										lTempFile);

		final SCIFIO lSCIFIO = new SCIFIO();
		final Reader lReader = lSCIFIO.initializer()
										.initializeReader(lTempFile.getAbsolutePath());

		final int lWidth = (int) lReader.openPlane(0, 0).getLengths()[0];
		final int lHeight = (int) lReader.openPlane(0, 0)
											.getLengths()[1];
		final int lDepth = (int) lReader.getPlaneCount(0);

		final int lPlaneLengthInElements = lWidth * lHeight;

		final DCTS2D lDCTS2D = new DCTS2D();

		final int repeats = 30;

		final OffHeapPlanarImgFactory<UnsignedShortType> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<UnsignedShortType>();

		final int[] lDim = new int[]
		{ lWidth, lHeight, lDepth };
		@SuppressWarnings("unchecked")
		final OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> lImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lOffHeapPlanarImgFactory.create(	lDim,
																																											new UnsignedShortType());

		for (int z = 0; z < lDepth; z++)
		{
			final ContiguousMemoryInterface lPlaneContiguousMemory = lImage.getPlaneContiguousMemory(z);

			final Plane lPlane = lReader.openPlane(0, z);
			final byte[] lBytes = lPlane.getBytes();

			for (int i = 0; i < lBytes.length; i++)
				lPlaneContiguousMemory.setByteAligned(i, lBytes[i]);
		}

		// new ImageJ();
		// final ImagePlus lShow = ImageJFunctions.show(lImage);

		double[] lComputeDCTS = new double[lDepth];

		final long lStartTimeInNs = System.nanoTime();
		for (int r = 0; r < repeats; r++)
			lComputeDCTS = lDCTS2D.computeImageQualityMetric(lImage);
		final long lStopTimeInNs = System.nanoTime();

		final double lElapsedTimeInMs = Magnitude.nano2milli((lStopTimeInNs - lStartTimeInNs) / repeats);
		System.out.println("time per slicewise-dcts computation on a stack: " + lElapsedTimeInMs
							+ " ms");

		System.out.println(Arrays.toString(lComputeDCTS));

		for (final double lValue : lComputeDCTS)
		{
			assertFalse(Double.isNaN(lValue));
			assertFalse(Double.isInfinite(lValue));
			assertFalse(lValue == 0);

		}

	}

}
