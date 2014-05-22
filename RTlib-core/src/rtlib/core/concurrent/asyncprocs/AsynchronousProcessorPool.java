package rtlib.core.concurrent.asyncprocs;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.concurrent.executors.CompletingThreadPoolExecutor;
import rtlib.core.concurrent.executors.RTlibExecutors;

public class AsynchronousProcessorPool<I, O>	extends
																							AsynchronousProcessorBase<I, O>	implements
																																							AsynchronousProcessorInterface<I, O>,
																																							AsynchronousExecutorServiceAccess,
																																							AsynchronousSchedulerServiceAccess
{

	private final ProcessorInterface<I, O> mProcessor;
	private CompletingThreadPoolExecutor mThreadPoolExecutor;

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
		Runnable lRunnable = () -> {
			mThreadPoolExecutor.getFutur(1, TimeUnit.NANOSECONDS);
		};
		
		scheduleAtFixedRate(lRunnable, 1, TimeUnit.NANOSECONDS);
		
		super.start();
	}

	@Override
	public boolean stop()
	{
		if (mEnhancedThread == null)
		{
			return false;
		}

		mEnhancedThread.stop();
		mEnhancedThread = null;
		return true;
	}

	@Override
	public final O process(final I pInput)
	{
		Callable<O> lCallable = () -> {
			return mProcessor.process(pInput);
		};
		return mThreadPoolExecutor.submit(lCallable);
	}

}
