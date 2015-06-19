package rtlib.signalcond.devices.SIM.adapters;

import rtlib.signalcond.devices.SIM.SIM900MainframeDevice;
import rtlib.signalcond.devices.SIM.adapters.protocol.ProtocolSIM;

public class OffsetAdapter extends SIMAdapter
{

	public OffsetAdapter(	SIM900MainframeDevice pSim900MainframeDevice,
												int pPort)
	{
		super(pSim900MainframeDevice, pPort, ProtocolSIM.cGain);
	}

}
