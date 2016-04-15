package rtlib.hardware.lasers;

import rtlib.core.device.NameableInterface;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.Variable;

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
