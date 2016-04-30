package clearcontrol.hardware.optomech.opticalswitch.devices.optojena.adapters;

import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.com.serial.adapters.SerialTextDeviceAdapter;
import clearcontrol.hardware.optomech.opticalswitch.devices.optojena.OptoJenaFiberSwitchDevice;

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
