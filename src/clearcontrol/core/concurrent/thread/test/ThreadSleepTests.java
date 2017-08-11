package clearcontrol.core.concurrent.thread.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.units.OrderOfMagnitude;

import org.junit.Test;

/**
 * ThreadSleep tests
 *
 * @author royer
 */
public class ThreadSleepTests
{

  /**
   * Tests sleep method
   * 
   * @throws InterruptedException
   *           N/A
   */
  @Test
  public void testSleep() throws InterruptedException
  {
    Runnable lParasiteRunnable = () -> {
      long[] lDummyData = new long[10000];
      for (int i = 0; i < 10000; i++)
      {
        lDummyData[i] += Math.random() * 10;
        ThreadSleep.sleep(lDummyData[i], TimeUnit.MILLISECONDS);
      }
    };

    for (int i = 0; i < 100; i++)
    {
      Thread lParasiteThread = new Thread(lParasiteRunnable);
      lParasiteThread.setDaemon(true);
      lParasiteThread.start();

      long lSleepTimeNanos = (long) (100000000 * Math.random());

      long lStart = System.nanoTime();
      ThreadSleep.sleep(lSleepTimeNanos, TimeUnit.NANOSECONDS);
      // Thread.sleep((long) Magnitude.nano2milli(lSleepTimeNanos));
      long lStop = System.nanoTime();
      long lElapsedTimeNanos = lStop - lStart;

      double lRelativeError =
                            OrderOfMagnitude.nano2milli((1.0
                                                         * lSleepTimeNanos
                                                         - lElapsedTimeNanos)
                                                        / lSleepTimeNanos);
      // System.out.println("rel error=" + lRelativeError);

      assertTrue(Math.abs(lRelativeError) < 1E-3);

    }
  }

  static volatile boolean flag;

  /**
   * Tests sleepWhile method
   * 
   * @throws Exception
   *           N/A
   */
  @Test
  public void testSleepWhile() throws Exception
  {

    Runnable lRunnable = () -> {
      ThreadSleep.sleep(250, TimeUnit.MILLISECONDS);
      flag = false;
    };

    Thread lOtherThread = new Thread(lRunnable);
    lOtherThread.setDaemon(true);

    Callable<Boolean> lCondition = () -> {
      return flag;
    };

    flag = true;

    long lStart = System.nanoTime();
    lOtherThread.start();
    ThreadSleep.sleepWhile(500, TimeUnit.MILLISECONDS, lCondition);
    long lStop = System.nanoTime();

    long lElapsed =
                  TimeUnit.MILLISECONDS.convert(lStop - lStart,
                                                TimeUnit.NANOSECONDS);

    assertTrue(lElapsed > 200 && lElapsed < 300);
  }
}
