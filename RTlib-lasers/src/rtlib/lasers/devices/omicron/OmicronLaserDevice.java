package rtlib.lasers.devices.omicron;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.lasers.LaserDeviceInterface;
import rtlib.lasers.LaserDeviceBase;
import rtlib.lasers.devices.omicron.adapters.GetCurrentPowerAdapter;
import rtlib.lasers.devices.omicron.adapters.GetDeviceIdAdapter;
import rtlib.lasers.devices.omicron.adapters.GetMaxPowerAdapter;
import rtlib.lasers.devices.omicron.adapters.GetSetTargetPowerAdapter;
import rtlib.lasers.devices.omicron.adapters.GetSpecPowerAdapter;
import rtlib.lasers.devices.omicron.adapters.GetWavelengthAdapter;
import rtlib.lasers.devices.omicron.adapters.GetWorkingHoursAdapter;
import rtlib.lasers.devices.omicron.adapters.SetLaserOnOffAdapter;
import rtlib.lasers.devices.omicron.adapters.SetOperatingModeAdapter;
import rtlib.lasers.devices.omicron.adapters.SetPowerOnOffAdapter;
import rtlib.lasers.devices.omicron.adapters.protocol.ProtocolXX;

public class OmicronLaserDevice extends LaserDeviceBase	implements
																												LaserDeviceInterface
{

	private final GetSetTargetPowerAdapter mGetSetTargetPowerAdapter;

	public OmicronLaserDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"laser.omicron",
																										pDeviceIndex,
																										"NULL"));
	}

	public OmicronLaserDevice(final String pPortName)
	{
		super("OmicronLaserDevice", pPortName, ProtocolXX.cBaudRate);

		final GetDeviceIdAdapter lGetDeviceIdAdapter = new GetDeviceIdAdapter();
		mDeviceIdVariable = addSerialDoubleVariable("DeviceId",
																								lGetDeviceIdAdapter);

		final GetWavelengthAdapter lGetWavelengthAdapter = new GetWavelengthAdapter();
		mWavelengthVariable = addSerialDoubleVariable("WavelengthInNanoMeter",
																									lGetWavelengthAdapter);

		final GetSpecPowerAdapter lGetSpecPowerAdapter = new GetSpecPowerAdapter();
		mSpecInMilliWattPowerVariable = addSerialDoubleVariable("SpecPowerInMilliWatt",
																														lGetSpecPowerAdapter);

		final GetMaxPowerAdapter lGetMaxPowerAdapter = new GetMaxPowerAdapter();
		mMaxPowerInMilliWattVariable = addSerialDoubleVariable(	"MaxPowerInMilliWatt",
																														lGetMaxPowerAdapter);

		final SetOperatingModeAdapter lSetOperatingModeAdapter = new SetOperatingModeAdapter();
		mSetOperatingModeVariable = addSerialDoubleVariable("OperatingMode",
																												lSetOperatingModeAdapter);

		final SetPowerOnOffAdapter lSetPowerOnOffAdapter = new SetPowerOnOffAdapter();
		mPowerOnVariable = addSerialBooleanVariable("PowerOn",
																								lSetPowerOnOffAdapter);

		final SetLaserOnOffAdapter lSetLaserOnOffAdapter = new SetLaserOnOffAdapter();
		mLaserOnVariable = addSerialBooleanVariable("LaserOn",
																								lSetLaserOnOffAdapter);

		final GetWorkingHoursAdapter lGetWorkingHoursAdapter = new GetWorkingHoursAdapter();
		mWorkingHoursVariable = addSerialDoubleVariable("WorkingHours",
																										lGetWorkingHoursAdapter);

		mGetSetTargetPowerAdapter = new GetSetTargetPowerAdapter();
		mTargetPowerInMilliWattVariable = addSerialDoubleVariable("TargetPowerInMilliWatt",
																															mGetSetTargetPowerAdapter);

		final GetCurrentPowerAdapter lGetCurrentPowerAdapter = new GetCurrentPowerAdapter();
		mCurrentPowerInMilliWattVariable = addSerialDoubleVariable(	"CurrentPowerInMilliWatt",
																																lGetCurrentPowerAdapter);
	}

	@Override
	public boolean open()
	{
		boolean lOpen;
		try
		{
			lOpen = super.open();
			ProtocolXX.setNoAdHocMode(mSerial);
			mGetSetTargetPowerAdapter.setMaxPowerInMilliWatt(mMaxPowerInMilliWattVariable.getValue());
			setTargetPowerInPercent(0);
			setOperatingMode(2);
			setPowerOn(true);
			return lOpen;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean start()
	{
		try
		{
			final boolean lStartResult = super.start();
			setLaserOn(true);
			return lStartResult;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean stop()
	{
		try
		{
			setLaserOn(false);
			return super.stop();
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
			setTargetPowerInPercent(0);
			setLaserOn(false);
			setPowerOn(false);
			return super.close();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
