package rtlib.stack.server;

import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;

public class AsynchronousStackSinkAdapter<T extends NativeType<T>, A extends ArrayDataAccess<A>>	implements
																																																	StackSinkInterface<T, A>
{

	private StackSinkInterface<T, A> mStackSink;

	private AsynchronousProcessorInterface<StackInterface<T, A>, StackInterface<T, A>> mAsynchronousConversionProcessor;

	private ObjectVariable<StackInterface<T, A>> mFinishedProcessingStackVariable;

	public static <ST extends NativeType<ST>, SA extends ArrayDataAccess<SA>> AsynchronousStackSinkAdapter<ST, SA> wrap(StackSinkInterface<ST, SA> pStackSink,
																																																											final int pMaxQueueSize)
	{
		return new AsynchronousStackSinkAdapter<ST, SA>(pStackSink,
																										pMaxQueueSize);
	}

	public AsynchronousStackSinkAdapter(final StackSinkInterface<T, A> pStackSink,
																			final int pMaxQueueSize)
	{
		super();
		mStackSink = pStackSink;

		mAsynchronousConversionProcessor = new AsynchronousProcessorBase<StackInterface<T, A>, StackInterface<T, A>>(	"AsynchronousStackSinkAdapter",
																																																									pMaxQueueSize)
		{
			@Override
			public StackInterface<T, A> process(final StackInterface<T, A> pStack)
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
	public boolean appendStack(final StackInterface<T, A> pStack)
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
	public void addMetaData(final String pPrefix, final double pValue)
	{
		mStackSink.addMetaData(pPrefix, pValue);
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

	public void setFinishedProcessingStackVariable(final ObjectVariable<StackInterface<T, A>> pVariable)
	{
		mFinishedProcessingStackVariable = pVariable;
	}

}
