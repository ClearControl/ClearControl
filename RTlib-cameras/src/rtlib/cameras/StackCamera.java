package rtlib.cameras;

import java.util.concurrent.atomic.AtomicBoolean;

import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.Stack;

public abstract class StackCamera extends CameraDevice
{

	private AtomicBoolean mReOpenDeviceNeeded = new AtomicBoolean(false);

	protected BooleanVariable mStackModeVariable;
	protected BooleanVariable mSingleShotModeVariable;

	protected ObjectVariable<Stack<Short>> mStackReference;

	public StackCamera(String pDeviceName)
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

	public BooleanVariable getStackModeVariable()
	{
		return mStackModeVariable;
	}

	public BooleanVariable getSingleShotModeVariable()
	{
		return mSingleShotModeVariable;
	}

	public ObjectVariable<Stack<Short>> getStackReferenceVariable()
	{
		return mStackReference;
	}

}