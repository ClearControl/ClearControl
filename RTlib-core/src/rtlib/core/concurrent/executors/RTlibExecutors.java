package rtlib.core.concurrent.executors;

import java.lang.ref.SoftReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class RTlibExecutors
{

	private static ConcurrentHashMap<String, SoftReference<CompletingThreadPoolExecutor>> cThreadPoolExecutorMap = new ConcurrentHashMap<>(100);
	private static ConcurrentHashMap<String, SoftReference<ScheduledThreadPoolExecutor>> cScheduledThreadPoolExecutorMap = new ConcurrentHashMap<>(100);

	public static final <T> CompletingThreadPoolExecutor getThreadPoolExecutor(final Class<T> pClass)
	{
		return getThreadPoolExecutor(pClass.getName());
	}

	public static final <T> ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor(final Class<T> pClass)
	{
		return getScheduledThreadPoolExecutor(pClass.getName());
	}

	public static final CompletingThreadPoolExecutor getThreadPoolExecutor(final String pName)
	{
		SoftReference<CompletingThreadPoolExecutor> lSoftReference = cThreadPoolExecutorMap.get(pName);
		if (lSoftReference == null)
			return null;
		return lSoftReference.get();
	}

	public static final ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor(final String pName)
	{
		SoftReference<ScheduledThreadPoolExecutor> lSoftReference = cScheduledThreadPoolExecutorMap.get(pName);
		if (lSoftReference == null)
			return null;
		return lSoftReference.get();
	}

	public static void resetThreadPoolExecutor(Class<? extends AsynchronousExecutorServiceAccess> pClass)
	{
		final String lName = pClass.getName();
		cThreadPoolExecutorMap.remove(lName);
	}

	public static void resetScheduledThreadPoolExecutor(Class<? extends AsynchronousSchedulerServiceAccess> pClass)
	{
		final String lName = pClass.getName();
		cScheduledThreadPoolExecutorMap.remove(lName);
	}

	public static final CompletingThreadPoolExecutor getOrCreateThreadPoolExecutor(	final Object pObject,
																																									final int pPriority,
																																									final int pCorePoolSize,
																																									final int pMaxPoolSize,
																																									final int pMaxQueueLength)
	{
		final String lName = pObject.getClass().getName();
		final String lSimpleName = pObject.getClass().getSimpleName();
		SoftReference<CompletingThreadPoolExecutor> lSoftReferenceOnThreadPoolExecutor = cThreadPoolExecutorMap.get(lName);

		CompletingThreadPoolExecutor lThreadPoolExecutor;

		if (lSoftReferenceOnThreadPoolExecutor == null || lSoftReferenceOnThreadPoolExecutor.get() == null)
		{
			BlockingQueue<Runnable> lNewQueue = new LinkedBlockingQueue<>(pMaxQueueLength);

			lThreadPoolExecutor = new CompletingThreadPoolExecutor(	pCorePoolSize,
																															pMaxPoolSize,
																															1,
																															TimeUnit.MINUTES,
																															lNewQueue,
																															getThreadFactory(	lSimpleName,
																																								pPriority));

			lThreadPoolExecutor.allowCoreThreadTimeOut(false);
			lThreadPoolExecutor.prestartAllCoreThreads();

			lSoftReferenceOnThreadPoolExecutor = new SoftReference<>(lThreadPoolExecutor);
			cThreadPoolExecutorMap.put(	lName,
																	lSoftReferenceOnThreadPoolExecutor);
		}

		lThreadPoolExecutor = lSoftReferenceOnThreadPoolExecutor.get();

		return lThreadPoolExecutor;
	}

	public static final ScheduledThreadPoolExecutor getOrCreateScheduledThreadPoolExecutor(	final Object pObject,
																																																		final int pPriority,
																																																		final int pCorePoolSize,
																																																		final int pMaxQueueLength)
	{
		final String lName = pObject.getClass().getName();
		final String lSimpleName = pObject.getClass().getSimpleName();
		SoftReference<ScheduledThreadPoolExecutor> lSoftReferenceOnThreadPoolExecutor = cScheduledThreadPoolExecutorMap.get(lName);

		ScheduledThreadPoolExecutor lScheduledThreadPoolExecutor;

		if (lSoftReferenceOnThreadPoolExecutor == null || lSoftReferenceOnThreadPoolExecutor.get() == null)
		{
			lScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(	pCorePoolSize,
																																								getThreadFactory(	lSimpleName,
																																																	pPriority));

			lScheduledThreadPoolExecutor.allowCoreThreadTimeOut(false);
			lScheduledThreadPoolExecutor.prestartAllCoreThreads();
			lScheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
			lScheduledThreadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
			lScheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);

			lSoftReferenceOnThreadPoolExecutor = new SoftReference<>(lScheduledThreadPoolExecutor);
			cScheduledThreadPoolExecutorMap.put(lName,
																					lSoftReferenceOnThreadPoolExecutor);
		}

		lScheduledThreadPoolExecutor = lSoftReferenceOnThreadPoolExecutor.get();

		return lScheduledThreadPoolExecutor;
	}

	public static final ThreadFactory getThreadFactory(	final String pName,
																											final int pPriority)
	{
		ThreadFactory lThreadFactory = new ThreadFactory()
		{
			@Override
			public Thread newThread(Runnable pRunnable)
			{
				Thread lThread = new Thread(pRunnable);
				lThread.setName(pName + "-" + pRunnable.hashCode());
				lThread.setPriority(pPriority);
				lThread.setDaemon(true);

				return lThread;
			}
		};
		return lThreadFactory;
	}

}
