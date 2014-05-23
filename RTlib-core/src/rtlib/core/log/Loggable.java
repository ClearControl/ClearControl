package rtlib.core.log;

public interface Loggable
{
	public default RTlibLogger getLogger(final String pSubSystemName)
	{
		RTlibLogger lRTlibLogger = RTlibLogger.getLogger(pSubSystemName);
		return lRTlibLogger;
	}

	public default void info(	final String pSubSystemName,
														String pMessage)
	{
		getLogger(pSubSystemName).logMessage(	"INFO",
																					this.getClass(),
																					pMessage);
	}

	public default void warn(	final String pSubSystemName,
														String pMessage)
	{
		getLogger(pSubSystemName).logMessage(	"WARNING",
																					this.getClass(),
																					pMessage);
	}

	public default void error(final String pSubSystemName,
														String pMessage)
	{
		getLogger(pSubSystemName).logMessage(	"ERROR",
																					this.getClass(),
																					pMessage);
	}

	public default void error(final String pSubSystemName,
														String pMessage,
														Throwable e)
	{
		getLogger(pSubSystemName).logMessage(	"ERROR",
																					this.getClass(),
																					pMessage + ": '"
																							+ e.toString()
																							+ "-> "
																							+ e.getStackTrace()[0]
																							+ "'");
	}

}
