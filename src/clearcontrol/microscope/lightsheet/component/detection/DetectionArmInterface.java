package clearcontrol.microscope.lightsheet.component.detection;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.device.change.HasChangeListenerInterface;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

/**
 * Interface for Detection arms.
 * 
 * @author royer
 */
public interface DetectionArmInterface extends
                                       NameableInterface,
                                       OpenCloseDeviceInterface,
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

}
