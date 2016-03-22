package rtlib.lasers.devices.omicron.adapters;

import rtlib.lasers.devices.omicron.adapters.protocol.ProtocolXX;
import rtlib.serial.adapters.SerialTextDeviceAdapter;

public class GetSetTargetPowerAdapter extends OmicronAdapter implements
															SerialTextDeviceAdapter
{

	private double mMaxPowerInMilliWatt;

	public void setMaxPowerInMilliWatt(final double pMaxPowerInMilliWatt)
	{
		mMaxPowerInMilliWatt = pMaxPowerInMilliWatt;
	}

	@Override
	public byte[] getGetValueCommandMessage()
	{
		// System.out.println("GET: sent: "+new
		// String(ProtocolXX.cGetPowerLevelCommand));
		return ProtocolXX.cGetPowerLevelCommand.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		// System.out.println("GET: received: "+new String(pMessage));
		final String[] lSplittedMessage = ProtocolXX.splitMessage(	ProtocolXX.cGetPowerLevelReplyPrefix,
																	pMessage);
		final String lSpecPowerString = lSplittedMessage[0];
		final int lCurrentPowerInBinaryUnits = Integer.parseInt(lSpecPowerString,
																16);
		final double lTargetPowerInPercent = (double) lCurrentPowerInBinaryUnits / (4096 - 1);

		return lTargetPowerInPercent * mMaxPowerInMilliWatt;
	}

	@Override
	public byte[] getSetValueCommandMessage(final double pOldPowerInMilliWatt,
											final double pNewPowerInMilliWatt)
	{
		final double lPowerInPercent = pNewPowerInMilliWatt / mMaxPowerInMilliWatt;
		// System.out.format("SET: power %g (percent) \n",lPowerInPercent);
		final int lPower = (int) Math.round(lPowerInPercent * (4096 - 1));
		// System.out.format("SET: power %d (percent*(4096-1)) \n",lPower);
		final String lHexPowerString = ProtocolXX.toHexadecimalString(	lPower,
																		3);
		// System.out.format("SET: power %s (percent*(4096-1) HEX) \n",lHexPowerString);
		final String lSetTargetPowerCommandString = String.format(	ProtocolXX.cSetPowerLevelCommand,
																	lHexPowerString);

		final byte[] lSetTargetPowerCommandBytes = lSetTargetPowerCommandString.getBytes();
		// System.out.println("SET: sent: "+new
		// String(lSetTargetPowerCommandBytes));
		return lSetTargetPowerCommandBytes;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		// System.out.println("SET: received: "+new String(pMessage));
		return super.checkAcknowledgementSetValueReturnMessage(pMessage);
	}

}
