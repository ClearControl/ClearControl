package rtlib.stack.server;

import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.Stack;

public class AsynchronousStackSinkAdapter<I>	implements
																					StackSinkInterface<I>
{

	private StackSinkInterface<I> mStackSink;

	private AsynchronousProcessorInterface<Stack<I>, Stack<I>> mAsynchronousConversionProcessor;

	private ObjectVariable<Stack<I>> mFinishedProcessingStackVariable;

	public AsynchronousStackSinkAdapter(final StackSinkInterface<I> pStackSink,
																			final int pMaxQueueSize)
	{
		super();
		mStackSink = pStackSink;

		mAsynchronousConversionProcessor = new AsynchronousProcessorBase<Stack<I>, Stack<I>>(	"AsynchronousStackSinkAdapter",
																																										pMaxQueueSize)
		{
			@Override
			public Stack<I> process(final Stack<I> pStack)
			{
				mStackSink.appendStack(pStack);
				if (mFinishedProcessingStackVariable != null)
				{
					mFinishedProcessingStackVariable.set(pStack);
				}
				return null;
			}
		};
	}

	public boolean start()
	{
		return mAsynchronousConversionProcessor.start();
	}

	@Override
	public boolean appendStack(final Stack<I> pStack)
	{
		return mAsynchronousConversionProcessor.passOrWait(pStack);
	}

	public boolean waitToFinish(final long pTimeOut, TimeUnit pTimeUnit)
	{
		return mAsynchronousConversionProcessor.waitToFinish(	pTimeOut,
																													pTimeUnit);
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
	public void addMetaDataVariable(final String pPrefix,
																	final VariableInterface<?> pVariable)
	{
		mStackSink.addMetaDataVariable(pPrefix, pVariable);
	}

	@Override
	public void removeMetaDataVariable(final VariableInterface<?> pVariable)
	{
		mStackSink.removeMetaDataVariable(pVariable);
	}

	@Override
	public void removeAllMetaDataVariables()
	{
		mStackSink.removeAllMetaDataVariables();
	}

	public void setFinishedProcessingStackVariable(final ObjectVariable<Stack<I>> pVariable)
	{
		mFinishedProcessingStackVariable = pVariable;
	}

}
