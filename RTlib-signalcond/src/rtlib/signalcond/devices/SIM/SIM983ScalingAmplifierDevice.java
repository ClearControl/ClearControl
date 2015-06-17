package rtlib.signalcond.devices.SIM;

import rtlib.serial.SerialDevice;
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
	private SerialDevice mSerialDevice;


	public SIM983ScalingAmplifierDevice()
	{
		super(cDeviceName);
	}

	public SerialDevice getSerialDevice()
	{
		return mSerialDevice;
	}

	@Override
	public void setSerialDevice(SerialDevice pSerialDevice)
	{
		mSerialDevice = pSerialDevice;
		setName(cDeviceName + "-" + mSerialDevice.getName());
	}

	@Override
	public boolean open()
	{
		boolean lOpen;
		try
		{
			lOpen = super.open();
			
			if(mSerialDevice==null)
				return false;

			final GainAdapter lGetDeviceIdAdapter = new GainAdapter();
			mGainVariable = mSerialDevice.addSerialDoubleVariable("Gain",
																																lGetDeviceIdAdapter);

			final OffsetAdapter lGetWavelengthAdapter = new OffsetAdapter();
			mOffsetVariable = mSerialDevice.addSerialDoubleVariable("Offset",
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
