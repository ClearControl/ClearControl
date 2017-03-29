package clearcontrol.stack;

import coremem.recycling.RecyclerInterface;

/**
 * Stack providers provide stacks when asked for it. Simple.
 *
 * @author royer
 * @param <D>
 *          stack description/info type
 */
public interface StackProviderInterface<D>
{
  /**
   * Returns a stack. The stack descriptor has the required information to
   * generate the appropriate stack. The given recycler should be used to
   * allocate the stack
   * 
   * @param pRecycler
   *          recycler to use for requesting stacks
   * @param pStackDescriptor
   *          stack description/info
   * 
   * @return a stack
   */
  StackInterface getStack(RecyclerInterface<StackInterface, StackRequest> pRecycler,
                          D pStackDescriptor);
}
