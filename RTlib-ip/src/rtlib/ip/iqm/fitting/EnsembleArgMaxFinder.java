package rtlib.ip.iqm.fitting;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.rank.Median;

public class EnsembleArgMaxFinder implements ArgMaxFinder1D
{

	private ArrayList<ArgMaxFinder1D> mArgMaxFinder1DList = new ArrayList<ArgMaxFinder1D>();
	private Median mMedian;

	public EnsembleArgMaxFinder()
	{
		super();
		mMedian = new Median();
	}

	public void add(ArgMaxFinder1D pArgMaxFinder1D)
	{
		mArgMaxFinder1DList.add(pArgMaxFinder1D);
	}

	@Override
	public double argmax(double[] pX, double[] pY)
	{
		TDoubleArrayList lArgMaxList = new TDoubleArrayList();

		for (ArgMaxFinder1D lArgMaxFinder1D : mArgMaxFinder1DList)
		{
			double lArgMax = lArgMaxFinder1D.argmax(pX, pY);
			lArgMaxList.add(lArgMax);
		}

		double lArgMaxMedian = mMedian.evaluate(lArgMaxList.toArray());

		return lArgMaxMedian;
	}

}
