package clearcontrol.core.device.active;

/**
 * Devices that implement this interface can be activated/deactivated
 *
 * @author royer
 */
public interface ActivableDeviceInterface
{
  /**
   * Returns true if device is active
   * 
   * @return true if active
   */
  public boolean isActive();

  /**
   * Sets the is-active flag for this device
   * 
   * @param pIsActive
   *          new value for flag
   */
  public void setActive(boolean pIsActive);
}
