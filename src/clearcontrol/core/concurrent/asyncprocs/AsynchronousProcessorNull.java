package clearcontrol.core.concurrent.asyncprocs;

import java.util.concurrent.TimeUnit;

/**
 * Null asynchronous processor
 *
 * @param <I>
 *          input type
 * @param <O>
 *          output type
 * @author royer
 */
public class AsynchronousProcessorNull<I, O> extends
                                      AsynchronousProcessorBase<I, O>
                                      implements
                                      AsynchronousProcessorInterface<I, O>
{

  /**
   * Instantiates an asynchronous processor
   * 
   * @param pName
   *          asynchronous processor name
   * @param pMaxQueueSize
   *          max queue size
   */
  public AsynchronousProcessorNull(final String pName,
                                   final int pMaxQueueSize)
  {
    super(pName, pMaxQueueSize);
  }

  @Override
  public O process(final I pInput)
  {
    // Example: here is where the logic happens, here nothing happens by design
    // and thus it returns null
    return null;
  }

  @Override
  public boolean waitToFinish(final long pTime, TimeUnit pTimeUnit)
  {
    return true;
  }

}
