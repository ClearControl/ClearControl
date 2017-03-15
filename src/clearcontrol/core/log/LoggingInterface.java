package clearcontrol.core.log;

import java.util.logging.Handler;
import java.util.logging.Logger;

public interface LoggingInterface
{

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

  static final String cMainLoggerName = "main";

  static Reference<Logger> sLoggerReference = new Reference<Logger>();

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

  public default Logger getLogger(final String pSubSystemName)
  {
    return Logger.getLogger(pSubSystemName);
  }

  public default void info(String pMessage)
  {
    getLoggerStatic().info(this.getClass().getSimpleName() + ": "
                           + pMessage.trim());
  }

  public default void info(String pFormat, Object... args)
  {
    getLoggerStatic().info(this.getClass().getSimpleName() + ": "
                           + String.format(pFormat, args).trim());
  }

  public default void warning(String pMessage)
  {
    getLoggerStatic().warning(this.getClass().getSimpleName() + ": "
                              + pMessage.trim());
  }

  public default void warning(String pFormat, Object... args)
  {
    getLoggerStatic().warning(this.getClass().getSimpleName() + ": "
                              + String.format(pFormat, args).trim());
  }

  public default void severe(String pMessage)
  {
    getLoggerStatic().severe(this.getClass().getSimpleName() + ": "
                             + pMessage.trim());
  }

  public default void severe(String pFormat, Object... args)
  {
    getLoggerStatic().severe(this.getClass().getSimpleName() + ": "
                             + String.format(pFormat, args).trim());
  }

}
