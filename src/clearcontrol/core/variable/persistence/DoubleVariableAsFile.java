package clearcontrol.core.variable.persistence;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import clearcontrol.core.variable.Variable;

/**
 * Double variable as file
 *
 * @author royer
 */
public class DoubleVariableAsFile extends Variable<Double>
                                  implements Closeable

{
  private final ExecutorService mSingleThreadExecutor =
                                                      Executors.newSingleThreadExecutor();

  private Double mCachedValue;

  private final File mFile;
  // private FileEventNotifier mFileEventNotifier;

  private final Object mLock = new Object();

  /**
   * Double variable as file
   * 
   * @param pFile
   *          file
   * @param pVariableName
   *          variable name
   * @param pDoubleValue
   *          double value
   */
  public DoubleVariableAsFile(final File pFile,
                              final String pVariableName,
                              final double pDoubleValue)
  {
    super(pVariableName, pDoubleValue);
    mFile = pFile;

  }

  @Override
  public Double get()
  {
    if (mCachedValue != null)
    {
      return mCachedValue;
    }

    try
    {
      synchronized (mLock)
      {
        if (!mFile.exists())
        {
          mCachedValue = super.get();
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
      return super.get();
    }
  }

  @Override
  public void set(final Double pNewValue)
  {
    super.set(pNewValue);
    mCachedValue = pNewValue;
    mSingleThreadExecutor.execute(mFileSaverRunnable);
  }

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

          /*if (mFileEventNotifier != null)
          	mFileEventNotifier.stopMonitoring();/**/
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
          /*if (mFileEventNotifier != null)
          	mFileEventNotifier.startMonitoring();/**/
        }
        // ensureFileEventNotifierAllocated();
      }
      catch (final Throwable e)
      {
        e.printStackTrace();
      }

    }

  };

  /*
  private void ensureFileEventNotifierAllocated() throws Exception
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
  				getValue();
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
    	if (mFileEventNotifier != null)
    		mFileEventNotifier.stopMonitoring();
    }
    catch (final Exception e)
    {
    	throw new IOException(e);
    }
    /**/
  }

}
