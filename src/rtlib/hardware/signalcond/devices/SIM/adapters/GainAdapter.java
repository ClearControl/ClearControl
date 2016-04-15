package rtlib.hardware.signalcond.devices.SIM.adapters;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.signum;

import rtlib.hardware.signalcond.devices.SIM.SIM900MainframeDevice;
import rtlib.hardware.signalcond.devices.SIM.adapters.protocol.ProtocolSIM;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GainAdapter extends SIMAdapter	implements
																						SerialTextDeviceAdapter<Double>
{

	public GainAdapter(	SIM900MainframeDevice pSim900MainframeDevice,
											int pPort)
	{
		super(pSim900MainframeDevice, pPort, ProtocolSIM.cGain);
	}

	@Override
	public Double clampSetValue(Double pValue)
	{
		double lSign = signum(pValue);
		double lAbs = abs(pValue);
		lAbs = min(max(lAbs, 0.01), 19.99);
		return lSign * lAbs;
	}

}
