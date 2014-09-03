package rtlib.core.device;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.concurrent.thread.ThreadUtils;
import rtlib.core.log.Loggable;
import rtlib.core.variable.doublev.DoubleVariable;

public abstract class SignalStartableLoopTaskDevice	extends
																										SignalStartableDevice	implements
																																					VirtualDeviceInterface,
																																					AsynchronousSchedulerServiceAccess,
																																					Loggable
{

	private final SignalStartableLoopTaskDevice lThis;
	private TimeUnit mTimeUnit;
	private final DoubleVariable mLoopPeriodVariable;
	private volatile ScheduledFuture<?> mScheduledFuture;

	public SignalStartableLoopTaskDevice(	final String pDeviceName,
																				final boolean pOnlyStart,
																				TimeUnit pTimeUnit)
	{
		super(pDeviceName, pOnlyStart);
		mTimeUnit = pTimeUnit;

		mLoopPeriodVariable = new DoubleVariable(	pDeviceName + "LoopPeriodIn"
																									+ pTimeUnit.name(),
																							0);

		lThis = this;
	}

	protected abstract boolean loop();

	@Override
	public boolean start()
	{
		Runnable lRunnable = () -> {
			final long lStartTime = System.nanoTime();
			loop();
			final long lStopTime = System.nanoTime();

			final long lElapsedTimeInNanoseconds = lStopTime - lStartTime;
			final long lExtraWaitTimeInNanoseconds = TimeUnit.NANOSECONDS.convert((long) mLoopPeriodVariable.getValue(),
																																						mTimeUnit) - lElapsedTimeInNanoseconds;
			if (lExtraWaitTimeInNanoseconds > 0)
				ThreadUtils.sleep(lExtraWaitTimeInNanoseconds,
													TimeUnit.NANOSECONDS);

		};
		mScheduledFuture = scheduleAtFixedRate(	lRunnable,
																						(long) 1,
																						TimeUnit.NANOSECONDS);

		return mScheduledFuture != null;
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
			boolean lStopScheduledThreadPoolAndWaitForCompletion = stopScheduledThreadPoolAndWaitForCompletion();
			return lStopScheduledThreadPoolAndWaitForCompletion;
		}
		catch (ExecutionException e)
		{
			String lError = "Error during previous execution of loop function!";
			error("Device", lError, e);
		}
		catch (InterruptedException e)
		{
			System.err.println(e.getLocalizedMessage());
		}
		catch (CancellationException e)
		{
			System.err.println(e.getLocalizedMessage());
		}
		return false;

	}

	public DoubleVariable getLoopPeriodVariable()
	{
		return mLoopPeriodVariable;
	}

}
