package rtlib.core.device;

import rtlib.core.concurrent.thread.EnhancedThread;

public abstract class SignalStartableLoopTaskDevice	extends
																										SignalStartableDevice	implements
																																					VirtualDeviceInterface
{

	private final SignalStartableLoopTaskDevice lThis;

	public SignalStartableLoopTaskDevice(	final String pDeviceName,
																				final boolean pOnlyStart)
	{
		super(pDeviceName, pOnlyStart);
		lThis = this;
	}

	protected EnhancedThread mTaskThread = new EnhancedThread()
	{
		@Override
		public boolean loop()
		{
			return lThis.loop();
		}
	};

	protected abstract boolean loop();

	@Override
	public boolean start()
	{
		mTaskThread.start();
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
		return true;
	}

	@Override
	public boolean stop()
	{
		if (mTaskThread.isRunning())
		{
			mTaskThread.stop();
			mTaskThread.waitToFinish();
		}
		return true;
	}

}
