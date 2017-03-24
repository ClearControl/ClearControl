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
   * Instanciates an identity polynomial function: x
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
  public PolynomialFunction(double[] pPolynomialCoefficients)
  {
    super(pPolynomialCoefficients);
  }

}
