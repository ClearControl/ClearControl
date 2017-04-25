package clearcontrol.stack.processor;

import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.stack.StackInterface;

/**
 * Synchronous stack processor pipeline. A stack processing pipeline that uses a
 * single thread to process stacks serially.
 *
 * @author royer
 */
public class AsynchronousStackProcessorPipeline extends
                                                StackProcessorPipelineBase
                                                implements
                                                StackProcessingPipelineInterface,
                                                OpenCloseDeviceInterface
{

  // single threaded asycn processor:
  AsynchronousProcessorBase<StackInterface, StackInterface> mAsyncStackProcessor;

  /**
   * Instanciates a synchronous stack processor
   * 
   * @param pName
   *          processor name
   * @param pStackRecyclerManager
   *          stack recycler manager
   * @param pMaxQueueSize
   *          max queue size
   */
  public AsynchronousStackProcessorPipeline(String pName,
                                            StackRecyclerManager pStackRecyclerManager,
                                            int pMaxQueueSize)
  {
    super(pName, pStackRecyclerManager);

    getInputVariable().addSetListener((o,
                                       n) -> mAsyncStackProcessor.passOrWait(n));

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

    mAsyncStackProcessor = new Processor(pName, pMaxQueueSize);
  }

  @Override
  public boolean open()
  {
    return mAsyncStackProcessor.start();
  }

  @Override
  public boolean close()
  {
    return mAsyncStackProcessor.stop();
  }

}
