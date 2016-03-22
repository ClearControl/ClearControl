package rtlib.lasers.devices.cobolt.adapters.protocol;

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

	public static String toHexadecimalString(final int n, final int k)
	{
		return String.format("%" + k + "s", Integer.toHexString(n))
						.replace(' ', '0')
						.toUpperCase();
	}

	public static double parseFloat(final byte[] pMessage)
	{
		final String lResponseString = new String(pMessage);
		final double lDoubleValue = Double.parseDouble(lResponseString.trim());
		return lDoubleValue;
	}

}
