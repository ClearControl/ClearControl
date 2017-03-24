package clearcontrol.devices.signalgen;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.queue.StateQueueDeviceInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.signalgen.score.ScoreInterface;

/**
 * Interface implemented by all signal generation devices.
 *
 * @author royer
 */
public interface SignalGeneratorInterface extends
                                          NameableInterface,
                                          OpenCloseDeviceInterface,
                                          StateQueueDeviceInterface
{

  /**
   * Returns temporal granularity in microseconds.
   * 
   * @return temporal granularity in microseconds.
   */
  public double getTemporalGranularityInMicroseconds();

  /**
   * Play score
   * 
   * @param pScore
   *          score
   * @return true if played successfully
   */
  public boolean playScore(ScoreInterface pScore);

  /**
   * Returns staging score
   * 
   * @return staging score
   */
  public ScoreInterface getStagingScore();

  /**
   * Returns queued score
   * 
   * @return queeud score
   */
  public ScoreInterface getQueuedScore();

  /**
   * Returns trigger variable
   * 
   * @return trigger variable
   */
  public Variable<Boolean> getTriggerVariable();

  /**
   * Returns true if the device is playing.
   * 
   * @return true if playing
   */
  public boolean isPlaying();

  /**
   * Estimates the play time in the given time unit.
   * 
   * @param pTimeUnit
   *          time unit
   * @return play time estimate
   */
  public long estimatePlayTime(TimeUnit pTimeUnit);

}
