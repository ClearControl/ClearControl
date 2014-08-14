package rtlib.symphony.devices;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.symphony.score.CompiledScore;

public interface SignalGenerator extends VirtualDeviceInterface
{
	boolean play(CompiledScore pCompiledScore);
	double getTemporalGranularityInMicroseconds();
}
