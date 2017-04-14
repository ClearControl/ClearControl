package clearcontrol.stack.sourcesink.sink;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;

/**
 * Asynchronous stack sink adapter. This sink adapter can wrap anotehr sink an
 * provides asynchronous decoupling via an elastic queue.
 *
 * @author royer
 */
public class AsynchronousStackSinkAdapter implements
                                          StackSinkInterface
{

  private StackSinkInterface mStackSink;

  private AsynchronousProcessorInterface<StackInterface, StackInterface> mAsynchronousConversionProcessor;

  private Variable<StackInterface> mFinishedProcessingStackVariable;

  /**
   * Wraps an existing stack sink to provide asynchronous capability
   * 
   * @param pStackSink
   *          sink to wrap
   * @param pMaxQueueSize
   *          max queue size
   * @return wrapped sink
   */
  public static AsynchronousStackSinkAdapter wrap(StackSinkInterface pStackSink,
                                                  final int pMaxQueueSize)
  {
    return new AsynchronousStackSinkAdapter(pStackSink,
                                            pMaxQueueSize);
  }

  /**
   * Instanciates an asynchronous stack sink adapter for a given existing sink
   * and max queue size.
   * 
   * @param pStackSink
   *          sink to wrap
   * @param pMaxQueueSize
   *          max queue size
   */
  public AsynchronousStackSinkAdapter(final StackSinkInterface pStackSink,
                                      final int pMaxQueueSize)
  {
    super();
    mStackSink = pStackSink;

    mAsynchronousConversionProcessor =
                                     new AsynchronousProcessorBase<StackInterface, StackInterface>("AsynchronousStackSinkAdapter",
                                                                                                   pMaxQueueSize)
                                     {
                                       @Override
                                       public StackInterface process(final StackInterface pStack)
                                       {
                                         mStackSink.appendStack(pStack);
                                         if (mFinishedProcessingStackVariable != null)
                                         {
                                           mFinishedProcessingStackVariable.set(pStack);
                                         }
                                         return null;
                                       }
                                     };
  }

  /**
   * Starts the thread that passes the stacks to the sink
   * 
   * @return true -> success
   */
  public boolean start()
  {
    return mAsynchronousConversionProcessor.start();
  }

  /**
   * Stops the thread that passes the stacks to the sink
   * 
   * @return true -> success
   */
  public boolean stop()
  {
    return mAsynchronousConversionProcessor.stop();
  }

  @Override
  public boolean appendStack(final StackInterface pStack)
  {
    return mAsynchronousConversionProcessor.passOrWait(pStack);
  }

  /**
   * Waits for this asynchronous sink adapter to pas all pending stacks to the
   * delegated sink.
   * 
   * @param pTimeOut
   *          time out
   * @param pTimeUnit
   *          time unit
   * @return true -> success (= no timeout)
   */
  public boolean waitToFinish(final long pTimeOut, TimeUnit pTimeUnit)
  {
    return mAsynchronousConversionProcessor.waitToFinish(pTimeOut,
                                                         pTimeUnit);
  }

  /**
   * Returns queue length
   * 
   * @return queue length
   */
  public int getQueueLength()
  {
    return mAsynchronousConversionProcessor.getInputQueueLength();
  }

  /**
   * Sets the variable that receives stacks once they have been successfully
   * passed to the sink.
   * 
   * @param pVariable
   *          variable that received stacks
   */
  public void setFinishedProcessingStackVariable(final Variable<StackInterface> pVariable)
  {
    mFinishedProcessingStackVariable = pVariable;
  }

}
