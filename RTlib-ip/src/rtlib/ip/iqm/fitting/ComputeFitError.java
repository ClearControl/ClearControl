package rtlib.ip.iqm.fitting;

import static java.lang.Math.abs;

public class ComputeFitError
{
	public static final double avgerror(double[] pY, double[] pFittedY)
	{
		double lAverageError = 0;
		for (int i = 0; i < pY.length; i++)
		{
			double lError = abs(pY[i] - pFittedY[i]);
			lAverageError += lError;
		}

		lAverageError = lAverageError / pY.length;

		return lAverageError;
	}
}
