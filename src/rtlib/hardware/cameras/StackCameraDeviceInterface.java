package rtlib.hardware.cameras;

import coremem.recycling.RecyclerInterface;
import rtlib.core.variable.Variable;
import rtlib.device.name.NameableInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;

public interface StackCameraDeviceInterface	extends
																						CameraDeviceInterface,
																						NameableInterface
{
	Variable<Long> getNumberOfImagesPerPlaneVariable();

	Variable<Boolean> getStackModeVariable();

	Variable<Boolean> getKeepPlaneVariable();

	Variable<Long> getStackBytesPerPixelVariable();

	Variable<Long> getStackWidthVariable();

	Variable<Long> getStackHeightVariable();

	Variable<Long> getStackDepthVariable();

	Variable<Long> getStackMaxWidthVariable();

	Variable<Long> getStackMaxHeightVariable();

	void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler);

	int getMinimalNumberOfAvailableStacks();

	void setMinimalNumberOfAvailableStacks(int pMinimalNumberOfAvailableStacks);

	RecyclerInterface<StackInterface, StackRequest> getStackRecycler();

	Variable<StackInterface> getStackVariable();

	@Override
	void trigger();

}
