package clearcontrol.core.math.argmax;

/**
 * Fit probability interface.
 * 
 * Argmax finders that implement this interface can return a fit probability
 *
 * @author royer
 */
public interface FitProbabilityInterface
{
  /**
   * Returns the probability of the last fit
   * 
   * @return fit probability
   */
  public Double getLastFitProbability();
}
