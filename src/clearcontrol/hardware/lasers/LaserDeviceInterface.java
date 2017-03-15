package clearcontrol.hardware.lasers;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.startstop.StartStopDeviceInterface;

public interface LaserDeviceInterface extends
                                      NameableInterface,
                                      OpenCloseDeviceInterface,
                                      StartStopDeviceInterface
{

  public int getWavelengthInNanoMeter();

  public void setTargetPowerInMilliWatt(double pTargetPowerinMilliWatt);

  public void setTargetPowerInPercent(double pTargetPowerInPercent);

  public double getTargetPowerInMilliWatt();

  public double getMaxPowerInMilliWatt();

  public double getCurrentPowerInMilliWatt();

  public Variable<Integer> getWavelengthInNanoMeterVariable();

  public Variable<Boolean> getLaserOnVariable();

  public Variable<Number> getTargetPowerInMilliWattVariable();

  public Variable<Number> getCurrentPowerInMilliWattVariable();

}
