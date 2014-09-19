package rtlib.ip.iqm.fitting;

public class COMArgMaxFinder implements ArgMaxFinder1D
{

	@Override
	public double argmax(double[] pX, double[] pY)
	{
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
