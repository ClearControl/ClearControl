package clearcontrol.core.concurrent.timing.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.concurrent.timing.ElapsedTime;
import clearcontrol.core.concurrent.timing.ExecuteMinDuration;

import org.junit.Test;

/**
 * 
 *
 * @author royer
 */
public class ExecuteMinDurationTests
{

  /**
   *  
   */
  @Test
  public void test()
  {
    {

      double lElapsedTimeInMilliseconds =
                                        ElapsedTime.measure("test",
                                                            () -> ExecuteMinDuration.execute(50,
                                                                                             TimeUnit.MILLISECONDS,
                                                                                             () -> System.gc()));

      assertEquals(50, lElapsedTimeInMilliseconds, 5);
    }

    {
      double lElapsedTimeInMilliseconds =
                                        ElapsedTime.measure("test",
                                                            () -> ExecuteMinDuration.execute(10,
                                                                                             TimeUnit.MILLISECONDS,
                                                                                             () -> ThreadUtils.sleep(20,
                                                                                                                     TimeUnit.MILLISECONDS)));

      assertEquals(20, lElapsedTimeInMilliseconds, 5);
    }
  }

}
