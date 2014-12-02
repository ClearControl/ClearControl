package rtlib.core.math.argmax.methods;

import rtlib.core.math.argmax.ArgMaxFinder1D;

public class COMArgMaxFinder implements ArgMaxFinder1D
{

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		if (pX.length == 0)
			return null;

		double lWeightedSum = 0;
		double lWeightsSum = 0;

		final int lLength = pY.length;
		for (int i = 0; i < lLength; i++)
		{
			final double lX = pX[i];
			final double lY = pY[i];
			lWeightedSum += lX * lY;
			lWeightsSum += lY;
		}

		final double lCenterOfMass = lWeightedSum / lWeightsSum;

		return lCenterOfMass;
	}

}
