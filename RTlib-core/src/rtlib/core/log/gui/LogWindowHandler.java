package rtlib.core.log.gui;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import rtlib.core.log.CompactFormatter;

public class LogWindowHandler extends Handler
{
	private LogWindow mWindow = null;

	private static LogWindowHandler mHandler = null;

	/**
	 * private constructor, preventing initialization
	 */
	private LogWindowHandler(String pTitle, int pWidth, int pHeight)
	{
		setLevel(Level.INFO);
		if (mWindow == null)
			mWindow = new LogWindow(pTitle, pWidth, pHeight);
	}

	public static synchronized void setVisible(boolean pVisible)
	{
		if (mHandler != null)
			mHandler.mWindow.setVisible(pVisible);
	}

	public static void dispose()
	{
		if (mHandler != null)
			mHandler.mWindow.dispose();
	}

	public static synchronized LogWindowHandler getInstance(String pTitle)
	{
		return getInstance(pTitle, 768, 320);
	}

	public static synchronized LogWindowHandler getInstance(String pTitle,
																													int pWidth,
																													int pHeight)
	{
		if (mHandler == null)
		{
			mHandler = new LogWindowHandler(pTitle, pWidth, pHeight);
			mHandler.setFormatter(new CompactFormatter());
		}
		return mHandler;
	}


	@Override
	public synchronized void publish(LogRecord record)
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

	@Override
	public void close()
	{
	}

	@Override
	public void flush()
	{
	}

}
