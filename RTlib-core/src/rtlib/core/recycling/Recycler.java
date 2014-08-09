package rtlib.core.recycling;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import rtlib.core.log.Loggable;
import rtlib.core.rgc.Freeable;

public class Recycler<R extends RecyclableInterface<R, P>, P extends RecyclerRequest> implements
																																											Freeable,
																																											Loggable
{
	private final Class<R> mRecyclableClass;
	private final ConcurrentLinkedQueue<SoftReference<R>> mAvailableObjectsQueue = new ConcurrentLinkedQueue<SoftReference<R>>();
	private final ConcurrentLinkedQueue<Long> mAvailableMemoryQueue = new ConcurrentLinkedQueue<Long>();

	private volatile AtomicLong mLiveObjectCounter = new AtomicLong(0);
	private volatile AtomicLong mLiveMemoryInBytes = new AtomicLong(0);
	private long mMaximumLiveMemoryInBytes;

	private final AtomicBoolean mIsFreed = new AtomicBoolean(false);

	public Recycler(final Class pRecyclableClass)
	{
		this(pRecyclableClass, Long.MAX_VALUE);
	}

	public Recycler(final Class pRecyclableClass,
									final long pMaximumLiveMemoryInBytes)
	{
		mRecyclableClass = pRecyclableClass;
		if (pMaximumLiveMemoryInBytes < 0)
		{
			final String lErrorString = "Maximum live memory must be strictly positive!";
			error("Recycling", lErrorString);
			throw new IllegalArgumentException(lErrorString);
		}
		mMaximumLiveMemoryInBytes = pMaximumLiveMemoryInBytes;
	}

	public long ensurePreallocated(	final int pNumberofPrealocatedRecyclablesNeeded,
																	@SuppressWarnings("unchecked") final P pRecyclerRequest)
	{
		complainIfFreed();
		final int lNumberOfAvailableObjects = mAvailableObjectsQueue.size();
		final int lNumberOfObjectsToAllocate = Math.max(0,
																										pNumberofPrealocatedRecyclablesNeeded - lNumberOfAvailableObjects);
		long i = 1;
		try
		{
			for (; i <= lNumberOfObjectsToAllocate; i++)
			{

				final R lNewInstance = createNewInstanceFromRequest(pRecyclerRequest);
				if (lNewInstance == null)
					return i - 1;

				lNewInstance.setRecycler(this);
				mAvailableObjectsQueue.add(new SoftReference<R>(lNewInstance));
				mAvailableMemoryQueue.add(lNewInstance.getSizeInBytes());

			}
			return lNumberOfObjectsToAllocate;
		}
		catch (final Throwable e)
		{
			final String lErrorString = "Error while creating new instance!";
			error("Recycling", lErrorString, e);
			return (i - 1);
		}

	}

	private R createNewInstanceFromRequest(final P pRecyclerRequest) throws NoSuchMethodException,
																																	InstantiationException,
																																	IllegalAccessException,
																																	InvocationTargetException
	{
		complainIfFreed();
		final Constructor<R> lDefaultConstructor = mRecyclableClass.getDeclaredConstructor();
		lDefaultConstructor.setAccessible(true);
		final R lNewInstance = lDefaultConstructor.newInstance();
		lDefaultConstructor.setAccessible(false);
		if (pRecyclerRequest != null)
			lNewInstance.initialize(pRecyclerRequest);

		if (mLiveMemoryInBytes.get() + lNewInstance.getSizeInBytes() > mMaximumLiveMemoryInBytes)
		{
			lNewInstance.free();
			return null;
		}

		mLiveObjectCounter.incrementAndGet();
		mLiveMemoryInBytes.addAndGet(lNewInstance.getSizeInBytes());

		return lNewInstance;
	}

	void destroyInstance(	final R lRecyclableObject,
												final boolean pCallFreeMethod)
	{
		final long lSizeInBytes = lRecyclableObject.getSizeInBytes();
		if (pCallFreeMethod)
			lRecyclableObject.free();
		mLiveObjectCounter.decrementAndGet();
		mLiveMemoryInBytes.addAndGet(-lSizeInBytes);
	}

	@SuppressWarnings("unchecked")
	public R waitOrRequestRecyclableObject(	final long pWaitTime,
																					final TimeUnit pTimeUnit,
																					final P pRecyclerRequest)
	{
		return requestRecyclableObject(	true,
																		pWaitTime,
																		pTimeUnit,
																		pRecyclerRequest);
	}

	@SuppressWarnings("unchecked")
	public R failOrRequestRecyclableObject(final P pRecyclerRequest)
	{
		return requestRecyclableObject(false, 0, null, pRecyclerRequest);
	}

	@SuppressWarnings("unchecked")
	public R requestRecyclableObject(	final boolean pWait,
																		final long pWaitTime,
																		final TimeUnit pTimeUnit,
																		final P pRecyclerRequest)
	{
		complainIfFreed();
		final SoftReference<R> lPolledSoftReference = mAvailableObjectsQueue.poll();

		if (lPolledSoftReference != null)
		{
			final Long lObjectsSizeInBytes = mAvailableMemoryQueue.poll();

			final R lObtainedReference = lPolledSoftReference.get();
			lPolledSoftReference.clear();

			if (lObtainedReference == null)
			{
				mLiveObjectCounter.decrementAndGet();
				mLiveMemoryInBytes.addAndGet(-lObjectsSizeInBytes);
				return requestRecyclableObject(	pWait,
																				pWaitTime,
																				pTimeUnit,
																				pRecyclerRequest);
			}
			if (pRecyclerRequest != null)
			{
				lObtainedReference.setReleased(false);

				if (!lObtainedReference.isCompatible(pRecyclerRequest))
				{
					destroyInstance(lObtainedReference, true);
					return requestRecyclableObject(	pWait,
																					pWaitTime,
																					pTimeUnit,
																					pRecyclerRequest);
				}

				mLiveMemoryInBytes.addAndGet(-lObtainedReference.getSizeInBytes());
				lObtainedReference.initialize(pRecyclerRequest);
				mLiveMemoryInBytes.addAndGet(lObtainedReference.getSizeInBytes());
			}
			return lObtainedReference;

		}

		if (!pWait && mLiveMemoryInBytes.get() >= mMaximumLiveMemoryInBytes)
		{
			final String lErrorString = "Recycler reached maximum allocation size!";
			error("Recycling", lErrorString);
			throw new OutOfMemoryError(lErrorString);
		}

		if (pWait && pWaitTime <= 0)
		{
			final String lErrorString = "Recycler reached maximum allocation size! (timeout)";
			error("Recycling", lErrorString);
			throw new OutOfMemoryError("Recycler reached maximum allocation size! (timeout)");
		}

		if (pWait && mLiveMemoryInBytes.get() >= mMaximumLiveMemoryInBytes)
		{
			final long lWaitPeriodInMilliseconds = 1;
			try
			{
				Thread.sleep(lWaitPeriodInMilliseconds);
			}
			catch (final InterruptedException e)
			{
			}
			return requestRecyclableObject(	pWait,
																			pTimeUnit.toMillis(pWaitTime) - lWaitPeriodInMilliseconds,
																			TimeUnit.MILLISECONDS,
																			pRecyclerRequest);
		}

		R lNewInstance;
		try
		{
			lNewInstance = createNewInstanceFromRequest(pRecyclerRequest);
			lNewInstance.setRecycler(this);
			lNewInstance.setReleased(false);

			return lNewInstance;
		}
		catch (final Throwable e)
		{
			final String lErrorString = "Error while creating new instance!";
			error("Recycling", lErrorString, e);
			return null;
		}

	}

	public void release(final R pObject)
	{
		complainIfFreed();
		mAvailableObjectsQueue.add(new SoftReference<R>(pObject));
		mAvailableMemoryQueue.add(pObject.getSizeInBytes());
	}

	public long getLiveObjectCount()
	{
		return mLiveObjectCounter.get();
	}

	public long getLiveMemoryInBytes()
	{
		return mLiveMemoryInBytes.get();
	}

	public void cleanupOnce()
	{
		final SoftReference<R> lPolledSoftReference = mAvailableObjectsQueue.poll();

		if (lPolledSoftReference != null)
		{
			final Long lObjectsSizeInBytes = mAvailableMemoryQueue.poll();

			final R lObtainedReference = lPolledSoftReference.get();
			lPolledSoftReference.clear();

			if (lObtainedReference == null)
			{
				mLiveObjectCounter.decrementAndGet();
				mLiveMemoryInBytes.addAndGet(-lObjectsSizeInBytes);
			}
			else
			{
				release(lObtainedReference);
			}
		}
	}

	public void freeReleasedObjects(final boolean pCallFreeMethod)
	{
		SoftReference<R> lPolledSoftReference;

		while ((lPolledSoftReference = mAvailableObjectsQueue.poll()) != null)
		{
			final Long lObjectsSizeInBytes = mAvailableMemoryQueue.poll();
			final R lRecyclableObject = lPolledSoftReference.get();
			if (lRecyclableObject == null)
			{
				mLiveObjectCounter.decrementAndGet();
				mLiveMemoryInBytes.addAndGet(-lObjectsSizeInBytes);
			}
			else
			{
				destroyInstance(lRecyclableObject, pCallFreeMethod);
			}
		}
		mAvailableMemoryQueue.clear();

	}

	@Override
	public void free()
	{
		mIsFreed.set(true);
		freeReleasedObjects(true);
	}

	@Override
	public boolean isFree()
	{
		return mIsFreed.get();
	}

}
