package asyncprocs;

import variable.objectv.ObjectVariable;
import device.VirtualDevice;

public class ObjectVariableAsynchronousProcessor<I, O>	implements
																												VirtualDevice
{
	ObjectVariable<I> mInputObjectVariable;
	ObjectVariable<O> mOutputObjectVariable;

	AsynchronousProcessorBase<I, O> mAsynchronousProcessorBase;

	private Object mObjectEventSource;

	public ObjectVariableAsynchronousProcessor(	final String pName,
																							final int pMaxQueueSize,
																							final ProcessorInterface<I, O> pProcessor,
																							final boolean pDropIfQueueFull)
	{
		super();

		mOutputObjectVariable = new ObjectVariable<O>(pName + "Output");
		mInputObjectVariable = new ObjectVariable<I>(pName + "Input")
		{
			@Override
			public void setReference(final I pNewReference)
			{

				if (pDropIfQueueFull)
					mAsynchronousProcessorBase.passOrFail(pNewReference);
				else
					mAsynchronousProcessorBase.passOrWait(pNewReference);
			}
		};

		mAsynchronousProcessorBase = new AsynchronousProcessorBase<I, O>(	pName,
																																			pMaxQueueSize)
		{
			@Override
			public O process(final I pInput)
			{
				return pProcessor.process(pInput);
			}
		};

		mAsynchronousProcessorBase.connectToReceiver(new AsynchronousProcessorAdapter<O, O>()
		{

			@Override
			public boolean passOrWait(final O pObject)
			{
				mOutputObjectVariable.setReference(pObject);
				return true;
			}

			@Override
			public boolean passOrFail(final O pObject)
			{
				mOutputObjectVariable.setReference(pObject);
				return true;
			}

		});

	}

	public ObjectVariable<I> getInputObjectVariable()
	{
		return mInputObjectVariable;
	}

	public ObjectVariable<O> getOutputObjectVariable()
	{
		return mOutputObjectVariable;
	}

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean start()
	{
		return mAsynchronousProcessorBase.start();
	}

	@Override
	public boolean stop()
	{
		return mAsynchronousProcessorBase.stop();
	}

	@Override
	public boolean close()
	{
		mAsynchronousProcessorBase.close();
		return true;
	}

}