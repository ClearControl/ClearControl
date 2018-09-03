package clearcontrol.core.cpu.test;

import clearcontrol.core.cpu.Affinity;

import org.junit.Test;

/**
 * Affinity tests
 *
 * @author royer
 */
public class AffinityTests
{

  /**
   * 
   */
  @Test
  public void test()
  {
    int lNumberOfCores = Runtime.getRuntime().availableProcessors();

    for (int i = 0; i < lNumberOfCores; i++)
    {
      Runnable lRunnable = () -> {
        long value = 0;
        while (true)
        {
          value += value;
        }
      };
      Thread lPinnedThreadOnSameCore =
                                     Affinity.createPinnedThread(Affinity.cDifferentCoreAfinityThreadFactory,
                                                                 "PinnedThread" + i,
                                                                 lRunnable);
      lPinnedThreadOnSameCore.setDaemon(true);
      lPinnedThreadOnSameCore.start();

    }

    // ThreadSleep.sleep(1000, TimeUnit.SECONDS);

  }

}
