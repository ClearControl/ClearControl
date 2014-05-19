package rtlib.core.concurrent.executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LimitedExecutionsRunnable implements Runnable
{
	private final AtomicInteger mExecutionCounter = new AtomicInteger();
	private final Runnable mDelegatedRunnable;
	private volatile ScheduledFuture<?> mScheduledFuture;
	private final int mMaximumNumberOfExecutions;

	public LimitedExecutionsRunnable(	Runnable pDelegateRunnable,
																		int pMaximumNumberOfExecutions)
	{
		this.mDelegatedRunnable = pDelegateRunnable;
		this.mMaximumNumberOfExecutions = pMaximumNumberOfExecutions;
	}

	@Override
	public void run()
	{
		mDelegatedRunnable.run();
		if (mExecutionCounter.incrementAndGet() == mMaximumNumberOfExecutions)
		{
			mScheduledFuture.cancel(false);
		}
	}

	public void runNTimes(ScheduledExecutorService pScheduledExecutorService,
												long pPeriod,
												TimeUnit pTimeUnit)
	{
		mScheduledFuture = pScheduledExecutorService.scheduleAtFixedRate(	this,
																																			0,
																																			pPeriod,
																																			pTimeUnit);
	}

	public void runNTimes(AsynchronousSchedulerServiceAccess pThis,
												long pPeriod,
												TimeUnit pUnit)
	{
		mScheduledFuture = pThis.scheduleAtFixedRate(this, pPeriod, pUnit);
	}

	public static Runnable wrap(Runnable pDelegateRunnable,
															int pMaximumNumberOfExecutions)
	{
		return new LimitedExecutionsRunnable(	pDelegateRunnable,
																					pMaximumNumberOfExecutions);
	}
}
