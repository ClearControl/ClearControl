package rtlib.hardware.cameras;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import coremem.recycling.RecyclerInterface;
import gnu.trove.list.array.TByteArrayList;
import rtlib.core.variable.Variable;
import rtlib.device.queue.StateQueueDeviceInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public abstract class StackCameraDeviceBase extends CameraDeviceBase implements
																																		StackCameraDeviceInterface,
																																		StateQueueDeviceInterface
{
	protected Variable<Boolean> mStackMode = new Variable<Boolean>(	"StackMode",
																																							true);

	protected Variable<Boolean> mKeepPlane = new Variable<Boolean>(	"KeepPlane",
																																							true);

	protected Variable<Long> mNumberOfImagesPerPlaneVariable = new Variable<Long>("NumberOfImagesPerPlane",
																																														1L);

	protected volatile int mQueueLength = 0;
	protected TByteArrayList mStagingKeepAcquiredImageArray;
	protected ConcurrentLinkedQueue<TByteArrayList> mKeepAcquiredImageArrayQueue = new ConcurrentLinkedQueue<TByteArrayList>();

	protected RecyclerInterface<StackInterface, StackRequest> mRecycler;

	protected Variable<StackInterface> mStackReference;

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
	public Variable<Long> getNumberOfImagesPerPlaneVariable()
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
	public Variable<Boolean> getStackModeVariable()
	{
		return mStackMode;
	}

	@Override
	public Variable<Boolean> getKeepPlaneVariable()
	{
		return mKeepPlane;
	}

	@Override
	public Variable<StackInterface> getStackVariable()
	{
		return mStackReference;
	}

	@Override
	public void clearQueue()
	{
		mQueueLength = 0;
		mStackDepthVariable.set(0L);
		mStagingKeepAcquiredImageArray = new TByteArrayList();
	}

	@Override
	public void addCurrentStateToQueue()
	{
		mQueueLength++;
		if (mStagingKeepAcquiredImageArray == null)
			mStagingKeepAcquiredImageArray = new TByteArrayList();
		mStagingKeepAcquiredImageArray.add((byte) (mKeepPlane.get()	? 1
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

		mStackDepthVariable.set((long) mQueueLength);
		mKeepAcquiredImageArrayQueue.add(new TByteArrayList(mStagingKeepAcquiredImageArray));
		// This method should be called by overriding methods of descendants.
		return null;
	}

}