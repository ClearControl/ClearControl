package rtlib.core.concurrent.executors;

import java.util.concurrent.ExecutionException;
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

	public default boolean stopScheduledThreadPoolAndWaitForCompletion() throws ExecutionException,
																																			InterruptedException
	{
		// TODO this does not seem to work!!!
		return stopScheduledThreadPoolAndWaitForCompletion(	Long.MAX_VALUE,
																												TimeUnit.DAYS);
	}

	public default boolean stopScheduledThreadPoolAndWaitForCompletion(	long pTimeOut,
																																			TimeUnit pTimeUnit) throws ExecutionException

	{
		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this.getClass());

		if (lScheduledThreadPoolExecutor == null)
			return false;

		lScheduledThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		lScheduledThreadPoolExecutor.shutdown();
		try
		{
			boolean lTerminatedBeforeTimeout = lScheduledThreadPoolExecutor.awaitTermination(	pTimeOut,
																											pTimeUnit);
			RTlibExecutors.resetScheduledThreadPoolExecutor(this.getClass());
			return lTerminatedBeforeTimeout;
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return false;
		}

	}

	public default boolean waitForScheduleCompletion(	long pTimeOut,
																										TimeUnit pTimeUnit) throws ExecutionException
	{
		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor = RTlibExecutors.getScheduledThreadPoolExecutor(this.getClass());

		if (lScheduledThreadPoolExecutor == null)
			return true;

		try
		{
			return lScheduledThreadPoolExecutor.awaitTermination(	pTimeOut,
																											pTimeUnit);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			return false;
		}

	}

	public default boolean waitForScheduleCompletion() throws ExecutionException,
																										InterruptedException
	{
		return waitForScheduleCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
	}

}
