package clearcontrol.microscope.lightsheet.processor.fusion.tasks;

import java.util.Set;

import clearcontrol.microscope.lightsheet.processor.fusion.FastFusionEngineInterface;

/**
 * Fusion task interface
 *
 * @author royer
 */
public interface FusionTaskInterface
{

  /**
   * Checks if required images are available
   * 
   * @param pAvailableImagesKeys
   *          set of available keys
   * @return true if all required images are available
   */
  public boolean checkIfRequiredImagesAvailable(Set<String> pAvailableImagesKeys);

  /**
   * Enqueues the computation nescessary to perform this task
   * 
   * @param pFastFusionEngine
   *          fast fusion engines
   * @param pWaitToFinish
   *          true -> waits for computation to finish
   * @return true if success in starting the task
   */
  public boolean enqueue(FastFusionEngineInterface pFastFusionEngine,
                         boolean pWaitToFinish);

}
