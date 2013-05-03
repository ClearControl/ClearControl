package variable.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import variable.doublev.DoubleInputOutputVariableInterface;
import variable.doublev.DoubleVariable;
import variable.objectv.ObjectInputOutputVariableInterface;
import variable.objectv.ObjectVariable;

public class ObjectVariableAsFile<O> extends ObjectVariable<O>

{
	private final ExecutorService cSingleThreadExecutor = Executors.newSingleThreadExecutor();

	private O mCachedReference;

	private final File mFile;

	private final Object mLock = new Object();

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
					FileOutputStream lFileOutputStream = new FileOutputStream(mFile);
					BufferedOutputStream lBufferedOutputStream = new BufferedOutputStream(lFileOutputStream);
					ObjectOutputStream lObjectOutputStream = new ObjectOutputStream(lBufferedOutputStream);
					try
					{
						lObjectOutputStream.writeObject(lReference);
					}
					finally
					{
						lObjectOutputStream.close();
					}
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}

		}

	};

	public ObjectVariableAsFile(final File pFile)
	{
		this(pFile, null);
	}

	public ObjectVariableAsFile(final File pFile, O pReference)
	{
		super(pReference);
		mFile = pFile;

		syncWith(new ObjectInputOutputVariableInterface<O>()
		{

			@Override
			public O getReference()
			{
				if (mCachedReference != null)
					return mCachedReference;

				try
				{
					synchronized (mLock)
					{
						if (!pFile.exists())
						{
							mCachedReference = mReference;
							return mCachedReference;
						}

						FileInputStream lFileInputStream = new FileInputStream(mFile);
						BufferedInputStream lBufferedInputStream = new BufferedInputStream(lFileInputStream);
						ObjectInputStream lObjectInputStream = new ObjectInputStream(lBufferedInputStream);
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
				catch (Throwable e)
				{
					e.printStackTrace();
					return mReference;
				}
			}

			@Override
			public void setReference(	Object pDoubleEventSource,
																O pNewReference)
			{
				mCachedReference = pNewReference;
				cSingleThreadExecutor.execute(mFileSaverRunnable);
			}

		});

	}

}
