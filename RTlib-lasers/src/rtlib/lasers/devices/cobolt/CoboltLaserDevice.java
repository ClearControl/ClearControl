package rtlib.lasers.devices.cobolt;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.lasers.LaserDeviceBase;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.devices.cobolt.adapters.GetCurrentPowerAdapter;
import rtlib.lasers.devices.cobolt.adapters.GetSetTargetPowerAdapter;
import rtlib.lasers.devices.cobolt.adapters.GetWorkingHoursAdapter;
import rtlib.lasers.devices.cobolt.adapters.SetPowerOnOffAdapter;
import rtlib.lasers.devices.cobolt.models.CoboltDeviceEnum;
import rtlib.serial.SerialDevice;

public class CoboltLaserDevice extends LaserDeviceBase implements
																											LaserDeviceInterface
{
	private final SerialDevice mSerialDevice;

	private final CoboltDeviceEnum mCoboltModel;
	private final int mMaxPowerInMilliWatt;

	public CoboltLaserDevice(	final String pCoboltModelName,
														final int pMaxPowerInMilliWatt,
														final int pDeviceIndex)
	{
		this(	pCoboltModelName,
					pMaxPowerInMilliWatt,
					MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"laser.cobolt",
																										pDeviceIndex,
																										"NULL"));
	}

	public CoboltLaserDevice(	final String pCoboltModelName,
														final int pMaxPowerInMilliWatt,
														final String pPortName)
	{
		super("Cobol" + pCoboltModelName);

		mSerialDevice = new SerialDevice(	"Cobol" + pCoboltModelName,
																			pPortName,
																			115200);

		mCoboltModel = CoboltDeviceEnum.valueOf(pCoboltModelName);
		mMaxPowerInMilliWatt = pMaxPowerInMilliWatt;

		mDeviceIdVariable = new DoubleVariable(	"DeviceId",
																						mCoboltModel.ordinal());

		mWavelengthVariable = new DoubleVariable(	"WavelengthInNanoMeter",
																							CoboltDeviceEnum.getWavelengthInNanoMeter(mCoboltModel));

		mSpecInMilliWattPowerVariable = new DoubleVariable(	"SpecPowerInMilliWatt",
																												mMaxPowerInMilliWatt);

		mMaxPowerInMilliWattVariable = new DoubleVariable("MaxPowerInMilliWatt",
																											mMaxPowerInMilliWatt);

		mSetOperatingModeVariable = new DoubleVariable("OperatingMode", 0);

		final SetPowerOnOffAdapter lSetPowerOnOffAdapter = new SetPowerOnOffAdapter();
		mPowerOnVariable = mSerialDevice.addSerialBooleanVariable("PowerOn",
																								lSetPowerOnOffAdapter);

		mLaserOnVariable = new BooleanVariable("LaserOn", true);

		final GetWorkingHoursAdapter lGetWorkingHoursAdapter = new GetWorkingHoursAdapter();
		mWorkingHoursVariable = mSerialDevice.addSerialDoubleVariable("WorkingHours",
																										lGetWorkingHoursAdapter);

		final GetSetTargetPowerAdapter lGetSetTargetPowerAdapter = new GetSetTargetPowerAdapter();
		mTargetPowerInMilliWattVariable = mSerialDevice.addSerialDoubleVariable("TargetPowerMilliWatt",
																															lGetSetTargetPowerAdapter);

		final GetCurrentPowerAdapter lGetCurrentPowerAdapter = new GetCurrentPowerAdapter();
		mCurrentPowerInMilliWattVariable = mSerialDevice.addSerialDoubleVariable(	"CurrentPowerInMilliWatt",
																																lGetCurrentPowerAdapter);
	}

	@Override
	public boolean open()
	{
		return mSerialDevice.open();
	}

	@Override
	public boolean start()
	{
		boolean lStart = mSerialDevice.start();
		mPowerOnVariable.setValue(true);
		return lStart;
	}

	@Override
	public boolean stop()
	{
		mPowerOnVariable.setValue(false);
		return mSerialDevice.stop();
	}

	@Override
	public boolean close()
	{
		return mSerialDevice.close();
	}

}
