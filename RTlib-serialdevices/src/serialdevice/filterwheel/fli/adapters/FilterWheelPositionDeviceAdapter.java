package serialdevice.filterwheel.fli.adapters;

import serialdevice.filterwheel.fli.FLIFilterWheelDevice;

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
	public byte[] getSetValueCommandMessage(final double pPosition)
	{
		return getSetPositionAndSpeedCommandMessage((int) pPosition,
																								mFLIFilterWheelDevice.getCachedSpeed());
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return checkAcknowledgement(pMessage);
	}

}
