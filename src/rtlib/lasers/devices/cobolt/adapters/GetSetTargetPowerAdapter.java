package rtlib.lasers.devices.cobolt.adapters;

import rtlib.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GetSetTargetPowerAdapter extends CoboltAdapter<Double>	implements
																																		SerialTextDeviceAdapter<Double>
{

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return ProtocolCobolt.cGetSetOutputPowerCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		final double lTargetPowerInMilliWatt = 1000 * ProtocolCobolt.parseFloat(pMessage);
		return lTargetPowerInMilliWatt;
	}

	@Override
	public byte[] getSetValueCommandMessage(final Double pOldPowerInMilliWatt,
																					final Double pNewPowerInMilliWatt)
	{
		final double lPowerInWatt = pNewPowerInMilliWatt * 0.001;
		final String lSetTargetPowerCommandString = String.format(	ProtocolCobolt.cSetOutputPowerCommand,
																	lPowerInWatt);
		final byte[] lSetTargetPowerCommandBytes = lSetTargetPowerCommandString.getBytes();
		return lSetTargetPowerCommandBytes;
	}

}
