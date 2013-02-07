package recycling;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Recycler<R extends RecyclableInterface>
{

	private ConcurrentLinkedQueue<R> mAvailableObjectsQueue = new ConcurrentLinkedQueue<R>();
	private volatile int mCounter = 0;

	@SuppressWarnings("unchecked")
	public R requestFrame(Class<R> pRecyclableClass)
	{

		R lHead = mAvailableObjectsQueue.poll();

		if (lHead != null)
		{
			lHead.setReleased(false);
			return lHead;
		}

		R lNewInstance;
		try
		{
			lNewInstance = pRecyclableClass.newInstance();
			lNewInstance.setRecycler((Recycler<R>) this);
			lNewInstance.setReleased(false);

			mCounter++;
			return lNewInstance;
		}
		catch (Throwable e)
		{
			System.err.println(Recycler.class.getSimpleName() + ": "
													+ e.getLocalizedMessage());
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
