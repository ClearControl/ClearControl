package asyncprocs;

import java.io.IOException;

import device.VirtualDevice;
import variable.objectv.ObjectInputVariableInterface;
import variable.objectv.ObjectOutputVariableInterface;
import variable.objectv.ObjectVariable;

public class ObjectVariableProcessor<I, O> implements VirtualDevice
{
	ObjectVariable<I> mInputObjectVariable = new ObjectVariable<I>();
	ObjectVariable<O> mOutputObjectVariable = new ObjectVariable<O>();

	AsynchronousProcessorPool<I, O> mAsynchronousProcessorPool;
	
	private Object mObjectEventSource;

	public ObjectVariableProcessor(String pName,
																	int pMaxQueueSize,
																	int pThreadPoolSize,
																	ProcessorInterface<I,O> pProcessor,
																	final boolean pDropIfQueueFull)
	{
		super();
		mAsynchronousProcessorPool = new AsynchronousProcessorPool<I, O>(pName, pMaxQueueSize, pThreadPoolSize, pProcessor);
		
		mInputObjectVariable.sendUpdatesTo(new ObjectInputVariableInterface<I>()
		{

			@Override
			public void setReference(	Object pObjectEventSource,
																I pNewReference)
			{
				mObjectEventSource = pObjectEventSource;
				if(pDropIfQueueFull)
				{
					mAsynchronousProcessorPool.passOrFail(pNewReference);
				}
				else
				{
					mAsynchronousProcessorPool.passOrWait(pNewReference);
				}
			}
		});
		
		mAsynchronousProcessorPool.connectToReceiver(new AsynchronousProcessorBase<O, O>("ObjectVariableProcessor-ConnectionToObjectVariable",pMaxQueueSize)
		{
			@Override
			public O process(O pOutput)
			{
				mOutputObjectVariable.setReference(mObjectEventSource, pOutput);
				return null;
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
		return mAsynchronousProcessorPool.start();
	}

	@Override
	public boolean stop()
	{
		return mAsynchronousProcessorPool.stop();
	}

	@Override
	public boolean close()
	{
		mAsynchronousProcessorPool.close();
		return true;
	}


}
