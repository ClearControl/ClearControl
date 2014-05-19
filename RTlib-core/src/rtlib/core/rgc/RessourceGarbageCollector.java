package rtlib.core.rgc;

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;

public class RessourceGarbageCollector implements
																			AsynchronousSchedulerServiceAccess,
																			AsynchronousExecutorServiceAccess
{

	private static RessourceGarbageCollector sRessourceGarbageCollector;

	static
	{
		sRessourceGarbageCollector = new RessourceGarbageCollector();

		sRessourceGarbageCollector.collectAtFixedRate(1, TimeUnit.MINUTES);
	}

	private static ConcurrentLinkedDeque<CleaningPhantomReference> sCleaningPhantomReferenceList = new ConcurrentLinkedDeque<>();

	public static final void register(Cleanable pCleanable)
	{
		CleaningPhantomReference lCleaningPhantomReference = new CleaningPhantomReference(pCleanable,
																																											pCleanable.getCleaner(),
																																											sRessourceGarbageCollector.getReferenceQueue());

		sCleaningPhantomReferenceList.add(lCleaningPhantomReference);
	}

	private final ReferenceQueue<Cleanable> mReferenceQueue = new ReferenceQueue<>();

	private ReferenceQueue<Cleanable> getReferenceQueue()
	{
		return mReferenceQueue;
	}

	private final AtomicBoolean mActive = new AtomicBoolean(true);

	private void collect()
	{
		if (mActive.get())
			do
			{
				CleaningPhantomReference lReference = (CleaningPhantomReference) mReferenceQueue.poll();
				if (lReference == null)
					return;
				executeAsynchronously(lReference.getCleaner());
				sCleaningPhantomReferenceList.remove(lReference);
			}
			while (true);
	}

	public void collectAtFixedRate(long pPeriod, TimeUnit pUnit)
	{
		Runnable lCollector = () -> collect();
		scheduleAtFixedRate(lCollector, pPeriod, pUnit);
	}

	public static void collectNow()
	{
		sRessourceGarbageCollector.collect();
	}

	public static void preventCollection(Runnable pRunnable)
	{
		final boolean lActive = sRessourceGarbageCollector.mActive.get();
		sRessourceGarbageCollector.mActive.set(false);
		pRunnable.run();
		sRessourceGarbageCollector.mActive.compareAndSet(false, lActive);
	}

	public static int getNumberOfRegisteredObjects()
	{
		return sCleaningPhantomReferenceList.size();
	}

}
