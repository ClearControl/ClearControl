package rtlib.cameras;

import gnu.trove.list.array.TByteArrayList;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

public abstract class StackCameraDeviceBase extends CameraDeviceBase implements
																																		StackCameraDeviceInterface,
																																		StateQueueDeviceInterface
{
	protected BooleanVariable mStackMode = new BooleanVariable(	"StackMode",
																															true);

	protected BooleanVariable mKeepPlane = new BooleanVariable(	"KeepPlane",
																															true);

	protected DoubleVariable mNumberOfImagesPerPlaneVariable = new DoubleVariable("NumberOfImagesPerPlane",
																																								1);

	protected volatile int mQueueLength = 0;
	protected TByteArrayList mStagingKeepAcquiredImageArray;
	protected ConcurrentLinkedQueue<TByteArrayList> mKeepAcquiredImageArrayQueue = new ConcurrentLinkedQueue<TByteArrayList>();

	protected RecyclerInterface<StackInterface, StackRequest> mRecycler;

	protected ObjectVariable<StackInterface> mStackReference;

	private int mMinimalNumberOfAvailableStacks = 6;

	public StackCameraDeviceBase(String pDeviceName)
	{
		super(pDeviceName);
	}

	@Override
	public int getMinimalNumberOfAvailableStacks()
	{
		return mMinimalNumberOfAvailableStacks;
	}

	@Override
	public void setMinimalNumberOfAvailableStacks(int pMinimalNumberOfAvailableStacks)
	{
		mMinimalNumberOfAvailableStacks = pMinimalNumberOfAvailableStacks;
	}

	@Override
	public DoubleVariable getNumberOfImagesPerPlaneVariable()
	{
		return mNumberOfImagesPerPlaneVariable;
	}

	@Override
	public void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler)
	{
		mRecycler = pRecycler;
	}

	@Override
	public RecyclerInterface<StackInterface, StackRequest> getStackRecycler()
	{
		return mRecycler;
	}

	@Override
	public BooleanVariable getStackModeVariable()
	{
		return mStackMode;
	}

	@Override
	public BooleanVariable getKeepPlaneVariable()
	{
		return mKeepPlane;
	}

	@Override
	public ObjectVariable<StackInterface> getStackVariable()
	{
		return mStackReference;
	}

	@Override
	public void clearQueue()
	{
		mQueueLength = 0;
		mStackDepthVariable.setValue(0);
		mStagingKeepAcquiredImageArray = new TByteArrayList();
	}

	@Override
	public void addCurrentStateToQueue()
	{
		mQueueLength++;
		if (mStagingKeepAcquiredImageArray == null)
			mStagingKeepAcquiredImageArray = new TByteArrayList();
		mStagingKeepAcquiredImageArray.add((byte) (mKeepPlane.getBooleanValue()	? 1
																																						: 0));
	}

	@Override
	public void finalizeQueue()
	{
	}

	@Override
	public int getQueueLength()
	{
		return mQueueLength;
	}

	@Override
	public Future<Boolean> playQueue()
	{
		if (getStackRecycler() == null)
		{
			System.err.println("No recycler defined for: " + this);
			return null;
		}

		mStackDepthVariable.setValue(mQueueLength);
		mKeepAcquiredImageArrayQueue.add(new TByteArrayList(mStagingKeepAcquiredImageArray));
		// This method should be called by overriding methods of descendants.
		return null;
	}

}