package rtlib.stages.devices.smc100.adapters;

import java.util.concurrent.TimeUnit;

import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.stages.devices.smc100.SMC100StageDevice;

public class SMC100PositionAdapter implements SerialTextDeviceAdapter
{
	protected static final double cEpsilon = 0.1; // 100nm

	private BooleanVariable mReadyVariable;
	private BooleanVariable mStopVariable;
	private DoubleVariable mMinPositionVariable;
	private DoubleVariable mMaxPositionVariable;

	private SMC100StageDevice mSmc100StageDevice;



	public SMC100PositionAdapter(SMC100StageDevice pSmc100StageDevice)
	{
		mSmc100StageDevice = pSmc100StageDevice;
		mReadyVariable = pSmc100StageDevice.getReadyVariable(0);
		mStopVariable = pSmc100StageDevice.getStopVariable(0);
		mMinPositionVariable = pSmc100StageDevice.getMinPositionVariable(0);
		mMaxPositionVariable = pSmc100StageDevice.getMaxPositionVariable(0);
	}



	@Override
	public byte[] getGetValueCommandMessage()
	{
		return SMC100Protocol.cGetAbsPosCommand.getBytes();
	}

	@Override
	public Double parseValue(byte[] pMessage)
	{
		return 1000 * SMC100Protocol.parseFloat(SMC100Protocol.cGetAbsPosCommand,
																						pMessage);
	}

	@Override
	public long getGetValueReturnWaitTimeInMilliseconds()
	{
		return SMC100Protocol.cWaitTimeInMilliSeconds;
	}

	@Override
	public byte[] getSetValueCommandMessage(double pOldValue,
																					double pNewValue)
	{
		double lMinPosition = mMinPositionVariable.getValue();
		double lMaxPosition = mMaxPositionVariable.getValue();

		if (pNewValue < lMinPosition)
			pNewValue = lMinPosition + cEpsilon;
		else if (pNewValue > lMaxPosition)
			pNewValue = lMaxPosition - cEpsilon;

		boolean lIsReady = mReadyVariable.getBooleanValue();

		if (!lIsReady)
		{
			System.out.println("Not ready-> stopping");
			mStopVariable.setEdge(true);
		}

		mSmc100StageDevice.waitToBeReady(0, 5, TimeUnit.SECONDS);

		String lSetPositionMessage = String.format(	SMC100Protocol.cSetAbsPosCommand,
																								pNewValue * 0.001);
		System.out.println(lSetPositionMessage);
		return lSetPositionMessage.getBytes();
	}

	@Override
	public long getSetValueReturnWaitTimeInMilliseconds()
	{
		return SMC100Protocol.cWaitTimeInMilliSeconds;
	}

	@Override
	public boolean checkAcknowledgementSetValueReturnMessage(byte[] pMessage)
	{
		return true;
	}

	@Override
	public Character getGetValueReturnMessageTerminationCharacter()
	{
		return SMC100Protocol.cMessageTerminationCharacter;
	}

	@Override
	public Character getSetValueReturnMessageTerminationCharacter()
	{
		return SMC100Protocol.cMessageTerminationCharacter;
	}

	@Override
	public boolean hasResponseForSet()
	{
		return false;
	}

	@Override
	public boolean hasResponseForGet()
	{
		return true;
	}

}
