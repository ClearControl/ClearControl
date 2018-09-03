package clearcontrol.devices.stages.devices.smc100.adapters;

public class SMC100Protocol
{
  public static final int cBaudRate = 57600;
  public static final long cWaitTimeInMilliSeconds = 10;

  public static final String cMessageTerminationStringForSending =
                                                                 "\r\n";
  public static final char cMessageTerminationCharacter = '\n';

  public static final String cHomeSearchCommand = "1OR"
                                                  + cMessageTerminationStringForSending;

  public static final String cSetAbsPosCommand = "1PA%g"
                                                 + cMessageTerminationStringForSending;
  public static final String cGetAbsPosCommand = "1PA?"
                                                 + cMessageTerminationStringForSending;
  public static final String cEnableCommand = "1MM1"
                                              + cMessageTerminationStringForSending;
  public static final String cDisableCommand = "1MM0"
                                               + cMessageTerminationStringForSending;
  public static final String cGetStateCommand = "1TS"
                                                + cMessageTerminationStringForSending;
  public static final String cStopCommand = "1ST"
                                            + cMessageTerminationStringForSending;
  public static final String cResetCommand = "1RS"
                                             + cMessageTerminationStringForSending;
  public static final String cSetMinPosCommand = "1SL%g"
                                                 + cMessageTerminationStringForSending;
  public static final String cGetMinPosCommand = "1Sl?"
                                                 + cMessageTerminationStringForSending;
  public static final String cSetMaxPosCommand = "1SR%g"
                                                 + cMessageTerminationStringForSending;
  public static final String cGetMaxPosCommand = "1SR?"
                                                 + cMessageTerminationStringForSending;

  public static double parseFloat(String pSentMessage,
                                  final byte[] pReceivedMessage)
  {
    try
    {
      String lReceivedMessageString = new String(pReceivedMessage);
      lReceivedMessageString = lReceivedMessageString.trim();
      /*System.out.println("lReceivedMessageString='" + lReceivedMessageString
      										+ "'");/**/

      final String lReceivedMessageDoubleString =
                                                lReceivedMessageString.substring(pSentMessage.length()
                                                                                 - 3)
                                                                      .trim();
      final double lDoubleValue =
                                Double.parseDouble(lReceivedMessageDoubleString);
      return lDoubleValue;
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      return Double.NaN;
    }
  }

  public static Boolean parseReadyFromState(byte[] pMessage)
  {
    String lString = new String(pMessage, 7, 2);

    // System.out.println("parseReadyFromState=" + lString);

    if (lString.equals("28"))
      return false;
    else if (lString.equals("14"))
      return false;
    else if (lString.equals("1E"))
      return false;
    else if (lString.equals("1F"))
      return false;
    else if (lString.equals("3C"))
      return false;
    else if (lString.equals("3D"))
      return false;
    else if (lString.equals("3E"))
      return false;
    else if (lString.equals("46"))
      return false;
    else if (lString.equals("47"))
      return false;
    else if (lString.equals("14"))
      return false;

    return true;
  }

}
