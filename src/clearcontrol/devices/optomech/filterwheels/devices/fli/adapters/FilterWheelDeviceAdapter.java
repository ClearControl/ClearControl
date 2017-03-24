package clearcontrol.devices.optomech.filterwheels.devices.fli.adapters;

import clearcontrol.com.serial.adapters.SerialBinaryDeviceAdapter;
import clearcontrol.com.serial.adapters.SerialDeviceAdapterAdapter;
import clearcontrol.devices.optomech.filterwheels.devices.fli.FLIFilterWheelDevice;

public abstract class FilterWheelDeviceAdapter extends
                                               SerialDeviceAdapterAdapter<Integer>
                                               implements
                                               SerialBinaryDeviceAdapter<Integer>
{
  static final byte cRequestFilterWheelStatusCode =
                                                  (byte) Integer.parseInt("cc",
                                                                          16);
  static final byte[] cRequestFilterWheelStatusMessage = new byte[]
  { cRequestFilterWheelStatusCode };

  final byte[] mSetFilterWheelPositionMessage = new byte[]
  { 0 };

  static final byte cAcknowledgementCode =
                                         (byte) Integer.parseInt("0D",
                                                                 16);

  protected final FLIFilterWheelDevice mFLIFilterWheelDevice;

  public FilterWheelDeviceAdapter(final FLIFilterWheelDevice pFLIFilterWheelDevice)
  {
    super();
    mFLIFilterWheelDevice = pFLIFilterWheelDevice;
  }

  @Override
  public byte[] getGetValueCommandMessage()
  {
    return null; // cRequestFilterWheelStatusMessage;
  }

  @Override
  public int getGetValueReturnMessageLength()
  {
    return 11;
  }

  @Override
  public long getGetValueReturnWaitTimeInMilliseconds()
  {
    return 0;
  }

  public Integer parsePositionOrSpeedValue(final byte[] pMessage,
                                           final boolean pReturnPosition)
  {
    if (pMessage == null)
    {
      return null;
    }

    // final byte lFirstFilterWheel = pMessage[1];
    // final String lBitString = binarify(lFirstFilterWheel);
    // System.out.println("'" + lBitString + "'");
    // final int lPosition = (lFirstFilterWheel & (16 + 32 + 64)) >> 5;
    // final int lSpeed = lFirstFilterWheel & (1 + 2 + 4 + 8);
    // return (double) (pReturnPosition ? lPosition : lSpeed);

    return (pReturnPosition ? mFLIFilterWheelDevice.getCachedPosition()
                            : mFLIFilterWheelDevice.getCachedSpeed());
  }

  public static String binarify(byte pByte)
  {
    final String lFullBitString = Integer.toBinaryString(pByte);
    // String lByteBitString =
    // lFullBitString.substring(lFullBitString.length()-8,
    // lFullBitString.length());
    return lFullBitString;
  }

  public byte[] getSetPositionAndSpeedCommandMessage(final int pPosition,
                                                     final int pSpeed)
  {
    final int lPositionByte = (pPosition % 10) & (1 + 2 + 4 + 8);
    final int lSpeedByte = (pSpeed % 8) << 5 & (16 + 32 + 64);

    mSetFilterWheelPositionMessage[0] = (byte) (lPositionByte
                                                | lSpeedByte);
    return mSetFilterWheelPositionMessage;
  }

  @Override
  public int getSetValueReturnMessageLength()
  {
    return 0;
  }

  @Override
  public long getSetValueReturnWaitTimeInMilliseconds()
  {
    return 0;
  }

  public boolean checkAcknowledgement(final byte[] pMessage)
  {
    if (pMessage == null)
    {
      return false;
    }
    return pMessage.length == 2
           && pMessage[1] == cAcknowledgementCode;
  }

  @Override
  public boolean hasResponseForGet()
  {
    return true;
  }

  @Override
  public boolean hasResponseForSet()
  {
    return true;
  }

}
