package file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class FileEventNotifier implements Closeable
{

	private static final long cDefaultMonitoringPeriodInMilliseconds = 100;
	private final File mFileToMonitor;
	private final File mParentFolder;
	private final FileAlterationObserver mFileAlterationObserver;

	private final CopyOnWriteArrayList<FileEventNotifierListener> mListenerList = new CopyOnWriteArrayList<FileEventNotifierListener>();
	private final FileAlterationMonitor mFileAlterationMonitor;

	public enum FileEventKind
	{
		Created, Modified, Deleted
	}

	public FileEventNotifier(final File pFileToMonitor)
	{
		this(pFileToMonitor, cDefaultMonitoringPeriodInMilliseconds);
	}

	public FileEventNotifier(	final File pFileToMonitor,
														final long pPeriodInMilliseconds)
	{
		super();
		mFileToMonitor = pFileToMonitor;
		mParentFolder = mFileToMonitor.getParentFile();

		final FileEventNotifier lThis = this;

		mFileAlterationObserver = new FileAlterationObserver(mParentFolder);
		mFileAlterationObserver.addListener(new FileAlterationListener()
		{

			@Override
			public void onStop(final FileAlterationObserver pObserver)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onStart(final FileAlterationObserver pObserver)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onFileDelete(final File pFile)
			{
				notifyFileEvent(lThis, pFile, FileEventKind.Deleted);
			}

			@Override
			public void onFileCreate(final File pFile)
			{
				notifyFileEvent(lThis, pFile, FileEventKind.Created);
			}

			@Override
			public void onFileChange(final File pFile)
			{
				notifyFileEvent(lThis, pFile, FileEventKind.Modified);
			}

			@Override
			public void onDirectoryDelete(final File pDirectory)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onDirectoryCreate(final File pDirectory)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onDirectoryChange(final File pDirectory)
			{
				// TODO Auto-generated method stub

			}
		});

		mFileAlterationMonitor = new FileAlterationMonitor(pPeriodInMilliseconds);
		mFileAlterationMonitor.addObserver(mFileAlterationObserver);
	}

	public void addFileEventListener(final FileEventNotifierListener pFileChangeNotifierListener)
	{
		mListenerList.add(pFileChangeNotifierListener);
	}

	public void removeFileEventListener(final FileEventNotifierListener pFileChangeNotifierListener)
	{
		mListenerList.remove(pFileChangeNotifierListener);
	}

	public void removeAllFileEventListener(final FileEventNotifierListener pFileChangeNotifierListener)
	{
		mListenerList.clear();
	}

	protected void notifyFileEvent(	final FileEventNotifier pThis,
																	final File pFile,
																	final FileEventKind pEventKind)
	{
		// System.out.format("%s \t\t %s \n", pFile, pEventKind);
		if (pFile.getName().equals(mFileToMonitor.getName()))
		{
			for (final FileEventNotifierListener lFileEventNotifierListener : mListenerList)
			{
				lFileEventNotifierListener.fileEvent(pThis, pFile, pEventKind);
			}
		}
	}

	public void startMonitoring() throws Exception
	{
		mFileAlterationMonitor.start();
	}

	public void stopMonitoring() throws Exception
	{
		mFileAlterationMonitor.stop();
	}

	@Override
	public void close() throws IOException
	{
		try
		{
			mFileAlterationObserver.destroy();
		}
		catch (final Exception e)
		{
			throw new IOException(e);
		}
	}

}
