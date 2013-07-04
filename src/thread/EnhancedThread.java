package thread;

import java.util.concurrent.locks.LockSupport;

import units.Units;

/**
 */
public abstract class EnhancedThread implements Runnable
{

	private volatile boolean mPauseRequested = false;
	private volatile boolean mStopRequested = false;
	private volatile boolean mStarted = false;
	private volatile boolean mStopped = false;
	private volatile boolean mPaused = false;
	private final Object mLock = new Object();
	private String mThreadName;

	private Thread mThread;
	private boolean mIsDeamon = true;
	private int mPriority = Thread.NORM_PRIORITY;

	private final Object mWaitToFinnishLock = new Object();
	private final Object mWaitForRunningLock = new Object();
	private final Object mWaitForPauseLock = new Object();

	public EnhancedThread()
	{
		super();
		mThreadName = "AnnonymousEnhancedThreadSpawnedBy(" + Thread.currentThread()
																																.getName()
									+ ")";
	}

	public EnhancedThread(final String pThreadName)
	{
		super();
		mThreadName = pThreadName;
	}

	public boolean initiate()
	{
		return true;
	};

	public boolean loop()
	{
		return false;
	};

	public boolean terminate()
	{
		return true;
	};

	public final void setPauseRequest(final boolean pSuspendRequested)
	{
		synchronized (mLock)
		{
			mPauseRequested = pSuspendRequested;
			if (!mPauseRequested)
			{
				mLock.notifyAll();
			}
		}
	}

	public final void setStopRequest(final boolean pStopRequested)
	{
		synchronized (mLock)
		{
			mStopRequested = pStopRequested;
			if (pStopRequested)
			{
				mLock.notifyAll();
			}
		}
	}

	@Override
	public final void run()
	{
		try
		{
			mStopped = false;

			mStarted = true;
			if (initiate())
			{

				synchronized (mWaitForRunningLock)
				{
					mWaitForRunningLock.notifyAll();
				}

				while (loop() && !mStopRequested)
				{

					// if suspended, then wait:
					while (mPauseRequested && !mStopRequested)
					{
						synchronized (mLock)
						{
							mPaused = true;
							synchronized (mWaitForPauseLock)
							{
								mWaitForPauseLock.notifyAll();
							}
							mLock.wait();
							mPaused = false;
						}
					}

				}
				terminate();
				synchronized (mWaitToFinnishLock)
				{
					mWaitToFinnishLock.notifyAll();
				}
				mThread = null;
			}
		}
		catch (final InterruptedException e)
		{

			mStopped = true;
		}
		mStopped = true;
	}

	public boolean start()
	{
		if (!mStarted || mStopped)
		{
			setPauseRequest(false);
			setStopRequest(false);
			mThread = new Thread(this, mThreadName);
			mThread.setPriority(mPriority);
			mThread.setDaemon(mIsDeamon);
			mThread.start();
			return true;
		}
		else
		{
			return false;
		}
	}

	public final void setName(final String pThreadName)
	{
		mThreadName = pThreadName;
	}

	public final void setDaemon(final boolean pIsDeamon)
	{
		mIsDeamon = pIsDeamon;
	}

	public final void setPriority(final int pPriority)
	{
		mPriority = pPriority;
	}

	public final void pause()
	{
		setPauseRequest(true);
	}

	public final void resume()
	{
		setPauseRequest(false);
	}

	public void stop()
	{
		setStopRequest(true);
	}

	public final void joinWhenStarted(final int pWaitTime)
	{
		while (isStarted() == false)
		{
			try
			{
				Thread.sleep(pWaitTime);
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace(System.out);
			}
		}
	}

	public final boolean isStarted()
	{
		return mStarted;
	}

	public final boolean isStopRequested()
	{
		return mStopRequested;
	}

	public final boolean isStopped()
	{
		return mStopped;
	}

	public final boolean isPauseRequested()
	{
		return mPauseRequested;
	}

	public final boolean isPaused()
	{
		return mPaused;
	}

	public boolean isRunning()
	{
		return mStarted && !mStopped && !mPaused;
	}

	public final void waitToFinish()
	{
		synchronized (mWaitToFinnishLock)
		{
			while (!mStarted || isRunning())
			{
				try
				{
					mWaitToFinnishLock.wait();
				}
				catch (final InterruptedException e)
				{
				}
			}

		}
	}

	public void waitForRunning()
	{
		if(!isRunning())
		synchronized (mWaitForRunningLock)
		{
			while (!mStarted || !isRunning())
			{
				try
				{
					mWaitForRunningLock.wait();
				}
				catch (final InterruptedException e)
				{
				}
			}

		}
	}

	public void waitForPause()
	{
		if(!isPaused())
		synchronized (mWaitForPauseLock)
		{
			while (!isPaused() && isRunning())
			{
				try
				{
					mWaitForPauseLock.wait();
				}
				catch (final InterruptedException e)
				{
				}
			}
		}
	}

	public void forceStop()
	{
		mThread.stop();
	}

	public static final long getTimeInNanoseconds()
	{
		return System.nanoTime();
	}

	public static double getTimeInMicroseconds()
	{
		return Units.nano2micro(System.nanoTime());
	}

	public static double getTimeInMilliseconds()
	{
		return Units.nano2milli(System.nanoTime());
	}

	public static double getTimeInSeconds()
	{
		return Units.nano2unit(System.nanoTime());
	}

	public static final void sleep(final double pMilliseconds)
	{
		try
		{
			Thread.sleep((long) pMilliseconds);
		}
		catch (final InterruptedException e)
		{
		}
		// sleepNanos((long) Units.milli2nano(pMilliseconds));
	}

	public static void sleepNanos(final long pNanosecondsToWait)
	{
		// default: 100 millisecond precision
		sleepNanos(	pNanosecondsToWait,
								(long) Units.micro2nano(500),
								(long) Units.micro2nano(100));
	}

	public static long sleepNanos(final long pNanosecondsToSleep,
																long pSleepGranularity,
																final long pSleepPrecision)
	{
		pSleepGranularity = Math.min(	pSleepGranularity,
																	pNanosecondsToSleep / 2);
		final long lEndTimeNanoseconds = System.nanoTime() + pNanosecondsToSleep;
		long lTimeLeftNanoseconds = pNanosecondsToSleep;
		do
		{
			if (lTimeLeftNanoseconds > 2 * pSleepGranularity)
				LockSupport.parkNanos(pSleepGranularity);
			else if (lTimeLeftNanoseconds >= 2)
				LockSupport.parkNanos(lTimeLeftNanoseconds / 2);
			final long lCurrentTimeNanoseconds = System.nanoTime();
			lTimeLeftNanoseconds = lEndTimeNanoseconds - lCurrentTimeNanoseconds;

		}
		while (lTimeLeftNanoseconds > pSleepPrecision);

		return lTimeLeftNanoseconds;
	}

	public static void sleepForEver()
	{
		while (true)
		{
			sleep(Long.MAX_VALUE);
		}
	}

}