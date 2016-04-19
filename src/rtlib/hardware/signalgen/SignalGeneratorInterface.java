package rtlib.hardware.signalgen;

import java.util.concurrent.TimeUnit;

import rtlib.core.variable.Variable;
import rtlib.device.name.NameableInterface;
import rtlib.device.openclose.OpenCloseDeviceInterface;
import rtlib.device.queue.StateQueueDeviceInterface;
import rtlib.hardware.signalgen.score.ScoreInterface;

public interface SignalGeneratorInterface	extends
																					NameableInterface,
																					OpenCloseDeviceInterface,
																					StateQueueDeviceInterface
{

	public double getTemporalGranularityInMicroseconds();

	public boolean playScore(ScoreInterface pScore);

	public ScoreInterface getStagingScore();

	public ScoreInterface getQueuedScore();

	public Variable<Boolean> getTriggerVariable();

	public boolean isPlaying();

	public long estimatePlayTime(TimeUnit pTimeUnit);

}
