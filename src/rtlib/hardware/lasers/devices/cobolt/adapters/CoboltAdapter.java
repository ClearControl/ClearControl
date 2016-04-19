package rtlib.hardware.lasers.devices.cobolt.adapters;

import rtlib.com.serial.adapters.SerialDeviceAdapterAdapter;
import rtlib.com.serial.adapters.SerialTextDeviceAdapter;
import rtlib.hardware.lasers.devices.cobolt.adapters.protocol.ProtocolCobolt;

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
