package rtlib.lasers.devices.omicron.adapters.protocol;

import rtlib.serial.Serial;

public class ProtocolXX
{
	public static final int cBaudRate = 500000;
	public static final long cWaitTimeInMilliSeconds = 0;

	public static final String cGetFirmwareCommand = "?GFw\r";
	public static final String cGetFirmwareReplyPrefix = "!GFw";

	public static final String cGetOperatingModeCommand = "?GOM\r";
	public static final String cGetOperatingModeReplyPrefix = "!GOM";
	public static final String cSetOperatingModeCommand = "?SOM%s\r";
	public static final String cGetSpecInfoCommand = "?GSI\r";
	public static final String cGetSpecInfoReplyPrefix = "!GSI";

	public static final String cGetMaxPowerCommand = "?GMP\r";
	public static final String cGetMaxPowerReplyPrefix = "!GMP";

	public static final String cGetWorkingHoursCommand = "?GWH\r";
	public static final String cGetWorkingHoursReplyPrefix = "!GWH";
	public static final String cRecallOperatingModeCommand = "?ROM%s\r";
	public static final String cGetPowerLevelCommand = "?GLP\r";
	public static final String cGetPowerLevelReplyPrefix = "!GLP";

	public static final String cSetPowerLevelCommand = "?SLP%s\r";
	public static final String cMeasureDiodePowerCommand = "?MDP\r";
	public static final String cMeasureDiodePowerReplyPrefix = "!MDP";
	public static final String cGetDiodeTempCommand = "?MTD\r";
	public static final String cGetAmbientTempCommand = "?MTA\r";

	public static final String cSetLaserOnCommand = "?LOn\r";
	public static final String cSetLaserOffCommand = "?LOf\r";
	public static final String cSetPowerOnCommand = "?POn\r";
	public static final String cSetPowerOffCommand = "?POf\r";

	public static final char cMessageTerminationCharacter = '\r';

	public static final String cParagraphCode = "\\xA7";

	private static final int cAdGocModeMask = 1 << 13;


	public static double parseDouble(	String pPrefix,
																		String pReceivedString)
	{
		int lIndex = pReceivedString.indexOf(pPrefix) + pPrefix.length();
		String lStringWithoutPrefix = pReceivedString.substring(lIndex)
																									.trim();
		double lDoubleValue = Double.parseDouble(lStringWithoutPrefix);
		return lDoubleValue;
	}

	public static final String[] splitMessage(String pPrefix,
																						final byte[] pMessage)
	{
		String lMessageString = new String(pMessage);
		int lIndex = lMessageString.indexOf(pPrefix) + pPrefix.length();
		lMessageString = lMessageString.substring(lIndex);
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
		return setNoAdHocModeInternal(pSerial, 40);
	}

	private static final boolean setNoAdHocModeInternal(final Serial pSerial,
																											final int pMaxtries)
	{
		if (pMaxtries <= 0)
		{
			return false;
		}
		purge(pSerial);
		boolean lSuccess;
		try
		{
			pSerial.setBinaryMode(false);
			pSerial.setLineTerminationCharacter(cMessageTerminationCharacter);
			pSerial.write(cGetOperatingModeCommand);
			final byte[] lReadTextMessage = pSerial.readTextMessage();
			final String[] lSplitMessage = splitMessage(cGetOperatingModeReplyPrefix,
																									lReadTextMessage);
			final String lOperatingModeAsHexString = lSplitMessage[0];

			int lOperatingModeAsInteger = Integer.parseInt(	lOperatingModeAsHexString,
																											16);

			lOperatingModeAsInteger = lOperatingModeAsInteger & ~cAdGocModeMask;

			final String lNewOperatingModeAsHexString = toHexadecimalString(lOperatingModeAsInteger,
																																			4);
			final String lNewOperatingModeCommand = String.format(cSetOperatingModeCommand,
																														lNewOperatingModeAsHexString);

			purge(pSerial);
			pSerial.write(lNewOperatingModeCommand.getBytes());
			final byte[] lReadTextMessage2 = pSerial.readTextMessage();

			lSuccess = new String(lReadTextMessage2).startsWith("!SOM");

			if (lSuccess)
			{
				System.out.println(ProtocolXX.class.getSimpleName() + ": Success setting non-AdHoc mode!");
			}
			else
			{
				lSuccess = setNoAdHocModeInternal(pSerial, pMaxtries - 1);
			}

			purge(pSerial);
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			System.out.println(ProtocolXX.class.getSimpleName() + ": Failed to set non-AdHoc mode, trying again...");
			purge(pSerial);
			lSuccess = setNoAdHocModeInternal(pSerial, pMaxtries - 1);
		}

		purge(pSerial);
		return lSuccess;
	}

	private static void purge(final Serial pSerial)
	{
		try
		{
			pSerial.setMessageLength(100);
			pSerial.readBinaryMessage(100);
			pSerial.purge();
		}
		catch (final Throwable e)
		{
		}
	}

}
