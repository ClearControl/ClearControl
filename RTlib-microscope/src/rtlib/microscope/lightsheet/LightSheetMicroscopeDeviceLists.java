package rtlib.microscope.lightsheet;

import java.util.ArrayList;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.ao.slms.SpatialPhaseModulatorDeviceInterface;
import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.filterwheels.FilterWheelDeviceInterface;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.microscope.lightsheet.detection.DetectionArmInterface;
import rtlib.microscope.lightsheet.illumination.LightSheetInterface;
import rtlib.stack.StackInterface;
import rtlib.stack.processor.SameTypeStackProcessingPipeline;
import rtlib.stages.StageDeviceInterface;
import rtlib.symphony.devices.SignalGeneratorInterface;

public class LightSheetMicroscopeDeviceLists
{

	private final ArrayList<Object> mAllDeviceList = new ArrayList<>();

	private final ArrayList<StageDeviceInterface> mStageDeviceList = new ArrayList<>();
	private final ArrayList<LaserDeviceInterface> mLaserDeviceList = new ArrayList<>();
	private final ArrayList<SignalGeneratorInterface> mSignalGeneratorList = new ArrayList<>();
	private final ArrayList<LightSheetInterface> mLightSheetList = new ArrayList<>();
	private final ArrayList<DetectionArmInterface> mDetectionArmList = new ArrayList<>();
	private final ArrayList<FilterWheelDeviceInterface> mFilterWheelList = new ArrayList<>();
	private final ArrayList<SpatialPhaseModulatorDeviceInterface> mDetectionPhaseModulatorDeviceList = new ArrayList<>();
	private final ArrayList<SpatialPhaseModulatorDeviceInterface> mIlluminationPhaseModulatorDeviceList = new ArrayList<>();

	private final ArrayList<StackCameraDeviceInterface<UnsignedShortType, ShortOffHeapAccess>> mStackCameraDeviceList = new ArrayList<>();
	private final ArrayList<SameTypeStackProcessingPipeline<UnsignedShortType, ShortOffHeapAccess>> mStackPipelineList = new ArrayList<>();
	private final ArrayList<ObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>>> mStackVariableList = new ArrayList<>();



	public LightSheetMicroscopeDeviceLists()
	{

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

	public ObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>> getStackVariable(int pIndex)
	{
		return mStackVariableList.get(pIndex);
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

	public int addDetectionArmDevice(DetectionArmInterface pDetectionArm)
	{
		mAllDeviceList.add(pDetectionArm);
		mDetectionArmList.add(pDetectionArm);
		return mDetectionArmList.size() - 1;
	}

	public int getNumberOfDetectionArmDevices()
	{
		return mDetectionArmList.size();
	}

	public DetectionArmInterface getDetectionArmDevice(int pIndex)
	{
		return mDetectionArmList.get(pIndex);
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


	public int addLaserDevice(LaserDeviceInterface pLaserDeviceInterface)
	{
		mAllDeviceList.add(pLaserDeviceInterface);
		mLaserDeviceList.add(pLaserDeviceInterface);
		return mLaserDeviceList.size() - 1;
	}

	public int getNumberOfLaserDevices()
	{
		return mLaserDeviceList.size();
	}

	public LaserDeviceInterface getLaserDevice(int pIndex)
	{
		return mLaserDeviceList.get(pIndex);
	}

	public ArrayList<Object> getAllDeviceList()
	{
		return mAllDeviceList;
	}

	/**
	 * Interface method implementation
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder lBuilder = new StringBuilder();
		for (final Object lDevice : mAllDeviceList)
		{
			lBuilder.append(lDevice.toString());
			lBuilder.append("\n");
		}
		return lBuilder.toString();
	}


}
