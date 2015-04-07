package rtlib.core.math.argmax.methods;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.rank.Median;

import rtlib.core.math.argmax.ArgMaxFinder1D;

public class EnsembleArgMaxFinder implements ArgMaxFinder1D
{
	private static final Executor sExecutor = Executors.newCachedThreadPool();
	private static final int cTimeOutInSeconds = 1;

	private final ArrayList<ArgMaxFinder1D> mArgMaxFinder1DList = new ArrayList<ArgMaxFinder1D>();
	private final Median mMedian;

	private final boolean mDebug = true;

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
		private final double[] mX;
		private final double[] mY;
		private final ArgMaxFinder1D mArgMaxFinder1D;

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
			final long lStartTimeInNs = System.nanoTime();
			final Double lArgMax = mArgMaxFinder1D.argmax(mX, mY);
			final long lStopTimeInNs = System.nanoTime();
			/*double lElapsedtimeInSeconds = Magnitudes.nano2unit(lStopTimeInNs - lStartTimeInNs);
			System.out.format("elapsed time: %g for %s \n",
													lElapsedtimeInSeconds,
													mArgMaxFinder1D.toString());/**/

			return lArgMax;
		}

		@Override
		public String toString()
		{
			return mArgMaxFinder1D.toString();
		}

	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		println("pX=" + Arrays.toString(pX));
		println("pY=" + Arrays.toString(pY));
		if (constant(pY))
			return null;

		final ArrayList<FutureTask<Double>> lTaskList = new ArrayList<FutureTask<Double>>();
		final HashMap<FutureTask<Double>, ArgMaxCallable> lTaskToCallableMap = new HashMap<FutureTask<Double>, ArgMaxCallable>();

		for (final ArgMaxFinder1D lArgMaxFinder1D : mArgMaxFinder1DList)
		{
			final ArgMaxCallable lArgMaxCallable = new ArgMaxCallable(lArgMaxFinder1D,
																													pX,
																													pY);
			final FutureTask<Double> lArgMaxFutureTask = new FutureTask<Double>(lArgMaxCallable);
			sExecutor.execute(lArgMaxFutureTask);
			lTaskList.add(lArgMaxFutureTask);
			lTaskToCallableMap.put(lArgMaxFutureTask, lArgMaxCallable);
		}

		final TDoubleArrayList lArgMaxList = new TDoubleArrayList();
		for (final FutureTask<Double> lArgMaxFutureTask : lTaskList)
		{
			try
			{
				final Double lArgMax = lArgMaxFutureTask.get(	cTimeOutInSeconds,
																								TimeUnit.SECONDS);
				if (lArgMax != null)
				{
					if (mDebug)
						System.out.println("class: " + lTaskToCallableMap.get(lArgMaxFutureTask)
																+ "\n\t\targmax="
																+ lArgMax);
					lArgMaxList.add(lArgMax);
				}
			}
			catch (final Throwable e)
			{
				if (mDebug)
					e.printStackTrace();
			}
		}

		final double lArgMaxMedian = mMedian.evaluate(lArgMaxList.toArray());

		return lArgMaxMedian;
	}

	private boolean constant(double[] pY)
	{
		for (int i = 0; i < pY.length; i++)
			if (pY[i] != pY[0])
				return false;
		return true;
	}

	@Override
	public String toString()
	{
		return String.format(	"EnsembleArgMaxFinder [mArgMaxFinder1DList=%s]",
													mArgMaxFinder1DList);
	}

	private void println(String pString)
	{

	}

}
