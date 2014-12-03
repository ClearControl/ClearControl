package rtlib.core.math.argmax.test;

import gnu.trove.list.array.TDoubleArrayList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Test;

import rtlib.core.math.argmax.FitQualityEstimator;
import rtlib.core.units.Magnitudes;

public class FitQualityEstimatorTests
{

	@Test
	public void basicTest()
	{
		FitQualityEstimator lFitQualityEstimator = new FitQualityEstimator();

		{
			double[] lX = new double[]
			{ -2.0, -1.0, 0.0, 1.0, 2.0 };
			double[] lY = new double[]
			{ 3.71E-05, 3.80E-05, 3.86E-05, 3.86E-05, 3.79E-05 };

			Double lPvalue = lFitQualityEstimator.probability(lX, lY);

			System.out.println(lPvalue);
		}

		{
			double[] lX = new double[]
			{ -2.0, -1.0, 0.0, 1.0, 2.0 };
			double[] lY = new double[]
			{ 0.2, 0.4, 0.1, 0.2, 0.1 };

			double lPvalue = 0;
			int lNumberOfIterations = 100;
			long lStart = System.nanoTime();
			for (int i = 0; i < lNumberOfIterations; i++)
				lPvalue = lFitQualityEstimator.probability(lX, lY);
			long lStop = System.nanoTime();
			double lElapsed = Magnitudes.nano2milli((1.0 * lStop - lStart) / lNumberOfIterations);

			System.out.format("%g ms per estimation. \n", lElapsed);

			System.out.println(lPvalue);
		}

	}

	@Test
	public void benchmark() throws IOException, URISyntaxException
	{
		FitQualityEstimator lFitQualityEstimator = new FitQualityEstimator();

		System.out.println("nofit:");
		run(lFitQualityEstimator,
				FitQualityEstimatorTests.class,
				"./benchmark/nofit.txt",
				6);

		System.out.println("fit:");
		run(lFitQualityEstimator,
				FitQualityEstimatorTests.class,
				"./benchmark/fit.txt",
				9);

	}

	private void run(	FitQualityEstimator lGaussianFitEstimator,
										Class<?> lContextClass,
										String lRessource,
										int lNumberOfDatasets) throws IOException,
																					URISyntaxException
	{
		for (int i = 0; i < lNumberOfDatasets; i++)
		{
			TDoubleArrayList lY = ArgMaxTester.loadData(lContextClass,
																									lRessource,
																									i);
			TDoubleArrayList lX = new TDoubleArrayList();
			for (int j = 0; j < lY.size(); j++)
				lX.add(j);

			Double lProbability = lGaussianFitEstimator.probability(lX.toArray(),
																										lY.toArray());



			double[] lFittedY = lGaussianFitEstimator.getFit(	lX.toArray(),
																										lY.toArray());

			System.out.println("__________________________________________________________________________");
			System.out.println("lX=" + Arrays.toString(lX.toArray()));
			System.out.println("lY=" + Arrays.toString(lY.toArray()));
			System.out.println("lFittedY=" + Arrays.toString(lFittedY));
			System.out.println("probability=" + lProbability);
			System.out.println("rmsd=" + lGaussianFitEstimator.getRMSD());

			/*
			Double lNRMSD = lGaussianFitEstimator.nrmsd(lX.toArray(),
																										lY.toArray());/**/

			// System.out.println("lNRMSD=" + lNRMSD);
		}
	}
}
