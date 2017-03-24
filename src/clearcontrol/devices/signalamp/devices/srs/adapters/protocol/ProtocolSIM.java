package clearcontrol.devices.signalamp.devices.srs.adapters.protocol;

public class ProtocolSIM
{
  public static final int cBaudRate = 115200;

  public static final char cMessageTerminationCharacter = '\n';

  public static long cWaitTimeInMilliSeconds = 1;

  public static final String cSIM900ForwardCommand =
                                                   "SNDT %d,\"%s\"\n";

  public static final String cOffset = "OFST";

  public static final String cGain = "GAIN";

  public static final String cGetCommand = "%s?\n";

  public static final String cSetCommand = "%s %010.8f\n";

}
