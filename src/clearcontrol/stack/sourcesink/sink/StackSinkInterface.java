package clearcontrol.stack.sourcesink.sink;

import clearcontrol.stack.StackInterface;

/**
 * Stack sync interface
 *
 * @author royer
 */
public interface StackSinkInterface
{

  /**
   * Appends stack to this sink
   * 
   * @param pStack
   *          stack
   * @return true -> success
   */
  public boolean appendStack(final StackInterface pStack);

}
