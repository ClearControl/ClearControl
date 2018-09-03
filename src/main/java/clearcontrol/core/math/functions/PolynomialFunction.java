package clearcontrol.core.math.functions;

/**
 * Polynomial function.
 *
 * @author royer
 */
public class PolynomialFunction extends
                                org.apache.commons.math3.analysis.polynomials.PolynomialFunction
{

  private static final long serialVersionUID = 1L;

  /**
   * Instantiates an identity polynomial function: x the coefficients order goes
   * from high power to low power, therefore, {1,0} is the identity function
   * y=1x+0.
   */
  public PolynomialFunction()
  {
    super(new double[]
    { 1, 0 });
  }

  /**
   * Instanciates a polynomial function given polynomial coefficients.
   * 
   * @param pPolynomialCoefficients
   *          polynomial coefficients
   */
  public PolynomialFunction(double... pPolynomialCoefficients)
  {
    super(pPolynomialCoefficients);
  }

}
