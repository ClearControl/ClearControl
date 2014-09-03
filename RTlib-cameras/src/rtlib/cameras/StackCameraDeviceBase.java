package rtlib.cameras;

import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.Stack;

public abstract class StackCameraDeviceBase<I, O> extends
																									CameraDeviceBase implements
																																	StackCameraDeviceInterface<I, O>
{

	protected BooleanVariable mStackModeVariable = new BooleanVariable(	"StackMode",
																																			false);
	protected BooleanVariable mSingleShotModeVariable = new BooleanVariable("SingleShotMode",
																																					false);

	protected ObjectVariable<Stack<O>> mStackReference;

	public StackCameraDeviceBase(String pDeviceName)
	{
		super(pDeviceName);
	}

	public BooleanVariable getStackModeVariable()
	{
		return mStackModeVariable;
	}

	public BooleanVariable getSingleShotModeVariable()
	{
		return mSingleShotModeVariable;
	}

	public ObjectVariable<Stack<O>> getStackReferenceVariable()
	{
		return mStackReference;
	}

	public void trigger()
	{

	}
	


}