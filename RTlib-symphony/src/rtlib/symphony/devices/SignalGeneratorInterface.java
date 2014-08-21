package rtlib.symphony.devices;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.symphony.score.CompiledScore;

public interface SignalGeneratorInterface extends VirtualDeviceInterface
{
	boolean play(CompiledScore pCompiledScore);
	double getTemporalGranularityInMicroseconds();
	BooleanVariable getTriggerVariable();
}
