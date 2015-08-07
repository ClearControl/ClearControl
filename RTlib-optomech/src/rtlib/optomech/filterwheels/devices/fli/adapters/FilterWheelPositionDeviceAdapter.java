package rtlib.optomech.filterwheels.devices.fli.adapters;

import rtlib.optomech.filterwheels.devices.fli.FLIFilterWheelDevice;

public class FilterWheelPositionDeviceAdapter	extends
												FilterWheelDeviceAdapter
{

	public FilterWheelPositionDeviceAdapter(final FLIFilterWheelDevice pFLIFilterWheelDevice)
	{
		super(pFLIFilterWheelDevice);
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		return parsePositionOrSpeedValue(pMessage, true);
	}

	@Override
	public byte[] getSetValueCommandMessage(final double pOldPosition,
											final double pNewPosition)
	{
		return getSetPositionAndSpeedCommandMessage((int) pNewPosition,
													mFLIFilterWheelDevice.getCachedSpeed());
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return checkAcknowledgement(pMessage);
	}

}
