package rtlib.symphony.devices;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.symphony.score.CompiledScore;
import rtlib.symphony.score.ScoreInterface;

public interface SignalGeneratorInterface	extends
																					VirtualDeviceInterface,
																					StateQueueDeviceInterface
{

	public double getTemporalGranularityInMicroseconds();

	public boolean playScore(CompiledScore pCompiledScore);

	public ScoreInterface getStagingScore();

	public BooleanVariable getTriggerVariable();

	public boolean isPlaying();

	public long estimatePlayTime(TimeUnit pTimeUnit);

}
