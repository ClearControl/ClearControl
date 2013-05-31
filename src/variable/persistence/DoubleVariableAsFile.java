package variable.persistence;

import java.io.File;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
					final Formatter lFormatter = new Formatter(mFile);
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
			catch (final Throwable e)
			{
				e.printStackTrace();
			}

		}

	};

	public DoubleVariableAsFile(final File pFile,
															final double pDoubleValue)
	{
		super(pDoubleValue);
		mFile = pFile;

	}

	@Override
	public double getValue()
	{
		if (mCachedValue != null)
			return mCachedValue;

		try
		{
			synchronized (mLock)
			{
				if (!mFile.exists())
				{
					mCachedValue = super.getValue();
					return mCachedValue;
				}
				final Scanner lScanner = new Scanner(mFile);
				final String lLine = lScanner.nextLine().trim();
				mCachedValue = Double.parseDouble(lLine);
				lScanner.close();
			}
			return mCachedValue;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return super.getValue();
		}
	}

	@Override
	public void setValue(final double pNewValue)
	{
		super.setValue(pNewValue);
		mCachedValue = pNewValue;
		mSingleThreadExecutor.execute(mFileSaverRunnable);
	}

}
