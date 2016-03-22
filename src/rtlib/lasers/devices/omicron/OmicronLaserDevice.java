package rtlib.lasers.devices.omicron;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.lasers.LaserDeviceBase;
import rtlib.lasers.LaserDeviceInterface;
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
import rtlib.serial.SerialDevice;

public class OmicronLaserDevice extends LaserDeviceBase	implements
																												LaserDeviceInterface
{
	private final SerialDevice mSerialDevice;
	private boolean mAnalog = true, mDigital = true;

	private final GetSetTargetPowerAdapter mGetSetTargetPowerAdapter;

	public OmicronLaserDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"laser.omicron",
																										pDeviceIndex,
																										"NULL"));
	}

	public OmicronLaserDevice(final int pDeviceIndex,
														boolean pDigitalControl,
														boolean pAnalogControl)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"laser.omicron",
																										pDeviceIndex,
																										"NULL"));
		mAnalog = pAnalogControl;
		mDigital = pDigitalControl;
	}

	public OmicronLaserDevice(final String pPortName)
	{
		super("OmicronLaserDevice" + pPortName);

		mSerialDevice = new SerialDevice(	"OmicronLaserDevice",
																			pPortName,
																			ProtocolXX.cBaudRate);

		final GetDeviceIdAdapter lGetDeviceIdAdapter = new GetDeviceIdAdapter();
		mDeviceIdVariable = mSerialDevice.addSerialDoubleVariable("DeviceId",
																															lGetDeviceIdAdapter);

		final GetWavelengthAdapter lGetWavelengthAdapter = new GetWavelengthAdapter();
		mWavelengthVariable = mSerialDevice.addSerialDoubleVariable("WavelengthInNanoMeter",
																																lGetWavelengthAdapter);

		final GetSpecPowerAdapter lGetSpecPowerAdapter = new GetSpecPowerAdapter();
		mSpecInMilliWattPowerVariable = mSerialDevice.addSerialDoubleVariable("SpecPowerInMilliWatt",
																																					lGetSpecPowerAdapter);

		final GetMaxPowerAdapter lGetMaxPowerAdapter = new GetMaxPowerAdapter();
		mMaxPowerInMilliWattVariable = mSerialDevice.addSerialDoubleVariable(	"MaxPowerInMilliWatt",
																																					lGetMaxPowerAdapter);

		final SetOperatingModeAdapter lSetOperatingModeAdapter = new SetOperatingModeAdapter();
		mSetOperatingModeVariable = mSerialDevice.addSerialDoubleVariable("OperatingMode",
																																			lSetOperatingModeAdapter);

		final SetPowerOnOffAdapter lSetPowerOnOffAdapter = new SetPowerOnOffAdapter();
		mPowerOnVariable = mSerialDevice.addSerialBooleanVariable("PowerOn",
																															lSetPowerOnOffAdapter);

		final SetLaserOnOffAdapter lSetLaserOnOffAdapter = new SetLaserOnOffAdapter();
		mLaserOnVariable = mSerialDevice.addSerialBooleanVariable("LaserOn",
																															lSetLaserOnOffAdapter);

		final GetWorkingHoursAdapter lGetWorkingHoursAdapter = new GetWorkingHoursAdapter();
		mWorkingHoursVariable = mSerialDevice.addSerialDoubleVariable("WorkingHours",
																																	lGetWorkingHoursAdapter);

		mGetSetTargetPowerAdapter = new GetSetTargetPowerAdapter();
		mTargetPowerInMilliWattVariable = mSerialDevice.addSerialDoubleVariable("TargetPowerInMilliWatt",
																																						mGetSetTargetPowerAdapter);

		final GetCurrentPowerAdapter lGetCurrentPowerAdapter = new GetCurrentPowerAdapter();
		mCurrentPowerInMilliWattVariable = mSerialDevice.addSerialDoubleVariable(	"CurrentPowerInMilliWatt",
																																							lGetCurrentPowerAdapter);
	}

	@Override
	public boolean open()
	{
		boolean lOpen;
		try
		{
			lOpen = super.open();
			mSerialDevice.open();
			ProtocolXX.setNoAdHocMode(mSerialDevice.getSerial());
			setTargetPowerInPercent(0);
			if (mAnalog && mDigital)
				setOperatingMode(5);
			else if (mDigital)
				setOperatingMode(2);
			else if (mAnalog)
				setOperatingMode(4);
			// setPowerOn(true);
			mGetSetTargetPowerAdapter.setMaxPowerInMilliWatt(mMaxPowerInMilliWattVariable.getValue());
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
			// setLaserOn(true);
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
			// setLaserOn(false);
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
			mSerialDevice.close();
			return super.close();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
