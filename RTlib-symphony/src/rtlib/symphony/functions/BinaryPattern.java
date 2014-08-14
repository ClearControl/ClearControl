package rtlib.symphony.functions;

import rtlib.symphony.interfaces.StaveInterface;

public class BinaryPattern
{

	public static final void mult(final StaveInterface pStave,
																final int pPatternPeriod,
																final int pPatternOnLength,
																final int pPatternPhaseIndex,
																final int pPatternPhaseIncrement)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		final int lPatternPhase = pPatternPhaseIndex * pPatternPhaseIncrement;

		final int lPatternPeriodNonZero = pPatternPeriod <= 0	? 1
																													: pPatternPeriod;
		for (int i = 0; i < lArrayLength; i++)
		{
			final int modulo = (i + lPatternPhase) % lPatternPeriodNonZero;
			if (modulo < pPatternOnLength)
			{
				array[i] *= 1;
			}
			else
			{
				array[i] *= 0;
			}
		}
	}

}
