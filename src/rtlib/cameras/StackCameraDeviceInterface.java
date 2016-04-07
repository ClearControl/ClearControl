package rtlib.cameras;

import rtlib.core.device.NameableInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

public interface StackCameraDeviceInterface	extends
																						CameraDeviceInterface,
																						NameableInterface
{
	DoubleVariable getNumberOfImagesPerPlaneVariable();

	BooleanVariable getStackModeVariable();

	BooleanVariable getKeepPlaneVariable();

	DoubleVariable getStackBytesPerPixelVariable();

	DoubleVariable getStackWidthVariable();

	DoubleVariable getStackHeightVariable();

	DoubleVariable getStackDepthVariable();

	void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler);

	int getMinimalNumberOfAvailableStacks();

	void setMinimalNumberOfAvailableStacks(int pMinimalNumberOfAvailableStacks);

	RecyclerInterface<StackInterface, StackRequest> getStackRecycler();

	ObjectVariable<StackInterface> getStackVariable();

	@Override
	void trigger();

}
