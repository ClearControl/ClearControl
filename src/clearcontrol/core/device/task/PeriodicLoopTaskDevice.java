package clearcontrol.core.device.task;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.bounded.BoundedVariable;

public abstract class PeriodicLoopTaskDevice extends TaskDevice
                                             implements
                                             LoggingInterface,
                                             WaitingInterface
{

  private final TimeUnit mTimeUnit;
  private final BoundedVariable<Double> mLoopPeriodVariable;

  private volatile long mDeadline = Long.MIN_VALUE;

  public PeriodicLoopTaskDevice(final String pDeviceName)
  {
    this(pDeviceName, 0d, TimeUnit.MILLISECONDS);
  }

  public PeriodicLoopTaskDevice(final String pDeviceName,
                                double pPeriod,
                                TimeUnit pTimeUnit)
  {
    super(pDeviceName);
    mTimeUnit = pTimeUnit;

    mLoopPeriodVariable = new BoundedVariable<Double>(pDeviceName
                                                      + "LoopPeriodIn"
                                                      + pTimeUnit.name(),
                                                      pPeriod,
                                                      0.0,
                                                      Double.POSITIVE_INFINITY,
                                                      0.0);

  }

  @Override
  public void run()
  {
    while (!getStopSignalBooleanVariable().get())
    {
      final long lNow = System.nanoTime();
      final long lFactor = TimeUnit.NANOSECONDS.convert(1, mTimeUnit);
      final long lPeriodInNanoSeconds =
                                      (long) (mLoopPeriodVariable.get()
                                              * lFactor);
      mDeadline = lNow + lPeriodInNanoSeconds;
      boolean lResult = loop();
      final long lStopTime = System.nanoTime();

      if (lStopTime < mDeadline)
        while (System.nanoTime() < mDeadline)
        {
          ThreadUtils.sleep((mDeadline - System.nanoTime()) / 4,
                            TimeUnit.NANOSECONDS);
        }

      if (!lResult)
        stopTask();
    }
  };

  public abstract boolean loop();

  public BoundedVariable<Double> getLoopPeriodVariable()
  {
    return mLoopPeriodVariable;
  }

}
