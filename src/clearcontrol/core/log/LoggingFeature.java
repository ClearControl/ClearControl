package clearcontrol.core.log;

import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Logging interface
 *
 * @author royer
 */
public interface LoggingFeature
{

  /**
   * Main logger name
   */
  static final String cMainLoggerName = "main";

  /**
   * Logger reference
   */
  static Reference<Logger> sLoggerReference = new Reference<Logger>();

  /**
   * Static method that returns the logger
   * 
   * @return logger instance
   */
  static Logger getLoggerStatic()
  {

    if (sLoggerReference.get() != null)
      return sLoggerReference.get();

    sLoggerReference.set(Logger.getLogger(cMainLoggerName));

    sLoggerReference.get().setUseParentHandlers(true);

    Handler[] lHandlers = sLoggerReference.get()
                                          .getParent()
                                          .getHandlers();

    for (Handler lHandler : lHandlers)
      sLoggerReference.get().getParent().removeHandler(lHandler);

    StdOutConsoleHandler lStdOutConsoleHandler =
                                               new StdOutConsoleHandler();
    sLoggerReference.get()
                    .getParent()
                    .addHandler(lStdOutConsoleHandler);

    for (final Handler lHandler : sLoggerReference.get()
                                                  .getHandlers())
      lHandler.setFormatter(new CompactFormatter());

    for (final Handler lHandler : sLoggerReference.get()
                                                  .getParent()
                                                  .getHandlers())
      lHandler.setFormatter(new CompactFormatter());

    return sLoggerReference.get();
  }

  /**
   * Returns the logger for a given subsystem
   * 
   * @param pSubSystemName
   *          subsystem name
   * @return logger
   */
  public default Logger getLogger(final String pSubSystemName)
  {
    return Logger.getLogger(pSubSystemName);
  }

  /**
   * Logs an information message
   * 
   * @param pMessage
   *          message
   */
  public default void info(String pMessage)
  {
    getLoggerStatic().info(getClassName(this) + ": "
                           + pMessage.trim());
  }

  /**
   * Logs an information message
   * 
   * @param pFormat
   *          format string
   * @param args
   *          format string parameters
   */
  public default void info(String pFormat, Object... args)
  {
    getLoggerStatic().info(getClassName(this) + ": "
                           + String.format(pFormat, args).trim());
  }

  /**
   * Logs a warning message
   * 
   * @param pMessage
   *          warning message
   */
  public default void warning(String pMessage)
  {
    getLoggerStatic().warning(getClassName(this) + ": "
                              + pMessage.trim());
  }

  /**
   * Logs a warning message
   * 
   * @param pFormat
   *          format string
   * @param args
   *          format string parameters
   */
  public default void warning(String pFormat, Object... args)
  {
    getLoggerStatic().warning(getClassName(this) + ": "
                              + String.format(pFormat, args).trim());
  }

  /**
   * Logs a severe message
   * 
   * @param pMessage
   *          severe message
   */
  public default void severe(String pMessage)
  {
    getLoggerStatic().severe(getClassName(this) + ": "
                             + pMessage.trim());
  }

  /**
   * Logs a sever message
   * 
   * @param pFormat
   *          format string
   * @param args
   *          format string parameters
   */
  public default void severe(String pFormat, Object... args)
  {
    getLoggerStatic().severe(getClassName(this) + ": "
                             + String.format(pFormat, args).trim());
  }

  /**
   * Utility method to return the class name for a given object
   * 
   * @param pObject
   *          object
   * @return object's class name
   */
  public default String getClassName(Object pObject)
  {
    String lSimpleName = pObject.getClass().getSimpleName();

    if (lSimpleName == null || lSimpleName.trim().isEmpty())
    {
      lSimpleName = pObject.getClass()
                           .getEnclosingClass()
                           .getSimpleName();
    }

    return lSimpleName;
  }

  /**
   * Reference
   *
   * @param <T>
   * @author royer
   */
  class Reference<T>
  {
    public volatile T mReference;

    public T get()
    {
      return mReference;
    }

    public void set(T pReference)
    {
      mReference = pReference;
    }
  }

}
