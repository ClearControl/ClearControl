package rtlib.symphony.devices;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.symphony.interfaces.ScoreInterface;
import rtlib.symphony.score.CompiledScore;

public interface SignalGeneratorInterface	extends
																					VirtualDeviceInterface,
																					StateQueueDeviceInterface
{

	double getTemporalGranularityInMicroseconds();

	boolean playScore(CompiledScore pCompiledScore);
	ScoreInterface getStagingScore();
	BooleanVariable getTriggerVariable();
	boolean isPlaying();

}
