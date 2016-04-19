package rtlib.hardware.lasers.devices.cobolt;

import jssc.SerialPortException;
import rtlib.com.serial.SerialDevice;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.Variable;
import rtlib.hardware.lasers.LaserDeviceBase;
import rtlib.hardware.lasers.LaserDeviceInterface;
import rtlib.hardware.lasers.devices.cobolt.adapters.GetCurrentPowerAdapter;
import rtlib.hardware.lasers.devices.cobolt.adapters.GetSetTargetPowerAdapter;
import rtlib.hardware.lasers.devices.cobolt.adapters.GetWorkingHoursAdapter;
import rtlib.hardware.lasers.devices.cobolt.adapters.SetPowerOnOffAdapter;
import rtlib.hardware.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;
import rtlib.hardware.lasers.devices.cobolt.models.CoboltDeviceEnum;

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

		mDeviceIdVariable = new Variable<Integer>("DeviceId",
																							mCoboltModel.ordinal());

		mWavelengthVariable = new Variable<Integer>("WavelengthInNanoMeter",
																								CoboltDeviceEnum.getWavelengthInNanoMeter(mCoboltModel));

		mSpecInMilliWattPowerVariable = new Variable<Number>(	"SpecPowerInMilliWatt",
																													mMaxPowerInMilliWatt);

		mMaxPowerInMilliWattVariable = new Variable<Number>("MaxPowerInMilliWatt",
																												mMaxPowerInMilliWatt);

		mSetOperatingModeVariable = new Variable<Integer>("OperatingMode",
																											0);

		final SetPowerOnOffAdapter lSetPowerOnOffAdapter = new SetPowerOnOffAdapter();
		mPowerOnVariable = mSerialDevice.addSerialVariable(	"PowerOn",
																												lSetPowerOnOffAdapter);

		mLaserOnVariable = new Variable<Boolean>("LaserOn", false);

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
		boolean lResult = mSerialDevice.open();
		mSerialDevice.getSerial()
									.setLineTerminationCharacter(ProtocolCobolt.cMessageTerminationCharacter);

		if (lResult)
		{
			// Clears 'faults'
			sendCommand("cf\r");

			// This command is needed otherwise the laser cannot be controlled via
			// Serial,
			// the documentation is really poor, and it took some work to figure this
			// out...
			sendCommand("@cobas 0\r");

			/*sendCommand("f?\r");
			sendCommand("ilk?\r");
			sendCommand("@cobas?\r");
			sendCommand("l?\r");
			sendCommand("p?\r");
			sendCommand("pa?\r");
			sendCommand("i?\r");
			sendCommand("leds?\r");
			sendCommand("@cobasdr?\r");
			sendCommand("@cobasky?\r");/**/
		}

		getPowerOnVariable().set(true);

		return lResult;
	}

	@Override
	public boolean start()
	{

		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		setTargetPowerInMilliWatt(0);
		setLaserOn(false);
		setPowerOn(false);
		return mSerialDevice.close();
	}

	public String sendCommand(String pCommandString)
	{
		try
		{
			System.out.print(pCommandString.replace('\r', ' ').trim() + " --> ");
			String lAnswer = mSerialDevice.getSerial()
																		.writeStringAndGetAnswer(pCommandString);
			System.out.println(lAnswer.trim());
			return lAnswer;
		}
		catch (SerialPortException e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
