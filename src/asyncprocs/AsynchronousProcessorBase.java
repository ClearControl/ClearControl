package asyncprocs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import thread.EnhancedThread;

public abstract class AsynchronousProcessorBase<I, O> implements
																											AsynchronousProcessorInterface<I, O>
{

	private AsynchronousProcessorInterface<O, ?> mReceiver;
	private final LinkedBlockingQueue<I> mInputQueue;
	private EnhancedThread mEnhancedThread;
	private String mName;
	private long mPollPeriodInSeconds;

	public AsynchronousProcessorBase(	final String pName,
																		final int pMaxQueueSize,
																		final int pPollPeriodInSeconds)
	{
		super();
		mName = pName;
		mPollPeriodInSeconds = pPollPeriodInSeconds;
		mInputQueue = new LinkedBlockingQueue<I>(pMaxQueueSize <= 0	? 1
																																: pMaxQueueSize);

	}

	public AsynchronousProcessorBase(	final String pName,
																		final int pMaxQueueSize)
	{
		this(pName, pMaxQueueSize, 1);
	}

	@Override
	public void connectToReceiver(final AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor)
	{
		mReceiver = pAsynchronousProcessor;
	}

	@Override
	public boolean start()
	{
		mEnhancedThread = new EnhancedThread(mName)
		{

			@Override
			public boolean loop()
			{
				try
				{
					final I lInput = mInputQueue.poll(mPollPeriodInSeconds,
																						TimeUnit.SECONDS);
					if (lInput == null)
						return true;
					final O lOutput = process(lInput);
					if (lOutput != null)
						send(lOutput);
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}

				return true;
			}
		};

		mEnhancedThread.setDaemon(true);
		return mEnhancedThread.start();
	}

	@Override
	public boolean stop()
	{
		if (mEnhancedThread == null)
			return false;

		mEnhancedThread.stop();
		mEnhancedThread = null;
		return true;
	}

	@Override
	public void close()
	{
		this.stop();
	}

	public void waitToStart()
	{
		mEnhancedThread.waitForRunning();
	}

	@Override
	public boolean passOrWait(final I pObject)
	{
		if (!mEnhancedThread.isRunning())
			mEnhancedThread.waitForRunning();
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

	@Override
	public boolean passOrFail(final I pObject)
	{
		if (!mEnhancedThread.isRunning())
			return false;

		return mInputQueue.offer(pObject);
	}

	@Override
	public abstract O process(I pInput);

	protected void send(final O lOutput)
	{
		if (mReceiver != null)
			mReceiver.passOrWait(lOutput);
	}

	public int getInputQueueLength()
	{
		return mInputQueue.size();
	}

	public int getRemainingCapacity()
	{
		return mInputQueue.remainingCapacity();
	}

	public void waitToFinish(final int pPollInterval)
	{
		while (!mInputQueue.isEmpty())
		{
			EnhancedThread.sleep(pPollInterval);
		}
	}

	public LinkedBlockingQueue<I> getInputQueue()
	{
		return mInputQueue;
	}

}
