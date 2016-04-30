package clearcontrol.hardware.lasers.devices.omicron.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.hardware.lasers.devices.omicron.adapters.protocol.ProtocolXX;

public abstract class OmicronAdapter<O> extends
																				SerialDeviceAdapterAdapter<O>	implements
																																			SerialTextDeviceAdapter<O>
{

	@Override
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return ProtocolXX.cMessageTerminationCharacter;
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolXX.cWaitTimeInMilliSeconds;
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return ProtocolXX.cMessageTerminationCharacter;
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolXX.cWaitTimeInMilliSeconds;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return pMessage[0] == '!';
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
