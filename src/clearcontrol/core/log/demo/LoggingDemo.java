package clearcontrol.core.log.demo;

import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.log.gui.LogWindowHandler;

import org.junit.Test;

/**
 * Logging Demo
 *
 * @author royer
 */
public class LoggingDemo implements LoggingFeature
{

  /**
   * Logging demo
   * 
   * @throws InterruptedException
   *           N/A
   */
  @Test
  public void demo() throws InterruptedException
  {
    for (int i = 0; i < 100; i++)
      info("test", "bla");

    final LogWindowHandler lLogWindowHandler =
                                             LogWindowHandler.getInstance("test",
                                                                          768,
                                                                          320);

    getLogger("test").addHandler(lLogWindowHandler);

    for (int i = 0; i < 100; i++)
      info("test", "blu");

    Thread.sleep(4000);

  }

}
