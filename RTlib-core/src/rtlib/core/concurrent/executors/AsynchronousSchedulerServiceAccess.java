package rtlib.core.concurrent.executors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface AsynchronousSchedulerServiceAccess
{

	public default CompletingScheduledThreadPoolExecutor initializeScheduledExecutors()
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
																												TimeUnit pTimeUnit)
	{
		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this.getClass());
		if (lScheduledThreadPoolExecutor == null)
			lScheduledThreadPoolExecutor = initializeScheduledExecutors();

		return lScheduledThreadPoolExecutor.scheduleAtFixedRate(pRunnable,
																														pInitialDelay,
																														pPeriod,
																														pTimeUnit);
	}

	public default boolean stopScheduledThreadPoolAndWaitForCompletion() throws ExecutionException
	{
		return stopScheduledThreadPoolAndWaitForCompletion(	Long.MAX_VALUE,
																												TimeUnit.DAYS);
	}

	public default boolean stopScheduledThreadPoolAndWaitForCompletion(	long pTimeOut,
																																			TimeUnit pTimeUnit) throws ExecutionException
	{
		CompletingScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this.getClass());

		lScheduledThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		lScheduledThreadPoolExecutor.shutdown();
		try
		{
			lScheduledThreadPoolExecutor.waitForCompletion(	true,
																											pTimeOut,
																											pTimeUnit);
			RTlibExecutors.resetScheduledThreadPoolExecutor(this.getClass());
			return true;
		}
		catch (TimeoutException e)
		{
			RTlibExecutors.resetScheduledThreadPoolExecutor(this.getClass());
			return false;
		}

	}

	public default boolean waitForScheduleCompletion(	long pTimeOut,
																										TimeUnit pTimeUnit) throws ExecutionException
	{
		CompletingScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this.getClass());

		if (lScheduledThreadPoolExecutor == null)
			return true;

		try
		{
			lScheduledThreadPoolExecutor.waitForCompletion(	false,
																											pTimeOut,
																											pTimeUnit);
			return true;
		}
		catch (TimeoutException e)
		{
			return false;
		}

	}

	public default boolean waitForScheduleCompletion() throws ExecutionException
	{
		return waitForScheduleCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
	}

}
