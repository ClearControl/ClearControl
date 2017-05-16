package clearcontrol.microscope.lightsheet.adaptive.modules;

/**
 * Result
 *
 * @author royer
 */
public class Result
{

  /**
   * argmax
   */
  public double argmax;

  /**
   * metric max
   */
  public double metricmax;

  /**
   * probability
   */
  public double probability;

  @SuppressWarnings("javadoc")
  public static Result of(double pArgMax,
                          double pMetricMax,
                          double pProbability)
  {
    Result lResult = new Result();
    lResult.argmax = pArgMax;
    lResult.metricmax = pMetricMax;
    lResult.probability = pProbability;
    return lResult;
  }

  @SuppressWarnings("javadoc")
  public static Result none()
  {
    return of(Double.NaN, 0.0, 0.0);
  }

}
