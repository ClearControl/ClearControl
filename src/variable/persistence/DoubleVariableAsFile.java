package variable.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import variable.doublev.DoubleInputOutputVariableInterface;
import variable.doublev.DoubleVariable;

public class DoubleVariableAsFile extends DoubleVariable

{
	private final ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();

	private Double mCachedValue;

	private final File mFile;

	private final Object mLock = new Object();

	private final Runnable mFileSaverRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			final double lValue = mCachedValue;

			try
			{
				synchronized (mLock)
				{
					Formatter lFormatter = new Formatter(mFile);
					try
					{
						lFormatter.format("%g\n", lValue);
						lFormatter.flush();
					}
					finally
					{
						lFormatter.close();
					}
				}

			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}

		}

	};

	public DoubleVariableAsFile(final File pFile, double pDoubleValue)
	{
		super(pDoubleValue);
		mFile = pFile;

		syncWith(new DoubleInputOutputVariableInterface()
		{

			@Override
			public double getValue()
			{
				if (mCachedValue != null)
					return mCachedValue;

				try
				{
					synchronized (mLock)
					{
						if (!pFile.exists())
						{
							mCachedValue = mValue;
							return mCachedValue;
						}
						final Scanner lScanner = new Scanner(pFile);
						final String lLine = lScanner.nextLine().trim();
						mCachedValue = Double.parseDouble(lLine);
						lScanner.close();
					}
					return mCachedValue;
				}
				catch (Throwable e)
				{
					e.printStackTrace();
					return mValue;
				}
			}

			@Override
			public void setValue(Object pDoubleEventSource, double pNewValue)
			{
				mCachedValue = pNewValue;
				mSingleThreadExecutor.execute(mFileSaverRunnable);
			}
		});

	}

}
