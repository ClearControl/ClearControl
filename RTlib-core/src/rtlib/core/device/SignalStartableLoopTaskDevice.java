package rtlib.core.device;

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
		ScheduledFuture<?> lScheduledFuture = scheduleAtFixedRate(lRunnable,
																															1,
																															TimeUnit.NANOSECONDS);

		return lScheduledFuture != null;
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
			return stopScheduledThreadPoolAndWaitForCompletion();
		}
		catch (ExecutionException e)
		{
			String lError = "Error during previous execution of loop function!";
			error("Device", lError, e);
			return false;
		}

	}

}