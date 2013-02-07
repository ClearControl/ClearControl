package old;

import java.util.concurrent.ConcurrentLinkedQueue;

import frames.Frame;

public class FrameManager<F extends Frame>
{

	private ConcurrentLinkedQueue<F> mAvailableFramesQueue = new ConcurrentLinkedQueue<F>();
	private volatile int mCounter = 0;

	@SuppressWarnings("unchecked")
	public F requestFrame(Class<F> pFrameClass)
	{

		F lHead = mAvailableFramesQueue.poll();

		if (lHead != null)
		{
			lHead.setReleased(false);
			//xSystem.out.println(Thread.currentThread().toString()+" Requested: "+lHead.hashCode());
			return lHead;
		}

		// System.out.println("mAvailableFramesQueue.size = "+mAvailableFramesQueue.size());

		F lNewInstance;
		try
		{
			lNewInstance = pFrameClass.newInstance();
			lNewInstance.setFrameManager((FrameManager<Frame>) this);
			lNewInstance.setReleased(false);

			// System.out.println(Thread.currentThread().toString()+" Allocating: "+lNewInstance.hashCode());
			mCounter++;
			return lNewInstance;
		}
		catch (Throwable e)
		{
			System.err.println(FrameManager.class.getSimpleName() + ": "
													+ e.getLocalizedMessage());
			return null;
		}

	}

	public void release(F pFrame)
	{
		mCounter--;
		// System.out.println(Thread.currentThread().toString()+" Release: "+pFrame.hashCode());
		// System.out.println("mAvailableFramesQueue.size = "+mAvailableFramesQueue.size());
		mAvailableFramesQueue.add(pFrame);
	}

	public int getCounter()
	{
		return mCounter;
	}

}
