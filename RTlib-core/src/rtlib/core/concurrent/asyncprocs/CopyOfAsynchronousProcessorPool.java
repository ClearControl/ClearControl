package rtlib.core.concurrent.asyncprocs;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import rtlib.core.concurrent.thread.EnhancedThread;

public class CopyOfAsynchronousProcessorPool<I, O>	extends
																							AsynchronousProcessorBase<I, O>	implements
																																							AsynchronousProcessorInterface<I, O>
{

	private final int mThreadPoolSize;
	private final ArrayList<ProcessorThread> mThreads = new ArrayList<ProcessorThread>();
	private final LinkedBlockingQueue<ProcessorThread> mAvailableThreads = new LinkedBlockingQueue<ProcessorThread>();
	private final LinkedBlockingQueue<ProcessorThread> mBusyThreads = new LinkedBlockingQueue<ProcessorThread>();
	private final ProcessorInterface<I, O> mProcessor;

	private class ProcessorThread extends EnhancedThread
	{
		public volatile boolean mBusy = false;
		private final LinkedBlockingQueue<I> mProcessorThreadInputQueue = new LinkedBlockingQueue<I>(1);

		public ProcessorThread(final int pIndex)
		{
			super(String.format("AsynchronousProcessorPool.ProcessorThread(%d)",
													pIndex));
		}

		public boolean receive(final I pInput)
		{
			try
			{
				mProcessorThreadInputQueue.put(pInput);
				// System.out.println("AsynchronousProcessorPool: pInput = "+pInput.hashCode());
				return true;
			}
			catch (final InterruptedException e)
			{
				System.err.println(e.getLocalizedMessage());
				return false;
			}
		}

		@Override
		public boolean loop()
		{

			try
			{
				mBusy = false;
				final I lInput = mProcessorThreadInputQueue.take();
				mBusy = true;
				final O lOutput = mProcessor.process(lInput);

				while (mBusyThreads.peek() != this)
				{
					sleepNanos(100);
				}
				final ProcessorThread lThreadShouldBeThis = mBusyThreads.poll();
				if (lThreadShouldBeThis == this)
				{
					send(lOutput);
					mAvailableThreads.add(this);
				}
				else
				{
					System.err.println("Removed wrong Thread from busy threads!!");
				}

			}
			catch (final InterruptedException e)
			{
				System.out.println(e.getLocalizedMessage());
			}

			return true;
		}

	}

	public CopyOfAsynchronousProcessorPool(	final String pName,
																		final int pMaxQueueSize,
																		final int pThreadPoolSize,
																		final ProcessorInterface<I, O> pProcessor)
	{
		super(pName, pMaxQueueSize);
		mThreadPoolSize = pThreadPoolSize;
		mProcessor = pProcessor;
	}

	public CopyOfAsynchronousProcessorPool(	final String pName,
																		final int pMaxQueueSize,
																		final ProcessorInterface<I, O> pProcessor)
	{
		this(	pName,
					pMaxQueueSize,
					Runtime.getRuntime().availableProcessors(),
					pProcessor);
	}

	@Override
	public boolean start()
	{
		if (mAvailableThreads.isEmpty())
		{
			for (int i = 0; i < mThreadPoolSize; i++)
			{
				final ProcessorThread lProcessorThread = new ProcessorThread(i);
				lProcessorThread.setDaemon(true);
				lProcessorThread.start();

				mThreads.add(lProcessorThread);
				mAvailableThreads.add(lProcessorThread);
			}

			return super.start();
		}
		return false;
	}

	@Override
	public final O process(final I pInput)
	{
		try
		{
			final ProcessorThread lAvailableProcessorThread = mAvailableThreads.take();
			mBusyThreads.add(lAvailableProcessorThread);
			lAvailableProcessorThread.receive(pInput);
			return null;
		}
		catch (final InterruptedException e)
		{
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}

	public final int getNumberOfThreadsInAvailableQueue()
	{
		return mAvailableThreads.size();
	}

	public final int getNumberOfNonBusyThreads()
	{
		int lCounter = 0;
		for (final ProcessorThread lProcessorThread : mThreads)
		{
			if (!lProcessorThread.mBusy)
			{
				lCounter++;
			}
		}
		return lCounter;
	}

	public final double getLoad()
	{
		final double lLoad = (double) (mThreadPoolSize - getNumberOfNonBusyThreads()) / mThreadPoolSize;
		return lLoad;
	}

	@Override
	public final boolean stop()
	{
		for (final ProcessorThread lProcessorThread : mThreads)
		{
			lProcessorThread.stop();
		}
		mThreads.clear();
		mBusyThreads.clear();
		mAvailableThreads.clear();
		super.stop();
		return true;
	}

	@Override
	public final void close()
	{
		if (!mThreads.isEmpty())
		{
			stop();
		}

		super.close();
	}

}
