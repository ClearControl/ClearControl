package rtlib.symphony.devices;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.ObjectVariable;
import rtlib.symphony.score.ScoreInterface;

public interface SignalGeneratorInterface	extends
																					OpenCloseDeviceInterface,
																					StateQueueDeviceInterface
{

	public double getTemporalGranularityInMicroseconds();

	public boolean playScore(ScoreInterface pScore);

	public ScoreInterface getStagingScore();

	public ScoreInterface getQueuedScore();

	public ObjectVariable<Boolean> getTriggerVariable();

	public boolean isPlaying();

	public long estimatePlayTime(TimeUnit pTimeUnit);

}
