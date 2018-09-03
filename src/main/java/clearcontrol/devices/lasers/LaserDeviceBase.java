package clearcontrol.devices.lasers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;

/**
 * Base class providing common fields and methods for all laser devices.
 *
 * @author royer
 */
public class LaserDeviceBase extends VirtualDevice
                             implements LaserDeviceInterface
{

  private final ScheduledExecutorService mScheduledExecutorService =
                                                                   Executors.newScheduledThreadPool(1);

  protected Variable<Number> mSpecInMilliWattPowerVariable,
      mMaxPowerInMilliWattVariable, mTargetPowerInMilliWattVariable,
      mCurrentPowerInMilliWattVariable;
  protected Variable<Integer> mWorkingHoursVariable,
      mSetOperatingModeVariable, mDeviceIdVariable,
      mWavelengthVariable;
  protected Variable<Boolean> mPowerOnVariable, mLaserOnVariable;
  private Runnable mCurrentPowerPoller;

  private ScheduledFuture<?> mCurrentPowerPollerScheduledFutur;

  /**
   * Instanciates a laser device
   * 
   * @param pDeviceName
   *          device name
   */
  public LaserDeviceBase(final String pDeviceName)
  {
    super(pDeviceName);
  }

  @Override
  public boolean open()
  {
    boolean lOpen;
    try
    {
      lOpen = super.open();

      return lOpen;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean start()
  {
    try
    {
      mCurrentPowerPoller = new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            final double lNewPowerValue =
                                        mCurrentPowerInMilliWattVariable.get()
                                                                        .doubleValue();
            // info("Current laser power: " + lNewPowerValue);
            mCurrentPowerInMilliWattVariable.set(lNewPowerValue);
          }
          catch (final Throwable e)
          {
            e.printStackTrace();
          }
        }
      };
      mCurrentPowerPollerScheduledFutur =
                                        mScheduledExecutorService.scheduleAtFixedRate(mCurrentPowerPoller,
                                                                                      1,
                                                                                      300,
                                                                                      TimeUnit.MILLISECONDS);

      return true;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean stop()
  {
    try
    {

      mCurrentPowerPollerScheduledFutur.cancel(true);
      return true;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean close()
  {
    try
    {
      return super.close();
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public final int getDeviceId()
  {
    return mWavelengthVariable.get();
  }

  @Override
  public final int getWavelengthInNanoMeter()
  {
    return mWavelengthVariable.get();
  }

  @Override
  public final void setOperatingMode(final int pMode)
  {
    mSetOperatingModeVariable.set(pMode);
  }

  @Override
  public final void setLaserPowerOn(final boolean pState)
  {
    mPowerOnVariable.set(pState);
  }

  @Override
  public final void setLaserOn(final boolean pState)
  {
    mLaserOnVariable.set(pState);
  }

  @Override
  public final double getSpecPowerInMilliWatt()
  {
    return mSpecInMilliWattPowerVariable.get().doubleValue();
  }

  @Override
  public final double getMaxPowerInMilliWatt()
  {
    return mMaxPowerInMilliWattVariable.get().doubleValue();
  }

  @Override
  public final int getWorkingHours()
  {
    return mWorkingHoursVariable.get();
  }

  @Override
  public final double getCurrentPowerInMilliWatt()
  {
    return mCurrentPowerInMilliWattVariable.get().doubleValue();
  }

  @Override
  public final double getCurrentPowerInPercent()
  {
    return getCurrentPowerInMilliWatt() / getMaxPowerInMilliWatt();
  }

  @Override
  public final double getTargetPowerInPercent()
  {
    return mTargetPowerInMilliWattVariable.get().doubleValue()
           / getMaxPowerInMilliWatt();
  }

  @Override
  public final void setTargetPowerInPercent(final double pPowerInPercent)
  {
    final double lPowerInMilliWatt = pPowerInPercent
                                     * getMaxPowerInMilliWatt();
    mTargetPowerInMilliWattVariable.set(lPowerInMilliWatt);
  }

  @Override
  public final double getTargetPowerInMilliWatt()
  {
    return mTargetPowerInMilliWattVariable.get().doubleValue();
  }

  @Override
  public final void setTargetPowerInMilliWatt(final double pPowerInMilliWatt)
  {
    mTargetPowerInMilliWattVariable.set(pPowerInMilliWatt);
  }

  @Override
  public final Variable<Integer> getDeviceIdVariable()
  {
    return mDeviceIdVariable;
  }

  @Override
  public final Variable<Integer> getWavelengthInNanoMeterVariable()
  {
    return mWavelengthVariable;
  }

  @Override
  public final Variable<Number> getSpecPowerVariable()
  {
    return mSpecInMilliWattPowerVariable;
  }

  @Override
  public final Variable<Number> getMaxPowerVariable()
  {
    return mMaxPowerInMilliWattVariable;
  }

  @Override
  public Variable<Number> getTargetPowerInMilliWattVariable()
  {
    return mTargetPowerInMilliWattVariable;
  }

  @Override
  public Variable<Number> getCurrentPowerInMilliWattVariable()
  {
    return mCurrentPowerInMilliWattVariable;
  }

  @Override
  public final Variable<Integer> getOperatingModeVariable()
  {
    return mSetOperatingModeVariable;
  }

  @Override
  public final Variable<Integer> getWorkingHoursVariable()
  {
    return mWorkingHoursVariable;
  }

  @Override
  public final Variable<Boolean> getPowerOnVariable()
  {
    return mPowerOnVariable;
  }

  @Override
  public final Variable<Boolean> getLaserOnVariable()
  {
    return mLaserOnVariable;
  }

  @Override
  public String toString()
  {
    if (mDeviceIdVariable == null || mWavelengthVariable == null
        || mMaxPowerInMilliWattVariable == null
        || mDeviceIdVariable.get() == null
        || mWavelengthVariable.get() == null
        || mMaxPowerInMilliWattVariable.get() == null)
    {
      return String.format("LaserDevice [null]");
    }
    else
    {
      return String.format("LaserDevice [mDeviceIdVariable=%d, mWavelengthVariable=%d, mMaxPowerVariable=%g]",
                           (int) mDeviceIdVariable.get(),
                           (int) mWavelengthVariable.get(),
                           mMaxPowerInMilliWattVariable.get());
    }
  }

}
