package clearcontrol.stack.processor;

import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorPool;
import clearcontrol.core.concurrent.asyncprocs.ProcessorInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.stack.StackInterface;

/**
 * Asynchronous thread pool stack processor pipeline. A stack processing
 * pipeline that uses a pool of threads to distribute the work load.
 *
 * @author royer
 */
public class AsynchronousPoolStackProcessorPipeline extends
                                                    StackProcessorPipelineBase
                                                    implements
                                                    StackProcessingPipelineInterface,
                                                    OpenCloseDeviceInterface
{

  private AsynchronousProcessorPool<StackInterface, StackInterface> mAsynchStackProcessorPool;

  /**
   * Instanciates an asynchronous thread pool stack processing pipeline
   * 
   * @param pName
   *          pipeline name
   * @param pStackRecyclerManager
   *          stack recycler manager
   * @param pMaxQueueSize
   *          max queue size
   * @param pThreadPoolSize
   *          thread pool size.
   */
  public AsynchronousPoolStackProcessorPipeline(String pName,
                                                StackRecyclerManager pStackRecyclerManager,
                                                final int pMaxQueueSize,
                                                final int pThreadPoolSize)
  {
    super(pName, pStackRecyclerManager);

    getInputVariable().addSetListener((o,
                                       n) -> mAsynchStackProcessorPool.passOrWait(n));

    class Processor extends
                    AsynchronousProcessorBase<StackInterface, StackInterface>
    {

      public Processor(String pName, int pMaxQueueSize)
      {
        super(pName, pMaxQueueSize);
      }

      @Override
      public StackInterface process(StackInterface pInput)
      {
        try
        {
          StackInterface lProcessedStack = doProcess(pInput);
          if (lProcessedStack != null)
            getOutputVariable().set(lProcessedStack);
          return lProcessedStack;
        }
        catch (Throwable e)
        {
          e.printStackTrace();
          pInput.release();
          return null;
        }
      }
    }

    final ProcessorInterface<StackInterface, StackInterface> lProcessor =
                                                                        new Processor(pName,
                                                                                      pMaxQueueSize);

    mAsynchStackProcessorPool =
                              new AsynchronousProcessorPool<>(pName,
                                                              pMaxQueueSize,
                                                              pThreadPoolSize,
                                                              lProcessor);

  }

  @Override
  public boolean open()
  {
    return mAsynchStackProcessorPool.start();
  }

  @Override
  public boolean close()
  {
    return mAsynchStackProcessorPool.stop();
  }

}
