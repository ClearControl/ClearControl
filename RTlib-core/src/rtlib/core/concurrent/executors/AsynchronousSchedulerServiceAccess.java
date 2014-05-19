package rtlib.core.concurrent.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public interface AsynchronousSchedulerServiceAccess
{


	public default ScheduledThreadPoolExecutor initializeScheduledExecutors()
	{
		return RTlibExecutors.getOrCreateScheduledThreadPoolExecutor(	this,
																																	Thread.NORM_PRIORITY,
																																	1,
																																	Integer.MAX_VALUE);

	}


	public default ScheduledFuture<?> schedule(	Runnable pRunnable,
																							long pDelay,
																							TimeUnit pUnit)
	{
		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this.getClass());
		if (lScheduledThreadPoolExecutor == null)
			lScheduledThreadPoolExecutor = initializeScheduledExecutors();

		return lScheduledThreadPoolExecutor.schedule(	pRunnable,
																									pDelay,
																									pUnit);
	}

	public default ScheduledFuture<?> scheduleAtFixedRate(Runnable pRunnable,
																												long pPeriod,
																												TimeUnit pUnit)
	{
		return scheduleAtFixedRate(pRunnable, 0, pPeriod, pUnit);
	}

	public default ScheduledFuture<?> scheduleAtFixedRate(Runnable pRunnable,
																												long pInitialDelay,
																												long pPeriod,
																												TimeUnit pUnit)
	{
		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this.getClass());
		if (lScheduledThreadPoolExecutor == null)
			lScheduledThreadPoolExecutor = initializeScheduledExecutors();

		return lScheduledThreadPoolExecutor.scheduleAtFixedRate(pRunnable,
																														pInitialDelay,
																														pPeriod,
																														pUnit);
	}

	public default boolean stopScheduledThreadPoolAndWaitForCompletion(long pTimeOut,
																																			TimeUnit pTimeUnit) throws InterruptedException
	{
		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this.getClass());

		lScheduledThreadPoolExecutor.shutdownNow();
		RTlibExecutors.resetScheduledThreadPoolExecutor(this.getClass());

		return lScheduledThreadPoolExecutor.awaitTermination(	pTimeOut,
																													pTimeUnit);
	}


	public default boolean waitForScheduleCompletion(	long pTimeOut,
																										TimeUnit pTimeUnit) throws InterruptedException
	{
		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this.getClass());

		if (lScheduledThreadPoolExecutor == null)
			return true;

		BlockingQueue<Runnable> lQueue = lScheduledThreadPoolExecutor.getQueue();

		long lNanoTimeOut = System.nanoTime();
		long lNanoTimeStart = pTimeUnit.toNanos(pTimeOut);
		do
		{
			long lNanoTimeCurrent = System.nanoTime();
			if (lNanoTimeCurrent > lNanoTimeStart + lNanoTimeOut)
				return false;
			Thread.sleep(1);
		}
		while (!lQueue.isEmpty() || lScheduledThreadPoolExecutor.getActiveCount() > 0);

		return true;
	}

	public default boolean waitForScheduleCompletion() throws InterruptedException
	{
		return waitForScheduleCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
	}

}
