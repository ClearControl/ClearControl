package rtlib.symphony.functions;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import rtlib.symphony.interfaces.StaveInterface;

public class SinusPattern
{

	public static final void add(	final StaveInterface pStave,
																final double pSinusPeriod,
																final double pSinusPhase,
																final double pSinusAmplitude)
	{
		final int lArrayLength = pStave.getNumberOfTimePoints();
		final short[] array = pStave.getStaveArray();

		final int lSinusAmplitude = (int) (pSinusAmplitude * pStave.getMaximalSignalIntegerAmplitude());

		for (int i = 0; i < lArrayLength; i++)
		{
			final double lNormalizedTime = pStave.getNormalizedTimePoint(i);
			final short value = Utils.clampToShort(lSinusAmplitude * sin((lNormalizedTime + pSinusPhase) * (2 * PI)
																																		/ pSinusPeriod));
			array[i] += value;

		}
	}

}
