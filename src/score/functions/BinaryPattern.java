package score.functions;

import score.interfaces.StaveInterface;

public class BinaryPattern
{

	public static final void mult(	final StaveInterface pStave,
																	final int pPatternPeriod,
																	final int pPatternPhase,
																	final int pPatternOnLength,
																	final int pPatternPhaseIncrement)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		final int lPatternPeriodNonZero = pPatternPeriod<=0?1:pPatternPeriod;
		for (int i = 0; i < lArrayLength; i++)
		{
			final int modulo = (i+pPatternPhase*pPatternPhaseIncrement) % lPatternPeriodNonZero;
			if (modulo < pPatternOnLength)
				array[i] *= 1;
			else
				array[i] *= 0;
		}
	}

}
