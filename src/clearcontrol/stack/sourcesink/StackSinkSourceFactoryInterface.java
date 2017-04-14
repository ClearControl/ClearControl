package clearcontrol.stack.sourcesink;

import clearcontrol.stack.sourcesink.sink.StackSinkInterface;

/**
 * Stack sink and source factory interface
 *
 * @author royer
 */
public interface StackSinkSourceFactoryInterface
{

  /**
   * Returns stack sink
   * 
   * @return stack sink
   */
  public StackSinkInterface getStackSink();

}
