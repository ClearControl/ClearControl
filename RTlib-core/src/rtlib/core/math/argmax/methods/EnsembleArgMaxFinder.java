package rtlib.core.math.argmax.methods;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.math3.stat.descriptive.rank.Median;

import rtlib.core.math.argmax.ArgMaxFinder1D;

public class EnsembleArgMaxFinder implements ArgMaxFinder1D
{
	private static final Executor sExecutor = Executors.newCachedThreadPool();

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

	private class ArgMaxCallable implements Callable<Double>
	{
		private double[] mX;
		private double[] mY;
		private ArgMaxFinder1D mArgMaxFinder1D;

		public ArgMaxCallable(ArgMaxFinder1D pArgMaxFinder1D,
													double[] pX,
													double[] pY)
		{
			mArgMaxFinder1D = pArgMaxFinder1D;
			mX = pX;
			mY = pY;
		}

		@Override
		public Double call() throws Exception
		{
			double lArgMax = mArgMaxFinder1D.argmax(mX, mY);
			return lArgMax;
		}

		@Override
		public String toString()
		{
			return mArgMaxFinder1D.getClass().toString();
		}

	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		ArrayList<FutureTask<Double>> lTaskList = new ArrayList<FutureTask<Double>>();
		HashMap<FutureTask<Double>, ArgMaxCallable> lTaskToCallableMap = new HashMap<FutureTask<Double>, ArgMaxCallable>();

		for (ArgMaxFinder1D lArgMaxFinder1D : mArgMaxFinder1DList)
		{
			ArgMaxCallable lArgMaxCallable = new ArgMaxCallable(lArgMaxFinder1D,
																													pX,
																													pY);
			FutureTask<Double> lArgMaxFutureTask = new FutureTask<Double>(lArgMaxCallable);
			sExecutor.execute(lArgMaxFutureTask);
			lTaskList.add(lArgMaxFutureTask);
			lTaskToCallableMap.put(lArgMaxFutureTask, lArgMaxCallable);
		}

		TDoubleArrayList lArgMaxList = new TDoubleArrayList();
		for (FutureTask<Double> lArgMaxFutureTask : lTaskList)
		{
			try
			{
				double lArgMax = lArgMaxFutureTask.get();
				System.out.println("class: " + lTaskToCallableMap.get(lArgMaxFutureTask)
														+ " argmax="
														+ lArgMax);
				lArgMaxList.add(lArgMax);
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		double lArgMaxMedian = mMedian.evaluate(lArgMaxList.toArray());

		return lArgMaxMedian;
	}
}
