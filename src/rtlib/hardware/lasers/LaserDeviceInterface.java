package rtlib.hardware.lasers;

import rtlib.core.variable.Variable;
import rtlib.device.name.NameableInterface;
import rtlib.device.openclose.OpenCloseDeviceInterface;
import rtlib.device.startstop.StartStopDeviceInterface;

public interface LaserDeviceInterface	extends
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
