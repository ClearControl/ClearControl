package recycling;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Recycler<R extends RecyclableInterface<R>>
{
	private final Class<R> mRecyclableClass;
	private final ConcurrentLinkedQueue<SoftReference<R>> mAvailableObjectsQueue = new ConcurrentLinkedQueue<SoftReference<R>>();
	private volatile int mCounter = 0;

	public Recycler(final Class<R> pRecyclableClass)
	{
		mRecyclableClass = pRecyclableClass;
	}

	public boolean ensurePreallocated(final int pNumberofPrealocatedRecyclablesNeeded,
																		final int... pParameters)
	{
		final int lNumberOfAvailableObjects = mAvailableObjectsQueue.size();
		final int lNumberOfObjectsToAllocate = Math.max(0,
																										pNumberofPrealocatedRecyclablesNeeded - lNumberOfAvailableObjects);

		try
		{
			for (int i = 0; i < lNumberOfObjectsToAllocate; i++)
			{
				final R lNewInstance = mRecyclableClass.newInstance();
				lNewInstance.initialize(pParameters);
				lNewInstance.setRecycler(this);
				mAvailableObjectsQueue.add(new SoftReference<R>(lNewInstance));
			}
			return true;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	public R requestRecyclableObject(final int... pRequestParameters)
	{

		final SoftReference<R> lPolledSoftReference = mAvailableObjectsQueue.poll();

		if (lPolledSoftReference != null)
		{

			final R lObtainedReference = lPolledSoftReference.get();
			lPolledSoftReference.clear();

			if (lObtainedReference != null)
			{
				lObtainedReference.setReleased(false);
				if (pRequestParameters != null)
				{
					lObtainedReference.initialize(pRequestParameters);
				}
				return lObtainedReference;
			}
			else
			{
				return requestRecyclableObject(pRequestParameters);
			}

		}

		R lNewInstance;
		try
		{
			lNewInstance = mRecyclableClass.newInstance();
			if (pRequestParameters != null)
			{
				lNewInstance.initialize(pRequestParameters);
			}
			lNewInstance.setRecycler(this);
			lNewInstance.setReleased(false);

			mCounter++;
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
		mAvailableObjectsQueue.add(new SoftReference<R>(pObject));
		mCounter--;
	}

	public int getCounter()
	{
		return mCounter;
	}

}
