package rtlib.hardware.signalcond.devices.simulator;

import rtlib.hardware.signalcond.ScalingAmplifierBaseDevice;
import rtlib.hardware.signalcond.ScalingAmplifierDeviceInterface;

public class ScalingAmplifierSimulator extends
																			ScalingAmplifierBaseDevice implements
																																ScalingAmplifierDeviceInterface
{

	public ScalingAmplifierSimulator(String pDeviceName)
	{
		super(pDeviceName);
	}

}
