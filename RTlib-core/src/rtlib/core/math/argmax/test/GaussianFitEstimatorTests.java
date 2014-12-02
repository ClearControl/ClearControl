package rtlib.core.math.argmax.test;

import gnu.trove.list.array.TDoubleArrayList;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import rtlib.core.math.argmax.GaussianFitEstimator;
import rtlib.core.units.Magnitudes;

public class GaussianFitEstimatorTests
{

	@Test
	public void basicTest()
	{
		GaussianFitEstimator lGaussianFitEstimator = new GaussianFitEstimator();

		{
			double[] lX = new double[]
			{ -2.0, -1.0, 0.0, 1.0, 2.0 };
			double[] lY = new double[]
			{ 3.71E-05, 3.80E-05, 3.86E-05, 3.86E-05, 3.79E-05 };

			Double lPvalue = lGaussianFitEstimator.pvalue(lX, lY);

			System.out.println(lPvalue);
		}

		{
			double[] lX = new double[]
			{ -2.0, -1.0, 0.0, 1.0, 2.0 };
			double[] lY = new double[]
			{ 0.2, 0.4, 0.1, 0.2, 0.1 };

			double lPvalue = 0;
			int lNumberOfIterations = 1;
			long lStart = System.nanoTime();
			for (int i = 0; i < lNumberOfIterations; i++)
				lPvalue = lGaussianFitEstimator.pvalue(lX, lY);
			long lStop = System.nanoTime();
			double lElapsed = Magnitudes.nano2milli((1.0 * lStop - lStart) / lNumberOfIterations);

			System.out.format("%g ms per estimation. \n", lElapsed);

			System.out.println(lPvalue);
		}

	}

	@Test
	public void benchmark() throws IOException, URISyntaxException
	{
		GaussianFitEstimator lGaussianFitEstimator = new GaussianFitEstimator();

		System.out.println("nofit:");
		run(lGaussianFitEstimator,
				GaussianFitEstimatorTests.class,
				"./benchmark/nofit.txt",
				6);

		System.out.println("fit:");
		run(lGaussianFitEstimator,
				GaussianFitEstimatorTests.class,
				"./benchmark/fit.txt",
				7);

	}

	private void run(	GaussianFitEstimator lGaussianFitEstimator,
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

			Double lPValue = lGaussianFitEstimator.pvalue(lX.toArray(),
																										lY.toArray());

			System.out.println("probability=" + (1 - lPValue));

			/*
			Double lNRMSD = lGaussianFitEstimator.nrmsd(lX.toArray(),
																										lY.toArray());/**/

			// System.out.println("lNRMSD=" + lNRMSD);
		}
	}
}
