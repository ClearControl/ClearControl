package clearcontrol.core.concurrent.executors;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous scheduler feature. Provides methods for starting scheduled tasks
 * 
 *
 * @author royer
 */
public interface AsynchronousSchedulerFeature
{

  /**
   * Initializes a scheduled executor
   * 
   * @return scheduled thread pool executor
   */
  public default ScheduledThreadPoolExecutor initializeScheduledThreadPoolExecutors()
  {
    return ClearControlExecutors.getOrCreateScheduledThreadPoolExecutor(this,
                                                                        Thread.NORM_PRIORITY,
                                                                        1,
                                                                        Integer.MAX_VALUE);

  }

  /**
   * Schedules a runnable to execute after a given delay
   * 
   * @param pRunnable
   *          runnable
   * @param pDelay
   *          delay
   * @param pUnit
   *          delay unit
   * @return future
   */
  @SuppressWarnings(
  { "unchecked", "rawtypes" })
  public default WaitingScheduledFuture<?> schedule(Runnable pRunnable,
                                                    long pDelay,
                                                    TimeUnit pUnit)
  {
    ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor =
                                                             ClearControlExecutors.getScheduledThreadPoolExecutor(this);
    if (lScheduledThreadPoolExecutor == null)
      lScheduledThreadPoolExecutor =
                                   initializeScheduledThreadPoolExecutors();

    return new WaitingScheduledFuture(lScheduledThreadPoolExecutor.schedule(pRunnable,
                                                                            pDelay,
                                                                            pUnit));
  }

  /**
   * Schedules a runnable to execute at fixed rate with a given period
   * 
   * @param pRunnable
   *          runnable
   * @param pPeriod
   *          period
   * @param pUnit
   *          period unit
   * @return future
   */
  public default WaitingScheduledFuture<?> scheduleAtFixedRate(Runnable pRunnable,
                                                               long pPeriod,
                                                               TimeUnit pUnit)
  {
    return scheduleAtFixedRate(pRunnable, 0, pPeriod, pUnit);
  }

  /**
   * Schedules a runnable to execute at fixed rate, with a given inicial delay,
   * period, and time unit.
   * 
   * @param pRunnable
   *          runnable
   * @param pInitialDelay
   *          initial delay
   * @param pPeriod
   *          period
   * @param pTimeUnit
   *          period unit
   * @return future
   */
  @SuppressWarnings(
  { "unchecked", "rawtypes" })
  public default WaitingScheduledFuture<?> scheduleAtFixedRate(Runnable pRunnable,
                                                               long pInitialDelay,
                                                               long pPeriod,
                                                               TimeUnit pTimeUnit)
  {
    ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor =
                                                             ClearControlExecutors.getScheduledThreadPoolExecutor(this);
    if (lScheduledThreadPoolExecutor == null)
      lScheduledThreadPoolExecutor =
                                   initializeScheduledThreadPoolExecutors();

    return new WaitingScheduledFuture(lScheduledThreadPoolExecutor.scheduleAtFixedRate(pRunnable,
                                                                                       pInitialDelay,
                                                                                       pPeriod,
                                                                                       pTimeUnit));
  }

  /**
   * Schedules the execution of a given runnable for a fixed number of times and
   * period
   * 
   * @param pRunnable
   *          runnable
   * @param pTimes
   *          number of times to execute runnable
   * @param pPeriod
   *          period
   * @param pTimeUnit
   *          time unit
   * @return future
   */
  public default WaitingScheduledFuture<?> scheduleNTimesAtFixedRate(Runnable pRunnable,
                                                                     long pTimes,
                                                                     long pPeriod,
                                                                     TimeUnit pTimeUnit)
  {
    return scheduleNTimesAtFixedRate(pRunnable,
                                     pTimes,
                                     0,
                                     pPeriod,
                                     pTimeUnit);
  }

  /**
   * Schedules the execution of a runnable for a fixed number of times, after an
   * initial delay and with a given period.
   * 
   * @param pRunnable
   *          runnable
   * @param pTimes
   *          number of times to execute runnable
   * @param pInitialDelay
   *          initial delay
   * @param pPeriod
   *          period
   * @param pTimeUnit
   *          time unit
   * @return future
   */
  @SuppressWarnings(
  { "unchecked", "rawtypes" })
  public default WaitingScheduledFuture<?> scheduleNTimesAtFixedRate(Runnable pRunnable,
                                                                     long pTimes,
                                                                     long pInitialDelay,
                                                                     long pPeriod,
                                                                     TimeUnit pTimeUnit)
  {
    final LimitedExecutionsRunnable lLimitedExecutionsRunnable =
                                                               LimitedExecutionsRunnable.wrap(pRunnable,
                                                                                              pTimes);

    ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor =
                                                             ClearControlExecutors.getScheduledThreadPoolExecutor(this);
    if (lScheduledThreadPoolExecutor == null)
      lScheduledThreadPoolExecutor =
                                   initializeScheduledThreadPoolExecutors();

    return new WaitingScheduledFuture(lLimitedExecutionsRunnable.runNTimes(lScheduledThreadPoolExecutor,
                                                                           pInitialDelay,
                                                                           pPeriod,
                                                                           pTimeUnit));

  }

}
