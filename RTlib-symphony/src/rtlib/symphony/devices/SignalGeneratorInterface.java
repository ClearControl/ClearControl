package rtlib.symphony.devices;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.symphony.score.ScoreInterface;

public interface SignalGeneratorInterface	extends
																					OpenCloseDeviceInterface,
																					StateQueueDeviceInterface
{

	public double getTemporalGranularityInMicroseconds();

	public boolean playScore(ScoreInterface pScore);

	public ScoreInterface getStagingScore();

	public ScoreInterface getQueuedScore();

	public BooleanVariable getTriggerVariable();

	public boolean isPlaying();

	public long estimatePlayTime(TimeUnit pTimeUnit);

}
