package rtlib.microscope;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import coremem.recycling.RecyclerInterface;
import rtlib.core.concurrent.future.FutureBooleanList;
import rtlib.core.variable.Variable;
import rtlib.core.variable.VariableSetListener;
import rtlib.device.active.ActivableDeviceInterface;
import rtlib.device.openclose.OpenCloseDeviceInterface;
import rtlib.device.queue.StateQueueDeviceInterface;
import rtlib.device.signal.SignalStartableLoopTaskDevice;
import rtlib.device.startstop.StartStopDeviceInterface;
import rtlib.hardware.cameras.StackCameraDeviceInterface;
import rtlib.hardware.signalgen.SignalGeneratorInterface;
import rtlib.microscope.lightsheet.component.detection.DetectionArmInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.StackProcessingPipeline;

public abstract class MicroscopeBase extends
																		SignalStartableLoopTaskDevice	implements
																																	MicroscopeInterface
{
	protected final StackRecyclerManager mStackRecyclerManager;
	protected final MicroscopeDeviceLists mLSMDeviceLists;
	protected volatile int mNumberOfEnqueuedStates;
	protected volatile long mAverageTimeInNS;

	// TODO: use this:
	private final ArrayList<Variable<StackInterface>> mStackVariableList = new ArrayList<>();
	private final ArrayList<StackProcessingPipeline> mStackPipelineList = new ArrayList<>();

	public MicroscopeBase(String pDeviceName, boolean pOnlyStart)
	{
		super(pDeviceName, pOnlyStart);
		mStackRecyclerManager = new StackRecyclerManager();
		mLSMDeviceLists = new MicroscopeDeviceLists(this);
	}

	@Override
	public MicroscopeDeviceLists getDeviceLists()
	{
		return mLSMDeviceLists;
	}

	public void addStackCameraDevice(int pIndex,
																	StackCameraDeviceInterface pCameraDevice,
																	StackProcessingPipeline pStackPipeline)
	{
		getDeviceLists().addDevice(pIndex, pCameraDevice);

		if (pStackPipeline != null)
		{
			getDeviceLists().addDevice(pIndex,pStackPipeline);
			pCameraDevice.getStackVariable()
										.sendUpdatesTo(pStackPipeline.getInputVariable());
			mStackVariableList.add(pStackPipeline.getOutputVariable());
		}
		else
		{
			mStackVariableList.add(pCameraDevice.getStackVariable());
		}
		
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
		mLSMDeviceLists.getDevice(SignalGeneratorInterface.class, 0)
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
	public long lastAcquiredStacksTimeStampInNS()
	{
		return mAverageTimeInNS;
	}

	@Override
	public Variable<StackInterface> getStackVariable(int pIndex)
	{
		return getStackVariable(pIndex);
	}

	@Override
	public void setRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler)
	{
		int lNumberOfStackCameraDevices = getDeviceLists().getNumberOfDevices(StackCameraDeviceInterface.class);
		for (int i = 0; i < lNumberOfStackCameraDevices; i++)
			setRecycler(i, pRecycler);
	}

	@Override
	public void setRecycler(int pStackCameraDeviceIndex,
													RecyclerInterface<StackInterface, StackRequest> pRecycler)
	{
		getDeviceLists().getDevice(	StackCameraDeviceInterface.class,
																pStackCameraDeviceIndex)
										.setStackRecycler(pRecycler);
	}

	@Override
	public RecyclerInterface<StackInterface, StackRequest> getRecycler(int pStackCameraDeviceIndex)
	{
		return getDeviceLists().getDevice(StackCameraDeviceInterface.class,
																			pStackCameraDeviceIndex)
														.getStackRecycler();
	}

	@Override
	public void useRecycler(final String pName,
													final int pMinimumNumberOfAvailableStacks,
													final int pMaximumNumberOfAvailableObjects,
													final int pMaximumNumberOfLiveObjects)
	{
		int lNumberOfStackCameraDevices = getDeviceLists().getNumberOfDevices(StackCameraDeviceInterface.class);
		RecyclerInterface<StackInterface, StackRequest> lRecycler = mStackRecyclerManager.getRecycler(pName,
																																																	lNumberOfStackCameraDevices * pMaximumNumberOfAvailableObjects,
																																																	lNumberOfStackCameraDevices * pMaximumNumberOfLiveObjects);

		for (int i = 0; i < lNumberOfStackCameraDevices; i++)
			getDeviceLists().getDevice(StackCameraDeviceInterface.class, i)
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
		int lNumberOfDetectionArmDevices = getDeviceLists().getNumberOfDevices(DetectionArmInterface.class);
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

}
