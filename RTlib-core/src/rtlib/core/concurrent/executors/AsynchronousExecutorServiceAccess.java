package rtlib.core.concurrent.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
																						TimeUnit pTimeUnit) throws InterruptedException
	{
		ThreadPoolExecutor lThreadPoolExecutor = RTlibExecutors.getThreadPoolExecutor(this.getClass());

		if (lThreadPoolExecutor == null)
			return true;

		BlockingQueue<Runnable> lQueue = lThreadPoolExecutor.getQueue();

		long lNanoTimeOut = System.nanoTime();
		long lNanoTimeStart = pTimeUnit.toNanos(pTimeOut);
		do
		{
			long lNanoTimeCurrent = System.nanoTime();
			if (lNanoTimeCurrent > lNanoTimeStart + lNanoTimeOut)
				return false;
			Thread.sleep(1);
		}
		while (!lQueue.isEmpty() || lThreadPoolExecutor.getActiveCount() > 0);

		return true;
	}

	public default boolean waitForCompletion() throws InterruptedException
	{
		return waitForCompletion(Long.MAX_VALUE, TimeUnit.DAYS);
	}


}
