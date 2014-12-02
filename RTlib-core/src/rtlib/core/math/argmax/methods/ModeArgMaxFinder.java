package rtlib.core.math.argmax.methods;

import rtlib.core.math.argmax.ArgMaxFinder1D;

public class ModeArgMaxFinder implements ArgMaxFinder1D
{

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		double lArgMax = 0;
		double lMaxY = Double.NEGATIVE_INFINITY;

		final int lLength = pY.length;
		for (int i = 0; i < lLength; i++)
		{
			final double lY = pY[i];
			if (lY > lMaxY)
			{
				lArgMax = pX[i];
				lMaxY = lY;
			}
		}
		return lArgMax;
	}

}
