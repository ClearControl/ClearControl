package rtlib.lasers.devices.cobolt;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.LaserDeviceBase;
import rtlib.lasers.devices.cobolt.adapters.GetCurrentPowerAdapter;
import rtlib.lasers.devices.cobolt.adapters.GetSetTargetPowerAdapter;
import rtlib.lasers.devices.cobolt.adapters.GetWorkingHoursAdapter;
import rtlib.lasers.devices.cobolt.adapters.SetPowerOnOffAdapter;
import rtlib.lasers.devices.cobolt.models.CoboltDeviceEnum;

public class CoboltLaserDevice extends LaserDeviceBase implements
																											LaserDeviceInterface
{
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
		super("Cobol" + pCoboltModelName, pPortName, 115200);
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
		mPowerOnVariable = addSerialBooleanVariable("PowerOn",
																								lSetPowerOnOffAdapter);

		mLaserOnVariable = new BooleanVariable("LaserOn", true);

		final GetWorkingHoursAdapter lGetWorkingHoursAdapter = new GetWorkingHoursAdapter();
		mWorkingHoursVariable = addSerialDoubleVariable("WorkingHours",
																										lGetWorkingHoursAdapter);

		final GetSetTargetPowerAdapter lGetSetTargetPowerAdapter = new GetSetTargetPowerAdapter();
		mTargetPowerInMilliWattVariable = addSerialDoubleVariable("TargetPowerMilliWatt",
																															lGetSetTargetPowerAdapter);

		final GetCurrentPowerAdapter lGetCurrentPowerAdapter = new GetCurrentPowerAdapter();
		mCurrentPowerInMilliWattVariable = addSerialDoubleVariable(	"CurrentPowerInMilliWatt",
																																lGetCurrentPowerAdapter);
	}

	@Override
	public boolean open()
	{
		return super.open();
	}

	@Override
	public boolean start()
	{
		return super.start();
	}

	@Override
	public boolean stop()
	{
		return super.stop();
	}

	@Override
	public boolean close()
	{
		return super.close();
	}

}
