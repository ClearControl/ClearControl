package clearcontrol.hardware.cameras;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

public interface StackCameraDeviceInterface extends
                                            CameraDeviceInterface,
                                            NameableInterface, LoggingInterface
{

  void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler);

  int getMinimalNumberOfAvailableStacks();

  void setMinimalNumberOfAvailableStacks(int pMinimalNumberOfAvailableStacks);

  RecyclerInterface<StackInterface, StackRequest> getStackRecycler();

  Variable<StackInterface> getStackVariable();

  @Override
  void trigger();

  Variable<Long> getNumberOfImagesPerPlaneVariable();

  Variable<Boolean> getStackModeVariable();

  Variable<Boolean> getKeepPlaneVariable();

  Variable<Long> getStackBytesPerPixelVariable();

  Variable<Long> getStackWidthVariable();

  Variable<Long> getStackHeightVariable();

  Variable<Long> getStackDepthVariable();

  Variable<Long> getStackMaxWidthVariable();

  Variable<Long> getStackMaxHeightVariable();

}
