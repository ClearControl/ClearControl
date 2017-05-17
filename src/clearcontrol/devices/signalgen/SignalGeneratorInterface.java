package clearcontrol.devices.signalgen;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.queue.QueueDeviceInterface;
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
                                          QueueDeviceInterface<SignalGeneratorQueue>
{

  /**
   * Returns temporal granularity in microseconds.
   * 
   * @return temporal granularity in microseconds.
   */
  public double getTemporalGranularityInMicroseconds();

  /**
   * Prepends a transition movement to the given score based on the last played
   * score. This prevents sudden jumps in the signal
   * 
   * @param pScore
   *          score to prepend transition movement to
   * @param pDuration
   *          duration
   * @param pTimeUnit
   *          time unit
   */
  void prependTransitionMovement(ScoreInterface pScore,
                                 long pDuration,
                                 TimeUnit pTimeUnit);

  /**
   * Play score
   * 
   * @param pScore
   *          score to play
   * @return true if played successfully
   */
  public boolean playScore(ScoreInterface pScore);

  /**
   * Returns the variable that holds the last played score. Listening to this
   * variable is a convenient way to be notified of played scores.
   * 
   * @return played score variable
   */
  Variable<ScoreInterface> getLastPlayedScoreVariable();

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


}
