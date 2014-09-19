package rtlib.ip.iqm.fitting;

public class ModeArgMaxFinder implements ArgMaxFinder1D
{

	@Override
	public double argmax(double[] pX, double[] pY)
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
