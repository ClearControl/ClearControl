package rtlib.hardware.lasers.devices.omicron.adapters.protocol.copy;

import jssc.SerialPortException;
import rtlib.com.serial.Serial;

public class ProtocolXX
{
	public static final long cWaitTimeInMilliSeconds = 0;

	public static final String cGetFirmwareCommand = "?GFw\r";
	public static final String cGetOperatingModeCommand = "?GOM\r";
	public static final String cSetOperatingModeCommand = "?SOM%s\r";
	public static final String cGetSpecInfoCommand = "?GSI\r";
	public static final String cGetMaxPowerCommand = "?GMP\r";
	public static final String cGetWorkingHoursCommand = "?GWH\r";
	public static final String cRecallOperatingModeCommand = "?ROM%s\r";
	public static final String cGetPowerLevelCommand = "?GLP\r";
	public static final String cSetPowerLevelCommand = "?SLP%s\r";
	public static final String cMeasureDiodePowerCommand = "?MDP\r";
	public static final String cGetDiodeTempCommand = "?MTD\r";
	public static final String cGetAmbientTempCommand = "?MTA\r";

	public static final String cSetLaserOnCommand = "?LOn\r";
	public static final String cSetLaserOffCommand = "?LOf\r";
	public static final String cSetPowerOnCommand = "?POn\r";
	public static final String cSetPowerOffCommand = "?POf\r";

	public static final char cMessageTerminationCharacter = '\r';

	public static final String cParagraphCode = "\\xA7";

	private static final int cAdGocModeMask = 1 << 13;

	public static final String[] splitMessage(final byte[] pMessage)
	{
		final String lMessageString = new String(	pMessage,
																							4,
																							pMessage.length - 4);
		final String[] lSplittedMessageString = lMessageString.split(cParagraphCode);

		return lSplittedMessageString;
	}

	public static String toHexadecimalString(final int n, final int k)
	{
		return String.format("%" + k + "s", Integer.toHexString(n))
									.replace(' ', '0')
									.toUpperCase();
	}

	public static final boolean setNoAdHocMode(final Serial pSerial)
	{
		try
		{
			pSerial.setBinaryMode(false);
			pSerial.setLineTerminationCharacter(cMessageTerminationCharacter);
			pSerial.write(cGetOperatingModeCommand);
			final byte[] lReadTextMessage = pSerial.readTextMessage();
			final String[] lSplitMessage = splitMessage(lReadTextMessage);
			final String lOperatingModeAsHexString = lSplitMessage[0];

			int lOperatingModeAsInteger = Integer.parseInt(	lOperatingModeAsHexString,
																											16);

			lOperatingModeAsInteger = lOperatingModeAsInteger & ~cAdGocModeMask;

			final String lNewOperatingModeAsHexString = toHexadecimalString(lOperatingModeAsInteger,
																																			4);
			final String lNewOperatingModeCommand = String.format(cSetOperatingModeCommand,
																														lNewOperatingModeAsHexString);

			pSerial.write(lNewOperatingModeCommand.getBytes());
			final byte[] lReadTextMessage2 = pSerial.readTextMessage();

			return lReadTextMessage2[0] == '!';
		}
		catch (final SerialPortException e)
		{
			e.printStackTrace();
			return false;
		}

	}
}
