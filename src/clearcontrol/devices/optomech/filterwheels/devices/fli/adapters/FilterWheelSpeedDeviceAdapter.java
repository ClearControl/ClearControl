package clearcontrol.devices.optomech.filterwheels.devices.fli.adapters;

import clearcontrol.devices.optomech.filterwheels.devices.fli.FLIFilterWheelDevice;

public class FilterWheelSpeedDeviceAdapter extends
                                           FilterWheelDeviceAdapter
{

  public FilterWheelSpeedDeviceAdapter(final FLIFilterWheelDevice pFLIFilterWheelDevice)
  {
    super(pFLIFilterWheelDevice);
  }

  @Override
  public Integer parseValue(final byte[] pMessage)
  {
    return parsePositionOrSpeedValue(pMessage, true);
  }

  @Override
  public byte[] getSetValueCommandMessage(final Integer pOldSpeed,
                                          final Integer pNewSpeed)
  {
    return getSetPositionAndSpeedCommandMessage(mFLIFilterWheelDevice.getCachedPosition(),
                                                pNewSpeed);
  }

  @Override
  public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
  {
    return checkAcknowledgement(pMessage);
  }

}
