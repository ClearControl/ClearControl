package clearcontrol.devices.lasers.devices.cobolt.adapters.protocol;

/**
 * Cobolt device serial protocol.
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public class ProtocolCobolt
{

  public static final int cBaudRate = 115200;
  public static final long cWaitTimeInMilliSeconds = 0;

  public static final char cMessageTerminationCharacter = '\r';

  public static final String cReadOutputPowerCommand = "pa?\r";
  public static final String cGetSetOutputPowerCommand = "p?\r";
  public static final String cSetOutputPowerCommand = "p %g\r";
  public static final String cGetWorkingHoursCommand = "hrs?\r";
  public static final String cSetLaserOnCommand = "l1\r";
  public static final String cSetLaserOffCommand = "l0\r";

  /**
   * Parses message containg a float
   * 
   * @param pMessage
   *          message
   * @return parsed float
   */
  public static double parseFloat(final byte[] pMessage)
  {
    final String lResponseString = new String(pMessage);
    final double lDoubleValue =
                              Double.parseDouble(lResponseString.trim());
    return lDoubleValue;
  }

}
