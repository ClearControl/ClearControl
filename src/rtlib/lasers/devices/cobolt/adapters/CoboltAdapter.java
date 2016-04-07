package rtlib.lasers.devices.cobolt.adapters;

import rtlib.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;
import rtlib.serial.adapters.SerialDeviceAdapterAdapter;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public abstract class CoboltAdapter<O>	extends
																				SerialDeviceAdapterAdapter<O>	implements
																																			SerialTextDeviceAdapter<O>
{

	@Override
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return ProtocolCobolt.cMessageTerminationCharacter;
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolCobolt.cWaitTimeInMilliSeconds;
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return ProtocolCobolt.cMessageTerminationCharacter;
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolCobolt.cWaitTimeInMilliSeconds;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		final String lResponseString = new String(pMessage);
		return lResponseString.contains("OK");
	}

	@Override
	public boolean hasResponseForSet()
	{
		return true;
	}

	@Override
	public boolean hasResponseForGet()
	{
		return true;
	}

}
