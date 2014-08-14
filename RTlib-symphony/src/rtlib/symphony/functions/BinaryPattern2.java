package rtlib.symphony.functions;

import rtlib.symphony.interfaces.StaveInterface;

public class BinaryPattern2
{

	public static final void mult(final StaveInterface pStave,
																final double pPatternLineLength,
																double pPatternPeriod,
																double pPatternOnLength,
																double pPatternPhaseIndex,
																double pPatternPhaseIncrement)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		final double lGranularity = pPatternLineLength / lArrayLength;

		pPatternPeriod = constrain(pPatternPeriod, lGranularity);
		pPatternOnLength = constrain(pPatternOnLength, lGranularity);
		pPatternPhaseIndex = constrain(pPatternPhaseIndex, lGranularity);
		pPatternPhaseIncrement = constrain(	pPatternPhaseIncrement,
																				lGranularity);

		final double lPatternPhase = pPatternPhaseIndex * pPatternPhaseIncrement;

		final double lPatternPeriodNonZero = pPatternPeriod <= 0 ? 1
																														: pPatternPeriod;
		for (int i = 0; i < lArrayLength; i++)
		{
			final double x = (double) i / (lArrayLength - 1);

			final int value = f(pPatternLineLength,
													lPatternPeriodNonZero,
													lPatternPhase,
													pPatternOnLength,
													x);
			array[i] *= value;
		}
	}

	private static final int f(	final double L,
															final double l,
															final double Phi,
															final double lon,
															final double x)
	{
		final double xp = L * x;
		final double xpphi = xp + Phi;
		final double y = mod(xpphi, l);
		if (y <= lon)
		{
			return 1;
		}
		else
		{
			return 0;
		}

	}

	private static final double mod(final double x, final double y)
	{
		final double modulo = x - y * Math.floor(x / y);
		return modulo;
	}

	private static final double constrain(final double pX,
																				final double pGranularity)
	{
		return pGranularity * Math.round(pX / pGranularity);

	}

}
