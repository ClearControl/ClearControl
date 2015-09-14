package rtlib.microscope.lightsheet.calibrator.utils;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.fragmented.FragmentedMemoryInterface;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class ImageAnalysisUtils
{

	public static double[] computeImageAverageIntensityPerPlane(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();
		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();
		double[] lIntensityArray = new double[lNumberOfPlanes];
		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemoryInterface = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemoryInterface);

			double lSum = (double) 0;
			long lCount = 0;

			while (lBuffer.hasRemaining())
			{
				lSum += lBuffer.readChar();
				lCount++;
			}
			lIntensityArray[p] = lSum / lCount;
		}

		return lIntensityArray;
	}

	public static double[] computeSumPowerIntensityPerPlane(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();
		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();
		double[] lIntensityArray = new double[lNumberOfPlanes];
		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemoryInterface = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemoryInterface);

			double lSumOfPowers = (double) 0;
			long lCount = 0;

			while (lBuffer.hasRemaining())
			{
				float lValue = 1.0f * lBuffer.readChar();
				float lSquareValue = lValue * lValue;
				float lQuarticValue = lSquareValue * lSquareValue;
				float lOcticValue = lQuarticValue * lQuarticValue;
				lSumOfPowers += lOcticValue;
				lCount++;
			}
			lIntensityArray[p] = lSumOfPowers / lCount;
		}

		return lIntensityArray;
	}

	public static double computeImageSumIntensity(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();
		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();
		double lSumIntensity = 0;
		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemoryInterface = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemoryInterface);

			while (lBuffer.hasRemaining())
			{
				lSumIntensity += lBuffer.readChar();
			}
		}

		return lSumIntensity;
	}

	public static Vector2D[] findBrightestPointsForEachPlane(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();

		Vector2D[] lPoints = new Vector2D[lNumberOfPlanes];

		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();

		long lWidth = pImage.dimension(0);
		long lHeight = pImage.dimension(1);

		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemory = lFragmentedMemory.get(p);

			int lMax = Integer.MIN_VALUE;
			int foundx = -1;
			int foundy = -1;

			for (int y = 0; y < lHeight; y++)
				for (int x = 0; x < lHeight; x++)
				{
					long lIndex = x + lWidth * y;
					int lValue = lContiguousMemory.getCharAligned(lIndex);
					if (lValue > lMax)
					{
						lMax = lValue;
						foundx = x;
						foundy = y;
					}
				}

			Vector2D lVector2D = new Vector2D(foundx, foundy);
			lPoints[p] = lVector2D;
		}

		return lPoints;
	}
}
