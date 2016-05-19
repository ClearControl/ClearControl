package clearcontrol.device.task;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import clearcontrol.core.concurrent.executors.WaitingScheduledFuture;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.log.Loggable;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

public abstract class TaskLoopDevice extends SignalStartStopDevice implements
																																	OpenCloseDeviceInterface,
																																	AsynchronousSchedulerServiceAccess,
																																	Loggable
{

	private final TaskLoopDevice lThis;
	private final TimeUnit mTimeUnit;
	private final BoundedVariable<Double> mLoopPeriodVariable;
	private final Variable<Boolean> mIsRunningVariable;
	private volatile WaitingScheduledFuture<?> mScheduledFuture;

	public TaskLoopDevice(final String pDeviceName,
												final boolean pOnlyStart)
	{
		this(pDeviceName, pOnlyStart, TimeUnit.MILLISECONDS);
	}

	public TaskLoopDevice(final String pDeviceName,
												final boolean pOnlyStart,
												TimeUnit pTimeUnit)
	{
		super(pDeviceName, pOnlyStart);
		mTimeUnit = pTimeUnit;

		mLoopPeriodVariable = new BoundedVariable<Double>(pDeviceName + "LoopPeriodIn"
																													+ pTimeUnit.name(),
																											0.0,
																											0.0,
																											Double.POSITIVE_INFINITY,
																											0.0);

		mIsRunningVariable = new Variable<Boolean>(	pDeviceName + "IsRunning",
																								false);

		lThis = this;
	}

	protected abstract boolean loop();

	@Override
	public boolean start()
	{
		if (mIsRunningVariable.get())
			return true;

		final Runnable lRunnable = () -> {
			final long lStartTime = System.nanoTime();
			boolean lResult = loop();
			final long lStopTime = System.nanoTime();

			final long lElapsedTimeInNanoseconds = lStopTime - lStartTime;

			final long lFactor = TimeUnit.NANOSECONDS.convert(1, mTimeUnit);

			final long lExtraWaitTimeInNanoseconds = (long) (mLoopPeriodVariable.get() * lFactor) - lElapsedTimeInNanoseconds;
			// System.out.println("lExtraWaitTimeInNanoseconds=" +
			// lExtraWaitTimeInNanoseconds);
			if (lExtraWaitTimeInNanoseconds > 0 && lResult)
				ThreadUtils.sleepWhile(	lExtraWaitTimeInNanoseconds,
																TimeUnit.NANOSECONDS,
																() -> {
																	return !mStopSignal.get() && mIsRunningVariable.get();
																});

			if (!lResult)
				stop();

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
		if (!mIsRunningVariable.get())
			return true;
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

	public BoundedVariable<Double> getLoopPeriodVariable()
	{
		return mLoopPeriodVariable;
	}

	public Variable<Boolean> getIsRunningVariable()
	{
		return mIsRunningVariable;
	}

}
