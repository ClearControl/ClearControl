package clearcontrol.stack.processor;

import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;

/**
 * Stack processing pipeline
 *
 * @author royer
 */
public interface StackProcessingPipelineInterface
{

  /**
   * Adds stack processor to pipeline
   * 
   * @param pStackProcessor
   *          stck processor
   * @param pRecyclerName
   *          recycler name (from stack recycler manager)
   * @param pMaximumNumberOfLiveObjects
   *          max num of live objects
   * @param pMaximumNumberOfAvailableObjects
   *          max num of available objects
   */
  public void addStackProcessor(StackProcessorInterface pStackProcessor,
                                String pRecyclerName,
                                int pMaximumNumberOfLiveObjects,
                                int pMaximumNumberOfAvailableObjects);

  /**
   * Returns the stack processor at a given index (processing order)
   * 
   * @param pProcessorIndex
   *          processor index
   * @return processor
   */
  StackProcessorInterface getStackProcessor(int pProcessorIndex);

  /**
   * Removes stack processor
   * 
   * @param pStackProcessor
   *          stack processor
   */
  public void removeStackProcessor(final StackProcessorInterface pStackProcessor);

  /**
   * Returns input variable
   * 
   * @return input variable
   */
  public Variable<StackInterface> getInputVariable();

  /**
   * Returns output variable
   * 
   * @return output variable
   */
  public Variable<StackInterface> getOutputVariable();

}
