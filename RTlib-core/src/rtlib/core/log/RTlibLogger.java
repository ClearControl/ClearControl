package rtlib.core.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.log.impl.ChronicleLoggerBackend;
import sun.misc.Contended;

public class RTlibLogger
{

	private static final File cTempFolder = new File(System.getProperty("java.io.tmpdir"));
	private static File cLogRootFolder = cTempFolder;
	private static final MachineConfiguration cMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();
	private static final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.E.HH.mm.ss.zzz");
	private static final ConcurrentHashMap<String, RTlibLogger> sLoggerMap = new ConcurrentHashMap<String, RTlibLogger>();

	private static Class<? extends LoggerBackendInterface> sDefaultLoggerBackEnd = ChronicleLoggerBackend.class;

	public static RTlibLogger getLogger(String pSubSystemName)
	{
		RTlibLogger lRTlibLogger = sLoggerMap.get(pSubSystemName);
		if (lRTlibLogger == null)
		{
			synchronized (sLoggerMap)
			{
				try
				{
					LoggerBackendInterface lLoggerbackendInstance = sDefaultLoggerBackEnd.newInstance();
					lRTlibLogger = new RTlibLogger(	lLoggerbackendInstance,
																					pSubSystemName);
					sLoggerMap.put(pSubSystemName, lRTlibLogger);

					File lLogFolderFile = getLogFolderLocation();
					File lDateTimeSubFolder = new File(	lLogFolderFile,
																							getDateTime());
					File lLogFile = new File(lDateTimeSubFolder, pSubSystemName);
					lLoggerbackendInstance.setLogFile(lLogFile);
				}
				catch (InstantiationException | IllegalAccessException
						| IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return lRTlibLogger;
	}

	private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
	private final String mSubSystemName;
	private final LoggerBackendInterface mLoggerBackend;

	@Contended
	private volatile boolean mStdOutputActive;

	public RTlibLogger(	LoggerBackendInterface pLoggerBackend,
											String pSubSystemName)
	{
		super();
		mLoggerBackend = pLoggerBackend;
		mSubSystemName = pSubSystemName;
	}

	public String getSubSystemName()
	{
		return mSubSystemName;
	}

	public boolean isStdOutputActive()
	{
		return mStdOutputActive;
	}

	public void setStdOutputActive(boolean mStdOutputActive)
	{
		this.mStdOutputActive = mStdOutputActive;
	}

	public File getLogFile()
	{
		return mLoggerBackend.getLogFile();
	}

	public File getLogDataFile()
	{
		return mLoggerBackend.getLogDataFile();
	}

	private static final String getDateTime()
	{
		final Date lNow = new Date();
		final String lFormattedDateTime = mSimpleDateFormat.format(lNow);
		return lFormattedDateTime;
	}

	private static File getLogFolderLocation()
	{
		File lLogFolder = cMachineConfiguration.getFileProperty("logging.folder",
																														new File(	cLogRootFolder,
																																			"RTlib.log"));
		lLogFolder.mkdir();
		return lLogFolder;
	}

	protected void logMessageInternal(String pType,
																		Class<?> pClass,
																		String pMessage)
	{

		if (isStdOutputActive())
		{
			if (pType.equals("ERROR"))
				System.err.println(getMessageLineString(pType,
																								pClass,
																								pMessage));
			else
				System.out.println(getMessageLineString(pType,
																								pClass,
																								pMessage));
		}

		mLoggerBackend.logMessage(pType, pClass, pMessage);

	}

	public void logMessage(	String pType,
													Class<?> pClass,
													String pMessage)
	{
		mExecutorService.execute(() -> logMessageInternal(pType,
																											pClass,
																											pMessage));
	}

	public void logInfo(Class<?> pClass, String pMessage)
	{
		logMessage("INFO", pClass, pMessage);
	}

	public void logWarning(Class<?> pClass, String pMessage)
	{
		logMessage("WARNING", pClass, pMessage);
	}

	public void logError(Class<?> pClass, String pMessage)
	{
		logMessage("ERROR", pClass, pMessage);
	}

	private String getMessageLineString(String pType,
																			Class<?> pClass,
																			String pMessage)
	{
		return pType + "\t"
						+ pClass.getCanonicalName()
						+ "\t"
						+ pMessage
						+ "\n";
	}

	public void flush()
	{
		mLoggerBackend.flush();
	}

	public void close() throws IOException
	{
		mLoggerBackend.close();
	}

	public void waitForCompletion()
	{
		try
		{
			mExecutorService.awaitTermination(1, TimeUnit.SECONDS);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public static File getLogRootFolder()
	{
		return cLogRootFolder;
	}

	public static void setLogRootFolder(File pLogRootFolder)
	{
		cLogRootFolder = pLogRootFolder;
	}

}
