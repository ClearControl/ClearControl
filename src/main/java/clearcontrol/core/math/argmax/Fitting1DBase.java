package clearcontrol.core.math.argmax;

/**
 * Base class for implementations for fitting 1D interface.
 *
 * @author royer
 */
public abstract class Fitting1DBase implements Fitting1DInterface
{
  protected double mRMSD = Double.POSITIVE_INFINITY;

  @Override
  public double getRMSD()
  {
    return mRMSD;
  }
}
