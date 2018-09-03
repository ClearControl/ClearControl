package clearcontrol.core.concurrent.executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Runnable that can be run for a given number of maximal executions
 *
 * @author royer
 */
public class LimitedExecutionsRunnable implements Runnable
{
  private final AtomicLong mExecutionCounter = new AtomicLong();
  private final Runnable mDelegatedRunnable;
  private volatile ScheduledFuture<?> mScheduledFuture;
  private final long mMaximumNumberOfExecutions;

  /**
   * Instantiates a limited executions runnable given a delegated runnable and
   * the number of maximal executions
   * 
   * @param pDelegateRunnable
   *          delegated runnable
   * @param pMaximumNumberOfExecutions
   *          maximal number of executions
   */
  public LimitedExecutionsRunnable(Runnable pDelegateRunnable,
                                   long pMaximumNumberOfExecutions)
  {
    this.mDelegatedRunnable = pDelegateRunnable;
    this.mMaximumNumberOfExecutions = pMaximumNumberOfExecutions;
  }

  @Override
  public void run()
  {
    if (mScheduledFuture == null)
      throw new UnsupportedOperationException("Scheduling and execution of "
                                              + LimitedExecutionsRunnable.class.getSimpleName()
                                              + " instances should be done using this class methods only. ");

    mDelegatedRunnable.run();
    if (mExecutionCounter.incrementAndGet() == mMaximumNumberOfExecutions)
    {
      mScheduledFuture.cancel(false);
    }
  }

  /**
   * Executes the delegated runnable a given maximal number of times on a given
   * scheduled executor service with a given execution period.
   * 
   * @param pScheduledExecutorService
   *          schedules executor service
   * @param pPeriod
   *          execution period
   * @param pTimeUnit
   *          execution period time unit
   * @return schedules future
   */
  public ScheduledFuture<?> runNTimes(ScheduledExecutorService pScheduledExecutorService,
                                      long pPeriod,
                                      TimeUnit pTimeUnit)
  {
    return runNTimes(pScheduledExecutorService, pPeriod, pTimeUnit);
  }

  /**
   * Executes the delegated runnable a given maximal number of times on a given
   * scheduled executor service with a given execution period but after a given
   * delay.
   * 
   * @param pScheduledExecutorService
   *          schedules executor service
   * @param pInitialDelay
   *          initial delay
   * @param pPeriod
   *          execution period
   * @param pTimeUnit
   *          execution period and delay time unit
   * @return schedules future
   */
  public ScheduledFuture<?> runNTimes(ScheduledExecutorService pScheduledExecutorService,
                                      long pInitialDelay,
                                      long pPeriod,
                                      TimeUnit pTimeUnit)
  {
    mScheduledFuture =
                     pScheduledExecutorService.scheduleAtFixedRate(this,
                                                                   pInitialDelay,
                                                                   pPeriod,
                                                                   pTimeUnit);
    return mScheduledFuture;
  }

  /**
   * Executes the delegated runnable on an object supporting access to an
   * asynchronous scheduler with a given execution period.
   * 
   * @param pAsynchronousSchedulerService
   *          object supporting access to an asynchronous scheduler
   * @param pPeriod
   *          execution period
   * @param pUnit
   *          execution period unit
   * @return scheduled future
   */
  public ScheduledFuture<?> runNTimes(AsynchronousSchedulerFeature pAsynchronousSchedulerService,
                                      long pPeriod,
                                      TimeUnit pUnit)
  {
    mScheduledFuture =
                     pAsynchronousSchedulerService.scheduleAtFixedRate(this,
                                                                       pPeriod,
                                                                       pUnit);
    return mScheduledFuture;
  }

  /**
   * Wraps a limited execution runnable around a delegated runnable. this will
   * limit the number of executions of the delegated runnable.
   * 
   * @param pDelegateRunnable
   *          delegated runnable
   * @param pMaximumNumberOfExecutions
   *          maximum number of executions
   * @return Limited executions runnable
   */
  public static LimitedExecutionsRunnable wrap(Runnable pDelegateRunnable,
                                               long pMaximumNumberOfExecutions)
  {
    return new LimitedExecutionsRunnable(pDelegateRunnable,
                                         pMaximumNumberOfExecutions);
  }
}
