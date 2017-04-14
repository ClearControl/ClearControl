package clearcontrol.core.concurrent.executors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This interface provides classes that implement it the basic infrastructure to
 * execute Runnables and Callables asynchronously from the calling thread. Each
 * instance of this class is provided with an Executor (configurable) that is
 * automatically allocated. You simply have to call the method
 * 'executeAsynchronously' such as for example:
 * 
 * executAsynchronously(() -> {System.out.println("tada...")})
 * 
 * And this code will be executed in a separate thread. The
 * executAsynchronously(0 method returns a Future that can be used to determine
 * when is the task finished.
 * 
 * @author royer
 */
public interface AsynchronousExecutorServiceAccess
{

  /**
   * The default executor has infinite queue length and a single execution
   * thread.
   * 
   * @return
   */
  public default ThreadPoolExecutor initializeDefaultExecutor()
  {
    return ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
                                                               Thread.NORM_PRIORITY,
                                                               1,
                                                               1,
                                                               Integer.MAX_VALUE);
  }

  /**
   * Call this method (typically in the constructor) to configure the instance's
   * executor to use a a certain number of Threads and a given queue length for
   * concurrent execution.
   * 
   * @param pQueueLength
   *          task queue length
   * @param pNumberOfThreads
   *          number of threads that can concurrently executed queued tasks.
   * @return
   */
  public default ThreadPoolExecutor initializeExecutor(int pQueueLength,
                                                       int pNumberOfThreads)
  {
    return ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
                                                               Thread.NORM_PRIORITY,
                                                               pNumberOfThreads,
                                                               pNumberOfThreads,
                                                               pQueueLength);
  }

  /**
   * Call this method (typically in the constructor) to configure the instance's
   * executor to use a single Thread for concurrent execution. Successive call
   * are queued.
   * 
   * @return executor (most of the time you can ignore this)
   */
  public default ThreadPoolExecutor initializeSerialExecutor()
  {
    return ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
                                                               Thread.NORM_PRIORITY,
                                                               1,
                                                               1,
                                                               Integer.MAX_VALUE);
  }

  /**
   * Call this method (typically in the constructor) to configure the instance's
   * executor to use as many threads as there are cores on the system, and an
   * infinite queue.
   * 
   * @return
   */
  public default ThreadPoolExecutor initializeConcurentExecutor()
  {
    return ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
                                                               Thread.NORM_PRIORITY,
                                                               1,
                                                               Runtime.getRuntime()
                                                                      .availableProcessors(),
                                                               Integer.MAX_VALUE);
  }

  /**
   * Executes the given Runnable on this instance's executor.
   * 
   * @param pRunnable
   *          Runnable
   * @return Future
   */
  public default Future<?> executeAsynchronously(final Runnable pRunnable)
  {
    ThreadPoolExecutor lThreadPoolExecutor =
                                           ClearControlExecutors.getThreadPoolExecutor(this);

    if (lThreadPoolExecutor == null)
      lThreadPoolExecutor = initializeDefaultExecutor();

    return lThreadPoolExecutor.submit(pRunnable);
  }

  /**
   * Executes the given Callable on this instance's executor.
   * 
   * @param pCallable
   *          Callable
   * @return Future
   */
  public default <O> Future<O> executeAsynchronously(final Callable<O> pCallable)
  {
    ThreadPoolExecutor lThreadPoolExecutor =
                                           ClearControlExecutors.getThreadPoolExecutor(this);
    if (lThreadPoolExecutor == null)
      lThreadPoolExecutor = initializeDefaultExecutor();

    return lThreadPoolExecutor.submit(pCallable);
  }

  /**
   * This method shuts down the executor and waits for termination of all tasks.
   * 
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true if this executor terminated and false if the timeout elapsed
   *         before termination
   * @throws InterruptedException
   *           if interrupted
   */
  public default boolean resetThreadPoolAndWaitForCompletion(long pTimeOut,
                                                             TimeUnit pTimeUnit) throws InterruptedException
  {
    final ThreadPoolExecutor lThreadPoolExecutor =
                                                 ClearControlExecutors.getThreadPoolExecutor(this);

    lThreadPoolExecutor.shutdown();
    ClearControlExecutors.resetThreadPoolExecutor(this);

    return lThreadPoolExecutor.awaitTermination(pTimeOut, pTimeUnit);
  }

  /**
   * Waits for completion of all tasks.
   * 
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true if completed before timeout, false otherwise.
   * @throws ExecutionException
   */
  public default boolean waitForCompletion(long pTimeOut,
                                           TimeUnit pTimeUnit) throws ExecutionException
  {
    final CompletingThreadPoolExecutor lThreadPoolExecutor =
                                                           ClearControlExecutors.getThreadPoolExecutor(this);

    if (lThreadPoolExecutor == null)
      return true;

    try
    {
      lThreadPoolExecutor.waitForCompletion(pTimeOut, pTimeUnit);
      return true;
    }
    catch (final TimeoutException e)
    {
      return false;
    }

  }

  public default boolean waitForCompletion() throws ExecutionException
  {
    return waitForCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
  }

}
