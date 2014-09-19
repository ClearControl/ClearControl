package rtlib.ip.iqm.fitting;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class GaussianFitArgMaxFinder implements
																		ArgMaxFinder1D,
																		Fitting1D
{

	private double[] mlLastResults;
	private GaussianCurveFitter mGaussianCurveFitter;

	public GaussianFitArgMaxFinder()
	{
		super();
		mGaussianCurveFitter = GaussianCurveFitter.create();
	}

	@Override
	public double argmax(double[] pX, double[] pY)
	{
		if (mlLastResults == null)
			fit(pX, pY);
		double lMean = mlLastResults[1];
		mlLastResults = null;
		return lMean;
	}

	@Override
	public double[] fit(double[] pX, double[] pY)
	{
		WeightedObservedPoints lObservedPoints = new WeightedObservedPoints();

		for (int i = 0; i < pX.length; i++)
			lObservedPoints.add(pX[i], pY[i]);

		mlLastResults = mGaussianCurveFitter.fit(lObservedPoints.toList());

		double lNorm = mlLastResults[1];
		double lMean = mlLastResults[1];
		double lSigma = mlLastResults[1];

		Gaussian lGaussian = new Gaussian(lNorm, lMean, lSigma);

		double[] lFittedY = new double[pY.length];

		for (int i = 0; i < pX.length; i++)
			lFittedY[i] = lGaussian.value(pX[i]);

		return lFittedY;
	}

}
