package clearcontrol.devices.signalgen.devices.sim;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.devices.signalgen.SignalGeneratorBase;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.signalgen.SignalGeneratorRealTimeQueue;
import clearcontrol.devices.signalgen.score.ScoreInterface;

/**
 * Signal generator device simulator
 *
 * @author royer
 */
public class SignalGeneratorSimulatorDevice extends
                                            SignalGeneratorBase
                                            implements
                                            SignalGeneratorInterface,
                                            LoggingInterface,
                                            SimulationDeviceInterface
{

  private volatile int mQueueLength;

  /**
   * Signal generator device simulator
   */
  public SignalGeneratorSimulatorDevice()
  {
    super(SignalGeneratorSimulatorDevice.class.getSimpleName());

    mTriggerVariable.addSetListener((o, n) -> {
      if (isSimLogging())
        info("Trigger received");
    });
  }

  @Override
  public boolean open()
  {
    return true;
  }

  @Override
  public boolean close()
  {
    return true;
  }

  @Override
  public Future<Boolean> playQueue(SignalGeneratorRealTimeQueue pSignalGeneratorRealTimeQueue)
  {
    mQueueLength = pSignalGeneratorRealTimeQueue.getQueueLength();
    return super.playQueue(pSignalGeneratorRealTimeQueue);
  }

  @Override
  public boolean playScore(ScoreInterface pScore)
  {

    final long lDurationInMilliseconds =
                                       pScore.getDuration(TimeUnit.MILLISECONDS);

    long ltriggerPeriodInMilliseconds = lDurationInMilliseconds
                                        / mQueueLength;

    for (int i = 0; i < mQueueLength; i++)
    {
      mTriggerVariable.setEdge(false, true);
      ThreadUtils.sleep(ltriggerPeriodInMilliseconds,
                        TimeUnit.MILLISECONDS);
    }

    return super.playScore(pScore);
  }

  @Override
  public double getTemporalGranularityInMicroseconds()
  {
    return 0;
  }

}
