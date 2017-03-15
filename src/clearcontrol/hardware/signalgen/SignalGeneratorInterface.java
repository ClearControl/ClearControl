package clearcontrol.hardware.signalgen;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.queue.StateQueueDeviceInterface;
import clearcontrol.hardware.signalgen.score.ScoreInterface;

public interface SignalGeneratorInterface extends
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
