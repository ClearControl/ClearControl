package rtlib.signalcond.devices.SIM;

import rtlib.serial.Serial;
import rtlib.signalcond.ScalingAmplifierBaseDevice;
import rtlib.signalcond.ScalingAmplifierDeviceInterface;
import rtlib.signalcond.devices.SIM.adapters.GainAdapter;
import rtlib.signalcond.devices.SIM.adapters.OffsetAdapter;

public class SIM983ScalingAmplifierDevice	extends
																					ScalingAmplifierBaseDevice implements
																																		ScalingAmplifierDeviceInterface,
																																		SIMModuleInterface
{

	private static final String cDeviceName = "ScalingAmplifierBaseDevice";
	private SIM900MainframeDevice mSim900MainframeDevice;
	private final int mPort;

	public SIM983ScalingAmplifierDevice(SIM900MainframeDevice pSim900MainframeDevice,
																			int pPort)
	{
		super(pSim900MainframeDevice + "." + pPort + "." + cDeviceName);
		mSim900MainframeDevice = pSim900MainframeDevice;
		mPort = pPort;
	}

	@Override
	public boolean open()
	{
		boolean lOpen;
		try
		{
			lOpen = super.open();

			if (mSim900MainframeDevice.getSerialDevice() == null)
				return false;

			Serial lSerial = mSim900MainframeDevice.getSerialDevice()
																							.getSerial();

			lSerial.format("SNDT %d, \"TERM 2\"", mPort);

			final GainAdapter lGetDeviceIdAdapter = new GainAdapter(mSim900MainframeDevice,
																															mPort);
			mGainVariable = mSim900MainframeDevice.getSerialDevice()
																						.addSerialVariable(	"Gain",
																																lGetDeviceIdAdapter);

			final OffsetAdapter lGetWavelengthAdapter = new OffsetAdapter(mSim900MainframeDevice,
																																		mPort);
			mOffsetVariable = mSim900MainframeDevice.getSerialDevice()
																							.addSerialVariable(	"Offset",
																																	lGetWavelengthAdapter);

			return lOpen;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean close()
	{
		try
		{
			mSim900MainframeDevice.getSerialDevice().removeAllVariables();
			return super.close();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
