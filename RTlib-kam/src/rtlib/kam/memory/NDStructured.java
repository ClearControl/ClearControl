package rtlib.kam.memory;

public interface NDStructured
{
	public long getDimension();

	public long getSizeAlongDimension(final int pDimensionIndex);

	public long getVolume();

	public long getLengthInElements();

	public boolean isVectorized();

	public default long[] getDimensions()
	{
		long[] lDimensions = new long[(int) (getDimension() + 1)];
		for (int i = 0; i < getDimension() + 1; i++)
		{
			lDimensions[i] = getSizeAlongDimension(i);
		}
		return lDimensions;
	}

	public default long getWidth()
	{
		return getSizeAlongDimension(1);
	}

	public default long getHeight()
	{
		return getSizeAlongDimension(2);
	}

	public default long getDepth()
	{
		return getSizeAlongDimension(3);
	}
}
