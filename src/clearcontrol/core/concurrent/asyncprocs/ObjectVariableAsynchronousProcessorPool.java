package clearcontrol.core.concurrent.asyncprocs;

import clearcontrol.core.device.startstop.StartStopDeviceInterface;
import clearcontrol.core.variable.Variable;

/**
 * Asynchronous processor pool that receives and sends objects via variable.
 *
 * @param <I>
 *          input type
 * @param <O>
 *          output type
 * @author royer
 */
public class ObjectVariableAsynchronousProcessorPool<I, O> extends
                                                    AsynchronousProcessorPool<I, O>
                                                    implements
                                                    StartStopDeviceInterface
{
  private final Variable<I> mInputObjectVariable;
  private final Variable<O> mOutputObjectVariable;

  /**
   * Instantiates an object variable asynchronous processor pool
   * 
   * @param pName
   *          processor pool name
   * @param pMaxQueueSize
   *          max input queue size
   * @param pThreadPoolSize
   *          thread pool size
   * @param pProcessor
   *          processor
   * @param pDropIfQueueFull
   *          drops objects if queue is full, otherwise just waits until slots
   *          available in queue
   */
  public ObjectVariableAsynchronousProcessorPool(final String pName,
                                                 final int pMaxQueueSize,
                                                 final int pThreadPoolSize,
                                                 final ProcessorInterface<I, O> pProcessor,
                                                 final boolean pDropIfQueueFull)
  {
    super(pName, pMaxQueueSize, pThreadPoolSize, pProcessor);

    mOutputObjectVariable = new Variable<O>(pName + "Output");

    mInputObjectVariable = new Variable<I>(pName + "Input")
    {
      @Override
      public void set(final I pNewReference)
      {
        if (pDropIfQueueFull)
        {
          passOrFail(pNewReference);
        }
        else
        {
          passOrWait(pNewReference);
        }
      }
    };

    final AsynchronousProcessorBase<O, O> lConnector =
                                                     new AsynchronousProcessorBase<O, O>("AsynchronousProcessorPool->OutputObjectVariable",
                                                                                         pMaxQueueSize)
                                                     {

                                                       @Override
                                                       public O process(final O pInput)
                                                       {
                                                         mOutputObjectVariable.set(pInput);
                                                         return null;
                                                       }
                                                     };

    lConnector.start();
    connectToReceiver(lConnector);

  }

  /**
   * Returns input variable
   * 
   * @return input variable
   */
  public Variable<I> getInputObjectVariable()
  {
    return mInputObjectVariable;
  }

  /**
   * Returns output variable
   * 
   * @return output variable
   */
  public Variable<O> getOutputObjectVariable()
  {
    return mOutputObjectVariable;
  }

}
