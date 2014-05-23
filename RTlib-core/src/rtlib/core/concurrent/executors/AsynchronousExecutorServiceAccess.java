package rtlib.core.concurrent.executors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface AsynchronousExecutorServiceAccess
{

	public default ThreadPoolExecutor initializeExecutors()
	{
		return RTlibExecutors.getOrCreateThreadPoolExecutor(this,
																												Thread.NORM_PRIORITY,
																												1,
																												1,
																												Integer.MAX_VALUE);
	}

	public default Future<?> executeAsynchronously(final Runnable pRunnable)
	{
		ThreadPoolExecutor lThreadPoolExecutor = RTlibExecutors.getThreadPoolExecutor(this.getClass());
		if (lThreadPoolExecutor == null)
			lThreadPoolExecutor = initializeExecutors();

		return lThreadPoolExecutor.submit(pRunnable);
	}

	public default boolean resetThreadPoolAndWaitForCompletion(	long pTimeOut,
																															TimeUnit pTimeUnit) throws InterruptedException
	{
		ThreadPoolExecutor lThreadPoolExecutor = RTlibExecutors.getThreadPoolExecutor(this.getClass());

		lThreadPoolExecutor.shutdown();
		RTlibExecutors.resetThreadPoolExecutor(this.getClass());

		return lThreadPoolExecutor.awaitTermination(pTimeOut, pTimeUnit);
	}

	public default boolean waitForCompletion(	long pTimeOut,
																						TimeUnit pTimeUnit) throws ExecutionException
	{
		CompletingThreadPoolExecutor lThreadPoolExecutor = RTlibExecutors.getThreadPoolExecutor(this.getClass());

		if (lThreadPoolExecutor == null)
			return true;

		try
		{
			lThreadPoolExecutor.waitForCompletion(pTimeOut, pTimeUnit);
			return true;
		}
		catch (TimeoutException e)
		{
			return false;
		}

	}

	public default boolean waitForCompletion() throws InterruptedException,
																						ExecutionException,
																						TimeoutException
	{
		return waitForCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
	}

}
