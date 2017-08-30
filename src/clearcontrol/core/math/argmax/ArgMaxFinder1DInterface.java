package clearcontrol.core.math.argmax;

/**
 * 1D Armax finder
 *
 * @author royer
 */
public interface ArgMaxFinder1DInterface
{
  /**
   * Returns the X that maximizes Y
   * 
   * @param pX
   *          list of x values
   * @param pY
   *          list of y values
   * @return X that maximizes Y
   */
  public Double argmax(double[] pX, double[] pY);
}
