package score.functions;

import score.interfaces.StaveInterface;

public class Interval
{

	public static void add(	StaveInterface pStave,
														double pSyncStart,
														double pSyncStop,
														double pValueInside,
														double pValueOutside)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		final int lStartInteger = pStave.getTimePointFromNormalized(pSyncStart);
		final int lStopInteger = pStave.getTimePointFromNormalized(pSyncStop);

		final int lMaxIntegerAmplitude = pStave.getMaximalSignalIntegerAmplitude();

		final short lValueInsideShort = (short) Math.round(lMaxIntegerAmplitude * pValueInside);
		final short lValueOutsideShort = (short) Math.round(lMaxIntegerAmplitude * pValueOutside);

		for (int i = 0; i < lStartInteger; i++)
		{
			array[i] += lValueOutsideShort;
		}

		for (int i = lStartInteger; i < lStopInteger; i++)
		{
			array[i] += lValueInsideShort;
		}

		for (int i = lStopInteger; i < lArrayLength; i++)
		{
			array[i] += lValueOutsideShort;
		}

	}

}
