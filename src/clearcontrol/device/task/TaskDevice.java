package clearcontrol.device.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.log.Loggable;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableEdgeListener;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

public abstract class TaskDevice extends SignalStartStopDevice implements
																															OpenCloseDeviceInterface,
																															AsynchronousExecutorServiceAccess,
																															Loggable
{

	private final TaskDevice lThis;

	protected final Variable<Boolean> mCancelBooleanVariable;

	protected volatile boolean mCanceledSignal = false;

	private Runnable mTask;

	public TaskDevice(final String pDeviceName, Runnable pTask)
	{
		super(pDeviceName, true);

		setTaskOnStart(this::startTask);
		setTaskOnStop(this::cancel);

		lThis = this;

		mCancelBooleanVariable = new Variable<Boolean>(	pDeviceName + "Cancel",
																										false);

		mCancelBooleanVariable.addEdgeListener(new VariableEdgeListener<Boolean>()
		{

			@Override
			public void fire(final Boolean pCurrentBooleanValue)
			{
				if (pCurrentBooleanValue)
				{
					mCanceledSignal = true;
				}
			}
		});
		mTask = pTask;
	}

	public boolean startTask()
	{
		clearCanceled();
		Future<?> lExecuteAsynchronously = executeAsynchronously(mTask);
		return lExecuteAsynchronously != null;
	}

	public boolean waitForTaskCompletion()
	{
		try
		{
			return waitForCompletion();
		}
		catch (ExecutionException e)
		{
			String lError = "Error during previous execution of loop function!";
			severe("Device", lError, e);
			return false;
		}
	}

	public Variable<Boolean> getIsCanceledBooleanVariable()
	{
		return mCancelBooleanVariable;
	}

	public void cancel()
	{
		mCancelBooleanVariable.set(false);
		mCanceledSignal = false;
	}

	public void clearCanceled()
	{
		mCancelBooleanVariable.set(false);
		mCanceledSignal = false;
	}

	public boolean isCanceled()
	{
		return mCanceledSignal;
	}

}
