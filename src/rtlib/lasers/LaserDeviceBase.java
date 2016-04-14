package rtlib.lasers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.Variable;

public class LaserDeviceBase extends NamedVirtualDevice	implements
																												LaserDeviceInterface
{

	private final ScheduledExecutorService mScheduledExecutorService = Executors.newScheduledThreadPool(1);

	protected Variable<Number> mSpecInMilliWattPowerVariable,
			mMaxPowerInMilliWattVariable, mTargetPowerInMilliWattVariable,
			mCurrentPowerInMilliWattVariable;
	protected Variable<Integer> mWorkingHoursVariable,
			mSetOperatingModeVariable, mDeviceIdVariable,
			mWavelengthVariable;
	protected Variable<Boolean> mPowerOnVariable, mLaserOnVariable;
	private Runnable mCurrentPowerPoller;

	private ScheduledFuture<?> mCurrentPowerPollerScheduledFutur;

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
						final double lNewPowerValue = mCurrentPowerInMilliWattVariable.get()
																																					.doubleValue();
						mCurrentPowerInMilliWattVariable.sync(lNewPowerValue,
																									true);
					}
					catch (final Throwable e)
					{
						e.printStackTrace();
					}
				}
			};
			mCurrentPowerPollerScheduledFutur = mScheduledExecutorService.scheduleAtFixedRate(mCurrentPowerPoller,
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

	public final Variable<Integer> getDeviceIdVariable()
	{
		return mDeviceIdVariable;
	}

	public final int getDeviceId()
	{
		return mDeviceIdVariable.get();
	}

	@Override
	public final Variable<Integer> getWavelengthInNanoMeterVariable()
	{
		return mWavelengthVariable;
	}

	@Override
	public final int getWavelengthInNanoMeter()
	{
		return mWavelengthVariable.get();
	}

	public final Variable<Number> getSpecPowerVariable()
	{
		return mSpecInMilliWattPowerVariable;
	}

	public final double getSpecPowerInMilliWatt()
	{
		return mSpecInMilliWattPowerVariable.get().doubleValue();
	}

	public final Variable<Integer> getWorkingHoursVariable()
	{
		return mWorkingHoursVariable;
	}

	public final int getWorkingHours()
	{
		return mWorkingHoursVariable.get();
	}

	public final Variable<Number> getMaxPowerVariable()
	{
		return mMaxPowerInMilliWattVariable;
	}

	@Override
	public final double getMaxPowerInMilliWatt()
	{
		return mMaxPowerInMilliWattVariable.get().doubleValue();
	}

	public final Variable<Integer> getOperatingModeVariable()
	{
		return mSetOperatingModeVariable;
	}

	public final void setOperatingMode(final int pMode)
	{
		mSetOperatingModeVariable.set(pMode);
	}

	public final Variable<Boolean> getPowerOnVariable()
	{
		return mPowerOnVariable;
	}

	public final void setPowerOn(final boolean pState)
	{
		mPowerOnVariable.set(pState);
	}

	@Override
	public final Variable<Boolean> getLaserOnVariable()
	{
		return mLaserOnVariable;
	}

	public final void setLaserOn(final boolean pState)
	{
		mLaserOnVariable.set(pState);
	}

	public final double getTargetPowerInPercent()
	{
		return mTargetPowerInMilliWattVariable.get().doubleValue() / getMaxPowerInMilliWatt();
	}

	@Override
	public final void setTargetPowerInPercent(final double pPowerInPercent)
	{
		final double lPowerInMilliWatt = pPowerInPercent * getMaxPowerInMilliWatt();
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
	public final Variable<Number> getTargetPowerInMilliWattVariable()
	{
		return mTargetPowerInMilliWattVariable;
	}

	@Override
	public final Variable<Number> getCurrentPowerInMilliWattVariable()
	{
		return mCurrentPowerInMilliWattVariable;
	}

	@Override
	public final double getCurrentPowerInMilliWatt()
	{
		return mCurrentPowerInMilliWattVariable.get().doubleValue();
	}

	public final double getCurrentPowerInPercent()
	{
		return getCurrentPowerInMilliWatt() / getMaxPowerInMilliWatt();
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
			return String.format(	"LaserDevice [mDeviceIdVariable=%d, mWavelengthVariable=%d, mMaxPowerVariable=%g]",
														(int) mDeviceIdVariable.get(),
														(int) mWavelengthVariable.get(),
														mMaxPowerInMilliWattVariable.get());
		}
	}

}
