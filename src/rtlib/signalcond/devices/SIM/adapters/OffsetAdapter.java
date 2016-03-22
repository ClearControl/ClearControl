package rtlib.signalcond.devices.SIM.adapters;

import static java.lang.Math.max;
import static java.lang.Math.min;
import rtlib.signalcond.devices.SIM.SIM900MainframeDevice;
import rtlib.signalcond.devices.SIM.adapters.protocol.ProtocolSIM;

public class OffsetAdapter extends SIMAdapter
{

	public OffsetAdapter(	SIM900MainframeDevice pSim900MainframeDevice,
							int pPort)
	{
		super(pSim900MainframeDevice, pPort, ProtocolSIM.cOffset);
	}

	@Override
	public double clampSetValue(double pValue)
	{
		return min(max(pValue, -10.00), 10.00);
	}

}
