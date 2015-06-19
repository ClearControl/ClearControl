package rtlib.signalcond.devices.SIM;

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

			final GainAdapter lGetDeviceIdAdapter = new GainAdapter(mSim900MainframeDevice,
																															mPort);
			mGainVariable = mSim900MainframeDevice.getSerialDevice()
																						.addSerialDoubleVariable(	"Gain",
																																lGetDeviceIdAdapter);

			final OffsetAdapter lGetWavelengthAdapter = new OffsetAdapter(mSim900MainframeDevice,
																																		mPort);
			mOffsetVariable = mSim900MainframeDevice.getSerialDevice()
																							.addSerialDoubleVariable(	"Offset",
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
			return super.close();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}




	
}
