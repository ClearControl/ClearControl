package clearcontrol.core.log.gui;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import clearcontrol.core.log.CompactFormatter;

/**
 * Log window handler
 *
 * @author royer
 */
public class LogWindowHandler extends Handler
{
  private static LogWindowHandler sHandler = null;

  private LogWindow mWindow = null;

  /**
   * private constructor, preventing initialization
   */
  private LogWindowHandler(String pTitle, int pWidth, int pHeight)
  {
    setLevel(Level.INFO);
    if (mWindow == null)
      mWindow = new LogWindow(pTitle, pWidth, pHeight);
  }

  /**
   * Sets the visibility of the log window
   * 
   * @param pVisible
   *          visible if true, invisible otherwise
   */
  public static synchronized void setVisible(boolean pVisible)
  {
    if (sHandler != null)
      sHandler.mWindow.setVisible(pVisible);
  }

  /**
   * Disposes the log window
   */
  public static void dispose()
  {
    if (sHandler != null)
      sHandler.mWindow.dispose();
  }

  /**
   * Returns a log window handler for a given title. if it does not exist it is
   * created.
   * 
   * @param pTitle
   *          log window title
   * @return log window handler
   */
  public static synchronized LogWindowHandler getInstance(String pTitle)
  {
    return getInstance(pTitle, 768, 320);
  }

  /**
   * Returns a log window handler for a given title and window dimensions. if it
   * does not exist it is created.
   * 
   * @param pTitle
   *          log window title
   * @param pWidth
   *          window width
   * @param pHeight
   *          window height
   * @return log window handler
   */
  public static synchronized LogWindowHandler getInstance(String pTitle,
                                                          int pWidth,
                                                          int pHeight)
  {
    if (sHandler == null)
    {
      sHandler = new LogWindowHandler(pTitle, pWidth, pHeight);
      sHandler.setFormatter(new CompactFormatter());
    }
    return sHandler;
  }

  @Override
  public synchronized void publish(LogRecord record)
  {
    try
    {
      String message = null;
      // check if the record is loggable
      if (!isLoggable(record))
        return;
      try
      {
        message = getFormatter().format(record);
      }
      catch (final Exception e)
      {
        e.printStackTrace();
      }

      try
      {
        mWindow.append(message);
      }
      catch (final Exception e)
      {
        e.printStackTrace();
      }
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
    }

  }

  @Override
  public void close()
  {
    try
    {
      if (mWindow != null)
        mWindow.dispose();
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void flush()
  {
  }

}
