package rtlib.microscope.lightsheet;

import java.util.concurrent.Future;

import rtlib.core.concurrent.future.FutureBooleanList;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.SignalStartableLoopTaskDevice;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;

public class LightSheetMicroscope	extends
																	SignalStartableLoopTaskDevice	implements
																																StateQueueDeviceInterface
{

	LightSheetMicroscopeDeviceLists mLSMDeviceLists;

	public LightSheetMicroscope(String pDeviceName)
	{
		super(pDeviceName, false);
		mLSMDeviceLists = new LightSheetMicroscopeDeviceLists();
	}

	public LightSheetMicroscopeDeviceLists getDeviceLists()
	{
		return mLSMDeviceLists;
	}


	@Override
	public boolean open()
	{
		boolean lIsOpen = super.open();
		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof OpenCloseDeviceInterface)
			{
				final OpenCloseDeviceInterface lOpenCloseDevice = (OpenCloseDeviceInterface) lDevice;
				lIsOpen &= lOpenCloseDevice.open();
			}
		}

		return lIsOpen;
	}

	@Override
	public boolean close()
	{
		boolean lIsClosed = true;
		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof OpenCloseDeviceInterface)
			{
				final OpenCloseDeviceInterface lOpenCloseDevice = (OpenCloseDeviceInterface) lDevice;
				lIsClosed &= lOpenCloseDevice.close();
			}
		}

		lIsClosed &= super.close();

		return lIsClosed;
	}

	@Override
	public boolean start()
	{
		boolean lIsStarted = super.start();
		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof StartStopDeviceInterface)
			{
				final StartStopDeviceInterface lStartStopDevice = (StartStopDeviceInterface) lDevice;
				lIsStarted &= lStartStopDevice.start();
			}
		}

		return lIsStarted;
	}

	@Override
	public boolean stop()
	{
		boolean lIsStopped = super.start();
		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof StartStopDeviceInterface)
			{
				final StartStopDeviceInterface lStartStopDevice = (StartStopDeviceInterface) lDevice;
				lIsStopped &= lStartStopDevice.stop();
			}
		}

		return lIsStopped;
	}

	@Override
	public void clearQueue()
	{
		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof StateQueueDeviceInterface)
			{
				final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
				lStateQueueDeviceInterface.clearQueue();
			}
		}
	}

	@Override
	public void addCurrentStateToQueueNotCounting()
	{
		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof StateQueueDeviceInterface)
			{
				final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
				lStateQueueDeviceInterface.addCurrentStateToQueueNotCounting();
			}
		}
	}

	@Override
	public void addCurrentStateToQueue()
	{
		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof StateQueueDeviceInterface)
			{
				final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
				lStateQueueDeviceInterface.addCurrentStateToQueue();
			}
		}
	}

	@Override
	public int getQueueLength()
	{
		return 0;
	}

	@Override
	public FutureBooleanList playQueue()
	{
		final FutureBooleanList lFutureBooleanList = new FutureBooleanList();

		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof StateQueueDeviceInterface)
			{
				final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
				final Future<Boolean> lPlayQueueFuture = lStateQueueDeviceInterface.playQueue();
				lFutureBooleanList.addFuture(lPlayQueueFuture);
			}
		}

		return lFutureBooleanList;
	}

	@Override
	protected boolean loop()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
