package clearcontrol.core.concurrent.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods for putting threads to sleep
 *
 * @author royer
 */
public class ThreadSleep
{
  /**
   * Puts current thread to sleep for a given duration.
   * 
   * @param pSleepDuration
   *          duration
   * @param pSleepDurationTimeUnit
   *          duration time unit
   */
  public static final void sleep(long pSleepDuration,
                                 TimeUnit pSleepDurationTimeUnit)
  {
    final long lStart = System.nanoTime();
    long lDeadlineInNanos = lStart
                            + pSleepDurationTimeUnit.toNanos(pSleepDuration);

    boolean lSleepTimeBelowMillisecond =
                                       pSleepDurationTimeUnit.toMillis(pSleepDuration) == 0;

    long lNanoTime;
    while ((lNanoTime = System.nanoTime()) < lDeadlineInNanos)
    {

      try
      {
        if (lSleepTimeBelowMillisecond)
        {
          long lTimeToWaitInNanos = 3 * (lDeadlineInNanos - lNanoTime)
                                    / 4;
          if (lTimeToWaitInNanos > 0)
            Thread.sleep(0, (int) lTimeToWaitInNanos);
        }
        else
        {
          long lTimeToWaitInNanos = 3 * (lDeadlineInNanos - lNanoTime)
                                    / 4;
          if (lTimeToWaitInNanos > 0)
          {
            long lTimeToWaitInMillis =
                                     TimeUnit.NANOSECONDS.toMillis(lTimeToWaitInNanos);

            Thread.sleep(lTimeToWaitInMillis,
                         (int) (lTimeToWaitInNanos % 1000000L));
          }
        }
      }
      catch (InterruptedException e)
      {
      }
    }
  }

  /**
   * Puts current thread to sleep for a given time but wakes up thread if
   * condition is not true anymore.
   * 
   * @param pSleepDuration
   *          sleep duration
   * @param pSleepDurationTimeUnit
   *          sleep duration unit
   * @param pCondition
   *          this callable is called repeatedly, once it returns false, the
   *          thread is woken up and execution continues past the call to this
   *          method.
   */
  public static final void sleepWhile(long pSleepDuration,
                                      TimeUnit pSleepDurationTimeUnit,
                                      Callable<Boolean> pCondition)
  {
    final long lStart = System.nanoTime();
    long lDeadlineInNanos = lStart
                            + pSleepDurationTimeUnit.toNanos(pSleepDuration);

    boolean lSleepTimeBelowMillisecond =
                                       pSleepDurationTimeUnit.toMillis(pSleepDuration) == 0;

    long lNanoTime;
    while ((lNanoTime = System.nanoTime()) < lDeadlineInNanos)
    {

      try
      {
        if (!pCondition.call())
          break;

        if (lSleepTimeBelowMillisecond)
        {
          long lTimeToWaitInNanos =
                                  (lDeadlineInNanos - lNanoTime) / 4;
          if (lTimeToWaitInNanos > 0)
            Thread.sleep(0, (int) lTimeToWaitInNanos);
        }
        else
        {
          long lTimeToWaitInNanos = (lDeadlineInNanos - lNanoTime)
                                    % 1000000;
          if (lTimeToWaitInNanos > 0)
          {
            long lTimeToWaitInMillis =
                                     TimeUnit.NANOSECONDS.toMillis(lTimeToWaitInNanos);

            Thread.sleep(lTimeToWaitInMillis,
                         (int) (lTimeToWaitInNanos % 1000000L));
          }
        }
      }
      catch (InterruptedException e)
      {
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}
