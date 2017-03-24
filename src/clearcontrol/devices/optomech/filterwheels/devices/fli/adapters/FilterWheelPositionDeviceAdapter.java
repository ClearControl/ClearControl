package clearcontrol.devices.optomech.filterwheels.devices.fli.adapters;

import clearcontrol.devices.optomech.filterwheels.devices.fli.FLIFilterWheelDevice;

public class FilterWheelPositionDeviceAdapter extends
                                              FilterWheelDeviceAdapter
{

  public FilterWheelPositionDeviceAdapter(final FLIFilterWheelDevice pFLIFilterWheelDevice)
  {
    super(pFLIFilterWheelDevice);
  }

  @Override
  public Integer parseValue(final byte[] pMessage)
  {
    return parsePositionOrSpeedValue(pMessage, true);
  }

  @Override
  public byte[] getSetValueCommandMessage(final Integer pOldPosition,
                                          final Integer pNewPosition)
  {
    return getSetPositionAndSpeedCommandMessage(pNewPosition,
                                                mFLIFilterWheelDevice.getCachedSpeed());
  }

  @Override
  public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
  {
    return checkAcknowledgement(pMessage);
  }

}
