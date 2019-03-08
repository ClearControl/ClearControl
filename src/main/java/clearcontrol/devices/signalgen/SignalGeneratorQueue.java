package clearcontrol.devices.signalgen;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.queue.QueueInterface;
import clearcontrol.devices.signalgen.measure.MeasureInterface;
import clearcontrol.devices.signalgen.score.Score;
import clearcontrol.devices.signalgen.score.ScoreInterface;

/**
 * Real time queue for signal generator devices
 *
 * @author royer
 */
public class SignalGeneratorQueue implements QueueInterface
{
  private final SignalGeneratorInterface mSignalGenerator;

  protected volatile int mEnqueuedStateCounter = 0;
  protected final ScoreInterface mQueuedScore;
  protected final ScoreInterface mStagingScore;
  protected final ScoreInterface mFinalizationScore;

  /**
   * Instantiates a real-time signal generator queue
   * 
   * @param pSignalGenerator
   *          parent signal generator
   * 
   */
  public SignalGeneratorQueue(SignalGeneratorInterface pSignalGenerator)
  {
    super();
    mSignalGenerator = pSignalGenerator;
    mQueuedScore = new Score("queuedscore");
    mStagingScore = new Score("stagingscore");
    mFinalizationScore = new Score("finalizationscore");
  }

  /**
   * Returns this queue's parent signal generator
   * 
   * @return parent signal generator
   */
  public SignalGeneratorInterface getSignalGenerator()
  {
    return mSignalGenerator;
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
   * Returns finalisation score
   * 
   * @return finalisation score
   */
  public ScoreInterface getFinalizationScore()
  {
    return mFinalizationScore;
  }

  /**
   * Returns queued score
   * 
   * @return queued score
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
    for (final MeasureInterface lMeasure : mQueuedScore.getMeasures())
    {
      lDuration += lMeasure.getDuration(pTimeUnit);
    }
    lDuration *= mQueuedScore.getNumberOfMeasures();
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
    mQueuedScore.addScoreCopy(mFinalizationScore);
  }

  @Override
  public int getQueueLength()
  {
    return mEnqueuedStateCounter;
  }

}
