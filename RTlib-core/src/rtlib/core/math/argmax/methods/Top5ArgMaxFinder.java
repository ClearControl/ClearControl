package rtlib.core.math.argmax.methods;

import gnu.trove.list.array.TDoubleArrayList;
import rtlib.core.math.argmax.ArgMaxFinder1D;

public class Top5ArgMaxFinder implements ArgMaxFinder1D
{

	private ArgMaxFinder1D mArgMaxFinder1D;

	public Top5ArgMaxFinder(ArgMaxFinder1D pArgMaxFinder1D)
	{
		super();
		mArgMaxFinder1D = pArgMaxFinder1D;
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		int lIndexMax = 0;
		double lMaxY = Double.NEGATIVE_INFINITY;

		final int lLength = pY.length;
		for (int i = 0; i < lLength; i++)
		{
			final double lY = pY[i];
			if (lY > lMaxY)
			{
				lIndexMax = i;
				lMaxY = lY;
			}
		}

		TDoubleArrayList lTop5X = new TDoubleArrayList();
		TDoubleArrayList lTop5Y = new TDoubleArrayList();

		lTop5X.add(pX[lIndexMax]);
		lTop5Y.add(pY[lIndexMax]);

		int lLeftToAdd = 4;

		int i = 1;
		while (lLeftToAdd > 0)
		{
			if (0 <= lIndexMax + i && lIndexMax + i < lLength - 1)
			{
				lTop5X.add(pX[lIndexMax + i]);
				lTop5Y.add(pY[lIndexMax + i]);
				lLeftToAdd--;
			}

			if (0 <= lIndexMax - i && lIndexMax - i < lLength - 1)
			{
				lTop5X.insert(0, pX[lIndexMax - i]);
				lTop5Y.insert(0, pY[lIndexMax - i]);
				lLeftToAdd--;
			}
		}
		
		double[] lTop5XArray = lTop5X.toArray();
		double[] lTop5YArray =lTop5Y.toArray();
		
		double lArgmax = mArgMaxFinder1D.argmax(lTop5XArray, lTop5YArray);

		return lArgmax;
	}

}
