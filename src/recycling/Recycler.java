package recycling;

import java.util.concurrent.ConcurrentLinkedQueue;

import frames.Frame;

public class Recycler<R extends RecyclableInterface>
{
	private final Class<R> mRecyclableClass;
	private final ConcurrentLinkedQueue<R> mAvailableObjectsQueue = new ConcurrentLinkedQueue<R>();
	private volatile int mCounter = 0;

	public Recycler(Class<R> pRecyclableClass)
	{
		mRecyclableClass = pRecyclableClass;
	}

	public boolean ensurePreallocated(int pNumberofPrealocatedRecyclablesNeeded,
																		int... pParameters)
	{
		final int lNumberOfAvailableObjects = mAvailableObjectsQueue.size();
		final int lNumberOfObjectsToAllocate = Math.max(0,
																										pNumberofPrealocatedRecyclablesNeeded - lNumberOfAvailableObjects);

		try
		{
			for (int i = 0; i < lNumberOfObjectsToAllocate; i++)
			{
				R lNewInstance = mRecyclableClass.newInstance();
				lNewInstance.initialize(pParameters);
				lNewInstance.setRecycler((Recycler<R>) this);
				mAvailableObjectsQueue.add(lNewInstance);
			}
			return true;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	public R requestFrame(final int... pRequestParameters)
	{

		R lHead = mAvailableObjectsQueue.poll();

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
		catch (Throwable e)
		{
			e.printStackTrace();
			return null;
		}

	}

	public void release(R pObject)
	{
		mAvailableObjectsQueue.add(pObject);
		mCounter--;
	}

	public int getCounter()
	{
		return mCounter;
	}

}
