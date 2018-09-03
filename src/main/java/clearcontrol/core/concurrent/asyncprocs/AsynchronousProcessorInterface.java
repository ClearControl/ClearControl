package clearcontrol.core.concurrent.asyncprocs;

import java.util.concurrent.TimeUnit;

/**
 * Asynchronous processor interface
 *
 * @param <I>
 *          input
 * @param <O>
 *          output
 * @author royer
 */
public interface AsynchronousProcessorInterface<I, O> extends
                                               ProcessorInterface<I, O>
{

  /**
   * Connects this asynchronous processor to another one. Object that are
   * finished to be processed by this processor will be passed along to teh
   * given processor.
   * 
   * @param pAsynchronousProcessor
   *          asynchronous processor to send objects to next
   */
  public void connectToReceiver(AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor);

  /**
   * Starts asynchronous operation
   * 
   * @return true if success
   */
  public boolean start();

  /**
   * Stops asynchronous operation
   * 
   * @return true if success
   */
  public boolean stop();

  /**
   * Stops asynchronous operation but waits for remaining computations to
   * finish.
   * 
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true if success
   */
  public boolean stop(final long pTimeOut, TimeUnit pTimeUnit);

  /**
   * Passes an object immediately for processing or waits for processor to be
   * available
   * 
   * @param pObject
   *          object
   * @return true if passed successfully
   */
  public boolean passOrWait(I pObject);

  /**
   * Passes an object immediately for processing or waits for a given duration
   * until the processor is available
   * 
   * @param pObject
   *          object
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true if passed successfully
   */
  public boolean passOrWait(I pObject,
                            final long pTimeOut,
                            TimeUnit pTimeUnit);

  /**
   * Passes an object immediately for processing or fails.
   * 
   * @param pObject
   *          object to pass
   * @return true if passed successfully
   */
  public boolean passOrFail(I pObject);

  /**
   * Waits for a given duration until asynchronous processing finishes.
   * 
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true if success, false if timeout
   */
  public boolean waitToFinish(final long pTimeOut,
                              TimeUnit pTimeUnit);

  /**
   * Returns input queue length
   * 
   * @return input queue length
   */
  public int getInputQueueLength();

  /**
   * Returns remaining capacity
   * 
   * @return remaining capacity
   */
  public int getRemainingCapacity();

}
