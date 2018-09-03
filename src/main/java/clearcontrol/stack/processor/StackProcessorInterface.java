package clearcontrol.stack.processor;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 * Stack processor interface
 *
 * @author royer
 */
public interface StackProcessorInterface
{

  /**
   * Sets processor active state
   * 
   * @param pIsActive
   *          true -> procesor active
   */
  public void setActive(boolean pIsActive);

  /**
   * Returns whether this processor is active
   * 
   * @return true -> processor active
   */
  public boolean isActive();

  /**
   * Processes a given stack.
   * 
   * @param pStack
   *          stack to process
   * @param pStackRecycler
   *          stack recycler
   * @return processed stack
   */
  public StackInterface process(StackInterface pStack,
                                RecyclerInterface<StackInterface, StackRequest> pStackRecycler);

}
