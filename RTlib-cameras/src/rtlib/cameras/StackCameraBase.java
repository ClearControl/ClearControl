package rtlib.cameras;

import java.util.concurrent.atomic.AtomicBoolean;

import rtlib.core.device.CameraDevice;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.Stack;

public abstract class StackCameraBase extends CameraDevice
{

	private AtomicBoolean mReOpenDeviceNeeded = new AtomicBoolean(false);

	protected DoubleVariable mFrameBytesPerPixelVariable;
	protected DoubleVariable mFrameWidthVariable;
	protected DoubleVariable mFrameHeightVariable;
	protected DoubleVariable mFrameDepthVariable;

	protected DoubleVariable mExposureInMicroseconds;

	protected DoubleVariable mPixelSizeinNanometers;

	protected BooleanVariable mIsAcquiring;
	protected BooleanVariable mStackModeVariable;
	protected BooleanVariable mSingleShotModeVariable;

	protected ObjectVariable<Stack> mStackReference;

	public StackCameraBase(String pDeviceName)
	{
		super(pDeviceName);
	}

	public boolean isReOpenDeviceNeeded()
	{
		return mReOpenDeviceNeeded.get();
	}

	public void requestReOpen()
	{
		mReOpenDeviceNeeded.set(true);
	}

	public void clearReOpen()
	{
		mReOpenDeviceNeeded.set(false);
	}

	public DoubleVariable getFrameBytesPerPixelVariable()
	{
		return mFrameBytesPerPixelVariable;
	}

	public DoubleVariable getFrameWidthVariable()
	{
		return mFrameWidthVariable;
	}

	public DoubleVariable getFrameHeightVariable()
	{
		return mFrameHeightVariable;
	}

	public DoubleVariable getFrameDepthVariable()
	{
		return mFrameDepthVariable;
	}

	public DoubleVariable getExposureVariable()
	{
		return mExposureInMicroseconds;
	}

	public DoubleVariable getPixelSizeInNanometersVariable()
	{
		return mPixelSizeinNanometers;
	}

	public BooleanVariable getIsAcquiringVariable()
	{
		return mIsAcquiring;
	}

	public BooleanVariable getStackModeVariable()
	{
		return mStackModeVariable;
	}

	public BooleanVariable getSingleShotModeVariable()
	{
		return mSingleShotModeVariable;
	}

	public ObjectVariable<Stack> getStackReferenceVariable()
	{
		return mStackReference;
	}

}