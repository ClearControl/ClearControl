package clearcontrol.core.concurrent.asyncprocs;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.core.concurrent.executors.AsynchronousSchedulerFeature;
import clearcontrol.core.concurrent.executors.ClearControlExecutors;
import clearcontrol.core.concurrent.executors.CompletingThreadPoolExecutor;
import clearcontrol.core.log.LoggingFeature;

/**
 * Asynchronous processor pool
 *
 * @param <I>
 *          input type
 * @param <O>
 *          output type
 * @author royer
 */
public class AsynchronousProcessorPool<I, O> extends
                                      AsynchronousProcessorBase<I, O>
                                      implements
                                      AsynchronousProcessorInterface<I, O>,
                                      AsynchronousExecutorFeature,
                                      AsynchronousSchedulerFeature,
                                      LoggingFeature
{

  private final ProcessorInterface<I, O> mProcessor;
  private CompletingThreadPoolExecutor mThreadPoolExecutor;

  /**
   * Instanciates an asynchronous processor pool given a name, max input queue
   * size, thread pool size, and processor.
   * 
   * @param pName
   *          processor pool name
   * @param pMaxQueueSize
   *          max input queue size
   * @param pThreadPoolSize
   *          thread pool size
   * @param pProcessor
   *          processor
   */
  public AsynchronousProcessorPool(final String pName,
                                   final int pMaxQueueSize,
                                   final int pThreadPoolSize,
                                   final ProcessorInterface<I, O> pProcessor)
  {
    super(pName, pMaxQueueSize);
    mThreadPoolExecutor =
                        ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
                                                                            Thread.NORM_PRIORITY,
                                                                            pThreadPoolSize,
                                                                            pThreadPoolSize,
                                                                            pMaxQueueSize);

    mProcessor = pProcessor;
  }

  /**
   * Instanciates an asynchronous processor pool given a name, max queue size,
   * and processor.
   * 
   * @param pName
   *          processor pool name
   * @param pMaxQueueSize
   *          max queue size
   * @param pProcessor
   *          processors
   */
  public AsynchronousProcessorPool(final String pName,
                                   final int pMaxQueueSize,
                                   final ProcessorInterface<I, O> pProcessor)
  {
    this(pName,
         pMaxQueueSize,
         Runtime.getRuntime().availableProcessors(),
         pProcessor);
  }

  @Override
  public boolean start()
  {
    final Runnable lRunnable = () -> {
      try
      {

        @SuppressWarnings("unchecked")
        final Future<O> lFuture =
                                (Future<O>) mThreadPoolExecutor.getFutur(1,
                                                                         TimeUnit.NANOSECONDS);
        if (lFuture != null)
        {
          final O lResult = lFuture.get();
          send(lResult);
        }
      }
      catch (final InterruptedException e)
      {
        return;
      }
      catch (final ExecutionException e)
      {
        e.printStackTrace();
      }
    };

    scheduleAtFixedRate(lRunnable, 1, TimeUnit.NANOSECONDS);

    return super.start();
  }

  @Override
  public boolean stop(final long pTimeOut, TimeUnit pTimeUnit)
  {
    return super.stop(pTimeOut, pTimeUnit);
  }

  @Override
  public boolean waitToFinish(final long pTimeOut, TimeUnit pTimeUnit)
  {
    final boolean lNoTimeOut =
                             super.waitToFinish(pTimeOut, pTimeUnit);
    if (!lNoTimeOut)
      return false;
    try
    {
      return waitForCompletion(pTimeOut, pTimeUnit);
    }
    catch (final ExecutionException e)
    {
      e.printStackTrace();
      return false;
    }

  }

  @Override
  public final O process(final I pInput)
  {
    final Callable<O> lCallable = () -> {
      return mProcessor.process(pInput);
    };
    mThreadPoolExecutor.submit(lCallable);
    return null;
  }

}
