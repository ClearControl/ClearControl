package rtlib.serialdevice.stage.smc100.adapters;

public class SMC100Protocol
{
	public static final int cBaudRate = 57600;
	public static final long cWaitTimeInMilliSeconds = 0;

	public static final String cMessageTerminationStringForSending = "\\r\\n";
	public static final char cMessageTerminationCharacter = '\n';

	public static final String cHomeSearchCommand = "1OR" + cMessageTerminationStringForSending;

	public static final String cSetAbsPosCommand = "1PA%g" + cMessageTerminationStringForSending;
	public static final String cGetAbsPosCommand = "1PA?" + cMessageTerminationStringForSending;


	public static double parseFloat(final byte[] pMessage)
	{
		final String lResponseString = new String(pMessage);
		final double lDoubleValue = Double.parseDouble(lResponseString.trim());
		return lDoubleValue;
	}

}
