package score.functions;

import score.interfaces.StaveInterface;

public class BinaryPattern2
{

	public static final void mult(final StaveInterface pStave,
																final double pPatternLineLength,
																final double pPatternPeriod,
																final double pPatternOnLength,
																final double pPatternPhaseIndex,
																final double pPatternPhaseIncrement)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		final double lPatternPhase = pPatternPhaseIndex * pPatternPhaseIncrement;

		final double lPatternPeriodNonZero = pPatternPeriod <= 0 ? 1
																														: pPatternPeriod;
		for (int i = 0; i < lArrayLength; i++)
		{
			final double x = ((double) i) / (lArrayLength - 1);

			final int value = f(pPatternLineLength,
													pPatternPeriod,
													lPatternPhase,
													pPatternOnLength,
													x);
			array[i] *= value;
		}
	}

	private static final int f(	double L,
															double l,
															double Phi,
															double lon,
															double x)
	{
		final double xp = L * x;
		final double xpphi = xp + Phi;
		final double y = mod(xpphi, l);
		if (y <= lon)
			return 1;
		else
			return 0;

	}

	private static final double mod(double x, double y)
	{
		final double modulo = x - y * Math.floor(x / y);
		return modulo;
	}

}
