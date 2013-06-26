package recycling;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Recycler<R extends RecyclableInterface>
{
	private final Class<R> mRecyclableClass;
	private final ConcurrentLinkedQueue<R> mAvailableObjectsQueue = new ConcurrentLinkedQueue<R>();
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
				lNewInstance.setRecycler((Recycler<R>) this);
				mAvailableObjectsQueue.add(lNewInstance);
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

		final R lHead = mAvailableObjectsQueue.poll();

		if (lHead != null)
		{
			lHead.setReleased(false);
			if (pRequestParameters != null)
				lHead.initialize(pRequestParameters);
			return lHead;
		}

		R lNewInstance;
		try
		{
			lNewInstance = mRecyclableClass.newInstance();
			if (pRequestParameters != null)
				lNewInstance.initialize(pRequestParameters);
			lNewInstance.setRecycler((Recycler<R>) this);
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
		mAvailableObjectsQueue.add(pObject);
		mCounter--;
	}

	public int getCounter()
	{
		return mCounter;
	}

}
