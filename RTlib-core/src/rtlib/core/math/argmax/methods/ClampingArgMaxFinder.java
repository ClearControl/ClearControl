package rtlib.core.math.argmax.methods;

import static java.lang.Math.max;
import static java.lang.Math.min;
import rtlib.core.math.argmax.ArgMaxFinder1D;

public class ClampingArgMaxFinder implements ArgMaxFinder1D
{

	private ArgMaxFinder1D mArgMaxFinder1D;

	public ClampingArgMaxFinder(ArgMaxFinder1D pArgMaxFinder1D)
	{
		super();
		mArgMaxFinder1D = pArgMaxFinder1D;
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		Double lArgmax = mArgMaxFinder1D.argmax(pX, pY);

		if (lArgmax == null)
			return null;

		lArgmax = min(lArgmax, pX[pX.length - 1]);
		lArgmax = max(lArgmax, pX[0]);

		return lArgmax;
	}

	@Override
	public String toString()
	{
		return String.format("ClampingArgMaxFinder [%s]",
													mArgMaxFinder1D);
	}

}
