package rtlib.stack.processor;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorPool;
import rtlib.core.concurrent.asyncprocs.ProcessorInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.BasicRecycler;
import coremem.recycling.RecyclableFactory;
import coremem.recycling.RecyclerInterface;

public class AsynchronousPoolStackProcessorPipeline<T extends NativeType<T>, A extends ArrayDataAccess<A>>	implements
																																																						SameTypeStackProcessingPipeline<T, A>,
																																																						StartStopDeviceInterface
{

	private final CopyOnWriteArrayList<SameTypeStackProcessorInterface<T, A>> mProcessorList = new CopyOnWriteArrayList<SameTypeStackProcessorInterface<T, A>>();
	private final CopyOnWriteArrayList<RecyclerInterface<StackInterface<T, A>, StackRequest<T>>> mRecyclerList = new CopyOnWriteArrayList<RecyclerInterface<StackInterface<T, A>, StackRequest<T>>>();
	private AsynchronousProcessorPool<StackInterface<T, A>, StackInterface<T, A>> mAsynchronousProcessorPool;

	private ObjectVariable<StackInterface<T, A>> mInputVariable;
	private ObjectVariable<StackInterface<T, A>> mOutputVariable;
	private AsynchronousProcessorInterface<StackInterface<T, A>, StackInterface<T, A>> mReceiver;

	public AsynchronousPoolStackProcessorPipeline(String pName,
																								final int pMaxQueueSize,
																								final int pThreadPoolSize)
	{
		super();

		mInputVariable = new ObjectVariable<StackInterface<T, A>>("InputVariable")
		{

			@Override
			public StackInterface<T, A> setEventHook(	StackInterface<T, A> pOldValue,
																								StackInterface<T, A> pNewValue)
			{
				mAsynchronousProcessorPool.passOrWait(pNewValue);
				return super.setEventHook(pOldValue, pNewValue);
			}

		};

		mOutputVariable = new ObjectVariable<StackInterface<T, A>>("OutputVariable");

		final ProcessorInterface<StackInterface<T, A>, StackInterface<T, A>> lProcessor = new ProcessorInterface<StackInterface<T, A>, StackInterface<T, A>>()
		{

			@Override
			public StackInterface<T, A> process(StackInterface<T, A> pInput)
			{
				StackInterface<T, A> lStack = pInput;
				for (int i = 0; i < mProcessorList.size(); i++)
				{
					final SameTypeStackProcessorInterface<T, A> lProcessor = mProcessorList.get(i);
					if (lProcessor.isActive())
					{
						// System.out.println("lProcessor=" + lProcessor);
						// System.out.println("lStack input=" + lStack);
						final RecyclerInterface<StackInterface<T, A>, StackRequest<T>> lRecycler = mRecyclerList.get(i);
						lStack = lProcessor.process(lStack, lRecycler);
						// System.out.println("lStack output=" + lStack);
					}
				}
				return lStack;
			}

			@Override
			public void close() throws IOException
			{

			}
		};

		mAsynchronousProcessorPool = new AsynchronousProcessorPool<>(	pName,
																																	pMaxQueueSize,
																																	pThreadPoolSize,
																																	lProcessor);

		mReceiver = new AsynchronousProcessorBase<StackInterface<T, A>, StackInterface<T, A>>("Receiver",
																																													10)
		{
			@Override
			public StackInterface<T, A> process(final StackInterface<T, A> pInput)
			{
				mOutputVariable.set(pInput);
				return pInput;
			}
		};

		mAsynchronousProcessorPool.connectToReceiver(mReceiver);

	}

	@Override
	public void addStackProcessor(SameTypeStackProcessorInterface<T, A> pStackProcessor,
																RecyclableFactory<StackInterface<T, A>, StackRequest<T>> pStackFactory,
																int pMaximumNumberOfObjects)
	{
		final RecyclerInterface<StackInterface<T, A>, StackRequest<T>> lStackRecycler = new BasicRecycler<StackInterface<T, A>, StackRequest<T>>(	pStackFactory,
																																																																							pMaximumNumberOfObjects);
		mRecyclerList.add(lStackRecycler);
		mProcessorList.add(pStackProcessor);
	}

	@Override
	public void removeStackProcessor(SameTypeStackProcessorInterface<T, A> pStackProcessor)
	{
		final int lIndex = mProcessorList.indexOf(pStackProcessor);
		mProcessorList.remove(pStackProcessor);
		mRecyclerList.remove(lIndex);
	}

	@Override
	public ObjectVariable<StackInterface<T, A>> getInputVariable()
	{
		return mInputVariable;
	}

	@Override
	public ObjectVariable<StackInterface<T, A>> getOutputVariable()
	{
		return mOutputVariable;
	}

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean start()
	{
		return mReceiver.start() && mAsynchronousProcessorPool.start();
	}

	@Override
	public boolean stop()
	{
		return mAsynchronousProcessorPool.stop() && mReceiver.stop();
	}

	@Override
	public boolean close()
	{
		return true;
	}

}
