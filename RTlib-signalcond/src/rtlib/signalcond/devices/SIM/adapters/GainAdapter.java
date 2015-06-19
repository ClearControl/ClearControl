package rtlib.signalcond.devices.SIM.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.signalcond.devices.SIM.SIM900MainframeDevice;
import rtlib.signalcond.devices.SIM.adapters.protocol.ProtocolSIM;

public class GainAdapter extends SIMAdapter	implements
																						SerialTextDeviceAdapter
{

	public GainAdapter(	SIM900MainframeDevice pSim900MainframeDevice,
											int pPort)
	{
		super(pSim900MainframeDevice, pPort, ProtocolSIM.cGain);
	}

}
