package rtlib.hardware.cameras;

import rtlib.core.device.NameableInterface;
import rtlib.core.variable.Variable;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

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
