package rtlib.core.math.functions;

import java.io.Serializable;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

public class UnivariateAffineFunction	implements
										UnivariateAffineComposableFunction,
										Serializable
{

	private static final long serialVersionUID = 1L;

	private volatile double mA;
	private volatile double mB;

	public static UnivariateAffineFunction identity()
	{
		return new UnivariateAffineFunction(1, 0);
	}

	public UnivariateAffineFunction()	throws NullArgumentException,
										NoDataException
	{
		this(1, 0);
	}

	public UnivariateAffineFunction(double pA, double pB)	throws NullArgumentException,
															NoDataException
	{
		mA = pA;
		mB = pB;
	}

	public void setConstant(double pB)
	{
		mB = pB;
	}

	public void setSlope(double pA)
	{
		mA = pA;
	}

	public double getConstant()
	{
		return mB;
	}

	public double getSlope()
	{
		return mA;
	}

	@Override
	public void composeWith(UnivariateAffineFunction pFunction)
	{
		mA = mA * pFunction.getSlope();
		mB = mA * pFunction.getConstant() + mB;
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
