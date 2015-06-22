package rtlib.sensors.devices.tc01;

import org.bridj.Pointer;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.sensors.TemperatureSensorDeviceBase;
import rtlib.sensors.devices.tc01.bridj.TC01libLibrary;

public class TC01 extends TemperatureSensorDeviceBase
{

	private NIThermoCoupleType mThermoCoupleNIType = NIThermoCoupleType.K;
	private final boolean mIsDevicePresent;
	private final Pointer<Byte> mPhysicalChannelPointer;

	public TC01(String pPhysicalChannel,
							NIThermoCoupleType pNIThermoCoupleType,
							final int pDeviceIndex)
	{
		super("TC01");
		mThermoCoupleNIType = pNIThermoCoupleType;
		mIsDevicePresent = MachineConfiguration.getCurrentMachineConfiguration()
																						.getIsDevicePresent("ni.tc01",
																																pDeviceIndex);

		mPhysicalChannelPointer = Pointer.pointerToCString(pPhysicalChannel);
	}

	@Override
	protected boolean loop()
	{
		if (!mIsDevicePresent)
			return false;
		final DoubleVariable lTemperatureInCelciusVariable = getTemperatureInCelciusVariable();
		final double lTemperatureInCelcius = TC01libLibrary.tC01lib(mPhysicalChannelPointer,
																													mThermoCoupleNIType.getValue());
		// System.out.println(lTemperatureInCelcius);
		lTemperatureInCelciusVariable.setValue(lTemperatureInCelcius);
		return true;
	}

	@Override
	public boolean open()
	{
		if (!mIsDevicePresent)
			return false;
		return true;
	}

	@Override
	public boolean close()
	{
		if (!mIsDevicePresent)
			return false;
		return true;
	}

}
