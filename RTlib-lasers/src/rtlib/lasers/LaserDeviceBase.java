package rtlib.lasers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.serial.SerialDevice;

public class LaserDeviceBase extends SerialDevice	implements
																									LaserDeviceInterface
{

	private final ScheduledExecutorService mScheduledExecutorService = Executors.newScheduledThreadPool(1);

	protected DoubleVariable mDeviceIdVariable, mWavelengthVariable,
			mSpecInMilliWattPowerVariable, mMaxPowerInMilliWattVariable,
			mWorkingHoursVariable, mSetOperatingModeVariable,
			mTargetPowerInMilliWattVariable,
			mCurrentPowerInMilliWattVariable;
	protected BooleanVariable mPowerOnVariable, mLaserOnVariable;
	private Runnable mCurrentPowerPoller;

	private ScheduledFuture<?> mCurrentPowerPollerScheduledFutur;

	public LaserDeviceBase(	final String pDeviceName,
													final String pPortName,
													final int pBaudRate)
	{
		super(pDeviceName, pPortName, pBaudRate);

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
			final boolean lStartResult = super.start();

			setTargetPowerInPercent(0);
			setPowerOn(true);

			mCurrentPowerPoller = new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						final double lNewPowerValue = mCurrentPowerInMilliWattVariable.getValue();
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

			setLaserOn(true);
			return lStartResult;
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
			setLaserOn(false);
			mCurrentPowerPollerScheduledFutur.cancel(true);
			return super.stop();
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
			setTargetPowerInPercent(0);
			setLaserOn(false);
			setPowerOn(false);
			return super.close();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public final DoubleVariable getDeviceIdVariable()
	{
		return mDeviceIdVariable;
	}

	public final int getDeviceId()
	{
		return (int) mDeviceIdVariable.getValue();
	}

	@Override
	public final DoubleVariable getWavelengthInNanoMeterVariable()
	{
		return mWavelengthVariable;
	}

	@Override
	public final int getWavelengthInNanoMeter()
	{
		return (int) mWavelengthVariable.getValue();
	}

	public final DoubleVariable getSpecPowerVariable()
	{
		return mSpecInMilliWattPowerVariable;
	}

	public final double getSpecPowerInMilliWatt()
	{
		return mSpecInMilliWattPowerVariable.getValue();
	}

	public final DoubleVariable getWorkingHoursVariable()
	{
		return mWorkingHoursVariable;
	}

	public final double getWorkingHours()
	{
		return mWorkingHoursVariable.getValue();
	}

	public final DoubleVariable getMaxPowerVariable()
	{
		return mMaxPowerInMilliWattVariable;
	}

	@Override
	public final double getMaxPowerInMilliWatt()
	{
		return mMaxPowerInMilliWattVariable.getValue();
	}

	public final DoubleVariable getOperatingModeVariable()
	{
		return mSetOperatingModeVariable;
	}

	public final void setOperatingMode(final int pMode)
	{
		mSetOperatingModeVariable.setValue(pMode);
	}

	public final BooleanVariable getPowerOnVariable()
	{
		return mPowerOnVariable;
	}

	public final void setPowerOn(final boolean pState)
	{
		mPowerOnVariable.setValue(pState);
	}

	@Override
	public final BooleanVariable getLaserOnVariable()
	{
		return mLaserOnVariable;
	}

	public final void setLaserOn(final boolean pState)
	{
		mLaserOnVariable.setValue(pState);
	}

	public final double getTargetPowerInPercent()
	{
		return mTargetPowerInMilliWattVariable.getValue() / getMaxPowerInMilliWatt();
	}

	@Override
	public final void setTargetPowerInPercent(final double pPowerInPercent)
	{
		final double lPowerInMilliWatt = pPowerInPercent * getMaxPowerInMilliWatt();
		mTargetPowerInMilliWattVariable.setValue(lPowerInMilliWatt);
	}

	@Override
	public final double getTargetPowerInMilliWatt()
	{
		return mTargetPowerInMilliWattVariable.getValue();
	}

	@Override
	public final void setTargetPowerInMilliWatt(final double pPowerInMilliWatt)
	{
		mTargetPowerInMilliWattVariable.setValue(pPowerInMilliWatt);
	}

	@Override
	public final DoubleVariable getTargetPowerInMilliWattVariable()
	{
		return mTargetPowerInMilliWattVariable;
	}

	@Override
	public final DoubleVariable getCurrentPowerInMilliWattVariable()
	{
		return mCurrentPowerInMilliWattVariable;
	}

	@Override
	public final double getCurrentPowerInMilliWatt()
	{
		return mCurrentPowerInMilliWattVariable.getValue();
	}

	public final double getCurrentPowerInPercent()
	{
		return getCurrentPowerInMilliWatt() / getMaxPowerInMilliWatt();
	}

	@Override
	public String toString()
	{
		return String.format(	"LaserDeviceBase [mDeviceIdVariable=%d, mWavelengthVariable=%d, mMaxPowerVariable=%g]",
													(int) mDeviceIdVariable.getValue(),
													(int) mWavelengthVariable.getValue(),
													mMaxPowerInMilliWattVariable.getValue());
	}

}
