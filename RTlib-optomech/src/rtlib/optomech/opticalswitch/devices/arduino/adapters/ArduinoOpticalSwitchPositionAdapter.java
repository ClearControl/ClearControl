package rtlib.optomech.opticalswitch.devices.arduino.adapters;

import rtlib.optomech.opticalswitch.devices.arduino.ArduinoOpticalSwitchDevice;
import rtlib.serial.adapters.SerialDeviceAdapterAdapter;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class ArduinoOpticalSwitchPositionAdapter extends
																								SerialDeviceAdapterAdapter implements
																																					SerialTextDeviceAdapter
{

	public ArduinoOpticalSwitchPositionAdapter(final ArduinoOpticalSwitchDevice pArduinoOpticalSwitchDevice)
	{

	}

	@Override
	public byte[] getSetValueCommandMessage(double pOldValue,
																					double pNewValue)
	{
		int lPositionInt = (int) (101 + pNewValue);
		
		if (pNewValue == 100)
			lPositionInt = 0;
		else if (pNewValue == 200)
			lPositionInt = 100;
		
		String lMessage = String.format("%d\n", lPositionInt);
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
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return '\n';
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return '\n';
	}


}
