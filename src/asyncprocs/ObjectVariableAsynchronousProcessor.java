package asyncprocs;

import java.io.IOException;

import device.VirtualDevice;
import variable.objectv.ObjectInputVariableInterface;
import variable.objectv.ObjectOutputVariableInterface;
import variable.objectv.ObjectVariable;

public class ObjectVariableAsynchronousProcessor<I, O>	implements
																												VirtualDevice
{
	ObjectVariable<I> mInputObjectVariable = new ObjectVariable<I>();
	ObjectVariable<O> mOutputObjectVariable = new ObjectVariable<O>();

	AsynchronousProcessorBase<I, O> mAsynchronousProcessorBase;

	private Object mObjectEventSource;

	public ObjectVariableAsynchronousProcessor(	String pName,
																							int pMaxQueueSize,
																							final ProcessorInterface<I, O> pProcessor,
																							final boolean pDropIfQueueFull)
	{
		super();

		mInputObjectVariable.sendUpdatesTo(new ObjectInputVariableInterface<I>()
		{
			@Override
			public void setReference(	Object pObjectEventSource,
																I pNewReference)
			{
				if (pDropIfQueueFull)
					mAsynchronousProcessorBase.passOrFail(pNewReference);
				else
					mAsynchronousProcessorBase.passOrWait(pNewReference);
			}
		});

		mAsynchronousProcessorBase = new AsynchronousProcessorBase<I, O>(	pName,
																																			pMaxQueueSize)
		{
			@Override
			public O process(I pInput)
			{
				return pProcessor.process(pInput);
			}
		};

		mAsynchronousProcessorBase.connectToReceiver(new AsynchronousProcessorAdapter<O, O>()
		{

			@Override
			public boolean passOrWait(O pObject)
			{
				mOutputObjectVariable.setReference(	mObjectEventSource,
																						pObject);
				return true;
			}

			@Override
			public boolean passOrFail(O pObject)
			{
				mOutputObjectVariable.setReference(	mObjectEventSource,
																						pObject);
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
