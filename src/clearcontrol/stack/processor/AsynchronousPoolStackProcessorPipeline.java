package clearcontrol.stack.processor;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorPool;
import clearcontrol.core.concurrent.asyncprocs.ProcessorInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.startstop.StartStopDeviceInterface;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclableFactoryInterface;
import coremem.recycling.RecyclerInterface;

public class AsynchronousPoolStackProcessorPipeline implements
                                                    StackProcessingPipeline,
                                                    StartStopDeviceInterface
{

  private final CopyOnWriteArrayList<StackProcessorInterface> mProcessorList =
                                                                             new CopyOnWriteArrayList<StackProcessorInterface>();
  private final CopyOnWriteArrayList<RecyclerInterface<StackInterface, StackRequest>> mRecyclerList =
                                                                                                    new CopyOnWriteArrayList<RecyclerInterface<StackInterface, StackRequest>>();
  private AsynchronousProcessorPool<StackInterface, StackInterface> mAsynchronousProcessorPool;

  private Variable<StackInterface> mInputVariable;
  private Variable<StackInterface> mOutputVariable;
  private AsynchronousProcessorInterface<StackInterface, StackInterface> mReceiver;

  public AsynchronousPoolStackProcessorPipeline(String pName,
                                                final int pMaxQueueSize,
                                                final int pThreadPoolSize)
  {
    super();

    mInputVariable = new Variable<StackInterface>("InputVariable")
    {

      @Override
      public StackInterface setEventHook(StackInterface pOldValue,
                                         StackInterface pNewValue)
      {
        mAsynchronousProcessorPool.passOrWait(pNewValue);
        return super.setEventHook(pOldValue, pNewValue);
      }

    };

    mOutputVariable = new Variable<StackInterface>("OutputVariable");

    final ProcessorInterface<StackInterface, StackInterface> lProcessor =
                                                                        new ProcessorInterface<StackInterface, StackInterface>()
                                                                        {

                                                                          @Override
                                                                          public StackInterface process(StackInterface pInput)
                                                                          {
                                                                            StackInterface lStack =
                                                                                                  pInput;
                                                                            for (int i =
                                                                                       0; i < mProcessorList.size(); i++)
                                                                            {
                                                                              final StackProcessorInterface lProcessor =
                                                                                                                       mProcessorList.get(i);
                                                                              if (lProcessor.isActive())
                                                                              {
                                                                                // System.out.println("lProcessor="
                                                                                // +
                                                                                // lProcessor);
                                                                                // System.out.println("lStack
                                                                                // input="
                                                                                // +
                                                                                // lStack);
                                                                                final RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                                                                                                                mRecyclerList.get(i);
                                                                                lStack =
                                                                                       lProcessor.process(lStack,
                                                                                                          lRecycler);
                                                                                // System.out.println("lStack
                                                                                // output="
                                                                                // +
                                                                                // lStack);
                                                                              }
                                                                            }
                                                                            return lStack;
                                                                          }

                                                                          @Override
                                                                          public void close() throws IOException
                                                                          {

                                                                          }
                                                                        };

    mAsynchronousProcessorPool =
                               new AsynchronousProcessorPool<>(pName,
                                                               pMaxQueueSize,
                                                               pThreadPoolSize,
                                                               lProcessor);

    mReceiver =
              new AsynchronousProcessorBase<StackInterface, StackInterface>("Receiver",
                                                                            10)
              {
                @Override
                public StackInterface process(final StackInterface pInput)
                {
                  mOutputVariable.set(pInput);
                  return pInput;
                }
              };

    mAsynchronousProcessorPool.connectToReceiver(mReceiver);

  }

  @Override
  public void addStackProcessor(StackProcessorInterface pStackProcessor,
                                RecyclableFactoryInterface<StackInterface, StackRequest> pStackFactory,
                                int pMaximumNumberOfObjects)
  {
    final RecyclerInterface<StackInterface, StackRequest> lStackRecycler =
                                                                         new BasicRecycler<StackInterface, StackRequest>(pStackFactory,
                                                                                                                         pMaximumNumberOfObjects);
    mRecyclerList.add(lStackRecycler);
    mProcessorList.add(pStackProcessor);
  }

  @Override
  public void removeStackProcessor(StackProcessorInterface pStackProcessor)
  {
    final int lIndex = mProcessorList.indexOf(pStackProcessor);
    mProcessorList.remove(pStackProcessor);
    mRecyclerList.remove(lIndex);
  }

  @Override
  public Variable<StackInterface> getInputVariable()
  {
    return mInputVariable;
  }

  @Override
  public Variable<StackInterface> getOutputVariable()
  {
    return mOutputVariable;
  }

  @Override
  public boolean open()
  {
    return true;
  }

  @Override
  public boolean start()
  {
    return mReceiver.start() && mAsynchronousProcessorPool.start();
  }

  @Override
  public boolean stop()
  {
    return mAsynchronousProcessorPool.stop() && mReceiver.stop();
  }

  @Override
  public boolean close()
  {
    return true;
  }

}
