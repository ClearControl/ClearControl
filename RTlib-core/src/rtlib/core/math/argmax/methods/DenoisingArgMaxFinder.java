package rtlib.core.math.argmax.methods;

import rtlib.core.math.argmax.ArgMaxFinder1D;

public class DenoisingArgMaxFinder implements ArgMaxFinder1D
{

	private final ArgMaxFinder1D mArgMaxFinder1D;

	public DenoisingArgMaxFinder(ArgMaxFinder1D pArgMaxFinder1D)
	{
		super();
		mArgMaxFinder1D = pArgMaxFinder1D;
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		final int lLength = pY.length;

		final double[] lY = new double[lLength];

		lY[0] = 0.25 * (3 * pY[0] + pY[1]);
		for (int i = 1; i < lLength - 1; i++)
		{
			lY[i] = 0.25 * (pY[i - 1] + 2 * pY[i] + pY[i + 1]);
		}
		lY[lLength - 1] = 0.25 * (3 * pY[lLength - 1] + pY[lLength - 2]);

		final Double lArgmax = mArgMaxFinder1D.argmax(pX, lY);

		return lArgmax;
	}

	@Override
	public String toString()
	{
		return String.format(	"DenoisingArgMaxFinder [%s]",
													mArgMaxFinder1D);
	}

}
