package clearcontrol.devices.signalgen;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.signalgen.movement.MovementInterface;
import clearcontrol.devices.signalgen.movement.TransitionMovement;
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

  protected void prependTransitionMovement(ScoreInterface pScore,
                                           long pDuration,
                                           TimeUnit pTimeUnit)
  {
    if (getLastPlayedScoreVariable().get() == null || pDuration == 0)
      return;

    MovementInterface lFirstMovementOfGivenScore =
                                                 pScore.getMovement(0);

    MovementInterface lLastMovementFromPreviouslyPlayedScore =
                                                             getLastPlayedScoreVariable().get()
                                                                                         .getLastMovement();
    if (lFirstMovementOfGivenScore.getName()
                                  .equals("TransitionMovement"))
    {
      TransitionMovement.adjust(lFirstMovementOfGivenScore,
                                lLastMovementFromPreviouslyPlayedScore,
                                pScore.getMovement(1),
                                pDuration,
                                pTimeUnit);

    }
    else
    {

      MovementInterface lTransitionMovement =
                                            TransitionMovement.make(lLastMovementFromPreviouslyPlayedScore,
                                                                    lFirstMovementOfGivenScore,
                                                                    pDuration,
                                                                    pTimeUnit);

      pScore.insertMovementAt(0, lTransitionMovement);
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