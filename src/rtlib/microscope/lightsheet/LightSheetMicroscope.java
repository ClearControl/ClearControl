package rtlib.microscope.lightsheet;

import static java.lang.Math.toIntExact;

import java.util.concurrent.TimeUnit;

import rtlib.device.queue.StateQueueDeviceInterface;
import rtlib.device.switches.SwitchingDeviceInterface;
import rtlib.hardware.cameras.StackCameraDeviceInterface;
import rtlib.hardware.lasers.LaserDeviceInterface;
import rtlib.microscope.MicroscopeBase;
import rtlib.microscope.lightsheet.component.detection.DetectionArmInterface;
import rtlib.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import rtlib.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

public class LightSheetMicroscope extends MicroscopeBase implements
																												StateQueueDeviceInterface,
																												LightSheetMicroscopeInterface
{
	
	private SwitchingDeviceInterface mLightSheetSwitch;

	public LightSheetMicroscope(String pDeviceName)
	{
		super(pDeviceName, false);

	}

	public void setLightSheetSelectorDevice(SwitchingDeviceInterface pDeviceSwitchingInterface)
	{
		mLightSheetSwitch = pDeviceSwitchingInterface;
	}
	
	private SwitchingDeviceInterface getLightSheetSwitchingDevice()
	{
		return mLightSheetSwitch;
	}
	
	
	@Override
	protected boolean loop()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void sendStacksToNull()
	{
		for (int i = 0; i < getDeviceLists().getNumberOfDevices(StackCameraDeviceInterface.class); i++)
		{
			getStackVariable(i).addSetListener((pCurrentValue, pNewValue) -> {
				pNewValue.release();
			});

		}
	}

	@Override
	public void setCameraWidthHeight(long pWidth, long pHeight)
	{
		for (int i = 0; i < getDeviceLists().getNumberOfDevices(StackCameraDeviceInterface.class); i++)
		{
			StackCameraDeviceInterface lDevice = getDeviceLists().getDevice(StackCameraDeviceInterface.class,
																																			i);
			lDevice.getStackWidthVariable().set(pWidth);
			lDevice.getStackHeightVariable().set(pHeight);
		}

		for (int i = 0; i < getDeviceLists().getNumberOfDevices(LightSheetInterface.class); i++)
		{
			getDeviceLists().getDevice(LightSheetInterface.class, i)
											.getImageHeightVariable()
											.set(pHeight);
		}
	};

	@Override
	public int getCameraWidth(int pCameraDeviceIndex)
	{
		return getDeviceLists().getDevice(StackCameraDeviceInterface.class,
																			pCameraDeviceIndex)
														.getStackWidthVariable()
														.get()
														.intValue();
	};

	@Override
	public int getCameraHeight(int pCameraDeviceIndex)
	{
		return getDeviceLists().getDevice(StackCameraDeviceInterface.class,
																			pCameraDeviceIndex)
														.getStackHeightVariable()
														.get()
														.intValue();
	};

	@Override
	public void setExposure(long pValue, TimeUnit pTimeUnit)
	{
		final double lExposureTimeInMicroseconds = TimeUnit.MICROSECONDS.convert(	pValue,
																																							pTimeUnit);

		for (StackCameraDeviceInterface lStackCamera : getDeviceLists().getDevices(StackCameraDeviceInterface.class))
			lStackCamera.getExposureInMicrosecondsVariable()
									.set(lExposureTimeInMicroseconds);

		for (LightSheetInterface lLightSheet : getDeviceLists().getDevices(LightSheetInterface.class))
			lLightSheet.getEffectiveExposureInMicrosecondsVariable()
									.set(lExposureTimeInMicroseconds);
	};

	@Override
	public long getExposure(int pCameraDeviceIndex, TimeUnit pTimeUnit)
	{

		long lExposureInMicroseconds = getDeviceLists().getDevice(StackCameraDeviceInterface.class,
																															pCameraDeviceIndex)
																										.getExposureInMicrosecondsVariable()
																										.get()
																										.longValue();

		long lExposureInProvidedUnit = pTimeUnit.convert(	lExposureInMicroseconds,
																											TimeUnit.MICROSECONDS);

		return toIntExact(lExposureInProvidedUnit);
	};

	public void zero()
	{
		for (int i = 0; i < getDeviceLists().getNumberOfDevices(DetectionArmInterface.class); i++)
		{
			setDZ(i, 0);
			setC(i, true);
		}

		for (int i = 0; i < getDeviceLists().getNumberOfDevices(LightSheetInterface.class); i++)
		{
			setIX(i, 0);
			setIY(i, 0);
			setIZ(i, 0);
			setIA(i, 0);
			setIB(i, 0);
			setIZ(i, 0);
			setIH(i, 0);

			for (int j = 0; j < getDeviceLists().getNumberOfDevices(LaserDeviceInterface.class); j++)
			{
				setIPatternOnOff(i, j, false);
			}
		}

	}

	@Override
	public void setC(int pCameraIndex, boolean pKeepImage)
	{
		getDeviceLists().getDevice(	StackCameraDeviceInterface.class,
																pCameraIndex)
										.getKeepPlaneVariable()
										.set(pKeepImage);
	};

	@Override
	public boolean getC(int pCameraIndex)
	{
		return getDeviceLists().getDevice(StackCameraDeviceInterface.class,
																			pCameraIndex)
														.getKeepPlaneVariable()
														.get();
	}

	@Override
	public void setC(boolean pKeepImage)
	{
		int lNumberOfStackCameraDevices = getDeviceLists().getNumberOfDevices(StackCameraDeviceInterface.class);

		for (int c = 0; c < lNumberOfStackCameraDevices; c++)
			getDeviceLists().getDevice(StackCameraDeviceInterface.class, c)
											.getKeepPlaneVariable()
											.set(pKeepImage);

	}

	@Override
	public void setLO(int pLaserIndex, boolean pLaserOnOff)
	{
		getDeviceLists().getDevice(	LaserDeviceInterface.class,
																pLaserIndex)
										.getLaserOnVariable()
										.set(pLaserOnOff);
	};

	@Override
	public boolean getLO(int pLaserIndex)
	{
		return getDeviceLists().getDevice(LaserDeviceInterface.class,
																			pLaserIndex)
														.getLaserOnVariable()
														.get();
	}

	@Override
	public void setLP(int pLaserIndex, double pLaserPowerInmW)
	{
		getDeviceLists().getDevice(	LaserDeviceInterface.class,
																pLaserIndex)
										.getTargetPowerInMilliWattVariable()
										.set(pLaserPowerInmW);
	};

	@Override
	public double getLP(int pLaserIndex)
	{
		return getDeviceLists().getDevice(LaserDeviceInterface.class,
																			pLaserIndex)
														.getTargetPowerInMilliWattVariable()
														.get()
														.doubleValue();
	}

	@Override
	public void setDZ(int pDetectionArmIndex, double pValue)
	{
		getDeviceLists().getDevice(	DetectionArmInterface.class,
																pDetectionArmIndex)
										.getZVariable()
										.set(pValue);
	};

	@Override
	public double getDZ(int pDetectionArmIndex)
	{
		return getDeviceLists().getDevice(DetectionArmInterface.class,
																			pDetectionArmIndex)
														.getZVariable()
														.get().doubleValue();
	}

	@Override
	public void setI(int pLightSheetIndex)
	{
		int lNumberOfSwitchableDevices = getLightSheetSwitchingDevice().getNumberOfSwitches();
		for (int i = 0; i < lNumberOfSwitchableDevices; i++)
			setI(i, i == pLightSheetIndex);

		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex).update();
	};

	@Override
	public void setI(int pLightSheetIndex, boolean pOnOff)
	{
		getLightSheetSwitchingDevice().getSwitchVariable(pLightSheetIndex)
																	.set(pOnOff);
	};



	@Override
	public void setI(boolean pOnOff)
	{
		int lNumberOfSwitchableDevices = getLightSheetSwitchingDevice().getNumberOfSwitches();
		for (int i = 0; i < lNumberOfSwitchableDevices; i++)
			setI(i, pOnOff);
	};

	@Override
	public boolean getI(int pLightSheetIndex)
	{
		return getLightSheetSwitchingDevice().getSwitchVariable(pLightSheetIndex)
																					.get();
	}

	@Override
	public void setIX(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getXVariable()
										.set(pValue);
	};

	@Override
	public double getIX(int pLightSheetIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getXVariable()
														.get();
	}

	@Override
	public void setIY(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getYVariable()
										.set(pValue);
	};

	@Override
	public double getIY(int pLightSheetIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getYVariable()
														.get();
	}

	@Override
	public void setIZ(int pIndex, double pValue)
	{
		getDeviceLists().getDevice(LightSheetInterface.class, pIndex)
										.getZVariable()
										.set(pValue);
	};

	@Override
	public double getIZ(int pLightSheetIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getZVariable()
														.get().doubleValue();
	}

	@Override
	public void setIA(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getAlphaInDegreesVariable()
										.set(pValue);
	};

	@Override
	public double getIA(int pLightSheetIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getAlphaInDegreesVariable()
														.get();
	}

	@Override
	public void setIB(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getBetaInDegreesVariable()
										.set(pValue);
	};

	@Override
	public double getIB(int pLightSheetIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getBetaInDegreesVariable()
														.get();
	}

	@Override
	public void setIW(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getWidthVariable()
										.set(pValue);
	};

	@Override
	public double getIW(int pLightSheetIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getWidthVariable()
														.get();
	}

	@Override
	public void setIH(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getHeightVariable()
										.set(pValue);
	}

	@Override
	public double getIH(int pLightSheetIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getHeightVariable()
														.get();
	}

	@Override
	public void setILO(boolean pOn)
	{
		int lNumberOfLightSheets = getDeviceLists().getNumberOfDevices(LightSheetInterface.class);

		for (int l = 0; l < lNumberOfLightSheets; l++)
		{
			LightSheetInterface lLightSheetDevice = getDeviceLists().getDevice(	LightSheetInterface.class,
																																					l);
			for (int i = 0; i < lLightSheetDevice.getNumberOfLaserDigitalControls(); i++)
				lLightSheetDevice.getLaserOnOffArrayVariable(i).set(pOn);
		}
	};

	@Override
	public void setILO(int pLightSheetIndex, boolean pOn)
	{
		LightSheetInterface lLightSheetDevice = getDeviceLists().getDevice(	LightSheetInterface.class,
																																				pLightSheetIndex);
		for (int i = 0; i < lLightSheetDevice.getNumberOfLaserDigitalControls(); i++)
			lLightSheetDevice.getLaserOnOffArrayVariable(i).set(pOn);
	};

	@Override
	public void setILO(	int pLightSheetIndex,
											int pLaserIndex,
											boolean pOn)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getLaserOnOffArrayVariable(pLaserIndex)
										.set(pOn);
	};

	@Override
	public boolean getILO(int pLightSheetIndex, int pLaserIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getLaserOnOffArrayVariable(pLaserIndex)
														.get();
	}

	@Override
	public void setIP(int pLightSheetIndex, double pValue)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getPowerVariable()
										.set(pValue);
	}

	@Override
	public double getIP(int pLightSheetIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getPowerVariable()
														.get();
	}

	@Override
	public void setIPA(boolean pAdapt)
	{
		int lNumberOfLightSheets = getDeviceLists().getNumberOfDevices(LightSheetInterface.class);

		for (int i = 0; i < lNumberOfLightSheets; i++)
			getDeviceLists().getDevice(LightSheetInterface.class, i)
											.getAdaptPowerToWidthHeightVariable()
											.set(pAdapt);

	}

	@Override
	public void setIPA(int pLightSheetIndex, boolean pAdapt)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getAdaptPowerToWidthHeightVariable()
										.set(pAdapt);

	}

	@Override
	public boolean getIPA(int pLightSheetIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getAdaptPowerToWidthHeightVariable()
														.get();
	}

	@Override
	public void setIPatternOnOff(	int pLightSheetIndex,
																int pLaserIndex,
																boolean pOnOff)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getSIPatternOnOffVariable(pLaserIndex)
										.set(pOnOff);
	}

	@Override
	public boolean getIPatternOnOff(int pLightSheetIndex,
																	int pLaserIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getSIPatternOnOffVariable(pLaserIndex)
														.get();
	}

	@Override
	public void setIPattern(int pLightSheetIndex,
													int pLaserIndex,
													StructuredIlluminationPatternInterface pPattern)
	{
		getDeviceLists().getDevice(	LightSheetInterface.class,
																pLightSheetIndex)
										.getSIPatternVariable(pLaserIndex)
										.set(pPattern);
	}

	@Override
	public StructuredIlluminationPatternInterface getIPattern(int pLightSheetIndex,
																														int pLaserIndex)
	{
		return getDeviceLists().getDevice(LightSheetInterface.class,
																			pLightSheetIndex)
														.getSIPatternVariable(pLaserIndex)
														.get();
	}

	public int getNumberOfDOF()
	{
		final int lNumberOfLightSheetsDOFs = getDeviceLists().getNumberOfDevices(LightSheetInterface.class) * 7;
		final int lNumberOfDetectionArmDOFs = getDeviceLists().getNumberOfDevices(DetectionArmInterface.class) * 1;

		return lNumberOfLightSheetsDOFs + lNumberOfDetectionArmDOFs;
	}

	@Override
	public String toString()
	{
		return String.format(	"LightSheetMicroscope: \n%s\n",
													mLSMDeviceLists.toString());
	}

}
