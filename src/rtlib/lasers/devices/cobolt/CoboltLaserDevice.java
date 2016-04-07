package rtlib.lasers.devices.cobolt;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.ObjectVariable;
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
	private final double mMaxPowerInMilliWatt;

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
		super("Cobolt" + pCoboltModelName);

		mSerialDevice = new SerialDevice(	"Cobolt" + pCoboltModelName,
																			pPortName,
																			115200);

		mCoboltModel = CoboltDeviceEnum.valueOf(pCoboltModelName);
		mMaxPowerInMilliWatt = pMaxPowerInMilliWatt;

		mDeviceIdVariable = new ObjectVariable<Integer>("DeviceId",
																										mCoboltModel.ordinal());

		mWavelengthVariable = new ObjectVariable<Integer>("WavelengthInNanoMeter",
																											CoboltDeviceEnum.getWavelengthInNanoMeter(mCoboltModel));

		mSpecInMilliWattPowerVariable = new ObjectVariable<Number>(	"SpecPowerInMilliWatt",
																																mMaxPowerInMilliWatt);

		mMaxPowerInMilliWattVariable = new ObjectVariable<Number>("MaxPowerInMilliWatt",
																															mMaxPowerInMilliWatt);

		mSetOperatingModeVariable = new ObjectVariable<Integer>("OperatingMode",
																														0);

		final SetPowerOnOffAdapter lSetPowerOnOffAdapter = new SetPowerOnOffAdapter();
		mPowerOnVariable = mSerialDevice.addSerialVariable(	"PowerOn",
																												lSetPowerOnOffAdapter);

		mLaserOnVariable = new ObjectVariable<Boolean>("LaserOn", false);

		final GetWorkingHoursAdapter lGetWorkingHoursAdapter = new GetWorkingHoursAdapter();
		mWorkingHoursVariable = mSerialDevice.addSerialVariable("WorkingHours",
																														lGetWorkingHoursAdapter);

		final GetSetTargetPowerAdapter lGetSetTargetPowerAdapter = new GetSetTargetPowerAdapter();
		mTargetPowerInMilliWattVariable = mSerialDevice.addSerialVariable("TargetPowerMilliWatt",
																																			lGetSetTargetPowerAdapter);

		final GetCurrentPowerAdapter lGetCurrentPowerAdapter = new GetCurrentPowerAdapter();
		mCurrentPowerInMilliWattVariable = mSerialDevice.addSerialVariable(	"CurrentPowerInMilliWatt",
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
		// mPowerOnVariable.setValue(true);
		return true;
	}

	@Override
	public boolean stop()
	{
		// mPowerOnVariable.setValue(false);
		return true;
	}

	@Override
	public boolean close()
	{
		return mSerialDevice.close();
	}

}
