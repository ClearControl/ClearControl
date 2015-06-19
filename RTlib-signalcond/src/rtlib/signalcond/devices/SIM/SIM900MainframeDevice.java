package rtlib.signalcond.devices.SIM;

import java.util.ArrayList;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.serial.SerialDevice;
import rtlib.signalcond.devices.SIM.adapters.protocol.ProtocolSIM;

public class SIM900MainframeDevice extends NamedVirtualDevice
{
	private final SerialDevice mSerialDevice;

	private final ArrayList<SIMModuleInterface> mSIMModuleList = new ArrayList<>();

	public SIM900MainframeDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"stanford.SIM900",
																										pDeviceIndex,
																										"NULL"));
	}

	public SIM900MainframeDevice(final String pPortName)
	{
		super("SIM900MainframeDevice" + pPortName);

		mSerialDevice = new SerialDevice(	"SIM900MainframeDevice",
																			pPortName,
																			ProtocolSIM.cBaudRate);
	}

	public SerialDevice getSerialDevice()
	{
		return mSerialDevice;
	}


	@Override
	public boolean open()
	{
		boolean lOpen;
		try
		{
			lOpen = super.open();
			mSerialDevice.open();

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
			mSerialDevice.close();
			return super.close();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public String wrapCommand(int pPort, String pSetGainCommandString)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
