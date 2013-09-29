package score.functions;

import score.interfaces.StaveInterface;

public class HalfHalfHolePattern
{

	public static void write(final StaveInterface pStave)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		int j = 0;
		for (int i = 0; i < lArrayLength / 2; i++)
		{
			j++;
			array[j++] = 0;
		}

	}

}
