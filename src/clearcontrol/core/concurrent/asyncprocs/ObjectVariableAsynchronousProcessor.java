package clearcontrol.core.concurrent.asyncprocs;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.startstop.StartStopDeviceInterface;
import clearcontrol.core.variable.Variable;

public class ObjectVariableAsynchronousProcessor<I, O> implements
                                                OpenCloseDeviceInterface,
                                                StartStopDeviceInterface
{
  private static final long cTimeOutInSeconds = 1;

  Variable<I> mInputObjectVariable;
  Variable<O> mOutputObjectVariable;

  AsynchronousProcessorBase<I, O> mAsynchronousProcessorBase;

  private Object mObjectEventSource;

  public ObjectVariableAsynchronousProcessor(final String pName,
                                             final int pMaxQueueSize,
                                             final ProcessorInterface<I, O> pProcessor,
                                             final boolean pDropIfQueueFull)
  {
    super();

    mOutputObjectVariable = new Variable<O>(pName + "Output");
    mInputObjectVariable = new Variable<I>(pName + "Input")
    {
      @Override
      public void set(final I pNewReference)
      {

        if (pDropIfQueueFull)
        {
          mAsynchronousProcessorBase.passOrFail(pNewReference);
        }
        else
        {
          mAsynchronousProcessorBase.passOrWait(pNewReference);
        }
      }
    };

    mAsynchronousProcessorBase =
                               new AsynchronousProcessorBase<I, O>(pName,
                                                                   pMaxQueueSize)
                               {
                                 @Override
                                 public O process(final I pInput)
                                 {
                                   return pProcessor.process(pInput);
                                 }
                               };

    mAsynchronousProcessorBase.connectToReceiver(new AsynchronousProcessorAdapter<O, O>()
    {

      @Override
      public boolean passOrWait(final O pObject)
      {
        mOutputObjectVariable.set(pObject);
        return true;
      }

      @Override
      public boolean passOrFail(final O pObject)
      {
        mOutputObjectVariable.set(pObject);
        return true;
      }

    });

  }

  public Variable<I> getInputObjectVariable()
  {
    return mInputObjectVariable;
  }

  public Variable<O> getOutputObjectVariable()
  {
    return mOutputObjectVariable;
  }

  @Override
  public boolean open()
  {
    return true;
  }

  @Override
  public boolean start()
  {
    return mAsynchronousProcessorBase.start();
  }

  @Override
  public boolean stop()
  {
    return mAsynchronousProcessorBase.stop(cTimeOutInSeconds,
                                           TimeUnit.SECONDS);
  }

  @Override
  public boolean close()
  {
    mAsynchronousProcessorBase.close();
    return true;
  }

}
