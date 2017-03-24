package clearcontrol.stack;

/**
 * Stack providers provide stacks when asked for it. Simple.
 *
 * @author royer
 * @param <R>
 *          receptor type
 */
public interface StackProviderInterface<R>
{
  /**
   * Returns a stack. The provider knows what stack to provide. A receptor is
   * provided so that the provider can have additional information about what
   * stacks to provide
   * 
   * @param pReceptor
   * @return a stack
   */
  StackInterface getStack(R pReceptor);
}
