package clearcontrol.microscope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import clearcontrol.core.concurrent.future.FutureBooleanList;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.Loggable;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.device.VirtualDevice;
import clearcontrol.device.active.ActivableDeviceInterface;
import clearcontrol.device.change.ChangeListener;
import clearcontrol.device.change.HasChangeListenerInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.queue.StateQueueDeviceInterface;
import clearcontrol.device.startstop.StartStopDeviceInterface;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import clearcontrol.hardware.signalgen.SignalGeneratorInterface;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import clearcontrol.stack.processor.StackProcessingPipeline;
import coremem.recycling.RecyclerInterface;

public abstract class MicroscopeBase extends VirtualDevice implements
																													MicroscopeInterface,
																													StartStopDeviceInterface,
																													AsynchronousSchedulerServiceAccess,
																													Loggable
{

	protected final StackRecyclerManager mStackRecyclerManager;
	protected final MicroscopeDeviceLists mDeviceLists;
	protected volatile int mNumberOfEnqueuedStates;
	protected volatile long mAverageTimeInNS;

	final ArrayList<Variable<Double>> mCameraPixelSizeInNanometerVariableList = new ArrayList<>();

	// Lock:
	protected Object mAcquisitionLock = new Object();

	private final HashMap<Integer, StackProcessingPipeline> mStackPipelines = new HashMap<>();

	public MicroscopeBase(String pDeviceName)
	{
		super(pDeviceName);
		mStackRecyclerManager = new StackRecyclerManager();
		mDeviceLists = new MicroscopeDeviceLists(this);

		for (int i = 0; i < 128; i++)
		{
			Double lPixelSizeInNanometers = MachineConfiguration.getCurrentMachineConfiguration()
																													.getDoubleProperty(	"device.camera" + i
																																									+ ".pixelsizenm",
																																							null);

			if (lPixelSizeInNanometers == null)
				break;

			Variable<Double> lPixelSizeInNanometersVariable = new Variable<Double>(	"Camera" + i
																																									+ "PixelSizeNm",
																																							lPixelSizeInNanometers);
			mCameraPixelSizeInNanometerVariableList.add(lPixelSizeInNanometersVariable);
		}

	}

	@Override
	public Variable<Double> getCameraPixelSizeInNanometerVariable(int pCameraIndex)
	{
		return mCameraPixelSizeInNanometerVariableList.get(pCameraIndex);
	}

	@Override
	public MicroscopeDeviceLists getDeviceLists()
	{
		return mDeviceLists;
	}

	@Override
	public <T> void addDevice(int pDeviceIndex, T pDevice)
	{
		mDeviceLists.addDevice(pDeviceIndex, pDevice);
	}

	@Override
	public <T> int getNumberOfDevices(Class<T> pClass)
	{
		return mDeviceLists.getNumberOfDevices(pClass);
	}

	@Override
	public <T> T getDevice(Class<T> pClass, int pIndex)
	{
		return mDeviceLists.getDevice(pClass, pIndex);
	}

	@Override
	public <T> ArrayList<T> getDevices(Class<T> pClass)
	{
		return mDeviceLists.getDevices(pClass);
	}

	@Override
	public void addChangeListener(ChangeListener pChangeListener)
	{
		for (final Object lDevice : mDeviceLists.getAllDeviceList())
		{
			if (lDevice instanceof HasChangeListenerInterface)
			{
				final HasChangeListenerInterface lHasChangeListenersInterface = (HasChangeListenerInterface) lDevice;
				lHasChangeListenersInterface.addChangeListener(pChangeListener);
			}
		}
	}

	@Override
	public void removeChangeListener(ChangeListener pChangeListener)
	{
		for (final Object lDevice : mDeviceLists.getAllDeviceList())
		{

			if (lDevice instanceof HasChangeListenerInterface)
			{
				final HasChangeListenerInterface lHasChangeListenersInterface = (HasChangeListenerInterface) lDevice;
				lHasChangeListenersInterface.removeChangeListener(pChangeListener);
			}
		}
	}

	public void setStackProcessingPipeline(	int pIndex,
																					StackProcessingPipeline pStackPipeline)
	{
		StackCameraDeviceInterface lDevice = mDeviceLists.getDevice(StackCameraDeviceInterface.class,
																																pIndex);
		StackProcessingPipeline lStackProcessingPipeline = mStackPipelines.get(pIndex);

		if (lStackProcessingPipeline != null)
		{
			lDevice.getStackVariable()
							.doNotSendUpdatesTo(lStackProcessingPipeline.getInputVariable());
		}

		lDevice.getStackVariable()
						.sendUpdatesTo(pStackPipeline.getInputVariable());

		mStackPipelines.put(pIndex, pStackPipeline);
	}

	@Override
	public boolean open()
	{
		synchronized (mAcquisitionLock)
		{
			boolean lIsOpen = true;

			for (final Object lDevice : mDeviceLists.getAllDeviceList())
			{

				if (lDevice instanceof OpenCloseDeviceInterface)
				{
					final OpenCloseDeviceInterface lOpenCloseDevice = (OpenCloseDeviceInterface) lDevice;

					System.out.println("Opening: " + lDevice);
					final boolean lResult = lOpenCloseDevice.open();
					System.out.println(lResult ? "successfully" : "failed!");

					lIsOpen &= lResult;
				}
			}

			return lIsOpen;
		}
	}

	@Override
	public boolean close()
	{
		synchronized (mAcquisitionLock)
		{
			boolean lIsClosed = true;
			for (final Object lDevice : mDeviceLists.getAllDeviceList())
			{
				if (lDevice instanceof OpenCloseDeviceInterface)
				{
					final OpenCloseDeviceInterface lOpenCloseDevice = (OpenCloseDeviceInterface) lDevice;

					System.out.println("Closing: " + lDevice);
					boolean lResult = lOpenCloseDevice.close();
					System.out.println(lResult ? "successfully" : "failed!");
					lIsClosed &= lResult;
				}
			}

			mStackRecyclerManager.clearAll();

			return lIsClosed;
		}
	}

	@Override
	public boolean start()
	{
		synchronized (mAcquisitionLock)
		{
			boolean lIsStarted = true;
			for (final Object lDevice : mDeviceLists.getAllDeviceList())
			{
				if (lDevice instanceof StartStopDeviceInterface)
				{
					final StartStopDeviceInterface lStartStopDevice = (StartStopDeviceInterface) lDevice;

					System.out.println("Starting: " + lDevice);
					boolean lResult = lStartStopDevice.start();
					System.out.println(lResult ? "successfully" : "failed!");
					lIsStarted &= lResult;
				}
			}

			return lIsStarted;
		}
	}

	@Override
	public boolean stop()
	{
		synchronized (mAcquisitionLock)
		{
			boolean lIsStopped = true;
			for (final Object lDevice : mDeviceLists.getAllDeviceList())
			{
				if (lDevice instanceof StartStopDeviceInterface)
				{
					final StartStopDeviceInterface lStartStopDevice = (StartStopDeviceInterface) lDevice;
					System.out.println("Stopping: " + lDevice);
					boolean lResult = lStartStopDevice.stop();
					System.out.println(lResult ? "successfully" : "failed!");
					lIsStopped &= lResult;
				}
			}

			return lIsStopped;
		}
	}

	@Override
	public void clearQueue()
	{
		synchronized (mAcquisitionLock)
		{
			for (final Object lDevice : mDeviceLists.getAllDeviceList())
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
	}

	@Override
	public void addCurrentStateToQueue()
	{
		synchronized (mAcquisitionLock)
		{
			for (final Object lDevice : mDeviceLists.getAllDeviceList())
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
	}

	@Override
	public void finalizeQueue()
	{
		synchronized (mAcquisitionLock)
		{
			// TODO: this should be put in a subclass specific to the way that we
			// trigger cameras...
			mDeviceLists.getDevice(SignalGeneratorInterface.class, 0)
									.addCurrentStateToQueue();

			for (final Object lDevice : mDeviceLists.getAllDeviceList())
			{
				if (lDevice instanceof StateQueueDeviceInterface)
					if (isActiveDevice(lDevice))
					{
						final StateQueueDeviceInterface lStateQueueDeviceInterface = (StateQueueDeviceInterface) lDevice;
						lStateQueueDeviceInterface.finalizeQueue();
					}
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
		StackProcessingPipeline lStackProcessingPipeline = mStackPipelines.get(pIndex);
		if (lStackProcessingPipeline != null)
			return lStackProcessingPipeline.getOutputVariable();
		else
			return mDeviceLists.getDevice(StackCameraDeviceInterface.class,
																		pIndex).getStackVariable();
	}


	@Override
	public void useRecycler(final String pName,
													final int pMinimumNumberOfAvailableStacks,
													final int pMaximumNumberOfAvailableObjects,
													final int pMaximumNumberOfLiveObjects)
	{
		int lNumberOfStackCameraDevices = getNumberOfDevices(StackCameraDeviceInterface.class);
		RecyclerInterface<StackInterface, StackRequest> lRecycler = mStackRecyclerManager.getRecycler(pName,
																																																	lNumberOfStackCameraDevices * pMaximumNumberOfAvailableObjects,
																																																	lNumberOfStackCameraDevices * pMaximumNumberOfLiveObjects);

		for (int i = 0; i < lNumberOfStackCameraDevices; i++)
			getDevice(StackCameraDeviceInterface.class, i).setMinimalNumberOfAvailableStacks(pMinimumNumberOfAvailableStacks);

		setRecycler(lRecycler);
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
		synchronized (mAcquisitionLock)
		{
			System.gc();
			final FutureBooleanList lFutureBooleanList = new FutureBooleanList();

			for (final Object lDevice : mDeviceLists.getAllDeviceList())
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
	}

	@Override
	public Boolean playQueueAndWait(long pTimeOut, TimeUnit pTimeUnit) throws InterruptedException,
																																		ExecutionException,
																																		TimeoutException
	{
		synchronized (mAcquisitionLock)
		{
			final FutureBooleanList lPlayQueue = playQueue();
			return lPlayQueue.get(pTimeOut, pTimeUnit);
		}
	}

	@Override
	public Boolean playQueueAndWaitForStacks(	long pTimeOut,
																						TimeUnit pTimeUnit)	throws InterruptedException,
																																ExecutionException,
																																TimeoutException
	{
		synchronized (mAcquisitionLock)
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

}
