package rtlib.microscope.lightsheet;

import java.util.ArrayList;
import java.util.concurrent.Future;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.ao.slms.SpatialPhaseModulatorDeviceInterface;
import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.core.concurrent.future.FutureBooleanList;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.SignalStartableLoopTaskDevice;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.filterwheels.FilterWheelDeviceInterface;
import rtlib.microscope.lightsheet.detection.DetectionPathInterface;
import rtlib.microscope.lightsheet.illumination.LightSheetInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.processor.SameTypeStackProcessingPipeline;
import rtlib.stages.StageDeviceInterface;
import rtlib.symphony.devices.SignalGeneratorInterface;

public class LightSheetMicroscope	extends
																	SignalStartableLoopTaskDevice	implements
																																StateQueueDeviceInterface
{

	private final ArrayList<Object> mAllDeviceList = new ArrayList<>();

	private final ArrayList<StageDeviceInterface> mStageDeviceList = new ArrayList<>();
	private final ArrayList<SignalGeneratorInterface> mSignalGeneratorList = new ArrayList<>();
	private final ArrayList<LightSheetInterface> mLightSheetList = new ArrayList<>();
	private final ArrayList<DetectionPathInterface> mDetectionPathList = new ArrayList<>();
	private final ArrayList<FilterWheelDeviceInterface> mFilterWheelList = new ArrayList<>();
	private final ArrayList<SpatialPhaseModulatorDeviceInterface> mDetectionPhaseModulatorDeviceList = new ArrayList<>();
	private final ArrayList<SpatialPhaseModulatorDeviceInterface> mIlluminationPhaseModulatorDeviceList = new ArrayList<>();

	private final ArrayList<StackCameraDeviceInterface<UnsignedShortType, ShortOffHeapAccess>> mStackCameraDeviceList = new ArrayList<>();
	private final ArrayList<SameTypeStackProcessingPipeline<UnsignedShortType, ShortOffHeapAccess>> mStackPipelineList = new ArrayList<>();
	private final ArrayList<ObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>>> mStackVariableList = new ArrayList<>();

	public LightSheetMicroscope(String pDeviceName)
	{
		super(pDeviceName, false);
	}

	public int addStackCameraDevice(StackCameraDeviceInterface<UnsignedShortType, ShortOffHeapAccess> pCameraDevice,
																	SameTypeStackProcessingPipeline<UnsignedShortType, ShortOffHeapAccess> pStackPipeline)
	{
		mStackCameraDeviceList.add(pCameraDevice);
		mAllDeviceList.add(pCameraDevice);
		if (pStackPipeline != null)
		{
			mAllDeviceList.add(pStackPipeline);
			mStackPipelineList.add(pStackPipeline);
			pCameraDevice.getStackVariable()
										.sendUpdatesTo(pStackPipeline.getInputVariable());
			mStackVariableList.add(pStackPipeline.getOutputVariable());
		}
		else
		{
			mStackVariableList.add(pCameraDevice.getStackVariable());
		}
		return mStackCameraDeviceList.size() - 1;
	}

	public int getNumberOfStackCameraDevices()
	{
		return mStackCameraDeviceList.size();
	}

	public StackCameraDeviceInterface<UnsignedShortType, ShortOffHeapAccess> getStackCameraDevice(int pIndex)
	{
		return mStackCameraDeviceList.get(pIndex);
	}

	public SameTypeStackProcessingPipeline<UnsignedShortType, ShortOffHeapAccess> getStackPipeline(int pIndex)
	{
		return mStackPipelineList.get(pIndex);
	}

	public int addSignalGeneratorDevice(SignalGeneratorInterface pSignalGenerator)
	{
		mAllDeviceList.add(pSignalGenerator);
		mSignalGeneratorList.add(pSignalGenerator);
		return mSignalGeneratorList.size() - 1;
	}

	public int getNumberOfSignalGeneratorDevices()
	{
		return mSignalGeneratorList.size();
	}

	public SignalGeneratorInterface getSignalGeneratorDevice(int pIndex)
	{
		return mSignalGeneratorList.get(pIndex);
	}

	public int addLightSheetDevice(LightSheetInterface pLightSheet)
	{
		mAllDeviceList.add(pLightSheet);
		mLightSheetList.add(pLightSheet);
		return mLightSheetList.size() - 1;
	}

	public int getNumberOfLightSheetDevices()
	{
		return mLightSheetList.size();
	}

	public LightSheetInterface getLightSheetDevice(int pIndex)
	{
		return mLightSheetList.get(pIndex);
	}

	public int addDetectionPathDevice(DetectionPathInterface pDetectionPath)
	{
		mAllDeviceList.add(pDetectionPath);
		mDetectionPathList.add(pDetectionPath);
		return mDetectionPathList.size() - 1;
	}

	public int getNumberOfDetectionPathDevices()
	{
		return mDetectionPathList.size();
	}

	public DetectionPathInterface getDetectionPathDevice(int pIndex)
	{
		return mDetectionPathList.get(pIndex);
	}

	public int addFilterWheelDevice(FilterWheelDeviceInterface pFilterWheelDeviceInterface)
	{
		mAllDeviceList.add(pFilterWheelDeviceInterface);
		mFilterWheelList.add(pFilterWheelDeviceInterface);
		return mFilterWheelList.size() - 1;
	}

	public int getNumberOfFilterWheelDevices()
	{
		return mFilterWheelList.size();
	}

	public FilterWheelDeviceInterface getFilterWheelDeviceDevice(int pIndex)
	{
		return mFilterWheelList.get(pIndex);
	}

	public int addDetectionPhaseModulatorDevice(SpatialPhaseModulatorDeviceInterface pSpatialPhaseModulatorDeviceInterface)
	{
		mAllDeviceList.add(pSpatialPhaseModulatorDeviceInterface);
		mDetectionPhaseModulatorDeviceList.add(pSpatialPhaseModulatorDeviceInterface);
		return mDetectionPhaseModulatorDeviceList.size() - 1;
	}

	public int getNumberOfDetectionPhaseModulatorDevices()
	{
		return mDetectionPhaseModulatorDeviceList.size();
	}

	public SpatialPhaseModulatorDeviceInterface getDetectionPhaseModulatorDevice(int pIndex)
	{
		return mDetectionPhaseModulatorDeviceList.get(pIndex);
	}

	public int addIlluminationPhaseModulatorDevice(SpatialPhaseModulatorDeviceInterface pSpatialPhaseModulatorDeviceInterface)
	{
		mAllDeviceList.add(pSpatialPhaseModulatorDeviceInterface);
		mIlluminationPhaseModulatorDeviceList.add(pSpatialPhaseModulatorDeviceInterface);
		return mIlluminationPhaseModulatorDeviceList.size() - 1;
	}

	public int getNumberOfIlluminationPhaseModulatorDevices()
	{
		return mIlluminationPhaseModulatorDeviceList.size();
	}

	public SpatialPhaseModulatorDeviceInterface getIlluminationPhaseModulatorDevice(int pIndex)
	{
		return mIlluminationPhaseModulatorDeviceList.get(pIndex);
	}

	public int addStageDevice(StageDeviceInterface pStageDeviceInterface)
	{
		mAllDeviceList.add(pStageDeviceInterface);
		mStageDeviceList.add(pStageDeviceInterface);
		return mStageDeviceList.size() - 1;
	}

	public int getNumberOfStageDevices()
	{
		return mStageDeviceList.size();
	}

	public StageDeviceInterface getStageDevice(int pIndex)
	{
		return mStageDeviceList.get(pIndex);
	}

	@Override
	public boolean open()
	{
		boolean lIsOpen = super.open();
		for (Object lDevice : mAllDeviceList)
		{
			if(lDevice instanceof OpenCloseDeviceInterface)
			{
				OpenCloseDeviceInterface lOpenCloseDevice = (OpenCloseDeviceInterface)lDevice;
				lIsOpen &= lOpenCloseDevice.open();
			}
		}

		return lIsOpen;
	}

	@Override
	public boolean close()
	{
		boolean lIsClosed = true;
		for (Object lDevice : mAllDeviceList)
		{
			if (lDevice instanceof OpenCloseDeviceInterface)
			{
				OpenCloseDeviceInterface lOpenCloseDevice = (OpenCloseDeviceInterface) lDevice;
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
		for (Object lDevice : mAllDeviceList)
		{
			if (lDevice instanceof StartStopDeviceInterface)
			{
				StartStopDeviceInterface lStartStopDevice = (StartStopDeviceInterface) lDevice;
				lIsStarted &= lStartStopDevice.start();
			}
		}

		return lIsStarted;
	}

	@Override
	public boolean stop()
	{
		boolean lIsStopped = super.start();
		for (Object lDevice : mAllDeviceList)
		{
			if (lDevice instanceof StartStopDeviceInterface)
			{
				StartStopDeviceInterface lStartStopDevice = (StartStopDeviceInterface) lDevice;
				lIsStopped &= lStartStopDevice.stop();
			}
		}

		return lIsStopped;
	}

	@Override
	public void clearQueue()
	{
		for (final Object lDevice : mAllDeviceList)
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
		for (final Object lDevice : mAllDeviceList)
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
		for (final Object lDevice : mAllDeviceList)
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

		for (final Object lDevice : mAllDeviceList)
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
