package rtlib.microscope.lightsheet;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rtlib.core.concurrent.future.FutureBooleanList;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.SignalStartableLoopTaskDevice;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;

public class LightSheetMicroscope	extends
																	SignalStartableLoopTaskDevice	implements
																																StateQueueDeviceInterface,
																																LightSheetMicroscopeInterface
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
				final boolean lIsThisDeviceOpen = lOpenCloseDevice.open();
				if (!lIsThisDeviceOpen)
				{
					System.out.println("Could not open device: " + lDevice);
				}

				lIsOpen &= lIsThisDeviceOpen;
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
				System.out.format("LightSheetMicroscope: playQueue() on device: %s \n",
													lDevice);
				final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
				final Future<Boolean> lPlayQueueFuture = lStateQueueDeviceInterface.playQueue();
				lFutureBooleanList.addFuture(lPlayQueueFuture);
			}
		}

		return lFutureBooleanList;
	}

	public Boolean playQueueAndWait(long pTimeOut, TimeUnit pTimeUnit) throws InterruptedException,
																																		ExecutionException,
																																		TimeoutException
	{
		FutureBooleanList lPlayQueue = playQueue();
		return lPlayQueue.get(pTimeOut, pTimeUnit);
	}

	@Override
	protected boolean loop()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void sendStacksToNull()
	{
		for (int i = 0; i < getDeviceLists().getNumberOfStackCameraDevices(); i++)
		{
			getDeviceLists().getStackVariable(i)
											.addSetListener((pCurrentValue, pNewValue) -> {
												pNewValue.release();
											});

		}
	}

	@Override
	public void setWidthHeight(int pWidth, int pHeight)
	{
		for (int i = 0; i < getDeviceLists().getNumberOfStackCameraDevices(); i++)
		{
			getDeviceLists().getStackCameraDevice(i)
											.getStackWidthVariable()
											.setValue(pWidth);
			getDeviceLists().getStackCameraDevice(i)
											.getStackHeightVariable()
											.setValue(pHeight);
		}
		
		for (int i = 0; i < getDeviceLists().getNumberOfLightSheetDevices(); i++)
		{
			getDeviceLists().getLightSheetDevice(i)
											.getImageHeightVariable()
											.setValue(pHeight);
		}
	};


	@Override
	public void setExposure(double pValue)
	{
		for (int i = 0; i < getDeviceLists().getNumberOfStackCameraDevices(); i++)
			getDeviceLists().getStackCameraDevice(i)
											.getExposureInMicrosecondsVariable()
											.set(pValue);

		for (int i = 0; i < getDeviceLists().getNumberOfLightSheetDevices(); i++)

			getDeviceLists().getLightSheetDevice(i)
											.getEffectiveExposureInMicrosecondsVariable()
											.setValue(pValue);
	};

	@Override
	public void setD(int pIndex, double pValue)
	{
		getDeviceLists().getDetectionPathDevice(pIndex)
										.getDetectionFocusZInMicronsVariable()
										.set(pValue);
	};

	@Override
	public void setI(int pIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pIndex)
										.getLightSheetZInMicronsVariable()
										.set(pValue);
	};

	@Override
	public void setY(int pIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pIndex)
										.getLightSheetYInMicronsVariable()
										.set(pValue);
	};

	@Override
	public void setA(int pIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pIndex)
										.getLightSheetAlphaInDegreesVariable()
										.set(pValue);
	};

	@Override
	public void setB(int pIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pIndex)
										.getLightSheetBetaInDegreesVariable()
										.set(pValue);
	};

	@Override
	public void setR(int pIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pIndex)
										.getLightSheetRangeInMicronsVariable()
										.set(pValue);
	};

	@Override
	public void setL(int pIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pIndex)
										.getLightSheetLengthInMicronsVariable()
										.set(pValue);
	}

	@Override
	public String toString()
	{
		return String.format(	"LightSheetMicroscope: \n%s\n",
													mLSMDeviceLists.toString());
	}


}
