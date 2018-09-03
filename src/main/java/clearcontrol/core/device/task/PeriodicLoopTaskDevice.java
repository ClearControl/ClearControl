package clearcontrol.core.device.task;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.bounded.BoundedVariable;

/**
 * Base class for periodic loop task device
 *
 * @author royer
 */
public abstract class PeriodicLoopTaskDevice extends TaskDevice
                                             implements
                                             LoggingFeature,
                                             WaitingInterface
{

  private final TimeUnit mTimeUnit;
  private final BoundedVariable<Double> mLoopPeriodVariable;

  private volatile long mDeadline = Long.MIN_VALUE;

  /**
   * Instanciates a periodic loop task device given a device name
   * 
   * @param pDeviceName
   *          device name
   */
  public PeriodicLoopTaskDevice(final String pDeviceName)
  {
    this(pDeviceName, 0d, TimeUnit.MILLISECONDS);
  }

  /**
   * Instanciates a periodic loop device given a devicebname, period, and time
   * unit.
   * 
   * @param pDeviceName
   *          device name
   * @param pPeriod
   *          period
   * @param pTimeUnit
   *          time-unit
   */
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

  /**
   * Returns loop period
   * 
   * @return loop period variable
   */
  public BoundedVariable<Double> getLoopPeriodVariable()
  {
    return mLoopPeriodVariable;
  }

  @Override
  public void run()
  {
    while (getStopSignalVariable().get() == false)
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
        while (System.nanoTime() < mDeadline
               && getStopSignalVariable().get() == false)
        {
          ThreadSleep.sleep((mDeadline - System.nanoTime()) / 4,
                            TimeUnit.NANOSECONDS);
        }

      if (!lResult)
        stopTask();
    }
  };

  /**
   * Loop to execute
   * 
   * @return true -> continue looping, false -> stop loop
   */
  public abstract boolean loop();

}
