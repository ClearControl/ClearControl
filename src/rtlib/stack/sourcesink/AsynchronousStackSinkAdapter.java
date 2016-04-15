package rtlib.stack.sourcesink;

import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.variable.Variable;
import rtlib.stack.StackInterface;

public class AsynchronousStackSinkAdapter	implements
																					StackSinkInterface
{

	private StackSinkInterface mStackSink;

	private AsynchronousProcessorInterface<StackInterface, StackInterface> mAsynchronousConversionProcessor;

	private Variable<StackInterface> mFinishedProcessingStackVariable;

	public static AsynchronousStackSinkAdapter wrap(StackSinkInterface pStackSink,
																									final int pMaxQueueSize)
	{
		return new AsynchronousStackSinkAdapter(pStackSink, pMaxQueueSize);
	}

	public AsynchronousStackSinkAdapter(final StackSinkInterface pStackSink,
																			final int pMaxQueueSize)
	{
		super();
		mStackSink = pStackSink;

		mAsynchronousConversionProcessor = new AsynchronousProcessorBase<StackInterface, StackInterface>(	"AsynchronousStackSinkAdapter",
																																																			pMaxQueueSize)
		{
			@Override
			public StackInterface process(final StackInterface pStack)
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
	public boolean appendStack(final StackInterface pStack)
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
																	final Variable<?> pVariable)
	{
		mStackSink.addMetaDataVariable(pPrefix, pVariable);
	}

	@Override
	public void removeMetaDataVariable(final Variable<?> pVariable)
	{
		mStackSink.removeMetaDataVariable(pVariable);
	}

	@Override
	public void removeAllMetaDataVariables()
	{
		mStackSink.removeAllMetaDataVariables();
	}

	public void setFinishedProcessingStackVariable(final Variable<StackInterface> pVariable)
	{
		mFinishedProcessingStackVariable = pVariable;
	}

}
