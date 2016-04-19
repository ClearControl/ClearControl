package rtlib.microscope.lsm.calibrator.utils;

import static java.lang.Math.max;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.fragmented.FragmentedMemoryInterface;
import gnu.trove.list.array.TDoubleArrayList;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class ImageAnalysisUtils
{

	public static double[] computePercentileIntensityPerPlane(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage,
																														int pPercentile)
	{
		int lNumberOfPlanes = pImage.numSlices();
		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();
		DescriptiveStatistics lDescriptiveStatistics = new DescriptiveStatistics();
		double[] lPercentileArray = new double[lNumberOfPlanes];
		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemoryInterface = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemoryInterface);

			lDescriptiveStatistics.clear();
			while (lBuffer.hasRemaining())
			{
				double lValue = lBuffer.readChar();
				lDescriptiveStatistics.addValue(lValue);
			}
			lPercentileArray[p] = lDescriptiveStatistics.getPercentile(pPercentile);
		}

		return lPercentileArray;
	}

	public static double[] computeImageAverageIntensityPerPlane(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();
		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();
		double[] lIntensityArray = new double[lNumberOfPlanes];
		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemoryInterface = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemoryInterface);

			double lSum = 0;
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

	public static double[] computeAveragePowerIntensityPerPlane(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();
		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();
		double[] lIntensityArray = new double[lNumberOfPlanes];
		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemoryInterface = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemoryInterface);

			double lSumOfPowers = 0;
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

	public static Vector2D[] findCOMOfBrightestPointsForEachPlane(OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pImage)
	{
		int lNumberOfPlanes = pImage.numSlices();

		Vector2D[] lPoints = new Vector2D[lNumberOfPlanes];

		FragmentedMemoryInterface lFragmentedMemory = pImage.getFragmentedMemory();

		long lWidth = pImage.dimension(0);
		long lHeight = pImage.dimension(1);

		TDoubleArrayList lXList = new TDoubleArrayList();
		TDoubleArrayList lYList = new TDoubleArrayList();

		for (int p = 0; p < lNumberOfPlanes; p++)
		{
			ContiguousMemoryInterface lContiguousMemory = lFragmentedMemory.get(p);
			ContiguousBuffer lBuffer = ContiguousBuffer.wrap(lContiguousMemory);

			int lMaxValue = 0;
			while (lBuffer.hasRemaining())
			{
				lMaxValue = max(lMaxValue, lBuffer.readChar());
			}

			lXList.clear();
			lYList.clear();

			for (int y = 0; y < lHeight; y++)
			{
				long lIndexY = lWidth * y;
				for (int x = 0; x < lWidth; x++)
				{
					long lIndex = lIndexY + x;
					int lValue = lContiguousMemory.getCharAligned(lIndex);
					if (lValue == lMaxValue)
					{
						lXList.add(x);
						lYList.add(y);
					}
				}
			}

			double lCOMX = StatUtils.percentile(lXList.toArray(), 50);
			double lCOMY = StatUtils.percentile(lYList.toArray(), 50);

			Vector2D lVector2D = new Vector2D(lCOMX, lCOMY);
			lPoints[p] = lVector2D;
		}

		return lPoints;
	}
}
