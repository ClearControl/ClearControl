package rtlib.ip.iqm;

import static java.lang.Math.sqrt;
import static java.lang.Math.toIntExact;

import org.jtransforms.dct.DoubleDCT_2D;

import pl.edu.icm.jlargearrays.DoubleLargeArray;
import rtlib.core.memory.SizeOf;
import rtlib.kam.memory.cursor.NDCursor;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;
import rtlib.kam.memory.ndarray.NDArrayTyped;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class DCTS2D implements ImageQualityMetricInterface<Character>
{
	private Table<Long, Long, DoubleDCT_2D> mDoubleDCT2DCache = HashBasedTable.create();

	private NDArrayTypedDirect<Double> mDoubleWorkingNDArray;

	private double mPSFSupportRadius = 3;

	public DCTS2D()
	{
		super();
	}

	private DoubleDCT_2D getDCTForWidthAndHeight(	long pWidth,
																								long pHeight)
	{
		DoubleDCT_2D lDoubleDCT_2D = mDoubleDCT2DCache.get(	pWidth,
																												pHeight);

		if (lDoubleDCT_2D == null)
		{
			try
			{
				lDoubleDCT_2D = new DoubleDCT_2D(pHeight, pWidth, true);
				mDoubleDCT2DCache.put(pWidth, pHeight, lDoubleDCT_2D);
			}
			catch (Throwable e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return lDoubleDCT_2D;
	}

	public final double[] computeImageQualityMetric(NDArrayTyped<Character> pNDArray)
	{
		final long lLengthInElements = pNDArray.getLengthInElements();
		final long lWidth = pNDArray.getWidth();
		final long lHeight = pNDArray.getHeight();
		final long lDepth = pNDArray.getDepth();

		if (mDoubleWorkingNDArray != null)
		{
			boolean lWrongDimensions = mDoubleWorkingNDArray.getWidth() != lWidth || mDoubleWorkingNDArray.getHeight() != lHeight
																	|| mDoubleWorkingNDArray.getDepth() != lDepth;
			if (lWrongDimensions)
			{
				mDoubleWorkingNDArray.free();
				mDoubleWorkingNDArray = null;
			}
		}

		if (mDoubleWorkingNDArray == null)
		{
			mDoubleWorkingNDArray = NDArrayTypedDirect.allocateTXYZ(Double.class,
																															lWidth,
																															lHeight,
																															lDepth);
		}

		for (long i = 0; i < lLengthInElements; i++)
		{
			final char lChar = pNDArray.getCharAligned(i);
			final double lDouble = lChar;
			mDoubleWorkingNDArray.setDoubleAligned(i, lDouble);
		}

		double[] lDCTSArray = new double[toIntExact(lDepth)];

		long lBaseAddress = mDoubleWorkingNDArray.getRAM().getAddress();
		NDCursor lDefaultCursor = mDoubleWorkingNDArray.getDefaultCursor();
		for (int z = 0; z < lDepth; z++, lDefaultCursor.incrementCursorPosition(3))
		{
			long lCurrentFlatIndex = lDefaultCursor.getCurrentFlatIndex();
			long lAddress = lBaseAddress + SizeOf.sizeOfDouble()
											* lCurrentFlatIndex;
			DoubleLargeArray lDoubleLargeArray = new DoubleLargeArray(mDoubleWorkingNDArray,
																																lAddress,
																																lWidth * lHeight);

			final double lDCTS = computeDCTSForSinglePlane(	lDoubleLargeArray,
																											lWidth,
																											lHeight,
																											getPSFSupportRadius());

			lDCTSArray[z] = lDCTS;
		}

		return lDCTSArray;
	}

	public final double computeDCTSForSinglePlane(DoubleLargeArray pDoubleLargeArray,
																								long pWidth,
																								long pHeight,
																								double pPSFSupportRadius)
	{
		DoubleDCT_2D lDCTForWidthAndHeight = getDCTForWidthAndHeight(	pWidth,
																																	pHeight);

		lDCTForWidthAndHeight.forward(pDoubleLargeArray, false);

		normalizeL2(pDoubleLargeArray);

		final long lOTFSupportRadiusX = Math.round(pWidth / pPSFSupportRadius);
		final long lOTFSupportRadiusY = Math.round(pHeight / pPSFSupportRadius);

		final double lEntropy = entropyPerPixelSubTriangle(	pDoubleLargeArray,
																												pWidth,
																												pHeight,
																												0,
																												0,
																												lOTFSupportRadiusX,
																												lOTFSupportRadiusY);

		return lEntropy;
	}

	private void normalizeL2(DoubleLargeArray pDoubleLargeArray)
	{
		final double lL2 = computeL2(pDoubleLargeArray);
		final double lIL2 = 1.0 / lL2;
		final long lLength = pDoubleLargeArray.length();

		for (long i = 0; i < lLength; i++)
		{
			final double lValue = pDoubleLargeArray.getDouble(i);
			pDoubleLargeArray.setDouble(i, lValue * lIL2);
		}
	}

	private double computeL2(DoubleLargeArray pDoubleLargeArray)
	{
		final long lLength = pDoubleLargeArray.length();

		double l2 = 0;
		for (long i = 0; i < lLength; i++)
		{
			final double lValue = pDoubleLargeArray.getDouble(i);
			l2 += lValue * lValue;
		}

		return sqrt(l2);
	}

	private final double entropyPerPixelSubTriangle(DoubleLargeArray pDoubleLargeArray,
																									final long pWidth,
																									final long pHeight,
																									final long xl,
																									final long yl,
																									final long xh,
																									final long yh)
	{
		double entropy = 0;
		for (long y = yl; y < yh; y++)
		{
			final long yi = y * pWidth;

			final long xend = xh - y * xh / yh;
			entropy = entropySub(pDoubleLargeArray, xl, entropy, yi, xend);
		}
		entropy = -entropy / (2 * xh * yh);

		return entropy;
	}

	private double entropySub(DoubleLargeArray pDoubleLargeArray,
														final long xl,
														final double entropy,
														final long yi,
														final long xend)
	{
		double lEntropy = entropy;
		for (long x = xl; x < xend; x++)
		{
			final long i = yi + x;
			final double value = pDoubleLargeArray.getDouble(i);
			if (value > 0)
			{
				lEntropy += value * Math.log(value);
			}
			else if (value < 0)
			{
				lEntropy += -value * Math.log(-value);
			}
		}
		return lEntropy;
	}

	public double getPSFSupportRadius()
	{
		return mPSFSupportRadius;
	}

	public void setPSFSupportRadius(double pPSFSupportRadius)
	{
		mPSFSupportRadius = pPSFSupportRadius;
	}

}
