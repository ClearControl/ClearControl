package rtlib.signalcond.devices.simulator;

import rtlib.signalcond.ScalingAmplifierBaseDevice;
import rtlib.signalcond.ScalingAmplifierDeviceInterface;

public class ScalingAmplifierSimulator extends
																			ScalingAmplifierBaseDevice implements
																													ScalingAmplifierDeviceInterface
{

	public ScalingAmplifierSimulator(String pDeviceName)
	{
		super(pDeviceName);
	}

}
