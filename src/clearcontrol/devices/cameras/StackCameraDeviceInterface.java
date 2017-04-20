package clearcontrol.devices.cameras;

import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.queue.QueueDeviceInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 * Interface implemented by all stack cameras. Stack cameras are cameras that in
 * addition to be able to acquire single images can acquire a sequence of images
 * as a stack.
 * 
 * @param <Q>
 *          queue type
 * @author royer
 * 
 */
public interface StackCameraDeviceInterface<Q extends StackCameraQueue<Q>>
                                           extends
                                           CameraDeviceInterface,
                                           QueueDeviceInterface<Q>,
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
  // int getMinimalNumberOfAvailableStacks();

  /**
   * Sets the minimal number of available stacks
   * 
   * @param pMinimalNumberOfAvailableStacks
   *          minimal number of available stacks
   */
  // void setMinimalNumberOfAvailableStacks(int
  // pMinimalNumberOfAvailableStacks);

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
   * Returns the variable holding the flag indicating whether this stack camera
   * is in stack mode versus single image mode.
   * 
   * @return stack mode variable
   */
  Variable<Boolean> getStackModeVariable();

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

}
