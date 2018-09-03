package clearcontrol.core.math.argmax;

/**
 * 
 * Argmax finders that implement this interface can return a fit Y' of the data
 * (X,Y)
 *
 * @author royer
 */
public interface Fitting1DInterface
{
  /**
   * Returns the fit Y' of the curve (X,Y)
   * 
   * @param pX
   *          x data
   * @param pY
   *          y data
   * @return Y' fit
   */
  public double[] fit(double[] pX, double[] pY);

  /**
   * Returns the RMSD of the fit
   * 
   * @return RMSD
   */
  public double getRMSD();
}
