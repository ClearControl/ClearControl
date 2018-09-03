package clearcontrol.core.math.functions;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Univariate affine function.
 *
 * @author royer
 */
@JsonPropertyOrder(
{ "slope", "constant" })
public class UnivariateAffineFunction implements
                                      ComposableFunction<UnivariateAffineFunction>,
                                      InvertibleFunction<UnivariateAffineFunction>,
                                      Serializable
{

  private static final long serialVersionUID = 1L;

  private volatile double mA, mB;

  /**
   * Returns the identity function.
   * 
   * @return identity function
   */
  public static UnivariateAffineFunction identity()
  {
    return new UnivariateAffineFunction(1, 0);
  }

  /**
   * Returns the function ax+b with given a and b parameters.
   * 
   * @param pA
   *          parameter a (slope)
   * @param pB
   *          parameter b (constant)
   * @return function ax+b
   */
  public static UnivariateAffineFunction axplusb(double pA, double pB)
  {
    return new UnivariateAffineFunction(pA, pB);
  }

  /**
   * Instanciates an identity univariate affine function
   */
  public UnivariateAffineFunction()
  {
    this(1, 0);
  }

  /**
   * Instanciates a copy of a given univariate affine function.
   * 
   * @param pUnivariateAffineFunction
   *          univariate affine function
   */
  public UnivariateAffineFunction(UnivariateAffineFunction pUnivariateAffineFunction)
  {
    mA = pUnivariateAffineFunction.getSlope();
    mB = pUnivariateAffineFunction.getConstant();
  }

  /**
   * Instanciates an univariate affine function of given slope and constant.
   * 
   * @param pA
   *          parameter a (slope)
   * @param pB
   *          parameter b (constant)
   */
  public UnivariateAffineFunction(double pA, double pB)
  {
    mA = pA;
    mB = pB;
  }

  /**
   * Sets this univariate affine function to the identity function
   */
  public void setIdentity()
  {
    mA = 1;
    mB = 0;
  }

  /**
   * Sets the constant parameter
   * 
   * @param pB
   *          paramter b (constant)
   */
  public void setConstant(double pB)
  {
    mB = pB;
  }

  /**
   * Sets the slope parameter
   * 
   * @param pA
   *          parameter a (slope)
   */
  public void setSlope(double pA)
  {
    mA = pA;
  }

  /**
   * Returns the constant parameter
   * 
   * @return parameter b (constant)
   */
  public double getConstant()
  {
    return mB;
  }

  /**
   * Returns the slope parameters
   * 
   * @return parameter a (slope)
   */
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

  /**
   * Returns true if this function has and inverse univariate affine function.
   * 
   * @return true if inverse defined.
   */
  @Override
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

}
