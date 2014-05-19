package rtlib.core.recycling;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import rtlib.core.rgc.Freeable;

public class Recycler<R extends RecyclableInterface<R, P>, P> implements
																															Freeable
{
	private final Class<R> mRecyclableClass;
	private final ConcurrentLinkedQueue<SoftReference<R>> mAvailableObjectsQueue = new ConcurrentLinkedQueue<SoftReference<R>>();
	private final ConcurrentLinkedQueue<Long> mAllocatedMemoryQueue = new ConcurrentLinkedQueue<Long>();

	private volatile AtomicLong mLiveObjectCounter = new AtomicLong(0);
	private volatile AtomicLong mLiveMemoryInBytes = new AtomicLong(0);
	private long mMaximumLiveMemoryInBytes;

	private AtomicBoolean mIsFreed = new AtomicBoolean(false);

	public Recycler(final Class<R> pRecyclableClass)
	{
		this(pRecyclableClass, Long.MAX_VALUE);
	}

	public Recycler(final Class<R> pRecyclableClass,
									final long pMaximumLiveMemoryInBytes)
	{
		mRecyclableClass = pRecyclableClass;
		if (pMaximumLiveMemoryInBytes < 0)
			throw new IllegalArgumentException("Maximum live memory must be strictly positive!");
		mMaximumLiveMemoryInBytes = pMaximumLiveMemoryInBytes;
	}

	public boolean ensurePreallocated(final int pNumberofPrealocatedRecyclablesNeeded,
																		@SuppressWarnings("unchecked") final P... pParameters)
	{
		complainIfFreed();
		final int lNumberOfAvailableObjects = mAvailableObjectsQueue.size();
		final int lNumberOfObjectsToAllocate = Math.max(0,
																										pNumberofPrealocatedRecyclablesNeeded - lNumberOfAvailableObjects);

		try
		{
			for (int i = 0; i < lNumberOfObjectsToAllocate; i++)
			{

				final R lNewInstance = createNewInstanceWithParameters(pParameters);
				// lNewInstance.initialize(pParameters);
				lNewInstance.setRecycler(this);
				mAvailableObjectsQueue.add(new SoftReference<R>(lNewInstance));
				mAllocatedMemoryQueue.add(lNewInstance.getSizeInBytes());
			}
			return true;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}

	}

	private R createNewInstanceWithParameters(final P[] pParameters) throws NoSuchMethodException,
																																	InstantiationException,
																																	IllegalAccessException,
																																	InvocationTargetException
	{
		complainIfFreed();
		Constructor<R> lDefaultConstructor = mRecyclableClass.getDeclaredConstructor();
		lDefaultConstructor.setAccessible(true);
		final R lNewInstance = lDefaultConstructor.newInstance();
		lDefaultConstructor.setAccessible(false);
		if (pParameters != null)
			lNewInstance.initialize(pParameters);
		return lNewInstance;
	}

	@SuppressWarnings("unchecked")
	public R requestOrWaitRecyclableObject(	final long pWaitTime,
																					final TimeUnit pTimeUnit,
																					final P... pRequestParameters)
	{
		return requestRecyclableObject(	true,
																		pWaitTime,
																		pTimeUnit,
																		pRequestParameters);
	}

	@SuppressWarnings("unchecked")
	public R requestOrFailRecyclableObject(final P... pRequestParameters)
	{
		return requestRecyclableObject(false, 0, null, pRequestParameters);
	}

	@SuppressWarnings("unchecked")
	public R requestRecyclableObject(	final boolean pWait,
																		final long pWaitTime,
																		final TimeUnit pTimeUnit,
																		final P... pRequestParameters)
	{
		complainIfFreed();
		final SoftReference<R> lPolledSoftReference = mAvailableObjectsQueue.poll();

		if (lPolledSoftReference != null)
		{
			Long lObjectsSizeInBytes = mAllocatedMemoryQueue.poll();

			final R lObtainedReference = lPolledSoftReference.get();
			lPolledSoftReference.clear();

			if (lObtainedReference == null)
			{
				mLiveObjectCounter.decrementAndGet();
				mLiveMemoryInBytes.addAndGet(-lObjectsSizeInBytes);
				return requestRecyclableObject(	pWait,
																				pWaitTime,
																				pTimeUnit,
																				pRequestParameters);
			}
			if (pRequestParameters != null)
			{
				lObtainedReference.setReleased(false);
				
				if (!lObtainedReference.isCompatible(pRequestParameters))
				{
					mLiveObjectCounter.decrementAndGet();
					mLiveMemoryInBytes.addAndGet(-lObtainedReference.getSizeInBytes());
					freeFreeableObject(lObtainedReference);
					return requestRecyclableObject(	pWait,
																					pWaitTime,
																					pTimeUnit,
																					pRequestParameters);
				}

				mLiveMemoryInBytes.addAndGet(-lObtainedReference.getSizeInBytes());
				lObtainedReference.initialize(pRequestParameters);
				mLiveMemoryInBytes.addAndGet(lObtainedReference.getSizeInBytes());
			}
			return lObtainedReference;

		}

		if (!pWait && mLiveMemoryInBytes.get() > mMaximumLiveMemoryInBytes)
			throw new OutOfMemoryError("Recycler reached maximum allocation size!");

		if (pWait && pWaitTime <= 0)
			throw new OutOfMemoryError("Recycler reached maximum allocation size! (timeout)");

		if (pWait && mLiveMemoryInBytes.get() > mMaximumLiveMemoryInBytes)
		{
			final long lWaitPeriodInMilliseconds = 1;
			try
			{
				Thread.sleep(lWaitPeriodInMilliseconds);
			}
			catch (InterruptedException e)
			{
			}
			return requestRecyclableObject(	pWait,
																			pTimeUnit.toMillis(pWaitTime) - lWaitPeriodInMilliseconds,
																			TimeUnit.MILLISECONDS,
																			pRequestParameters);
		}

		R lNewInstance;
		try
		{
			lNewInstance = createNewInstanceWithParameters(pRequestParameters);
			lNewInstance.setRecycler(this);
			lNewInstance.setReleased(false);

			mLiveObjectCounter.incrementAndGet();
			mLiveMemoryInBytes.addAndGet(lNewInstance.getSizeInBytes());
			return lNewInstance;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return null;
		}

	}

	public void release(final R pObject)
	{
		complainIfFreed();
		mAvailableObjectsQueue.add(new SoftReference<R>(pObject));
		mAllocatedMemoryQueue.add(pObject.getSizeInBytes());
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
			Long lObjectsSizeInBytes = mAllocatedMemoryQueue.poll();

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

	public void purge(final boolean pCallFreeMethod)
	{
		SoftReference<R> lPolledSoftReference;

		while ((lPolledSoftReference = mAvailableObjectsQueue.poll()) != null)
		{
			Long lSizeInBytes = mAllocatedMemoryQueue.poll();
			mLiveMemoryInBytes.addAndGet(-lSizeInBytes);

			R lRecyclableObject = lPolledSoftReference.get();
			if (lRecyclableObject != null && pCallFreeMethod)
			{
				mLiveObjectCounter.decrementAndGet();
				freeFreeableObject(lRecyclableObject);
			}
		}
	}

	void freeFreeableObject(R lRecyclableObject)
	{
		lRecyclableObject.free();
	}

	@Override
	public void free()
	{
		mIsFreed.set(true);
		purge(true);
		assert (mLiveMemoryInBytes.get() == 0);
	}

	@Override
	public boolean isFree()
	{
		return mIsFreed.get();
	}

}
