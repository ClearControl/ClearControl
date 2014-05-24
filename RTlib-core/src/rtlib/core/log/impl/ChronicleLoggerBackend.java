package rtlib.core.log.impl;

import java.io.File;
import java.io.IOException;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;
import rtlib.core.log.LoggerBackendInterface;

public class ChronicleLoggerBackend implements LoggerBackendInterface
{
	private static final long cMaxExcerptLength = 2048;

	private Chronicle mChronicle;
	private ExcerptAppender mAppender;
	private File mLogFile;

	public ChronicleLoggerBackend()
	{
		super();
	}

	@Override
	public void setLogFile(File lLogFile) throws IOException
	{
		if (mChronicle != null)
			close();
		mLogFile = lLogFile;
		mChronicle = new IndexedChronicle(lLogFile.getAbsolutePath());
	}

	@Override
	public File getLogFile()
	{
		return mLogFile;
	}

	@Override
	public File getLogDataFile()
	{
		return new File(mLogFile.getAbsolutePath() + ".data");
	}

	public File getLogIndexFile()
	{
		return new File(mLogFile.getAbsolutePath() + ".index");
	}

	private void ensureAppenderAllocated()
	{
		if (mChronicle != null && mAppender == null)
			try
			{
				mAppender = mChronicle.createAppender();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}

	@Override
	public void logMessage(	String pType,
													Class<?> pClass,
													String pMessage)
	{
		ensureAppenderAllocated();

		if (mAppender == null)
		{
			if (pType.equals("ERROR"))
				System.err.println(getMessageLineString(pType,
																								pClass,
																								pMessage));
			else
				logMessage("ERROR", pClass, pMessage);
			return;
		}

		mAppender.startExcerpt(cMaxExcerptLength);
		mAppender.appendDateTimeMillis(System.currentTimeMillis());
		mAppender.append('\t');
		mAppender.append(pType);
		mAppender.append('\t');
		mAppender.append(pClass.getCanonicalName());
		mAppender.append('\t');
		mAppender.append(pMessage);
		mAppender.append('\n');
		mAppender.finish();
	}

	@Override
	public void flush()
	{
		if (mAppender == null)
			return;
		mAppender.flush();
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

	@Override
	public void close() throws IOException
	{
		if (mAppender != null)
			mAppender.close();
		mAppender = null;
		if (mChronicle != null)
			mChronicle.close();
		mChronicle = null;
	}

}
