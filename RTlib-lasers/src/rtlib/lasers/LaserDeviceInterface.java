package rtlib.lasers;

import rtlib.core.device.NameableInterface;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;

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

	public BooleanVariable getLaserOnVariable();

	public DoubleVariable getTargetPowerInMilliWattVariable();

	public DoubleVariable getCurrentPowerInMilliWattVariable();

	public DoubleVariable getWavelengthInNanoMeterVariable();

}
