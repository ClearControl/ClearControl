package clearcontrol.core.concurrent.timing;

import java.util.concurrent.TimeUnit;

/**
 * Executes a runnable synchronously and ensures that the call lasts for a given
 * duration.
 *
 * @author royer
 */
public class ExecuteMinDuration
{
  /**
   * Executes a runnable synchronously and ensures that the call last for a
   * given duration.
   * 
   * @param pTime
   *          minimal execution time for call
   * @param pTimeUnit
   *          time unit
   * @param pRunnable
   *          runnable to execute
   */
  public static void execute(long pTime,
                             TimeUnit pTimeUnit,
                             Runnable pRunnable)
  {
    long lDeadlineTime = System.nanoTime() + pTimeUnit.toNanos(pTime);
    pRunnable.run();
    WaitingInterface.waitForStatic(() -> System.nanoTime() >= lDeadlineTime);
  }
}
