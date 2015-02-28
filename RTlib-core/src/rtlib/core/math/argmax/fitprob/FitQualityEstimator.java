package rtlib.core.math.argmax.fitprob;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

public class FitQualityEstimator
{
	private static final int cMaxNumberOfRandomizedDatasets = 10000;

	private static final Executor sExecutor = Executors.newFixedThreadPool(Runtime.getRuntime()
																																								.availableProcessors());

	private static final ConcurrentHashMap<Integer, NormalDistribution> sNullHypothesisDistribution = new ConcurrentHashMap<>();

	private UnivariateDifferentiableFunction mUnivariateDifferentiableFunction;

	private Double mRealDataRMSD;

	public NormalDistribution getNullHypothesisDistribution(int lLength)
	{
		// System.out.println("getNullHypothesisDistribution...");
		NormalDistribution lNormalDistribution = sNullHypothesisDistribution.get(lLength);
		if (lNormalDistribution == null)
		{
			lNormalDistribution = computeNullHypothesisDistribution(lLength);
			sNullHypothesisDistribution.put(lLength, lNormalDistribution);
		}
		// System.out.println("getNullHypothesisDistribution=" +
		// lNormalDistribution);
		return lNormalDistribution;
	}

	public NormalDistribution computeNullHypothesisDistribution(int lLength)
	{
		// System.out.println("computeNullHypothesisDistribution...");
		final int lNumberOfRandomizedDatasets = cMaxNumberOfRandomizedDatasets;

		ArrayList<FutureTask<Double>> lTaskList = new ArrayList<FutureTask<Double>>(lNumberOfRandomizedDatasets);

		double[] lX = new double[lLength];
		for (int i = 0; i < lLength; i++)
			lX[i] = i;

		for (int i = 0; i < lNumberOfRandomizedDatasets; i++)
		{
			RandomizedDataGaussianFitter lRandomizedDataGaussianFitter = new RandomizedDataGaussianFitter();
			Callable<Double> lCallable = () -> {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
				return lRandomizedDataGaussianFitter.computeRMSDForRandomData(lX);
			};
			FutureTask<Double> lFutureTask = new FutureTask<Double>(lCallable);
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

		double[] lIRMSDArray = lIRMSDList.toArray();
		Mean lMean = new Mean();
		Variance lVariance = new Variance();
		double lVarianceValue = lVariance.evaluate(lIRMSDArray);
		double lCenterValue = lMean.evaluate(lIRMSDArray);
		double lStandardDeviation = sqrt(lVarianceValue);

		/*System.out.format("n= %d, mu=%g, sigma=%g \n",
											lLength,
											lCenterValue,
											lStandardDeviation);/**/

		// lCenterValue = 0.25;// lMean.evaluate(lIRMSDArray);
		// lStandardDeviation = 0.0625; // sqrt(lVarianceValue);

		NormalDistribution lNormalDistribution = new NormalDistribution(lCenterValue,
																																		lStandardDeviation);
		return lNormalDistribution;

	}

	public Double probability(double[] pX, double[] pY)
	{
		double[] lNormY = RandomizedDataGaussianFitter.normalizeCopy(pY);

		RandomizedDataGaussianFitter lDataGaussianFitter = new RandomizedDataGaussianFitter(pX,
																																												lNormY);
		try
		{
			mRealDataRMSD = lDataGaussianFitter.computeRMSD();

			if (mRealDataRMSD == null)
				return 0.0;

			mUnivariateDifferentiableFunction = lDataGaussianFitter.getFunction();

			NormalDistribution lNormalDistribution = getNullHypothesisDistribution(pX.length);

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
										* (mUnivariateDifferentiableFunction == null ? 0
																																: mUnivariateDifferentiableFunction.value(pX[i]));
		}
		return lFittedY;
	}

}
