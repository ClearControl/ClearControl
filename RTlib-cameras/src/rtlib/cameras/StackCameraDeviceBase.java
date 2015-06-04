package rtlib.cameras;

import java.util.concurrent.Future;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.StackInterface;

public abstract class StackCameraDeviceBase<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																																		CameraDeviceBase implements
																																																										StackCameraDeviceInterface<T, A>,
																																																										StateQueueDeviceInterface
{

	protected BooleanVariable mStackModeVariable = new BooleanVariable(	"StackMode",
																																			false);
	protected BooleanVariable mSingleShotModeVariable = new BooleanVariable("SingleShotMode",
																																					false);
	protected DoubleVariable mNumberOfImagesPerPlaneVariable = new DoubleVariable("NumberOfImagesPerPlane",
																																								1);

	protected volatile int mQueueLength = 0;


	protected ObjectVariable<StackInterface<T, A>> mStackReference;

	public StackCameraDeviceBase(String pDeviceName)
	{
		super(pDeviceName);
	}

	@Override
	public BooleanVariable getStackModeVariable()
	{
		return mStackModeVariable;
	}

	@Override
	public BooleanVariable getSingleShotModeVariable()
	{
		return mSingleShotModeVariable;
	}

	@Override
	public DoubleVariable getNumberOfImagesPerPlaneVariable()
	{
		return mNumberOfImagesPerPlaneVariable;
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
	}

	@Override
	public void addCurrentStateToQueueNotCounting()
	{

	}

	@Override
	public void addCurrentStateToQueue()
	{
		mQueueLength++;
	}

	@Override
	public int getQueueLength()
	{
		return mQueueLength;
	}

	@Override
	public abstract Future<Boolean> playQueue();


}