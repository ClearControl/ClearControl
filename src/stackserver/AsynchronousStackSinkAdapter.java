package stackserver;

import java.io.IOException;

import stack.Stack;
import variable.VariableInterface;
import variable.bundle.VariableBundle;
import asyncprocs.AsynchronousProcessorBase;
import asyncprocs.AsynchronousProcessorInterface;

public class AsynchronousStackSinkAdapter	implements
																					StackSinkInterface
{

	private StackSinkInterface mStackSink;

	private AsynchronousProcessorInterface<Stack, Stack> mAsynchronousConversionProcessor;

	public AsynchronousStackSinkAdapter(final StackSinkInterface pStackSink,
																			final int pMaxQueueSize)
	{
		super();
		mStackSink = pStackSink;

		mAsynchronousConversionProcessor = new AsynchronousProcessorBase<Stack, Stack>(	"AsynchronousStackSinkAdapter",
																																										pMaxQueueSize)
		{
			@Override
			public Stack process(final Stack pStack)
			{
				mStackSink.appendStack(pStack);

				return null;
			}
		};
	}

	public boolean start()
	{
		return mAsynchronousConversionProcessor.start();
	}

	@Override
	public boolean appendStack(final Stack pStack)
	{
		return mAsynchronousConversionProcessor.passOrWait(pStack);
	}

	public void waitToFinish(final int pPollIntervall)
	{
		mAsynchronousConversionProcessor.waitToFinish(pPollIntervall);
	}

	public boolean stop()
	{
		return mAsynchronousConversionProcessor.stop();
	}

	public int getQueueLength()
	{
		return mAsynchronousConversionProcessor.getInputQueueLength();
	}

	@Override
	public void addMetaDataVariable(String pPrefix,
																	VariableInterface<?> pVariable)
	{
		mStackSink.addMetaDataVariable(pPrefix, pVariable);
	}

	public void removeAllMetaDataVariables()
	{
		mStackSink.removeAllMetaDataVariables();
	}

}
