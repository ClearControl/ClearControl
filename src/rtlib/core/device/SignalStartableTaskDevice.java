package rtlib.core.device;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.log.Loggable;
import rtlib.core.variable.Variable;
import rtlib.core.variable.VariableEdgeListener;

public abstract class SignalStartableTaskDevice	extends
																								SignalStartableDevice	implements
																																			OpenCloseDeviceInterface,
																																			AsynchronousExecutorServiceAccess,
																																			Loggable,
																																			Runnable
{

	private final SignalStartableTaskDevice lThis;

	protected final Variable<Boolean> mCancelBooleanVariable;

	protected volatile boolean mCanceledSignal = false;

	public SignalStartableTaskDevice(final String pDeviceName)
	{
		super(pDeviceName, true);
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
	}

	@Override
	public abstract void run();

	@Override
	public boolean start()
	{
		clearCanceled();
		Future<?> lExecuteAsynchronously = executeAsynchronously(this);
		return lExecuteAsynchronously != null;
	}

	public boolean pause()
	{

		return true;
	}

	public boolean resume()
	{

		return true;
	}

	@Override
	public boolean stop()
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
