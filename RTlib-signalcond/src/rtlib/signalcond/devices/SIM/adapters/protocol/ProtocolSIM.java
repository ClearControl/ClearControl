package rtlib.signalcond.devices.SIM.adapters.protocol;


public class ProtocolSIM
{
	public static final int cBaudRate = 115200;

	public static final char cMessageTerminationCharacter = '\r';

	public static long cWaitTimeInMilliSeconds;

	public static final String sOffset = "OFST";

	public static final String cGain = "GAIN";

	public static final String cGetCommand = "%s?\n";

	public static final String cSetCommand = "%s %010.8f\n";

}
