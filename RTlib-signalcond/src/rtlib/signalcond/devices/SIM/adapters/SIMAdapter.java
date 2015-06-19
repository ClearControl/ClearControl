package rtlib.signalcond.devices.SIM.adapters;

import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.signalcond.devices.SIM.SIM900MainframeDevice;
import rtlib.signalcond.devices.SIM.adapters.protocol.ProtocolSIM;

public abstract class SIMAdapter implements SerialTextDeviceAdapter
{

	private final SIM900MainframeDevice mSim900MainframeDevice;
	private final int mPort;
	private final String mVariableName;

	public SIMAdapter(SIM900MainframeDevice pSim900MainframeDevice,
										int pPort,
										String pVariableName)
	{
		mSim900MainframeDevice = pSim900MainframeDevice;
		mPort = pPort;
		mVariableName = pVariableName;
	}

	@Override
	public byte[] getGetValueCommandMessage()
	{
		return String.format(ProtocolSIM.cGetCommand, mVariableName)
									.getBytes();
	}

	@Override
	public Double parseValue(final byte[] pMessage)
	{
		final String lAnswer = new String(pMessage);
		final Double lValue = Double.parseDouble(lAnswer);
		return lValue;
	}

	@Override
	public byte[] getSetValueCommandMessage(double pOldValue,
																					double pNewValue)
	{
		final String lSetCommandString = String.format(	ProtocolSIM.cSetCommand,
																												mVariableName,
																												pNewValue);

		final String lWrappedSetCommandString = mSim900MainframeDevice.wrapCommand(	mPort,
																																										lSetCommandString);

		final byte[] lWrappedSetCommandBytes = lWrappedSetCommandString.getBytes();

		return lWrappedSetCommandBytes;
	}

	@Override
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return ProtocolSIM.cMessageTerminationCharacter;
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolSIM.cWaitTimeInMilliSeconds;
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return ProtocolSIM.cMessageTerminationCharacter;
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return ProtocolSIM.cWaitTimeInMilliSeconds;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(final byte[] pMessage)
	{
		return pMessage[0] == '!';
	}

	@Override
	public boolean hasResponseForSet()
	{
		return true;
	}

	@Override
	public boolean hasResponseForGet()
	{
		return true;
	}

}