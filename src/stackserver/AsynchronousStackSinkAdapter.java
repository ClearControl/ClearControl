package stackserver;

import java.io.IOException;

import stack.Stack;
import variable.VariableInterface;
import variable.bundle.VariableBundle;
import variable.objectv.ObjectVariable;
import asyncprocs.AsynchronousProcessorBase;
import asyncprocs.AsynchronousProcessorInterface;

public class AsynchronousStackSinkAdapter	implements
																					StackSinkInterface
{

	private StackSinkInterface mStackSink;

	private AsynchronousProcessorInterface<Stack, Stack> mAsynchronousConversionProcessor;

	private ObjectVariable<Stack> mFinishedProcessingStackVariable;

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
				if (mFinishedProcessingStackVariable != null)
					mFinishedProcessingStackVariable.set(pStack);
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

	public boolean waitToFinish(final int pTimeOutInMilliseconds)
	{
		return mAsynchronousConversionProcessor.waitToFinish(pTimeOutInMilliseconds);
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

	@Override
	public void removeMetaDataVariable(VariableInterface<?> pVariable)
	{
		mStackSink.removeMetaDataVariable(pVariable);
	}

	@Override
	public void removeAllMetaDataVariables()
	{
		mStackSink.removeAllMetaDataVariables();
	}

	public void setFinishedProcessingStackVariable(ObjectVariable<Stack> pVariable)
	{
		mFinishedProcessingStackVariable = pVariable;
	}



}
