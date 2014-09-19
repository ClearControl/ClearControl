package rtlib.core.math.regression.linear;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

public class UnivariateAffineFunction implements UnivariateFunction
{

	private volatile double mA;
	private volatile double mB;

	public UnivariateAffineFunction(double pA, double pB)	throws NullArgumentException,
																											NoDataException
	{
		mA = pA;
		mB = pB;
	}

	public void setConstant(double pA)
	{
		mA = pA;
	}

	public void setSlope(double pB)
	{
		mB = pB;
	}

	public double getConstant()
	{
		return mA;
	}

	public double getSlope()
	{
		return mB;
	}

	@Override
	public double value(double pX)
	{
		return mA * pX + mB;
	}

	@Override
	public String toString()
	{
		return "UnivariateAffineFunction [Y = " + mA
						+ " * X + "
						+ mB
						+ "]";
	}

}
