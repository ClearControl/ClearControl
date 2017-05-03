package clearcontrol.core.math.interpolation.bezier;

import static java.lang.Math.pow;

import clearcontrol.core.math.functions.PolynomialFunction;

/**
 * 1D Bezier interpolation
 *
 * @author royer
 */
public class Bezier extends PolynomialFunction
{

  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a four point Bezier Spline interpolation
   * 
   * @param x1
   *          first value
   * @param x2
   *          second value
   * @param x3
   *          third value
   * @param x4
   *          fourth value
   * 
   */
  public Bezier(float x1, float x2, float x3, float x4)
  {
    super(x4 - x1
          + 3 * x2
          - 3 * x3,
          3 * (x1 + x3 - (2 * x2)),
          3 * (x2 - x1),
          x1);

  }

  /**
   * Static function to compute the bezier curve.
   * 
   * @param x1
   *          first value
   * @param x2
   *          second value
   * @param x3
   *          third value
   * @param x4
   *          fourth value
   * @param x
   *          parameter
   * @return bezier value
   */
  public static double bezier(double x1,
                              double x2,
                              double x3,
                              double x4,
                              double x)
  {
    return x1 + 3 * x * (x2 - x1)
           + 3 * pow(x, 2) * (x1 + x3 - (2 * x2))
           + pow(x, 3) * (x4 - x1 + 3 * x2 - 3 * x3);

  }

}
