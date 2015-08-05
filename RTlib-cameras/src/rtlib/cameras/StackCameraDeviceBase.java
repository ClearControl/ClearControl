package rtlib.cameras;

import gnu.trove.list.array.TByteArrayList;

import java.util.concurrent.Future;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;

public abstract class StackCameraDeviceBase<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																																		CameraDeviceBase implements
																																																										StackCameraDeviceInterface<T, A>,
																																																										StateQueueDeviceInterface
{
	protected BooleanVariable mStackMode = new BooleanVariable(	"StackMode",
																															true);

	protected BooleanVariable mKeepPlane = new BooleanVariable(	"KeepPlane",
																															true);

	protected DoubleVariable mNumberOfImagesPerPlaneVariable = new DoubleVariable("NumberOfImagesPerPlane",
																																								1);

	protected volatile int mQueueLength = 0;

	protected ObjectVariable<StackInterface<T, A>> mStackReference;

	protected TByteArrayList mKeepAcquiredImageArray = new TByteArrayList();

	public StackCameraDeviceBase(String pDeviceName)
	{
		super(pDeviceName);

	}

	@Override
	public DoubleVariable getNumberOfImagesPerPlaneVariable()
	{
		return mNumberOfImagesPerPlaneVariable;
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
		mKeepAcquiredImageArray.clear();
	}

	@Override
	public void addCurrentStateToQueue()
	{
		mQueueLength++;
		mKeepAcquiredImageArray.add((byte) (mKeepPlane.getBooleanValue() ? 1
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
		mStackDepthVariable.setValue(mQueueLength);
		// This method should be called by overriding methods of descendants.
		return null;
	}

}