package clearcontrol.core.log.gui.demo;

import java.util.logging.Logger;

import clearcontrol.core.log.CompactFormatter;
import clearcontrol.core.log.gui.LogWindowHandler;

/**
 * Log window demo
 *
 * @author royer
 */
public class LogWindowDemo
{
  private LogWindowHandler mLogWindowHandler = null;

  private Logger mLogger = null;

  /**
   * Instancates a log window demo
   */
  public LogWindowDemo()
  {
    mLogWindowHandler =
                      LogWindowHandler.getInstance("test", 768, 320);
    mLogger = Logger.getLogger("test");
    // mLogger.setUseParentHandlers(false);
    mLogWindowHandler.setFormatter(new CompactFormatter());
    mLogger.addHandler(mLogWindowHandler);
  }

  /**
   * Logs a message
   * 
   * @param i
   *          counter
   */
  public void logMessage(int i)
  {
    mLogger.info(i + " Hello from LogWindowHandler...");
  }

  /**
   * Main
   * 
   * @param args
   *          N/A
   * @throws InterruptedException
   *           N/A
   */
  public static void main(String args[]) throws InterruptedException
  {
    final LogWindowDemo lLogWindowDemo = new LogWindowDemo();

    for (int i = 0; i < 1000; i++)
    {
      lLogWindowDemo.logMessage(i);
      Thread.sleep(1);
    }

    LogWindowHandler.setVisible(true);
    Thread.sleep(4000);
    LogWindowHandler.dispose();
  }
}