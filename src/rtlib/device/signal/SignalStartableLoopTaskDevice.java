package rtlib.device.signal;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.concurrent.executors.WaitingScheduledFuture;
import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.log.Loggable;
import rtlib.core.variable.Variable;
import rtlib.device.openclose.OpenCloseDeviceInterface;

public abstract class SignalStartableLoopTaskDevice	extends
																										SignalStartableDevice	implements
																																					OpenCloseDeviceInterface,
																																					AsynchronousSchedulerServiceAccess,
																																					Loggable
{

	private final SignalStartableLoopTaskDevice lThis;
	private final TimeUnit mTimeUnit;
	private final Variable<Long> mLoopPeriodVariable;
	private final Variable<Boolean> mIsRunningVariable;
	private volatile WaitingScheduledFuture<?> mScheduledFuture;

	public SignalStartableLoopTaskDevice(	final String pDeviceName,
																				final boolean pOnlyStart)
	{
		this(pDeviceName, pOnlyStart, TimeUnit.MILLISECONDS);
	}

	public SignalStartableLoopTaskDevice(	final String pDeviceName,
																				final boolean pOnlyStart,
																				TimeUnit pTimeUnit)
	{
		super(pDeviceName, pOnlyStart);
		mTimeUnit = pTimeUnit;

		mLoopPeriodVariable = new Variable<Long>(	pDeviceName + "LoopPeriodIn"
																												+ pTimeUnit.name(),
																										0L);

		mIsRunningVariable = new Variable<Boolean>(	pDeviceName + "IsRunning",
																											false);

		lThis = this;
	}

	protected abstract boolean loop();

	@Override
	public boolean start()
	{
		final Runnable lRunnable = () -> {
			final long lStartTime = System.nanoTime();
			loop();
			final long lStopTime = System.nanoTime();

			final long lElapsedTimeInNanoseconds = lStopTime - lStartTime;
			final long lExtraWaitTimeInNanoseconds = TimeUnit.NANOSECONDS.convert(mLoopPeriodVariable.get(),
																																						mTimeUnit) - lElapsedTimeInNanoseconds;
			if (lExtraWaitTimeInNanoseconds > 0)
				ThreadUtils.sleep(lExtraWaitTimeInNanoseconds,
													TimeUnit.NANOSECONDS);

		};
		mScheduledFuture = scheduleAtFixedRate(	lRunnable,
																						1,
																						TimeUnit.NANOSECONDS);

		final boolean lStarted = mScheduledFuture != null;

		mIsRunningVariable.set(lStarted);

		return lStarted;
	}

	public boolean pause()
	{
		return stop();
	}

	public boolean resume()
	{
		return start();
	}

	@Override
	public boolean stop()
	{
		try
		{
			if (mScheduledFuture != null)
			{
				mScheduledFuture.cancel(false);
				mScheduledFuture.waitForCompletion(10, TimeUnit.SECONDS);
			}
			mIsRunningVariable.set(false);
			return true;
		}
		catch (final ExecutionException e)
		{
			final String lError = "Error during previous execution of loop function!";
			severe("Device", lError, e);
		}
		catch (final CancellationException e)
		{
			System.err.println(e.getLocalizedMessage());
		}
		return false;

	}

	public Variable<Long> getLoopPeriodVariable()
	{
		return mLoopPeriodVariable;
	}

	public Variable<Boolean> getIsRunningVariable()
	{
		return mIsRunningVariable;
	}

}
