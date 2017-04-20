package clearcontrol.devices.signalgen;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.devices.signalgen.movement.MovementInterface;
import clearcontrol.devices.signalgen.score.Score;
import clearcontrol.devices.signalgen.score.ScoreInterface;

/**
 * Real time queue for signal generator devices
 *
 * @author royer
 */
public class SignalGeneratorRealTimeQueue implements QueueInterface
{
  protected volatile int mEnqueuedStateCounter = 0;
  protected final ScoreInterface mQueuedScore;
  protected final ScoreInterface mStagingScore;

  /**
   * Instanciates a real-time signal generator queue
   * 
   */
  public SignalGeneratorRealTimeQueue()
  {
    super();
    mQueuedScore = new Score("queuedscore");
    mStagingScore = new Score("stagingscore");
  }

  /**
   * Returns staging score
   * 
   * @return staging score
   */
  public ScoreInterface getStagingScore()
  {
    return mStagingScore;
  }

  /**
   * Returns queued score
   * 
   * @return queeud score
   */
  public ScoreInterface getQueuedScore()
  {
    return mQueuedScore;
  }

  /**
   * Estimates the play time in the given time unit.
   * 
   * @param pTimeUnit
   *          time unit
   * @return play time estimate
   */
  public long estimatePlayTime(TimeUnit pTimeUnit)
  {
    long lDuration = 0;
    for (final MovementInterface lMovement : mQueuedScore.getMovements())
    {
      lDuration += lMovement.getDuration(pTimeUnit);
    }
    lDuration *= mQueuedScore.getNumberOfMovements();
    return lDuration;
  }

  @Override
  public void clearQueue()
  {
    mEnqueuedStateCounter = 0;
    mQueuedScore.clear();
  }

  @Override
  public void addCurrentStateToQueue()
  {
    mQueuedScore.addScoreCopy(mStagingScore);
    mEnqueuedStateCounter++;
  }

  @Override
  public void finalizeQueue()
  {

  }

  @Override
  public int getQueueLength()
  {
    return mEnqueuedStateCounter;
  }

}
