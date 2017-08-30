package clearcontrol.stack.sourcesink.sink;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.sourcesink.StackSinkSourceInterface;

/**
 * Stack sync interface
 *
 * @author royer
 */
public interface StackSinkInterface extends StackSinkSourceInterface
{

  /**
   * Appends stack to this sink
   * 
   * @param pStack
   *          stack
   * @return true -> success
   */
  boolean appendStack(StackInterface pStack);

  /**
   * Appends stack to this sink for at a given channel
   * 
   * @param pChannel
   *          Channel to append stack to
   * 
   * @param pStack
   *          stack
   * @return true -> success
   */
  public boolean appendStack(String pChannel,
                             final StackInterface pStack);

}
