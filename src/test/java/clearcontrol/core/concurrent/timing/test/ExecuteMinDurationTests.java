package clearcontrol.core.concurrent.timing.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.concurrent.timing.ElapsedTime;
import clearcontrol.core.concurrent.timing.ExecuteMinDuration;

import org.junit.Test;

/**
 * Execute min druation tests
 *
 * @author royer
 */
public class ExecuteMinDurationTests
{

  /**
   * Test
   */
  @Test
  public void test()
  {
    {

      double lElapsedTimeInMilliseconds = 0;

      for (int i = 0; i < 10; i++)
      {
        lElapsedTimeInMilliseconds =
                                   ElapsedTime.measure("test",
                                                       () -> ExecuteMinDuration.execute(50,
                                                                                        TimeUnit.MILLISECONDS,
                                                                                        () -> ThreadSleep.sleep(5,
                                                                                                                TimeUnit.MILLISECONDS)));
      }

      assertEquals(50, lElapsedTimeInMilliseconds, 10);
    }

    {
      double lElapsedTimeInMilliseconds = 0;

      for (int i = 0; i < 10; i++)
      {
        lElapsedTimeInMilliseconds =
                                   ElapsedTime.measure("test",
                                                       () -> ExecuteMinDuration.execute(10,
                                                                                        TimeUnit.MILLISECONDS,
                                                                                        () -> ThreadSleep.sleep(20,
                                                                                                                TimeUnit.MILLISECONDS)));
      }

      assertEquals(20, lElapsedTimeInMilliseconds, 10);
    }
  }

}
