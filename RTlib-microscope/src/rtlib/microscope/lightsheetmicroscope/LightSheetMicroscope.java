package rtlib.microscope.lightsheetmicroscope;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rtlib.cameras.CameraDeviceInterface;
import rtlib.core.device.SignalStartableLoopTaskDevice;

public class LightSheetMicroscope	extends
																	SignalStartableLoopTaskDevice
{

	ArrayList<CameraDeviceInterface> mCameraDeviceList = new ArrayList<>();

	public LightSheetMicroscope(String pDeviceName,
															boolean pOnlyStart,
															TimeUnit pTimeUnit)
	{
		super(pDeviceName, pOnlyStart, pTimeUnit);
	}

	@Override
	protected boolean loop()
	{
		return false;
	}

	public int addCameraDevice(CameraDeviceInterface pCameraDevice)
	{
		mCameraDeviceList.add(pCameraDevice);
		return mCameraDeviceList.size() - 1;
	}


}
