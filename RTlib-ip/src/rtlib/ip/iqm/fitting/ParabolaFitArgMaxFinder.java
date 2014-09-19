package rtlib.ip.iqm.fitting;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class ParabolaFitArgMaxFinder implements
																		ArgMaxFinder1D,
																		Fitting1D
{

	private double[] mlLastResults;

	@Override
	public double argmax(double[] pX, double[] pY)
	{
		if (mlLastResults == null)
			fit(pX, pY);
		
		double a = mlLastResults[2];
		double b = mlLastResults[1];
		double c = mlLastResults[0];

		double lArgMax = -b / (2 * a);
		
		mlLastResults=null;
		return lArgMax;
	}

	@Override
	public double[] fit(double[] pX, double[] pY)
	{
		WeightedObservedPoints lObservedPoints = new WeightedObservedPoints();

		for (int i = 0; i < pX.length; i++)
			lObservedPoints.add(pX[i], pY[i]);

		mlLastResults = PolynomialCurveFitter.create(2)
																				.fit(lObservedPoints.toList());



		
		PolynomialFunction lPolynomialFunction = new PolynomialFunction(mlLastResults);

		double[] lFittedY = new double[pY.length];

		for (int i = 0; i < pX.length; i++)
			lFittedY[i] = lPolynomialFunction.value(pX[i]);

		return lFittedY;
	}

}
