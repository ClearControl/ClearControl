package rtlib.microscope.lsm;

import static java.lang.Math.toIntExact;

import java.util.concurrent.TimeUnit;

import rtlib.device.queue.StateQueueDeviceInterface;
import rtlib.microscope.MicroscopeBase;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;
import rtlib.microscope.lsm.component.lightsheet.si.StructuredIlluminationPatternInterface;

public class LightSheetMicroscope extends MicroscopeBase implements
																										StateQueueDeviceInterface,
																										LightSheetMicroscopeInterface
{

	
	


	public LightSheetMicroscope(String pDeviceName)
	{
		super(pDeviceName, false);
		
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
										.getSwitchVariable(pLightSheetIndex)
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
														.getSwitchVariable(pLightSheetIndex)
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
