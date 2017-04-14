package clearcontrol.core.concurrent.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Class managing executors via static methods
 *
 * @author royer
 */
public class ClearControlExecutors
{

  private static ConcurrentHashMap<Object, CompletingThreadPoolExecutor> cThreadPoolExecutorMap =
                                                                                                new ConcurrentHashMap<>(100);
  private static ConcurrentHashMap<Object, ScheduledThreadPoolExecutor> cScheduledThreadPoolExecutorMap =
                                                                                                        new ConcurrentHashMap<>(100);

  /**
   * Returns a completing thread pool executor given a key
   * 
   * @param pObject
   *          key to use
   * @return completing thread pool executor
   */
  public static final CompletingThreadPoolExecutor getThreadPoolExecutor(final Object pObject)
  {
    final CompletingThreadPoolExecutor lExecutor =
                                                 cThreadPoolExecutorMap.get(pObject);

    return lExecutor;
  }

  /**
   * Returns a scheduled thread pool executor given a key
   * 
   * @param pObject
   *          key
   * @return scheduled thread pool executor
   */
  public static final ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor(final Object pObject)
  {
    final ScheduledThreadPoolExecutor lExecutor =
                                                cScheduledThreadPoolExecutorMap.get(pObject);

    return lExecutor;
  }

  /**
   * Clears all thread pol executors
   */
  public static void clearsAllThreadPoolExecutors()
  {
    cThreadPoolExecutorMap.clear();
  }

  /**
   * Clears all scheduled thread pool executors
   */
  public static void clearsAllScheduledThreadPoolExecutors()
  {
    cScheduledThreadPoolExecutorMap.clear();
  }

  /**
   * Resets the thread pool executor for a given key
   * 
   * @param pObject
   *          key
   */
  public static void resetThreadPoolExecutor(final Object pObject)
  {
    cThreadPoolExecutorMap.remove(pObject);
  }

  /**
   * Resets the scheduled thread pool executor for a given key
   * 
   * @param pObject
   *          key
   */
  public static void resetScheduledThreadPoolExecutor(final Object pObject)
  {
    cScheduledThreadPoolExecutorMap.remove(pObject);
  }

  /**
   * Returns (or creates) a completing thread pool executor for a given key.
   * 
   * @param pObject
   *          key
   * @param pPriority
   *          priority
   * @param pCorePoolSize
   *          core pool size
   * @param pMaxPoolSize
   *          max pool size
   * @param pMaxQueueLength
   *          max queue length
   * @return scheduld thread pool executor
   */
  public static final CompletingThreadPoolExecutor getOrCreateThreadPoolExecutor(final Object pObject,
                                                                                 final int pPriority,
                                                                                 final int pCorePoolSize,
                                                                                 final int pMaxPoolSize,
                                                                                 final int pMaxQueueLength)
  {
    CompletingThreadPoolExecutor lThreadPoolExecutor =
                                                     cThreadPoolExecutorMap.get(pObject);

    if (lThreadPoolExecutor == null)
    {
      final BlockingQueue<Runnable> lNewQueue =
                                              new LinkedBlockingQueue<>(pMaxQueueLength);

      String lThreadName = getThreadName(pObject);

      lThreadPoolExecutor =
                          new CompletingThreadPoolExecutor(pCorePoolSize,
                                                           pMaxPoolSize,
                                                           1,
                                                           TimeUnit.MINUTES,
                                                           lNewQueue,
                                                           getThreadFactory(lThreadName,
                                                                            pPriority));

      lThreadPoolExecutor.allowCoreThreadTimeOut(false);
      lThreadPoolExecutor.prestartAllCoreThreads();

      cThreadPoolExecutorMap.put(pObject, lThreadPoolExecutor);
    }

    return lThreadPoolExecutor;
  }

  /**
   * Returns (or creates) a scheduled thread pool executor for a given key.
   * 
   * @param pObject
   *          key
   * @param pPriority
   *          priority
   * @param pCorePoolSize
   *          core pool size
   * @param pMaxQueueLength
   *          max queue length
   * @return scheduld thread pool executor
   */
  public static final ScheduledThreadPoolExecutor getOrCreateScheduledThreadPoolExecutor(final Object pObject,
                                                                                         final int pPriority,
                                                                                         final int pCorePoolSize,
                                                                                         final int pMaxQueueLength)
  {

    ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor =
                                                             cScheduledThreadPoolExecutorMap.get(pObject);

    if (lScheduledThreadPoolExecutor == null)
    {
      String lThreadName = getThreadName(pObject);

      lScheduledThreadPoolExecutor =
                                   new ScheduledThreadPoolExecutor(pCorePoolSize,
                                                                   getThreadFactory(lThreadName,
                                                                                    pPriority));

      lScheduledThreadPoolExecutor.allowCoreThreadTimeOut(false);
      lScheduledThreadPoolExecutor.prestartAllCoreThreads();
      lScheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
      lScheduledThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
      lScheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);

      cScheduledThreadPoolExecutorMap.put(pObject,
                                          lScheduledThreadPoolExecutor);
    }

    return lScheduledThreadPoolExecutor;
  }

  private static String getThreadName(final Object pObject)
  {
    String lName = pObject.getClass().getSimpleName();
    if (lName.isEmpty())
      lName = pObject.getClass().getName();
    return lName;
  }

  /**
   * Returns a thread factory with given thread name and priority
   * 
   * @param pName
   *          name
   * @param pPriority
   *          thread priority
   * @return thread factory
   */
  public static final ThreadFactory getThreadFactory(final String pName,
                                                     final int pPriority)
  {
    if (pName.isEmpty())
      throw new IllegalArgumentException("Thread name cannot be empty");

    final ThreadFactory lThreadFactory = new ThreadFactory()
    {
      long mThreadId = 0;

      @Override
      public Thread newThread(Runnable pRunnable)
      {
        final Thread lThread = new Thread(pRunnable);
        lThread.setName(pName + "-" + mThreadId++);
        lThread.setPriority(pPriority);
        lThread.setDaemon(true);

        return lThread;
      }
    };
    return lThreadFactory;
  }

}
