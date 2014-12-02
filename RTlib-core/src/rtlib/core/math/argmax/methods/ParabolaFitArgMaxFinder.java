package rtlib.core.math.argmax.methods;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import rtlib.core.math.argmax.ArgMaxFinder1D;
import rtlib.core.math.argmax.ComputeFitError;
import rtlib.core.math.argmax.Fitting1D;
import rtlib.core.math.argmax.Fitting1DBase;

public class ParabolaFitArgMaxFinder extends Fitting1DBase implements
																													ArgMaxFinder1D,
																													Fitting1D
{

	private double[] mlLastResults;
	private PolynomialCurveFitter mPolynomialCurveFitter;

	public ParabolaFitArgMaxFinder()
	{
		this(1024);
	}

	public ParabolaFitArgMaxFinder(int pMaxIterations)
	{
		mPolynomialCurveFitter = PolynomialCurveFitter.create(2)
																									.withMaxIterations(pMaxIterations);
	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		if (mlLastResults == null)
			fit(pX, pY);

		double a = mlLastResults[2];
		double b = mlLastResults[1];
		// double c = mlLastResults[0];

		double lArgMax = -b / (2 * a);

		mlLastResults = null;
		return lArgMax;
	}

	@Override
	public double[] fit(double[] pX, double[] pY)
	{
		WeightedObservedPoints lObservedPoints = new WeightedObservedPoints();

		for (int i = 0; i < pX.length; i++)
			lObservedPoints.add(pX[i], pY[i]);

		mlLastResults = mPolynomialCurveFitter.fit(lObservedPoints.toList());

		PolynomialFunction lPolynomialFunction = new PolynomialFunction(mlLastResults);

		double[] lFittedY = new double[pY.length];

		for (int i = 0; i < pX.length; i++)
			lFittedY[i] = lPolynomialFunction.value(pX[i]);

		mRMSD = ComputeFitError.rmsd(pY, lFittedY);

		return lFittedY;
	}
}
