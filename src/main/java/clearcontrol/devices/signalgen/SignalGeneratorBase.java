package clearcontrol.devices.signalgen;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.signalgen.measure.MeasureInterface;
import clearcontrol.devices.signalgen.measure.TransitionMeasure;
import clearcontrol.devices.signalgen.score.ScoreInterface;

/**
 *
 *
 * @author royer
 */
public abstract class SignalGeneratorBase extends VirtualDevice
                                          implements
                                          SignalGeneratorInterface,
                                          AsynchronousExecutorFeature
{

  protected final Variable<Boolean> mTriggerVariable =
                                                     new Variable<Boolean>("Trigger",
                                                                           false);

  private final Variable<Long> mTransitionDurationInNanosecondsVariable =
                                                                        new Variable<>("TransitionTimeInNanoseconds",
                                                                                       0L);

  private final Variable<ScoreInterface> mLastPlayedScoreVariable =
                                                                  new Variable<>("PlayedScore",
                                                                                 null);

  protected volatile boolean mIsPlaying;

  /**
   * Instantiates a signal generator.
   * 
   * @param pDeviceName
   *          signal generator name
   */
  public SignalGeneratorBase(String pDeviceName)
  {
    super(pDeviceName);
  }

  @Override
  public Variable<Boolean> getTriggerVariable()
  {
    return mTriggerVariable;
  }

  @Override
  public SignalGeneratorQueue requestQueue()
  {
    SignalGeneratorQueue lQueue = new SignalGeneratorQueue(this);
    return lQueue;
  }

  protected void prependTransitionMeasure(ScoreInterface pScore,
                                           long pDuration,
                                           TimeUnit pTimeUnit)
  {
    if (getLastPlayedScoreVariable().get() == null || pDuration == 0)
      return;

    MeasureInterface lFirstMeasureOfGivenScore =
                                                 pScore.getMeasure(0);

    MeasureInterface lLastMeasureFromPreviouslyPlayedScore =
                                                             getLastPlayedScoreVariable().get()
                                                                                         .getLastMeasure();
    if (lFirstMeasureOfGivenScore.getName()
                                  .equals("TransitionMeasure"))
    {
      TransitionMeasure.adjust(lFirstMeasureOfGivenScore,
                                lLastMeasureFromPreviouslyPlayedScore,
                                pScore.getMeasure(1),
                                pDuration,
                                pTimeUnit);

    }
    else
    {

      MeasureInterface lTransitionMeasure =
                                            TransitionMeasure.make(lLastMeasureFromPreviouslyPlayedScore,
                                                                    lFirstMeasureOfGivenScore,
                                                                    pDuration,
                                                                    pTimeUnit);

      pScore.insertMeasureAt(0, lTransitionMeasure);
    }
  }

  @Override
  public Future<Boolean> playQueue(SignalGeneratorQueue pSignalGeneratorRealTimeQueue)
  {
    final Callable<Boolean> lCall = () -> {
      final Thread lCurrentThread = Thread.currentThread();
      final int lCurrentThreadPriority = lCurrentThread.getPriority();
      lCurrentThread.setPriority(Thread.MAX_PRIORITY);
      mIsPlaying = true;
      final boolean lPlayed =
                            playScore(pSignalGeneratorRealTimeQueue.getQueuedScore());
      mIsPlaying = false;
      lCurrentThread.setPriority(lCurrentThreadPriority);
      return lPlayed;
    };
    final Future<Boolean> lFuture = executeAsynchronously(lCall);
    return lFuture;
  }

  @Override
  public boolean playScore(ScoreInterface pScore)
  {
    mLastPlayedScoreVariable.set(pScore.duplicate());
    return true;
  }

  @Override
  public boolean isPlaying()
  {
    return mIsPlaying;
  }

  @Override
  public Variable<ScoreInterface> getLastPlayedScoreVariable()
  {
    return mLastPlayedScoreVariable;
  }

  @Override
  public Variable<Long> getTransitionDurationInNanosecondsVariable()
  {
    return mTransitionDurationInNanosecondsVariable;
  }

  @Override
  public void setTransitionDuration(long pDuration,
                                    TimeUnit pTimeUnit)
  {
    getTransitionDurationInNanosecondsVariable().set(TimeUnit.NANOSECONDS.convert(pDuration,
                                                                                  pTimeUnit));
  }

}