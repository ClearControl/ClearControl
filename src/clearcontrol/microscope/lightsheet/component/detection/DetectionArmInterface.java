package clearcontrol.microscope.lightsheet.component.detection;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.queue.QueueDeviceInterface;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;

/**
 * Interface for Detection arms.
 * 
 * @author royer
 */
public interface DetectionArmInterface extends
                                       NameableInterface,
                                       OpenCloseDeviceInterface,
                                       QueueDeviceInterface<DetectionArmQueue>,
                                       HasChangeListenerInterface<VirtualDevice>
{
  /**
   * Resets functions
   */
  public void resetFunctions();

  /**
   * Resets variable bounds
   */
  public void resetBounds();

  /**
   * Returns the detection plane Z position variable
   * 
   * @return Z variable
   */
  public BoundedVariable<Number> getZVariable();

  /**
   * Returns function that translates Z values into actual control values.
   * 
   * @return Z function
   */
  public Variable<UnivariateAffineFunction> getZFunction();

  /**
   * Returns the variable holding the pixel size in micrometers.
   * 
   * @return variable holding the pixel size in micrometers.
   */
  public Variable<Double> getPixelSizeInMicrometerVariable();

}
