package rtlib.lasers;

import rtlib.core.device.NameableInterface;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.ObjectVariable;

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

	public ObjectVariable<Integer> getWavelengthInNanoMeterVariable();

	public ObjectVariable<Boolean> getLaserOnVariable();

	public ObjectVariable<Number> getTargetPowerInMilliWattVariable();

	public ObjectVariable<Number> getCurrentPowerInMilliWattVariable();

}
