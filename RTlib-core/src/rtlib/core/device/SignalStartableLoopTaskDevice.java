package rtlib.core.device;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.log.Loggable;

public abstract class SignalStartableLoopTaskDevice	extends
																										SignalStartableDevice	implements
																																					VirtualDeviceInterface,
																																					AsynchronousSchedulerServiceAccess,
																																					Loggable
{

	private final SignalStartableLoopTaskDevice lThis;
	private volatile ScheduledFuture<?> mScheduledFuture;

	public SignalStartableLoopTaskDevice(	final String pDeviceName,
																				final boolean pOnlyStart)
	{
		super(pDeviceName, pOnlyStart);
		lThis = this;
	}

	protected abstract boolean loop();

	@Override
	public boolean start()
	{
		Runnable lRunnable = () -> {
			loop();
		};
		mScheduledFuture = scheduleAtFixedRate(lRunnable,
																															1,
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

}
