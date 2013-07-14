package device;

import thread.EnhancedThread;
import variable.booleanv.BooleanEventListenerInterface;
import variable.booleanv.BooleanVariable;

public abstract class SignalStartableTaskDevice	extends
																								SignalStartableDevice	implements
																																			VirtualDevice,
																																			Runnable
{

	private SignalStartableTaskDevice lThis;

	protected final BooleanVariable mCancelBooleanVariable;

	public SignalStartableTaskDevice(final String pDeviceName)
	{
		super(pDeviceName, true);
		lThis = this;

		mCancelBooleanVariable = new BooleanVariable(	pDeviceName + "Cancel",
																									false);
	}

	protected EnhancedThread mTaskThread = new EnhancedThread()
	{
		@Override
		public boolean loop()
		{
			lThis.run();
			return false;
		}
	};

	public abstract void run();

	@Override
	public boolean start()
	{
		mTaskThread.start();
		mCancelBooleanVariable.setValue(false);
		return true;
	}

	public boolean pause()
	{
		mTaskThread.pause();
		mTaskThread.waitForPause();
		return true;
	}

	public boolean resume()
	{
		mTaskThread.resume();
		mTaskThread.waitForRunning();
		return true;
	}

	@Override
	public boolean stop()
	{
		mCancelBooleanVariable.setValue(true);
		mTaskThread.stop();
		return true;
	}

	public BooleanVariable getIsCanceledBooleanVariable()
	{
		return mCancelBooleanVariable;
	}

	public boolean isCanceled()
	{
		return mCancelBooleanVariable.getBooleanValue();
	}

}
