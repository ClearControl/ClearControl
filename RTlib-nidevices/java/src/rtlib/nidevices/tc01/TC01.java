package rtlib.nidevices.tc01;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.ThermocoupleDevice;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.nidevices.tc01.bridj.TC01libLibrary;

public class TC01 extends ThermocoupleDevice
{

	private NIThermoCoupleType mThermoCoupleNIType = NIThermoCoupleType.K;
	private boolean mIsDevicePresent;

	public TC01(NIThermoCoupleType pNIThermoCoupleType,
							final int pDeviceIndex)
	{
		super("TC01");
		mThermoCoupleNIType = pNIThermoCoupleType;
		mIsDevicePresent = MachineConfiguration.getCurrentMachineConfiguration()
																						.getIsDevicePresent("ni.tc01",
																																pDeviceIndex);
	}

	@Override
	protected boolean loop()
	{
		if (!mIsDevicePresent)
			return false;
		DoubleVariable lTemperatureInCelciusVariable = getTemperatureInCelciusVariable();
		double lTemperatureInCelcius = TC01libLibrary.tC01lib(mThermoCoupleNIType.getValue());
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
