package rtlib.microscope.lsm;

import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rtlib.core.concurrent.future.FutureBooleanList;
import rtlib.core.device.ActivableDeviceInterface;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.SignalStartableLoopTaskDevice;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.Variable;
import rtlib.core.variable.VariableSetListener;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;
import rtlib.microscope.lsm.component.lightsheet.si.StructuredIlluminationPatternInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

public class LightSheetMicroscope	extends
																	SignalStartableLoopTaskDevice	implements
																																StateQueueDeviceInterface,
																																LightSheetMicroscopeInterface
{

	private final LightSheetMicroscopeDeviceLists mLSMDeviceLists;
	private final StackRecyclerManager mStackRecyclerManager;
	private volatile int mNumberOfEnqueuedStates;
	private volatile long mAverageTimeInNS;

	public LightSheetMicroscope(String pDeviceName)
	{
		super(pDeviceName, false);
		mLSMDeviceLists = new LightSheetMicroscopeDeviceLists();
		mStackRecyclerManager = new StackRecyclerManager();
	}

	@Override
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

		mStackRecyclerManager.clearAll();

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
				if (isActiveDevice(lDevice))
				{
					final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
					lStateQueueDeviceInterface.clearQueue();
				}
		}
		mNumberOfEnqueuedStates = 0;
	}

	@Override
	public void addCurrentStateToQueue()
	{
		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof StateQueueDeviceInterface)
				if (isActiveDevice(lDevice))
				{
					final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
					lStateQueueDeviceInterface.addCurrentStateToQueue();
				}

		}
		mNumberOfEnqueuedStates++;
	}

	@Override
	public void finalizeQueue()
	{
		// TODO: this should be put in a subclass specific to the way that we
		// trigger cameras...
		mLSMDeviceLists.getSignalGeneratorDevice(0)
										.addCurrentStateToQueue();

		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof StateQueueDeviceInterface)
				if (isActiveDevice(lDevice))
				{
					final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
					lStateQueueDeviceInterface.finalizeQueue();
				}
		}
	}

	private boolean isActiveDevice(final Object lDevice)
	{
		boolean lIsActive = true;
		if (lDevice instanceof ActivableDeviceInterface)
		{
			ActivableDeviceInterface lActivableDeviceInterface = (ActivableDeviceInterface) lDevice;
			lIsActive = lActivableDeviceInterface.isActive();
		}
		return lIsActive;
	}

	@Override
	public void setRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler)
	{
		int lNumberOfStackCameraDevices = getDeviceLists().getNumberOfStackCameraDevices();
		for (int i = 0; i < lNumberOfStackCameraDevices; i++)
			setRecycler(i, pRecycler);
	}

	@Override
	public void setRecycler(int pStackCameraDeviceIndex,
													RecyclerInterface<StackInterface, StackRequest> pRecycler)
	{
		getDeviceLists().getStackCameraDevice(pStackCameraDeviceIndex)
										.setStackRecycler(pRecycler);
	}

	@Override
	public RecyclerInterface<StackInterface, StackRequest> getRecycler(int pStackCameraDeviceIndex)
	{
		return getDeviceLists().getStackCameraDevice(pStackCameraDeviceIndex)
														.getStackRecycler();
	}

	@Override
	public void useRecycler(final String pName,
													final int pMinimumNumberOfAvailableStacks,
													final int pMaximumNumberOfAvailableObjects,
													final int pMaximumNumberOfLiveObjects)
	{
		int lNumberOfStackCameraDevices = getDeviceLists().getNumberOfStackCameraDevices();
		RecyclerInterface<StackInterface, StackRequest> lRecycler = mStackRecyclerManager.getRecycler(pName,
																																																	lNumberOfStackCameraDevices * pMaximumNumberOfAvailableObjects,
																																																	lNumberOfStackCameraDevices * pMaximumNumberOfLiveObjects);

		for (int i = 0; i < lNumberOfStackCameraDevices; i++)
			getDeviceLists().getStackCameraDevice(i)
											.setMinimalNumberOfAvailableStacks(pMinimumNumberOfAvailableStacks);

		setRecycler(lRecycler);
	}

	@Override
	public void clearRecycler(String pName)
	{
		mStackRecyclerManager.clear(pName);
	}

	@Override
	public void clearAllRecyclers()
	{
		mStackRecyclerManager.clearAll();
	}

	@Override
	public int getQueueLength()
	{
		return mNumberOfEnqueuedStates;
	}

	@Override
	public FutureBooleanList playQueue()
	{
		System.gc();
		final FutureBooleanList lFutureBooleanList = new FutureBooleanList();

		for (final Object lDevice : mLSMDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof StateQueueDeviceInterface)
			{
				System.out.format("LightSheetMicroscope: playQueue() on device: %s \n",
													lDevice);
				final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
				final Future<Boolean> lPlayQueueFuture = lStateQueueDeviceInterface.playQueue();
				lFutureBooleanList.addFuture(	lDevice.toString(),
																			lPlayQueueFuture);
			}
		}

		return lFutureBooleanList;
	}

	@Override
	public Boolean playQueueAndWait(long pTimeOut, TimeUnit pTimeUnit) throws InterruptedException,
																																		ExecutionException,
																																		TimeoutException
	{
		final FutureBooleanList lPlayQueue = playQueue();
		return lPlayQueue.get(pTimeOut, pTimeUnit);
	}

	@Override
	public Boolean playQueueAndWaitForStacks(	long pTimeOut,
																						TimeUnit pTimeUnit)	throws InterruptedException,
																																ExecutionException,
																																TimeoutException
	{
		int lNumberOfDetectionArmDevices = getDeviceLists().getNumberOfDetectionArmDevices();
		CountDownLatch[] lStacksReceivedLatches = new CountDownLatch[lNumberOfDetectionArmDevices];

		mAverageTimeInNS = 0;

		ArrayList<VariableSetListener<StackInterface>> lListenerList = new ArrayList<>();
		for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
		{
			lStacksReceivedLatches[i] = new CountDownLatch(1);

			final int fi = i;

			VariableSetListener<StackInterface> lVariableSetListener = new VariableSetListener<StackInterface>()
			{

				@Override
				public void setEvent(	StackInterface pCurrentValue,
															StackInterface pNewValue)
				{
					lStacksReceivedLatches[fi].countDown();
					mAverageTimeInNS += pNewValue.getTimeStampInNanoseconds() / lNumberOfDetectionArmDevices;
				}
			};

			lListenerList.add(lVariableSetListener);

			getStackVariable(i).addSetListener(lVariableSetListener);
		}

		System.out.println("Playing queue of length: " + getQueueLength());
		final FutureBooleanList lPlayQueue = playQueue();

		Boolean lBoolean = lPlayQueue.get(pTimeOut, pTimeUnit);

		if (lBoolean != null && lBoolean)
		{
			for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
			{
				lStacksReceivedLatches[i].await(pTimeOut, pTimeUnit);
			}
		}

		for (VariableSetListener<StackInterface> lVariableSetListener : lListenerList)
			for (int i = 0; i < lNumberOfDetectionArmDevices; i++)
			{
				getStackVariable(i).removeSetListener(lVariableSetListener);
			}

		return lBoolean;
	}

	@Override
	public long lastAcquiredStacksTimeStampInNS()
	{
		return mAverageTimeInNS;
	}

	@Override
	public Variable<StackInterface> getStackVariable(int pIndex)
	{
		return getDeviceLists().getStackVariable(pIndex);
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
	public void setCameraWidthHeight(long pWidth, long pHeight)
	{
		for (int i = 0; i < getDeviceLists().getNumberOfStackCameraDevices(); i++)
		{
			getDeviceLists().getStackCameraDevice(i)
											.getStackWidthVariable()
											.set(pWidth);
			getDeviceLists().getStackCameraDevice(i)
											.getStackHeightVariable()
											.set(pHeight);
		}

		for (int i = 0; i < getDeviceLists().getNumberOfLightSheetDevices(); i++)
		{
			getDeviceLists().getLightSheetDevice(i)
											.getImageHeightVariable()
											.set(pHeight);
		}
	};

	@Override
	public int getCameraWidth(int pCameraDeviceIndex)
	{
		return getDeviceLists().getStackCameraDevice(pCameraDeviceIndex)
														.getStackWidthVariable()
														.get()
														.intValue();
	};

	@Override
	public int getCameraHeight(int pCameraDeviceIndex)
	{
		return getDeviceLists().getStackCameraDevice(pCameraDeviceIndex)
														.getStackHeightVariable()
														.get()
														.intValue();
	};

	@Override
	public void setExposure(long pValue, TimeUnit pTimeUnit)
	{
		final double lExposureTimeInMicroseconds = TimeUnit.MICROSECONDS.convert(	pValue,
																																							pTimeUnit);

		for (int i = 0; i < getDeviceLists().getNumberOfStackCameraDevices(); i++)
			getDeviceLists().getStackCameraDevice(i)
											.getExposureInMicrosecondsVariable()
											.set(lExposureTimeInMicroseconds);

		for (int i = 0; i < getDeviceLists().getNumberOfLightSheetDevices(); i++)
			getDeviceLists().getLightSheetDevice(i)
											.getEffectiveExposureInMicrosecondsVariable()
											.set(lExposureTimeInMicroseconds);
	};

	@Override
	public long getExposure(int pCameraDeviceIndex, TimeUnit pTimeUnit)
	{

		long lExposureInMicroseconds = getDeviceLists().getStackCameraDevice(pCameraDeviceIndex)
																										.getExposureInMicrosecondsVariable()
																										.get()
																										.longValue();

		long lExposureInProvidedUnit = pTimeUnit.convert(	lExposureInMicroseconds,
																											TimeUnit.MICROSECONDS);

		return toIntExact(lExposureInProvidedUnit);
	};

	public void zero()
	{
		for (int i = 0; i < getDeviceLists().getNumberOfDetectionArmDevices(); i++)
		{
			setDZ(i, 0);
			setC(i, true);
		}

		for (int i = 0; i < getDeviceLists().getNumberOfLightSheetDevices(); i++)
		{
			setIX(i, 0);
			setIY(i, 0);
			setIZ(i, 0);
			setIA(i, 0);
			setIB(i, 0);
			setIZ(i, 0);
			setIH(i, 0);

			for (int j = 0; j < getDeviceLists().getNumberOfLaserDevices(); j++)
			{
				setIPatternOnOff(i, j, false);
			}
		}

	}

	@Override
	public void setC(int pCameraIndex, boolean pKeepImage)
	{
		getDeviceLists().getStackCameraDevice(pCameraIndex)
										.getKeepPlaneVariable()
										.set(pKeepImage);
	};

	@Override
	public boolean getC(int pCameraIndex)
	{
		return getDeviceLists().getStackCameraDevice(pCameraIndex)
														.getKeepPlaneVariable()
														.get();
	}

	@Override
	public void setC(boolean pKeepImage)
	{
		int lNumberOfStackCameraDevices = getDeviceLists().getNumberOfStackCameraDevices();

		for (int c = 0; c < lNumberOfStackCameraDevices; c++)
			getDeviceLists().getStackCameraDevice(c)
											.getKeepPlaneVariable()
											.set(pKeepImage);

	}

	@Override
	public void setLO(int pLaserIndex, boolean pLaserOnOff)
	{
		getDeviceLists().getLaserDevice(pLaserIndex)
										.getLaserOnVariable()
										.set(pLaserOnOff);
	};

	@Override
	public boolean getLO(int pLaserIndex)
	{
		return getDeviceLists().getLaserDevice(pLaserIndex)
														.getLaserOnVariable()
														.get();
	}

	@Override
	public void setLP(int pLaserIndex, double pLaserPowerInmW)
	{
		getDeviceLists().getLaserDevice(pLaserIndex)
										.getTargetPowerInMilliWattVariable()
										.set(pLaserPowerInmW);
	};

	@Override
	public double getLP(int pLaserIndex)
	{
		return getDeviceLists().getLaserDevice(pLaserIndex)
														.getTargetPowerInMilliWattVariable()
														.get()
														.doubleValue();
	}

	@Override
	public void setDZ(int pDetectionArmIndex, double pValue)
	{
		getDeviceLists().getDetectionArmDevice(pDetectionArmIndex)
										.getZVariable()
										.set(pValue);
	};

	@Override
	public double getDZ(int pDetectionArmIndex)
	{
		return getDeviceLists().getDetectionArmDevice(pDetectionArmIndex)
														.getZVariable()
														.get();
	}

	@Override
	public void setI(int pLightSheetIndex)
	{
		int lNumberOfSwitchableDevices = getDeviceLists().getLightSheetSwitchingDevice()
																											.getNumberOfSwitches();
		for (int i = 0; i < lNumberOfSwitchableDevices; i++)
			setI(i, i == pLightSheetIndex);

		getDeviceLists().getLightSheetDevice(pLightSheetIndex).update();
	};

	@Override
	public void setI(int pLightSheetIndex, boolean pOnOff)
	{
		getDeviceLists().getLightSheetSwitchingDevice()
										.getSwitchingVariable(pLightSheetIndex)
										.set(pOnOff);
	};

	@Override
	public void setI(boolean pOnOff)
	{
		int lNumberOfSwitchableDevices = getDeviceLists().getLightSheetSwitchingDevice()
																											.getNumberOfSwitches();
		for (int i = 0; i < lNumberOfSwitchableDevices; i++)
			setI(i, pOnOff);
	};

	@Override
	public boolean getI(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetSwitchingDevice()
														.getSwitchingVariable(pLightSheetIndex)
														.get();
	}

	@Override
	public void setIX(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getXVariable()
										.set(pValue);
	};

	@Override
	public double getIX(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getXVariable()
														.get();
	}

	@Override
	public void setIY(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getYVariable()
										.set(pValue);
	};

	@Override
	public double getIY(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getYVariable()
														.get();
	}

	@Override
	public void setIZ(int pIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pIndex)
										.getZVariable()
										.set(pValue);
	};

	@Override
	public double getIZ(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getZVariable()
														.get();
	}

	@Override
	public void setIA(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getAlphaInDegreesVariable()
										.set(pValue);
	};

	@Override
	public double getIA(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getAlphaInDegreesVariable()
														.get();
	}

	@Override
	public void setIB(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getBetaInDegreesVariable()
										.set(pValue);
	};

	@Override
	public double getIB(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getBetaInDegreesVariable()
														.get();
	}

	@Override
	public void setIW(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getWidthVariable()
										.set(pValue);
	};

	@Override
	public double getIW(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getWidthVariable()
														.get();
	}

	@Override
	public void setIH(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getHeightVariable()
										.set(pValue);
	}

	@Override
	public double getIH(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getHeightVariable()
														.get();
	}

	@Override
	public void setILO(boolean pOn)
	{
		int lNumberOfLightSheets = getDeviceLists().getNumberOfLightSheetDevices();

		for (int l = 0; l < lNumberOfLightSheets; l++)
		{
			LightSheetInterface lLightSheetDevice = getDeviceLists().getLightSheetDevice(l);
			for (int i = 0; i < lLightSheetDevice.getNumberOfLaserDigitalControls(); i++)
				lLightSheetDevice.getLaserOnOffArrayVariable(i).set(pOn);
		}
	};

	@Override
	public void setILO(int pLightSheetIndex, boolean pOn)
	{
		LightSheetInterface lLightSheetDevice = getDeviceLists().getLightSheetDevice(pLightSheetIndex);
		for (int i = 0; i < lLightSheetDevice.getNumberOfLaserDigitalControls(); i++)
			lLightSheetDevice.getLaserOnOffArrayVariable(i).set(pOn);
	};

	@Override
	public void setILO(	int pLightSheetIndex,
											int pLaserIndex,
											boolean pOn)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getLaserOnOffArrayVariable(pLaserIndex)
										.set(pOn);
	};

	@Override
	public boolean getILO(int pLightSheetIndex, int pLaserIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getLaserOnOffArrayVariable(pLaserIndex)
														.get();
	}

	@Override
	public void setIP(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getPowerVariable()
										.set(pValue);
	}

	@Override
	public double getIP(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getPowerVariable()
														.get();
	}

	@Override
	public void setIPA(boolean pAdapt)
	{
		int lNumberOfLightSheets = getDeviceLists().getNumberOfLightSheetDevices();

		for (int i = 0; i < lNumberOfLightSheets; i++)
			getDeviceLists().getLightSheetDevice(i)
											.getAdaptPowerToWidthHeightVariable()
											.set(pAdapt);

	}

	@Override
	public void setIPA(int pLightSheetIndex, boolean pAdapt)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getAdaptPowerToWidthHeightVariable()
										.set(pAdapt);

	}

	@Override
	public boolean getIPA(int pLightSheetIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getAdaptPowerToWidthHeightVariable()
														.get();
	}

	@Override
	public void setIPatternOnOff(	int pLightSheetIndex,
																int pLaserIndex,
																boolean pOnOff)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getSIPatternOnOffVariable(pLaserIndex)
										.set(pOnOff);
	}

	@Override
	public boolean getIPatternOnOff(int pLightSheetIndex,
																	int pLaserIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getSIPatternOnOffVariable(pLaserIndex)
														.get();
	}

	@Override
	public void setIPattern(int pLightSheetIndex,
													int pLaserIndex,
													StructuredIlluminationPatternInterface pPattern)
	{
		getDeviceLists().getLightSheetDevice(pLightSheetIndex)
										.getSIPatternVariable(pLaserIndex)
										.set(pPattern);
	}

	@Override
	public StructuredIlluminationPatternInterface getIPattern(int pLightSheetIndex,
																														int pLaserIndex)
	{
		return getDeviceLists().getLightSheetDevice(pLightSheetIndex)
														.getSIPatternVariable(pLaserIndex)
														.get();
	}

	public int getNumberOfDOF()
	{
		final int lNumberOfLightSheetsDOFs = getDeviceLists().getNumberOfLightSheetDevices() * 7;
		final int lNumberOfDetectionArmDOFs = getDeviceLists().getNumberOfDetectionArmDevices() * 1;

		return lNumberOfLightSheetsDOFs + lNumberOfDetectionArmDOFs;
	}

	@Override
	public String toString()
	{
		return String.format(	"LightSheetMicroscope: \n%s\n",
													mLSMDeviceLists.toString());
	}

}
