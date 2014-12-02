package rtlib.core.math.argmax.methods;

import gnu.trove.list.array.TDoubleArrayList;
import rtlib.core.math.argmax.ArgMaxFinder1D;

public class DenoisingArgMaxFinder implements ArgMaxFinder1D
{

	private ArgMaxFinder1D mArgMaxFinder1D;

	public DenoisingArgMaxFinder(ArgMaxFinder1D pArgMaxFinder1D)
	{
		super();
		mArgMaxFinder1D = pArgMaxFinder1D;
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{

		TDoubleArrayList lY = new TDoubleArrayList(pY);

		final int lLength = pY.length;

		lY.set(0, 0.25 * (3 * pY[0] + pY[1]));
		for (int i = 1; i < lLength - 1; i++)
		{
			lY.set(i, 0.25 * (pY[i - 1] + 2 * pY[i] + pY[i + 1]));
		}
		lY.set(0, 0.25 * (3 * pY[lLength - 1] + pY[lLength - 2]));

		final double lArgmax = mArgMaxFinder1D.argmax(pX,
																									lY.toArray());

		return lArgmax;
	}

}
