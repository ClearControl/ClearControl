package clearcontrol.core.math.functions;

import java.io.Serializable;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(
{ "slope", "constant" })
public class UnivariateAffineFunction	implements
																			ComposableFunction<UnivariateAffineFunction>,
																			InvertibleFunction<UnivariateAffineFunction>,
																			Serializable
{

	private static final long serialVersionUID = 1L;

	private volatile double mA, mB;

	public static UnivariateAffineFunction identity()
	{
		return new UnivariateAffineFunction(1, 0);
	}

	public static UnivariateAffineFunction axplusb(double pA, double pB)
	{
		return new UnivariateAffineFunction(pA, pB);
	}

	public UnivariateAffineFunction()	throws NullArgumentException,
																		NoDataException
	{
		this(1, 0);
	}

	public UnivariateAffineFunction(UnivariateAffineFunction pUnivariateAffineFunction)
	{
		mA = pUnivariateAffineFunction.getSlope();
		mB = pUnivariateAffineFunction.getConstant();
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

	public boolean hasInverse()
	{
		return (mA > 0 || mA < 0) && Double.isFinite(mA)
						&& !Double.isNaN(mA)
						&& Double.isFinite(mB)
						&& !Double.isNaN(mB);
	}

	@Override
	public UnivariateAffineFunction inverse()
	{
		if (mA == 0)
			return null;
		double lInverseA = 1 / mA;
		double lInverseB = -mB / mA;
		return new UnivariateAffineFunction(lInverseA, lInverseB);
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

	public void setIdentity()
	{
		mA = 1;
		mB = 0;
	}

}
