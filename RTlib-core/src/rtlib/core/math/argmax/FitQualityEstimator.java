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

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import rtlib.core.math.argmax.methods.GaussianFitArgMaxFinder;
import rtlib.core.math.argmax.methods.ParabolaFitArgMaxFinder;

public class FitQualityEstimator
{

	private static final Executor sExecutor = Executors.newCachedThreadPool();

	private UnivariateDifferentiableFunction mUnivariateDifferentiableFunction;

	private Double mRealDataRMSD;

	private class RandomizedDataGaussianFitter implements
																						Callable<Double>
	{
		private static final int cMaxIterationsForRandomizedDataFitting = 128;
		private Random lRandom = new Random();
		private double[] mX;
		private double[] mY;
		private UnivariateDifferentiableFunction mUnivariateDifferentiableFunction;

		public RandomizedDataGaussianFitter(double[] pX,
																				double[] pY,
																				boolean pShuffle)
		{
			mX = pX;
			mY = shuffle(pShuffle, lRandom, pY);
		}

		@Override
		public Double call() throws Exception
		{
			GaussianFitArgMaxFinder lGaussianFitArgMaxFinder = new GaussianFitArgMaxFinder(cMaxIterationsForRandomizedDataFitting);
			try
			{
				double[] lFit = lGaussianFitArgMaxFinder.fit(mX, mY);
				if (lFit == null)
					throw new Exception();
				setFunction(lGaussianFitArgMaxFinder.getFunction());

				double lRMSD = lGaussianFitArgMaxFinder.getRMSD();
				// System.out.println("Gaussian lRMSD=" + lRMSD);
				return lRMSD;
			}
			catch (Throwable e)
			{
				// e.printStackTrace();
				ParabolaFitArgMaxFinder lParabolaFitArgMaxFinder = new ParabolaFitArgMaxFinder(cMaxIterationsForRandomizedDataFitting);
				try
				{
					double[] lFit = lParabolaFitArgMaxFinder.fit(mX, mY);
					if (lFit == null)
						return null;
					setFunction(lParabolaFitArgMaxFinder.getFunction());
					double lRMSD = lParabolaFitArgMaxFinder.getRMSD();

					double[] lCoefficients = lParabolaFitArgMaxFinder.getFunction()
																														.getCoefficients();

					if (lCoefficients.length == 1)
						return null;
					if (lCoefficients.length == 3)
					{
						double a = lCoefficients[2];
						if (a > 0)
							return null;
					}

					// System.out.println("Parabola lRMSD=" + lRMSD);/**/

					return lRMSD;
				}
				catch (Throwable e1)
				{
					e1.printStackTrace();
					return null;
				}/**/

			}
		}

		public UnivariateDifferentiableFunction getFunction()
		{
			return mUnivariateDifferentiableFunction;
		}

		public void setFunction(UnivariateDifferentiableFunction pUnivariateDifferentiableFunction)
		{
			mUnivariateDifferentiableFunction = pUnivariateDifferentiableFunction;
		}

	}

	public Double probability(double[] pX, double[] pY)
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
			mRealDataRMSD = lDataGaussianFitter.call();

			if (mRealDataRMSD == null)
				return 0.0;

			mUnivariateDifferentiableFunction = lDataGaussianFitter.getFunction();

			final int lNumberOfRandomizedDatasets = max(128, 32 * pX.length);
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
					if (lRMSD != null)
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

			final double lProbabilityThatRandomDataHasWorseFit = lNormalDistribution.cumulativeProbability(mRealDataRMSD);

			final double lFitProbability = 1 - lProbabilityThatRandomDataHasWorseFit;

			return lFitProbability;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return 0.0;
		}
	}

	public Double getRMSD()
	{
		return mRealDataRMSD;
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

	public double[] getFit(double[] pX, double[] pY)
	{
		double lMin = Double.POSITIVE_INFINITY;
		double lMax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < pY.length; i++)
		{
			lMin = min(lMin, pY[i]);/**/
			lMax = max(lMax, pY[i]);/**/
		}

		double[] lFittedY = new double[pX.length];
		for (int i = 0; i < pX.length; i++)
		{
			lFittedY[i] = lMin + (lMax - lMin)
										* mUnivariateDifferentiableFunction.value(pX[i]);
		}
		return lFittedY;
	}

}
