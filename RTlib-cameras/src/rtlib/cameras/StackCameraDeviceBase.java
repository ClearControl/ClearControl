package rtlib.cameras;

import gnu.trove.list.array.TByteArrayList;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import coremem.recycling.RecyclerInterface;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public abstract class StackCameraDeviceBase<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																									CameraDeviceBase implements
																													StackCameraDeviceInterface<T, A>,
																													StateQueueDeviceInterface
{
	protected BooleanVariable mStackMode = new BooleanVariable(	"StackMode",
																true);

	protected BooleanVariable mKeepPlane = new BooleanVariable(	"KeepPlane",
																true);

	protected DoubleVariable mNumberOfImagesPerPlaneVariable = new DoubleVariable(	"NumberOfImagesPerPlane",
																					1);

	protected volatile int mQueueLength = 0;
	protected TByteArrayList mStagingKeepAcquiredImageArray;
	protected ConcurrentLinkedQueue<TByteArrayList> mKeepAcquiredImageArrayQueue = new ConcurrentLinkedQueue<TByteArrayList>();

	protected RecyclerInterface<StackInterface<T, A>, StackRequest<T>> mRecycler;

	protected ObjectVariable<StackInterface<T, A>> mStackReference;
	
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
	public void setStackRecycler(RecyclerInterface<StackInterface<T, A>, StackRequest<T>> pRecycler)
	{
		mRecycler = pRecycler;
	}

	@Override
	public RecyclerInterface<StackInterface<T, A>, StackRequest<T>> getStackRecycler()
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
	public ObjectVariable<StackInterface<T, A>> getStackVariable()
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
		if(getStackRecycler()==null)
		{
			System.err.println("No recycler defined for: "+this);
			return null;
		}
			
		mStackDepthVariable.setValue(mQueueLength);
		mKeepAcquiredImageArrayQueue.add(new TByteArrayList(mStagingKeepAcquiredImageArray));
		// This method should be called by overriding methods of descendants.
		return null;
	}

}