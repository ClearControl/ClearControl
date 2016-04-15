package rtlib.hardware.optomech.opticalswitch.devices.optojena.adapters;

import rtlib.hardware.optomech.opticalswitch.devices.optojena.OptoJenaFiberSwitchDevice;
import rtlib.serial.adapters.SerialDeviceAdapterAdapter;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class FiberSwitchPositionAdapter	extends
																				SerialDeviceAdapterAdapter<Integer>	implements
																																						SerialTextDeviceAdapter<Integer>
{

	public FiberSwitchPositionAdapter(final OptoJenaFiberSwitchDevice pOptoJenaFiberSwitchDevice)
	{

	}

	@Override
	public byte[] getSetValueCommandMessage(Integer pOldValue,
																					Integer pNewValue)
	{
		String lMessage = String.format("ch%d\r\n", pNewValue + 1);
		return lMessage.getBytes();
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return 10;
	}

	@Override
	public boolean hasResponseForSet()
	{
		return false;
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return '\n';
	}

	@Override
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return '\n';
	};

}
