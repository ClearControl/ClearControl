package rtlib.kam.memory.cursor;

import java.util.Arrays;

public class NDBoundedCursor implements NDCursor
{
	protected final long[] dimensions;
	protected final long[] multipliers;
	protected final long[] ndindex;
	protected long flatindex;

	public static final NDBoundedCursor create1DCursor(final long pLength)
	{
		return new NDBoundedCursor(1, pLength);
	}

	public static final NDBoundedCursor create2DCursor(	final long pWidth,
																											final long pHeight)
	{
		return new NDBoundedCursor(1, pWidth, pHeight);
	}

	public static final NDBoundedCursor create3DCursor(	final long pWidth,
																											final long pHeight,
																											final long pDepth)
	{
		return new NDBoundedCursor(1, pWidth, pHeight, pDepth);
	}

	public static final NDBoundedCursor createNDCursor(final long... pDimensions)
	{
		return createNDVectorCursor(1, pDimensions);
	}

	public static final NDBoundedCursor createNDVectorCursor(	long pVectorLength,
																														long... pDimensions)
	{

		long[] lDimensions = new long[pDimensions.length + 1];
		for (int i = 0; i < lDimensions.length; i++)
		{
			if (i == 0)
				lDimensions[i] = pVectorLength;
			else
				lDimensions[i] = pDimensions[i - 1];
		}

		return new NDBoundedCursor(lDimensions);
	}

	private NDBoundedCursor(long... pDimensions)
	{
		super();
		dimensions = Arrays.copyOf(pDimensions, pDimensions.length);
		multipliers = new long[pDimensions.length];
		computeMultipliers(dimensions, multipliers);
		ndindex = new long[dimensions.length];
		flatindex = 0;
	}

	public static void computeMultipliers(long[] pDimensions,
																				long[] pMultipliers)
	{
		pMultipliers[0] = 1;
		for (int i = 1; i < pDimensions.length; i++)
		{
			pMultipliers[i] = pMultipliers[i - 1] * pDimensions[i - 1];
		}
	}

	@Override
	public long getDimension()
	{
		return dimensions.length - 1;
	}

	@Override
	public long[] getDimensions()
	{
		return dimensions;
	}

	@Override
	public long getMinPosition(int pDimensionIndex)
	{
		return 0;
	}

	@Override
	public long getMaxPosition(int pDimensionIndex)
	{
		return dimensions[pDimensionIndex] - 1;
	}

	@Override
	public long getVolume()
	{
		return getLengthFor(dimensions, 1, dimensions.length);
	}

	@Override
	public long getLengthInElements()
	{
		return getLengthFor(dimensions, 0, dimensions.length);
	}

	@Override
	public boolean isVectorized()
	{
		return dimensions[0] > 1;
	}

	public long getSizeAlongDimension(int pDimensionIndex)
	{
		return 1 + getMaxPosition(pDimensionIndex)
						- getMinPosition(pDimensionIndex);
	}

	@Override
	public long getCurrentFlatIndex()
	{
		return flatindex;
	}

	@Override
	public long getCurrentVectorIndex()
	{
		if (dimensions[0] == 1)
			return flatindex;
		else
			return flatindex / dimensions[0];
	}

	@Override
	public void setCursorPosition(int pDimensionIndex,
																final long pNewPosition)
	{
		final long lOldPosition = ndindex[pDimensionIndex];
		ndindex[pDimensionIndex] = pNewPosition;
		flatindex += delta(	multipliers,
												pDimensionIndex,
												pNewPosition - lOldPosition);
	}

	protected final long delta(	final long[] pMultipliers,
															final int pDimensionIndex,
															final long lDelta)
	{
		return pMultipliers[pDimensionIndex] * lDelta;
	}

	@Override
	public long[] getCursorPosition()
	{
		return ndindex;
	}

	@Override
	public long getCursorPosition(int pDimensionIndex)
	{
		return ndindex[pDimensionIndex];
	}

	public static long getVolume(long[] pDim)
	{
		return getLengthFor(pDim, 1, pDim.length);
	}

	public static long getLength(long[] pDim)
	{
		return getLengthFor(pDim, 0, pDim.length);
	}

	public static final long getLengthFor(long[] pDimensions,
																				final int pStart,
																				final int pEnd)
	{
		long volume = 1;
		final long[] ldim = pDimensions;
		final int l = ldim.length;
		for (int i = pStart; i < pEnd; i++)
		{
			volume *= ldim[i];
		}

		return volume;
	}

	public static final long getIndex(final long[] pDimensions,
																		final long... pVector)
	{
		final long[] ldim = pDimensions;
		final int nbdim = ldim.length;
		final int vectorl = pVector.length;
		final int diff = nbdim - vectorl;
		long index = 0;
		long stride = 1;

		for (int i = 0; i < nbdim; i++)
		{
			final int lVectorIndex = i - diff;
			if (lVectorIndex >= 0)
				index += stride * pVector[lVectorIndex];
			stride *= ldim[i];
		}

		return index;
	}

	@Override
	public void incrementCursorPosition(final int pDimensionIndex)
	{
		if (ndindex[pDimensionIndex] != dimensions[pDimensionIndex] - 1)
		{
			ndindex[pDimensionIndex]++;
			flatindex += multipliers[pDimensionIndex];
		}
	}

	@Override
	public void decrementCursorPosition(int pDimensionIndex)
	{
		if (ndindex[pDimensionIndex] != 0)
		{
			ndindex[pDimensionIndex]--;
			flatindex -= multipliers[pDimensionIndex];
		}
	}

}
