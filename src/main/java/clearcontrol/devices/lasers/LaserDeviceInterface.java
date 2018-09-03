package clearcontrol.devices.lasers;

import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.startstop.StartStopDeviceInterface;
import clearcontrol.core.variable.Variable;

/**
 * Interface implemented by all laser devices.
 *
 * @author royer
 */
public interface LaserDeviceInterface extends
                                      NameableInterface,
                                      OpenCloseDeviceInterface,
                                      StartStopDeviceInterface
{

  // getters for variables:

  /**
   * Returns the variable holding the device id
   * 
   * @return device id variable
   */
  public Variable<Integer> getDeviceIdVariable();

  /**
   * Returns wavelength in nanometer variable
   * 
   * @return wavelength variable
   */
  public Variable<Integer> getWavelengthInNanoMeterVariable();

  /**
   * Returns the variable holding the
   * 
   * @return power on variable
   */
  public Variable<Boolean> getPowerOnVariable();

  /**
   * Returns the variable holding the
   * 
   * @return laser on variable
   */
  public Variable<Boolean> getLaserOnVariable();

  /**
   * Returns the variable holding the
   * 
   * @return target power variable
   */
  public Variable<Number> getTargetPowerInMilliWattVariable();

  /**
   * Returns the variable holding the
   * 
   * @return current power variable
   */
  public Variable<Number> getCurrentPowerInMilliWattVariable();

  /**
   * Returns the variable holding the
   * 
   * @return working hours variable
   */
  public Variable<Integer> getWorkingHoursVariable();

  /**
   * Returns the variable holding the
   * 
   * @return operating mode variable
   */
  public Variable<Integer> getOperatingModeVariable();

  /**
   * Returns the variable holding the
   * 
   * @return max power variable
   */
  public Variable<Number> getMaxPowerVariable();

  /**
   * Returns the variable holding the
   * 
   * @return spec power variable
   */
  public Variable<Number> getSpecPowerVariable();

  // Convenience methods for accessing variables values (read or write):

  /**
   * Returns laser device id
   * 
   * @return device id
   */
  public int getDeviceId();

  /**
   * Returns wavelength in nanometer
   * 
   * @return wavelength in nanometer
   */
  public int getWavelengthInNanoMeter();

  /**
   * Returns spec power in milliwatt
   * 
   * @return spec power
   */
  public double getSpecPowerInMilliWatt();

  /**
   * Returns max power in milliwatt
   * 
   * @return max power in milliwatt
   */
  public double getMaxPowerInMilliWatt();

  /**
   * Sets operating mode
   * 
   * @param pMode
   *          operating mode
   */
  public void setOperatingMode(int pMode);

  /**
   * Sets power on/off state. On some lasers there is a difference between
   * 'powering' the laser, and turnng on the laser - the first refers to the
   * actual power supply while the second refers to actually turning on the
   * laser itself.
   * 
   * @param pState
   *          power on/off state
   */
  public void setLaserPowerOn(boolean pState);

  /**
   * Sets the laser on or off.
   * 
   * @param pState
   *          on/off state
   */
  public void setLaserOn(boolean pState);

  /**
   * Returns the number of working hours.
   * 
   * @return working hours.
   */
  public int getWorkingHours();

  /**
   * Sets the target laser power in milliwatt
   * 
   * @param pTargetPowerinMilliWatt
   *          target power in milliwatt
   */
  public void setTargetPowerInMilliWatt(double pTargetPowerinMilliWatt);

  /**
   * Sets teh target power in percent
   * 
   * @param pTargetPowerInPercent
   *          target power in percent
   */
  public void setTargetPowerInPercent(double pTargetPowerInPercent);

  /**
   * Returns the target power in milliwatt
   * 
   * @return target power in milliwatt
   */
  public double getTargetPowerInMilliWatt();

  /**
   * Returns the target power in percent
   * 
   * @return target power in percent
   */
  public double getTargetPowerInPercent();

  /**
   * Returns the current laser power in milliwatts
   * 
   * @return current power in milliwatts
   */
  public double getCurrentPowerInMilliWatt();

  /**
   * Returns the current laser power in percent of the max power.
   * 
   * @return current power in percent
   */
  public double getCurrentPowerInPercent();

}
