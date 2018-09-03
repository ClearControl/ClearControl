package clearcontrol.core.concurrent.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Thread pool executor that can complete execution
 *
 * @author royer
 */
public class CompletingThreadPoolExecutor extends ThreadPoolExecutor
{

  private final BlockingQueue<Future<?>> mFutureQueue =
                                                      new LinkedBlockingQueue<Future<?>>(Integer.MAX_VALUE);

  /**
   * Instantiates a completing thread pool executor given a core pool size,
   * maximum pool size, a given keep alive time, and a working queue.
   * 
   * @param pCorePoolSize
   *          core pool size
   * @param pMaximumPoolSize
   *          maximum pool size
   * @param pKeepAliveTime
   *          keep alive time
   * @param pUnit
   *          keep alive time unit
   * @param pWorkQueue
   *          working queue
   */
  public CompletingThreadPoolExecutor(int pCorePoolSize,
                                      int pMaximumPoolSize,
                                      long pKeepAliveTime,
                                      TimeUnit pUnit,
                                      BlockingQueue<Runnable> pWorkQueue)
  {
    super(pCorePoolSize,
          pMaximumPoolSize,
          pKeepAliveTime,
          pUnit,
          pWorkQueue);
  }

  /**
   * Instantiates a completing thread pool executor given a core pool size,
   * maximum pool size, a given keep alive time, a working queue, and a rejected
   * execution handler.
   * 
   * @param pCorePoolSize
   *          core pool size
   * @param pMaximumPoolSize
   *          maximum pool size
   * @param pKeepAliveTime
   *          keep alive time
   * @param pUnit
   *          keep alive time unit
   * @param pWorkQueue
   *          working queue
   * @param pHandler
   *          rejected execution handler
   */
  public CompletingThreadPoolExecutor(int pCorePoolSize,
                                      int pMaximumPoolSize,
                                      long pKeepAliveTime,
                                      TimeUnit pUnit,
                                      BlockingQueue<Runnable> pWorkQueue,
                                      RejectedExecutionHandler pHandler)
  {
    super(pCorePoolSize,
          pMaximumPoolSize,
          pKeepAliveTime,
          pUnit,
          pWorkQueue,
          pHandler);
  }

  /**
   * Instantiates a completing thread pool executor given a core pool size,
   * maximum pool size, a given keep alive time, a working queue, a thread
   * factory, and a rejected execution handler.
   * 
   * @param pCorePoolSize
   *          core pool size
   * @param pMaximumPoolSize
   *          maximum pool size
   * @param pKeepAliveTime
   *          keep alive time
   * @param pUnit
   *          keep alive unit
   * @param pWorkQueue
   *          working queue
   * @param pThreadFactory
   *          thread factory
   * @param pHandler
   *          rejected execution handler
   */
  public CompletingThreadPoolExecutor(int pCorePoolSize,
                                      int pMaximumPoolSize,
                                      long pKeepAliveTime,
                                      TimeUnit pUnit,
                                      BlockingQueue<Runnable> pWorkQueue,
                                      ThreadFactory pThreadFactory,
                                      RejectedExecutionHandler pHandler)
  {
    super(pCorePoolSize,
          pMaximumPoolSize,
          pKeepAliveTime,
          pUnit,
          pWorkQueue,
          pThreadFactory,
          pHandler);
  }

  /**
   * Instantiates a completing thread pool executor given a core pool size,
   * maximum pool size, a given keep alive time, a working queue, and a thread
   * factory.
   * 
   * @param pCorePoolSize
   *          core pool size
   * @param pMaximumPoolSize
   *          maximum pool size
   * @param pKeepAliveTime
   *          keep alive time
   * @param pUnit
   *          keep alive unit
   * @param pWorkQueue
   *          working queue
   * @param pThreadFactory
   *          thread factory
   */
  public CompletingThreadPoolExecutor(int pCorePoolSize,
                                      int pMaximumPoolSize,
                                      long pKeepAliveTime,
                                      TimeUnit pUnit,
                                      BlockingQueue<Runnable> pWorkQueue,
                                      ThreadFactory pThreadFactory)
  {
    super(pCorePoolSize,
          pMaximumPoolSize,
          pKeepAliveTime,
          pUnit,
          pWorkQueue,
          pThreadFactory);
  }

  @Override
  public Future<?> submit(Runnable pTask)
  {
    Future<?> lFutur = super.submit(pTask);
    addFutur(lFutur);
    return lFutur;
  }

  @Override
  public <T> Future<T> submit(Runnable pTask, T pResult)
  {
    Future<T> lFutur = super.submit(pTask, pResult);
    addFutur(lFutur);
    return lFutur;
  }

  @Override
  public <T> Future<T> submit(Callable<T> pTask)
  {
    Future<T> lFutur = super.submit(pTask);
    addFutur(lFutur);
    return lFutur;
  }

  private void addFutur(Future<?> pFutur)
  {
    mFutureQueue.add(pFutur);
  }

  /**
   * Returns the oldest Future of this executor or rreturns after a given
   * timeout.
   * 
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          time unit
   * @return oldest future
   * @throws InterruptedException
   *           if interrupted before timeout expired.
   */
  public Future<?> getFutur(long pTimeOut,
                            TimeUnit pTimeUnit) throws InterruptedException
  {
    return mFutureQueue.poll(pTimeOut, pTimeUnit);
  }

  /**
   * Waist for completion of all tasks given to this executor or returns aftera
   * given timeout.
   * 
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @throws ExecutionException
   *           thrown if execution occurs during execution
   * @throws TimeoutException
   *           thrown if timeout occurs
   */
  public void waitForCompletion(long pTimeOut,
                                TimeUnit pTimeUnit) throws ExecutionException,
                                                    TimeoutException
  {
    final long lStartTimeNanos = System.nanoTime();
    final long lDeadlineTimeNanos = lStartTimeNanos
                                    + pTimeUnit.toNanos(pTimeOut);

    while (mFutureQueue.peek() != null
           && System.nanoTime() <= lDeadlineTimeNanos)
    {
      Future<?> lFuture = null;
      try
      {
        lFuture = mFutureQueue.poll();
        if (lFuture != null)
          lFuture.get(pTimeOut, pTimeUnit);
      }
      catch (InterruptedException e)
      {
        reinject(lFuture);
      }
    }

    if (System.nanoTime() > lDeadlineTimeNanos)
      throw new TimeoutException("Run out of time waiting for "
                                 + this.getClass().getSimpleName()
                                 + " tasks to finish!");
  }

  private void reinject(Future<?> lFuture)
  {
    if (lFuture != null)
      try
      {
        mFutureQueue.put(lFuture);
      }
      catch (InterruptedException e1)
      {
        reinject(lFuture);
      }
  }

}
