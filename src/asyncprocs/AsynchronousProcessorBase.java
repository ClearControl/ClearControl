package asyncprocs;

import java.util.concurrent.LinkedBlockingQueue;

import utils.concurency.thread.EnhancedThread;

public abstract class AsynchronousProcessorBase<I, O> implements
																											AsynchronousProcessorInterface<I, O>
{

	private AsynchronousProcessorInterface<O, ?> mReceiver;
	private final LinkedBlockingQueue<I> mInputQueue;
	private EnhancedThread mEnhancedThread;
	private String mName;

	public AsynchronousProcessorBase(	final String pName,
																		final int pMaxQueueSize)
	{
		super();
		mName = pName;
		mInputQueue = new LinkedBlockingQueue<I>(pMaxQueueSize);

		mEnhancedThread = new EnhancedThread(mName)
		{
			@Override
			public boolean loop()
			{
				try
				{
					final I lInput = mInputQueue.take();
					final O lOutput = process(lInput);
					if (lOutput != null)
						send(lOutput);
				}
				catch (InterruptedException e)
				{
					System.out.println(e.getLocalizedMessage());
				}
				
				return true;
			}
		};

	}

	public void connectToReceiver(AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor)
	{
		mReceiver = pAsynchronousProcessor;
	}

	public boolean start()
	{
		mEnhancedThread.setDaemon(true);
		return mEnhancedThread.start();
	}

	public void close()
	{
		mEnhancedThread.stop();
	}

	public boolean passOrWait(final I pObject)
	{
		if (!mEnhancedThread.isRunning())
			return false;
		try
		{
			mInputQueue.put(pObject);
			return true;
		}
		catch (final InterruptedException e)
		{
			System.err.println(e.getLocalizedMessage());
			return false;
		}
	}
	
	public boolean passOrFail(final I pObject)
	{
		if (!mEnhancedThread.isRunning())
			return false;
		
		return	mInputQueue.offer(pObject);
	}

	public abstract O process(I pInput);

	protected void send(final O lOutput)
	{
		if (mReceiver != null)
			mReceiver.passOrWait(lOutput);
	}

	public int getQueueLength()
	{
		return mInputQueue.size();
	}

	public int getRemainingCapacity()
	{
		return mInputQueue.remainingCapacity();
	}

	public LinkedBlockingQueue<I> getInputQueue()
	{
		return mInputQueue;
	}

}
