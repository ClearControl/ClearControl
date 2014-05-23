package rtlib.core.concurrent.executors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletingScheduledThreadPoolExecutor extends
																									ScheduledThreadPoolExecutor
{
	private final BlockingQueue<Future<?>> mScheduledFutureQueue = new LinkedBlockingQueue<Future<?>>(Integer.MAX_VALUE);

	public CompletingScheduledThreadPoolExecutor(	int pCorePoolSize,
																								RejectedExecutionHandler pHandler)
	{
		super(pCorePoolSize, pHandler);
	}

	public CompletingScheduledThreadPoolExecutor(	int pCorePoolSize,
																								ThreadFactory pThreadFactory,
																								RejectedExecutionHandler pHandler)
	{
		super(pCorePoolSize, pThreadFactory, pHandler);
	}

	public CompletingScheduledThreadPoolExecutor(	int pCorePoolSize,
																								ThreadFactory pThreadFactory)
	{
		super(pCorePoolSize, pThreadFactory);
	}

	public CompletingScheduledThreadPoolExecutor(int pCorePoolSize)
	{
		super(pCorePoolSize);
	}

	@Override
	public ScheduledFuture<?> schedule(	Runnable pCommand,
																			long pDelay,
																			TimeUnit pUnit)
	{
		ScheduledFuture<?> lScheduledFuture = super.schedule(	pCommand,
																													pDelay,
																													pUnit);
		addFuture(lScheduledFuture);
		return lScheduledFuture;
	}

	@Override
	public <V> ScheduledFuture<V> schedule(	Callable<V> pCallable,
																					long pDelay,
																					TimeUnit pUnit)
	{
		ScheduledFuture<V> lScheduledFuture = super.schedule(	pCallable,
																													pDelay,
																													pUnit);
		addFuture(lScheduledFuture);
		return lScheduledFuture;
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable pCommand,
																								long pInitialDelay,
																								long pPeriod,
																								TimeUnit pUnit)
	{
		ScheduledFuture<?> lScheduleAtFixedRate = super.scheduleAtFixedRate(pCommand,
																																				pInitialDelay,
																																				pPeriod,
																																				pUnit);
		addFuture(lScheduleAtFixedRate);
		return lScheduleAtFixedRate;
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(	Runnable pCommand,
																										long pInitialDelay,
																										long pDelay,
																										TimeUnit pUnit)
	{
		ScheduledFuture<?> lScheduleWithFixedDelay = super.scheduleWithFixedDelay(pCommand,
																																							pInitialDelay,
																																							pDelay,
																																							pUnit);
		addFuture(lScheduleWithFixedDelay);
		return lScheduleWithFixedDelay;
	}

	@Override
	public Future<?> submit(Runnable pTask)
	{
		Future<?> lFuture = super.submit(pTask);
		addFuture(lFuture);
		return lFuture;
	}

	@Override
	public <T> Future<T> submit(Runnable pTask, T pResult)
	{
		Future<T> lFuture = super.submit(pTask, pResult);
		addFuture(lFuture);
		return lFuture;
	}

	@Override
	public <T> Future<T> submit(Callable<T> pTask)
	{
		Future<T> lFuture = super.submit(pTask);
		addFuture(lFuture);
		return lFuture;
	}

	private void addFuture(Future<?> pFutur)
	{
		mScheduledFutureQueue.add(pFutur);
	}

	public Future<?> getFutur(long pTimeOut, TimeUnit pTimeUnit) throws InterruptedException
	{
		return mScheduledFutureQueue.poll(pTimeOut, pTimeUnit);
	}

	public void waitForCompletion(long pTimeOut, TimeUnit pTimeUnit) throws ExecutionException,
																																	TimeoutException
	{
		while (mScheduledFutureQueue.peek() != null)
		{
			try
			{
				Future<?> lScheduledFuture = mScheduledFutureQueue.poll();
				lScheduledFuture.cancel(false);
				if (lScheduledFuture != null)
				{
					Object lObject = lScheduledFuture.get(pTimeOut, pTimeUnit);
					System.out.println(lObject);
				}
			}
			catch (CancellationException ce)
			{
			}
			catch (InterruptedException e)
			{
			}
		}
	}

}
