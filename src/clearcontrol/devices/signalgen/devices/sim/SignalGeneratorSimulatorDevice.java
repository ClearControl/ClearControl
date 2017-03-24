package clearcontrol.devices.signalgen.devices.sim;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.device.sim.SimulationDeviceInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.devices.signalgen.SignalGeneratorBase;
import clearcontrol.devices.signalgen.SignalGeneratorInterface;
import clearcontrol.devices.signalgen.score.ScoreInterface;

public class SignalGeneratorSimulatorDevice extends
                                            SignalGeneratorBase
                                            implements
                                            SignalGeneratorInterface,
                                            LoggingInterface,
                                            SimulationDeviceInterface
{

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
  public boolean playScore(ScoreInterface pScore)
  {
    final long lDurationInMilliseconds =
                                       pScore.getDuration(TimeUnit.MILLISECONDS);

    long ltriggerPeriodInMilliseconds = lDurationInMilliseconds
                                        / mEnqueuedStateCounter;

    for (int i = 0; i < mEnqueuedStateCounter; i++)
    {
      mTriggerVariable.setEdge(false, true);
      ThreadUtils.sleep(ltriggerPeriodInMilliseconds,
                        TimeUnit.MILLISECONDS);
    }

    return true;
  }

  @Override
  public Future<Boolean> playQueue()
  {
    return super.playQueue();
  }

  @Override
  public double getTemporalGranularityInMicroseconds()
  {
    return 0;
  }

  @Override
  public Variable<Boolean> getTriggerVariable()
  {
    return mTriggerVariable;
  }

}
