package rtlib.cameras;

import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.Stack;

public interface StackCameraDeviceInterface<I, O> extends
																						CameraDeviceInterface
{
	BooleanVariable getStackModeVariable();

	BooleanVariable getSingleShotModeVariable();

	ObjectVariable<Stack<O>> getStackReferenceVariable();

	void trigger();



}
