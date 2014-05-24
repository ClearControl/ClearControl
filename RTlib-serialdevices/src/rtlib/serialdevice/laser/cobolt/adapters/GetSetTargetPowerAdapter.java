package rtlib.serialdevice.laser.cobolt.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.serialdevice.laser.cobolt.adapters.protocol.ProtocolCobolt;

public class GetSetTargetPowerAdapter extends CoboltAdapter	implements
																														SerialTextDeviceAdapter
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
	public byte[] getSetValueCommandMessage(final double pPowerInMilliWatt)
	{
		final double lPowerInWatt = pPowerInMilliWatt * 0.001;
		final String lSetTargetPowerCommandString = String.format(ProtocolCobolt.cSetOutputPowerCommand,
																															lPowerInWatt);
		final byte[] lSetTargetPowerCommandBytes = lSetTargetPowerCommandString.getBytes();
		return lSetTargetPowerCommandBytes;
	}

}
