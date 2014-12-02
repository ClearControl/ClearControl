package rtlib.core.math.argmax;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import rtlib.core.math.argmax.methods.GaussianFitArgMaxFinder;
import rtlib.core.math.argmax.methods.ParabolaFitArgMaxFinder;

public class GaussianFitEstimator
{

	private static final Executor sExecutor = Executors.newCachedThreadPool();

	private class RandomizedDataGaussianFitter implements
																						Callable<Double>
	{
		private static final int cMaxIterationsForRandomizedDataFitting = 128;
		private Random lRandom = new Random();
		private double[] mX;
		private double[] mY;
		private boolean mShuffle;

		public RandomizedDataGaussianFitter(double[] pX,
																				double[] pY,
																				boolean pShuffle)
		{
			mX = pX;
			mShuffle = pShuffle;
			mY = shuffle(pShuffle, lRandom, pY);
		}

		@Override
		public Double call() throws Exception
		{
			GaussianFitArgMaxFinder lGaussianFitArgMaxFinder = new GaussianFitArgMaxFinder(cMaxIterationsForRandomizedDataFitting);
			try
			{
				lGaussianFitArgMaxFinder.fit(mX, mY);
				double lRMSD = lGaussianFitArgMaxFinder.getRMSD();
				return lRMSD;
			}
			catch (Throwable e)
			{
				ParabolaFitArgMaxFinder lParabolaFitArgMaxFinder = new ParabolaFitArgMaxFinder(cMaxIterationsForRandomizedDataFitting);
				try
				{
					lParabolaFitArgMaxFinder.fit(mX, mY);
					double lRMSD = lParabolaFitArgMaxFinder.getRMSD();
					return lRMSD;
				}
				catch (Throwable e1)
				{
					// e1.printStackTrace();
					return null;
				}/**/

			}
		}

	}

	public Double pvalue(double[] pX, double[] pY)
	{
		TDoubleArrayList lNormY = new TDoubleArrayList();
		double lMin = Double.POSITIVE_INFINITY;
		double lMax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < pY.length; i++)
		{
			lMin = min(lMin, pY[i]);/**/
			lMax = max(lMax, pY[i]);/**/
		}

		for (int i = 0; i < pX.length; i++)
		{
			final double lScaledValue = (pY[i] - lMin) / (lMax - lMin);
			lNormY.add(lScaledValue);
		}

		RandomizedDataGaussianFitter lDataGaussianFitter = new RandomizedDataGaussianFitter(pX,
																																												lNormY.toArray(),
																																												false);
		try
		{
			Double lRealDataRMSD = lDataGaussianFitter.call();

			if (lRealDataRMSD == null)
				return 1.0;

			final int lNumberOfRandomizedDatasets = 16 * pX.length;
			ArrayList<FutureTask<Double>> lTaskList = new ArrayList<FutureTask<Double>>(lNumberOfRandomizedDatasets);

			for (int i = 0; i < lNumberOfRandomizedDatasets; i++)
			{
				RandomizedDataGaussianFitter lRandomizedDataGaussianFitter = new RandomizedDataGaussianFitter(pX,
																																																			lNormY.toArray(),
																																																			true);
				FutureTask<Double> lFutureTask = new FutureTask<Double>(lRandomizedDataGaussianFitter);
				sExecutor.execute(lFutureTask);
				lTaskList.add(lFutureTask);
			}

			TDoubleArrayList lIRMSDList = new TDoubleArrayList();
			for (FutureTask<Double> lFutureTask : lTaskList)
			{
				try
				{
					Double lRMSD = lFutureTask.get();
					if (lIRMSDList != null)
						lIRMSDList.add(lRMSD);
				}
				catch (Throwable e)
				{
				}
			}

			Mean lMean = new Mean();
			Variance lVariance = new Variance();

			double lMeanValue = lMean.evaluate(lIRMSDList.toArray());
			double lVarianceValue = lVariance.evaluate(lIRMSDList.toArray());
			double lStandardDeviation = sqrt(lVarianceValue);

			NormalDistribution lNormalDistribution = new NormalDistribution(lMeanValue,
																																			lStandardDeviation);

			final double lProbabilityThatRandomDataHasWorseFit = lNormalDistribution.cumulativeProbability(lRealDataRMSD);

			final double lPValue = lProbabilityThatRandomDataHasWorseFit;

			return lPValue;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return 1.0;
		}
	}

	public Double nrmsd(double[] pX, double[] pY)
	{
		try
		{
			TDoubleArrayList lNormY = new TDoubleArrayList();
			double lMin = Double.POSITIVE_INFINITY;
			double lMax = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < pY.length; i++)
			{
				lMin = min(lMin, pY[i]);/**/
				lMax = max(lMax, pY[i]);/**/
			}

			for (int i = 0; i < pX.length; i++)
			{
				final double lScaledValue = (pY[i] - lMin) / (lMax - lMin);
				lNormY.add(lScaledValue);
			}
			GaussianFitArgMaxFinder lGaussianFitArgMaxFinder = new GaussianFitArgMaxFinder();
			lGaussianFitArgMaxFinder.fit(pX, lNormY.toArray());
			double lRMSD = lGaussianFitArgMaxFinder.getRMSD();
			return lRMSD;
		}
		catch (Exception e)
		{
			return 1.0;
		}
	}

	public static double[] shuffle(	boolean pShuffle,
																	Random pRandom,
																	double[] pArray)
	{
		double[] lNewArray = Arrays.copyOf(pArray, pArray.length);
		if (pShuffle)
			for (int i = lNewArray.length - 1; i > 0; i--)
			{
				int lIndex = pRandom.nextInt(i + 1);
				double lValue = lNewArray[lIndex];
				lNewArray[lIndex] = lNewArray[i];
				lNewArray[i] = lValue;
			}

		return lNewArray;
	}

}
