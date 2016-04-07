package rtlib.cameras;

import rtlib.core.device.NameableInterface;
import rtlib.core.variable.ObjectVariable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

public interface StackCameraDeviceInterface	extends
																						CameraDeviceInterface,
																						NameableInterface
{
	ObjectVariable<Long> getNumberOfImagesPerPlaneVariable();

	ObjectVariable<Boolean> getStackModeVariable();

	ObjectVariable<Boolean> getKeepPlaneVariable();

	ObjectVariable<Long> getStackBytesPerPixelVariable();

	ObjectVariable<Long> getStackWidthVariable();

	ObjectVariable<Long> getStackHeightVariable();

	ObjectVariable<Long> getStackDepthVariable();

	void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler);

	int getMinimalNumberOfAvailableStacks();

	void setMinimalNumberOfAvailableStacks(int pMinimalNumberOfAvailableStacks);

	RecyclerInterface<StackInterface, StackRequest> getStackRecycler();

	ObjectVariable<StackInterface> getStackVariable();

	@Override
	void trigger();

}
