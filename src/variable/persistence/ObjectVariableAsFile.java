package variable.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import variable.objectv.ObjectVariable;

public class ObjectVariableAsFile<O> extends ObjectVariable<O> implements
																															Closeable

{
	private final ExecutorService cSingleThreadExecutor = Executors.newSingleThreadExecutor();

	private O mCachedReference;

	private final File mFile;
	// private FileEventNotifier mFileEventNotifier;

	private final Object mLock = new Object();

	public ObjectVariableAsFile(final String pVariableName,
															final File pFile)
	{
		this(pVariableName, pFile, null);
	}

	public ObjectVariableAsFile(final String pVariableName,
															final File pFile,
															final O pReference)
	{
		super(pVariableName, pReference);
		mFile = pFile;
		mFile.getParentFile().mkdirs();
	}

	@Override
	public O getReference()
	{
		if (mCachedReference != null)
			return mCachedReference;

		try
		{
			synchronized (mLock)
			{
				if (!(mFile.exists() && mFile.isFile()))
				{
					mCachedReference = mReference;
					return mCachedReference;
				}

				final FileInputStream lFileInputStream = new FileInputStream(mFile);
				final BufferedInputStream lBufferedInputStream = new BufferedInputStream(lFileInputStream);
				final ObjectInputStream lObjectInputStream = new ObjectInputStream(lBufferedInputStream);
				try
				{
					mCachedReference = (O) lObjectInputStream.readObject();
				}
				finally
				{
					lObjectInputStream.close();
				}
			}
			return mCachedReference;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return mReference;
		}
	}

	@Override
	public void setReference(final O pNewReference)
	{
		mCachedReference = pNewReference;
		cSingleThreadExecutor.execute(mFileSaverRunnable);
	}

	private final Runnable mFileSaverRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			final O lReference = mCachedReference;
			try
			{
				synchronized (mLock)
				{

					/*if (mFileEventNotifier != null)
						mFileEventNotifier.stopMonitoring();/**/

					final FileOutputStream lFileOutputStream = new FileOutputStream(mFile);
					final BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
					final ObjectOutputStream lObjectOutputStream = new ObjectOutputStream(lBufferedOutputStream);
					try
					{
						lObjectOutputStream.writeObject(lReference);
					}
					finally
					{
						lObjectOutputStream.close();
					}
					/*if (mFileEventNotifier != null)
						mFileEventNotifier.startMonitoring();/**/
				}

				// ensureFileEventNotifierActive();
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}

		}

	};

	/*
	private void ensureFileEventNotifierActive() throws Exception
	{
		if (mFileEventNotifier == null)
		{
			mFileEventNotifier = new FileEventNotifier(mFile);
			mFileEventNotifier.startMonitoring();
			mFileEventNotifier.addFileEventListener(new FileEventNotifierListener()
			{

				@Override
				public void fileEvent(final FileEventNotifier pThis,
															final File pFile,
															final FileEventKind pEventKind)
				{
					getReference();
				}
			});

		}
	}/**/

	@Override
	public void close() throws IOException
	{
		/*
		try
		{
			mFileEventNotifier.stopMonitoring();
		}
		catch (final Exception e)
		{
			throw new IOException(e);
		}/**/
	}

}
