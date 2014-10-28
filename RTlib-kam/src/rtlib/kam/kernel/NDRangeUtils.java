package rtlib.kam.kernel;

public class NDRangeUtils
{

	public static int[] range(int... pRange)
	{
		return pRange;
	}

	public static int volume(int... pRange)
	{
		int lVolume = 1;
		for (long value : pRange)
			lVolume *= value;
		return lVolume;
	}

	public static long get(long[] pRange, int pIndex)
	{
		if (pIndex >= pRange.length)
			return 1;
		return pRange[pIndex];
	}

	public static long[] zero(int pLength)
	{
		return new long[pLength];
	}

	public static long[] toLong4(long[] pRange)
	{
		if (pRange == null)
			return null;
		long[] lRange4 = new long[4];
		for (int i = 0; i < pRange.length; i++)
			lRange4[i] = pRange[i];
		return lRange4;
	}
}
