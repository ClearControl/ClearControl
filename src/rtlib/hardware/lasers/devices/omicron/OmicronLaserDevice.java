package rtlib.hardware.lasers.devices.omicron;

import rtlib.com.serial.SerialDevice;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.hardware.lasers.LaserDeviceBase;
import rtlib.hardware.lasers.LaserDeviceInterface;
import rtlib.hardware.lasers.devices.omicron.adapters.GetCurrentPowerAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.GetDeviceIdAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.GetMaxPowerAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.GetSetTargetPowerAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.GetSpecPowerAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.GetWavelengthAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.GetWorkingHoursAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.SetLaserOnOffAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.SetOperatingModeAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.SetPowerOnOffAdapter;
import rtlib.hardware.lasers.devices.omicron.adapters.protocol.ProtocolXX;

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
		mDeviceIdVariable = mSerialDevice.addSerialVariable("DeviceId",
																												lGetDeviceIdAdapter);

		final GetWavelengthAdapter lGetWavelengthAdapter = new GetWavelengthAdapter();
		mWavelengthVariable = mSerialDevice.addSerialVariable("WavelengthInNanoMeter",
																													lGetWavelengthAdapter);

		final GetSpecPowerAdapter lGetSpecPowerAdapter = new GetSpecPowerAdapter();
		mSpecInMilliWattPowerVariable = mSerialDevice.addSerialVariable("SpecPowerInMilliWatt",
																																		lGetSpecPowerAdapter);

		final GetMaxPowerAdapter lGetMaxPowerAdapter = new GetMaxPowerAdapter();
		mMaxPowerInMilliWattVariable = mSerialDevice.addSerialVariable(	"MaxPowerInMilliWatt",
																																		lGetMaxPowerAdapter);

		final SetOperatingModeAdapter lSetOperatingModeAdapter = new SetOperatingModeAdapter();
		mSetOperatingModeVariable = mSerialDevice.addSerialVariable("OperatingMode",
																																lSetOperatingModeAdapter);

		final SetPowerOnOffAdapter lSetPowerOnOffAdapter = new SetPowerOnOffAdapter();
		mPowerOnVariable = mSerialDevice.addSerialVariable(	"PowerOn",
																												lSetPowerOnOffAdapter);

		final SetLaserOnOffAdapter lSetLaserOnOffAdapter = new SetLaserOnOffAdapter();
		mLaserOnVariable = mSerialDevice.addSerialVariable(	"LaserOn",
																												lSetLaserOnOffAdapter);

		final GetWorkingHoursAdapter lGetWorkingHoursAdapter = new GetWorkingHoursAdapter();
		mWorkingHoursVariable = mSerialDevice.addSerialVariable("WorkingHours",
																														lGetWorkingHoursAdapter);

		mGetSetTargetPowerAdapter = new GetSetTargetPowerAdapter();
		mTargetPowerInMilliWattVariable = mSerialDevice.addSerialVariable("TargetPowerInMilliWatt",
																																			mGetSetTargetPowerAdapter);

		final GetCurrentPowerAdapter lGetCurrentPowerAdapter = new GetCurrentPowerAdapter();
		mCurrentPowerInMilliWattVariable = mSerialDevice.addSerialVariable(	"CurrentPowerInMilliWatt",
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
			mGetSetTargetPowerAdapter.setMaxPowerInMilliWatt(mMaxPowerInMilliWattVariable.get()
																																										.doubleValue());
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
