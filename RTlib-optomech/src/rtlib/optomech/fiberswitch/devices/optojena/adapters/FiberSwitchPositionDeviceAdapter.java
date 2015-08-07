package rtlib.optomech.fiberswitch.devices.optojena.adapters;

import rtlib.optomech.fiberswitch.devices.optojena.OptoJenaFiberSwitchDevice;
import rtlib.serial.adapters.SerialDeviceAdapterAdapter;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class FiberSwitchPositionDeviceAdapter	extends
												SerialDeviceAdapterAdapter	implements
																			SerialTextDeviceAdapter
{

	public FiberSwitchPositionDeviceAdapter(final OptoJenaFiberSwitchDevice pOptoJenaFiberSwitchDevice)
	{

	}

	@Override
	public byte[] getSetValueCommandMessage(double pOldValue,
											double pNewValue)
	{
		String lMessage = String.format("ch%d\r\n",
										(int) pNewValue + 1);
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
