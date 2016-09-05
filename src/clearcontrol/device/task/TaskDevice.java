package clearcontrol.device.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.executors.ClearControlExecutors;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.startstop.SignalStartStopDevice;

public abstract class TaskDevice extends SignalStartStopDevice implements
																															Runnable,
																															OpenCloseDeviceInterface,
																															AsynchronousExecutorServiceAccess,
																															LoggingInterface
{

	private final Variable<Boolean> mIsRunningVariable;

	private final Variable<Throwable> mLastExceptionVariable;

	public TaskDevice(final String pDeviceName)
	{
		this(pDeviceName, Thread.NORM_PRIORITY);
	}

	public TaskDevice(final String pDeviceName, int pThreadPriority)
	{
		super(pDeviceName);

		setTaskOnStart(this::startTask);

		mIsRunningVariable = new Variable<Boolean>(	pDeviceName + "IsRunning",
																								false);

		mLastExceptionVariable = new Variable<Throwable>(	pDeviceName + "LastException",
																											null);

		ClearControlExecutors.getOrCreateThreadPoolExecutor(this,
																												pThreadPriority,
																												1,
																												1,
																												Integer.MAX_VALUE);
	}

	public Variable<Boolean> getIsRunningVariable()
	{
		return mIsRunningVariable;
	}

	public void stopTask()
	{
		mStopSignal.set(true);
	}

	public void clearStopped()
	{
		mStopSignal.set(false);
	}

	public boolean isStopped()
	{
		return mStopSignal.get();
	}

	public boolean startTask()
	{

		Runnable lRunnableWrapper = () -> {
			clearStopped();
			mIsRunningVariable.set(true);
			try
			{
				run();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				mLastExceptionVariable.set(e);
			}
			mIsRunningVariable.set(false);
		};

		Future<?> lExecuteAsynchronously = executeAsynchronously(lRunnableWrapper);
		return lExecuteAsynchronously != null;
	}

	public boolean waitForTaskCompletion(	long pTimeOut,
																				TimeUnit pTimeUnit)
	{
		try
		{
			boolean lWaitForCompletion = waitForCompletion(	pTimeOut,
																											pTimeUnit);
			mIsRunningVariable.set(false);
			return lWaitForCompletion;
		}
		catch (ExecutionException e)
		{
			String lError = "Error during previous execution of loop function!";
			severe("Device", lError, e);
			return false;
		}
	}

	public Variable<Boolean> getIsStoppedBooleanVariable()
	{
		return mStopSignal;
	}

}
