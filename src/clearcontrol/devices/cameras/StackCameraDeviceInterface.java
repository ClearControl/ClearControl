package clearcontrol.devices.cameras;

import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.queue.RealTimeQueueDeviceInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 * Interface implemented by all stack cameras. Stack cameras are cameras that in
 * addition to be able to acquire single images can acquire a sequence of images
 * as a stack.
 *
 * @author royer
 */
public interface StackCameraDeviceInterface extends
                                            CameraDeviceInterface,
                                            RealTimeQueueDeviceInterface<StackCameraRealTimeQueue>,
                                            NameableInterface
{

  @Override
  void trigger();

  /**
   * Returns the current tack index
   * 
   * @return current stack index
   */
  long getCurrentStackIndex();

  /**
   * Sets the recycler to be used by this stack camera
   * 
   * @param pStackRecycler
   *          recycler.
   */
  void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pStackRecycler);

  /**
   * Returns the minimal numer of available stacks
   * 
   * @return minimal number of available stacks
   */
  int getMinimalNumberOfAvailableStacks();

  /**
   * Sets the minimal number of available stacks
   * 
   * @param pMinimalNumberOfAvailableStacks
   *          minimal number of available stacks
   */
  void setMinimalNumberOfAvailableStacks(int pMinimalNumberOfAvailableStacks);

  /**
   * Returns this camera's stack recycler.
   * 
   * @return stack recycler
   */
  RecyclerInterface<StackInterface, StackRequest> getStackRecycler();

  /**
   * Returns the variable that will receive the stacks.
   * 
   * @return stack variable
   */
  Variable<StackInterface> getStackVariable();

  /**
   * Returns the variable holding the number of images per plane. It is possible
   * to have multiple images for a single plane, this is usefull for exampe for
   * structured illumination or other similar acquiistion schemes.
   * 
   * @return number of images per plane
   */
  Variable<Long> getNumberOfImagesPerPlaneVariable();

  /**
   * Returns the variable holding the flag indicating whether this stack camera
   * is in stack mode versus single image mode.
   * 
   * @return stack mode variable
   */
  Variable<Boolean> getStackModeVariable();

  /**
   * Returns the variable holding the number of bytes per pixel/voxel.
   * 
   * @return bytes per pixel/voxel variable
   */
  Variable<Long> getStackBytesPerPixelVariable();

  /**
   * Returns the variable that holds the stack width
   * 
   * @return stack width variable
   */
  Variable<Long> getStackWidthVariable();

  /**
   * Returns the variable that holds the stack height
   * 
   * @return stack height variable
   */
  Variable<Long> getStackHeightVariable();

  /**
   * Returns the variable that holds the stack depth
   * 
   * @return stack depth variable
   */
  Variable<Long> getStackDepthVariable();

  /**
   * Returns the variable that holds the stack maximal width (limited by the
   * cameras hardware)
   * 
   * @return stack max width variable
   */
  Variable<Long> getStackMaxWidthVariable();

  /**
   * Returns the variable that holds the stack maximal height (limited by the
   * cameras hardware)
   * 
   * @return stack max height variable
   */
  Variable<Long> getStackMaxHeightVariable();

}
