package score.functions;

import score.interfaces.StaveInterface;

public class BinaryPattern
{

	public static final void write(	final StaveInterface pStave,
																	final int pPatternPeriod,
																	final int pPatternOnLength)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		for (int i = 0; i < lArrayLength; i++)
		{
			final int modulo = i % pPatternPeriod;
			if (modulo < pPatternOnLength)
				array[i] = 1;
			else
				array[i] = 0;
		}
	}

}
