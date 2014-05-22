package rtlib.core.concurrent.asyncprocs;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.concurrent.executors.RTlibExecutors;

public class AsynchronousProcessorPool<I, O>	implements
																							AsynchronousProcessorInterface<I, O>,
																							AsynchronousExecutorServiceAccess
{

	private final ProcessorInterface<I, O> mProcessor;
	private ThreadPoolExecutor mThreadPoolExecutor;

	public AsynchronousProcessorPool(	final String pName,
																		final int pMaxQueueSize,
																		final int pThreadPoolSize,
																		final ProcessorInterface<I, O> pProcessor)
	{
		super(pName, pMaxQueueSize);
		mThreadPoolExecutor = RTlibExecutors.getOrCreateThreadPoolExecutor(	this,
																																				Thread.NORM_PRIORITY,
																																				pThreadPoolSize,
																																				pThreadPoolSize,
																																				pMaxQueueSize);

		mProcessor = pProcessor;
	}

	public AsynchronousProcessorPool(	final String pName,
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

	}

	@Override
	public boolean passOrWait(I pObject)
	{
		return false;
	}

	@Override
	public boolean passOrFail(I pObject)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final O process(final I pInput)
	{
		Callable<O> lCallable = () -> {
			return mProcessor.process(pInput);
		};

		mThreadPoolExecutor.submit(lCallable);
	}

	public final int getNumberOfThreadsInAvailableQueue()
	{
		return mAvailableThreads.size();
	}

	public final int getNumberOfNonBusyThreads()
	{
		return 0;
	}

	public final double getLoad()
	{
		final double lLoad = (double) (mThreadPoolSize - getNumberOfNonBusyThreads()) / mThreadPoolSize;
		return lLoad;
	}

	@Override
	public final boolean stop()
	{

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

	@Override
	public void connectToReceiver(AsynchronousProcessorInterface<O, ?> pAsynchronousProcessor)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean waitToFinish(int pPollInterval)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInputQueueLength()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRemainingCapacity()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
