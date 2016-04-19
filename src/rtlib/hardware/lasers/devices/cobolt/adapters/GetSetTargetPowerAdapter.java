package rtlib.hardware.lasers.devices.cobolt.adapters;

import rtlib.com.serial.adapters.SerialTextDeviceAdapter;
import rtlib.hardware.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;

public class GetSetTargetPowerAdapter extends CoboltAdapter<Number>	implements
																																		SerialTextDeviceAdapter<Number>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolCobolt.cGetSetOutputPowerCommand.getBytes();
	}

	@Override
	public Number parseValue(final byte[] pMessage)
	{
		final double lTargetPowerInMilliWatt = 1000 * ProtocolCobolt.parseFloat(pMessage);
		return lTargetPowerInMilliWatt;
	}

	@Override
	public byte[] getSetValueCommandMessage(final Number pOldPowerInMilliWatt,
																					final Number pNewPowerInMilliWatt)
	{
		final double lPowerInWatt = pNewPowerInMilliWatt.doubleValue() * 0.001;
		final String lSetTargetPowerCommandString = String.format(ProtocolCobolt.cSetOutputPowerCommand,
																															lPowerInWatt);
		final byte[] lSetTargetPowerCommandBytes = lSetTargetPowerCommandString.getBytes();
		return lSetTargetPowerCommandBytes;
	}

}
