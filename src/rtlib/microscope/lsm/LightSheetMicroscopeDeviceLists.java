package rtlib.microscope.lsm;

import java.util.ArrayList;

import rtlib.ao.slms.SpatialPhaseModulatorDeviceInterface;
import rtlib.cameras.StackCameraDeviceInterface;
import rtlib.core.device.SwitchingDeviceInterface;
import rtlib.core.variable.Variable;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.microscope.lsm.component.detection.DetectionArmInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;
import rtlib.optomech.OptoMechDeviceInterface;
import rtlib.optomech.filterwheels.FilterWheelDeviceInterface;
import rtlib.scripting.engine.ScriptingEngine;
import rtlib.stack.StackInterface;
import rtlib.stack.processor.StackProcessingPipeline;
import rtlib.stages.StageDeviceInterface;
import rtlib.symphony.devices.SignalGeneratorInterface;

public class LightSheetMicroscopeDeviceLists
{
	private LightSheetMicroscope mLightSheetMicroscope;

	private final ArrayList<Object> mAllDeviceList = new ArrayList<>();

	private final ArrayList<StageDeviceInterface> mStageDeviceList = new ArrayList<>();
	private final ArrayList<LaserDeviceInterface> mLaserDeviceList = new ArrayList<>();
	private final ArrayList<SignalGeneratorInterface> mSignalGeneratorList = new ArrayList<>();
	private final ArrayList<LightSheetInterface> mLightSheetList = new ArrayList<>();
	private final ArrayList<DetectionArmInterface> mDetectionArmList = new ArrayList<>();
	private final ArrayList<FilterWheelDeviceInterface> mFilterWheelList = new ArrayList<>();
	private final ArrayList<OptoMechDeviceInterface> mOptoMechDeviceList = new ArrayList<>();
	private final ArrayList<SpatialPhaseModulatorDeviceInterface> mDetectionPhaseModulatorDeviceList = new ArrayList<>();
	private final ArrayList<SpatialPhaseModulatorDeviceInterface> mIlluminationPhaseModulatorDeviceList = new ArrayList<>();

	private final ArrayList<StackCameraDeviceInterface> mStackCameraDeviceList = new ArrayList<>();
	private final ArrayList<StackProcessingPipeline> mStackPipelineList = new ArrayList<>();
	private final ArrayList<Variable<StackInterface>> mStackVariableList = new ArrayList<>();

	private final ArrayList<ScriptingEngine> mScriptingEngineList = new ArrayList<>();

	private SwitchingDeviceInterface mLightSheetSwitch;

	public LightSheetMicroscopeDeviceLists(LightSheetMicroscope pLightSheetMicroscope)
	{
		mLightSheetMicroscope = pLightSheetMicroscope;

	}

	public int addStackCameraDevice(StackCameraDeviceInterface pCameraDevice,
																	StackProcessingPipeline pStackPipeline)
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

	public StackCameraDeviceInterface getStackCameraDevice(int pIndex)
	{
		return mStackCameraDeviceList.get(pIndex);
	}

	public StackProcessingPipeline getStackPipeline(int pIndex)
	{
		return mStackPipelineList.get(pIndex);
	}

	public Variable<StackInterface> getStackVariable(int pIndex)
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

	public FilterWheelDeviceInterface getFilterWheelDevice(int pIndex)
	{
		return mFilterWheelList.get(pIndex);
	}

	public int addOptoMechanicalDevice(OptoMechDeviceInterface pOpticalSwitchDeviceInterface)
	{
		mAllDeviceList.add(pOpticalSwitchDeviceInterface);
		mOptoMechDeviceList.add(pOpticalSwitchDeviceInterface);
		return mOptoMechDeviceList.size() - 1;
	}

	public int getNumberOfOpticalSwitchDevices()
	{
		return mOptoMechDeviceList.size();
	}

	public OptoMechDeviceInterface getOpticalSwitchDevice(int pIndex)
	{
		return mOptoMechDeviceList.get(pIndex);
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

	public SwitchingDeviceInterface getLightSheetSwitchingDevice()
	{
		return mLightSheetSwitch;
	}

	public void setLightSheetSelectorDevice(SwitchingDeviceInterface pDeviceSwitchingInterface)
	{
		mLightSheetSwitch = pDeviceSwitchingInterface;
	}

	public int addScriptingEngine(ScriptingEngine pScriptingEngine)
	{
		mAllDeviceList.add(pScriptingEngine);
		mScriptingEngineList.add(pScriptingEngine);
		pScriptingEngine.set("lsm", mLightSheetMicroscope);
		return mScriptingEngineList.size() - 1;
	}

	public int getNumberOfScriptingEngines()
	{
		return mScriptingEngineList.size();
	}

	public ScriptingEngine getScriptingEngine(int pIndex)
	{
		return mScriptingEngineList.get(pIndex);
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
