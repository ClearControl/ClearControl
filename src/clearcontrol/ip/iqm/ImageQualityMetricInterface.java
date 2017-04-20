package clearcontrol.ip.iqm;

import clearcontrol.stack.OffHeapPlanarStack;

/**
 * Interface for all image quality metrics
 *
 * @author royer
 */
public interface ImageQualityMetricInterface
{
  /**
   * Computes the metric per plane
   * 
   * @param pStack
   *          stack
   * @return array of metric values
   */
  double[] computeImageQualityMetric(OffHeapPlanarStack pStack);
}
