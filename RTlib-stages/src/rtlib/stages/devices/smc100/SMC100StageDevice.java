package rtlib.stages.devices.smc100;

import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.timing.Waiting;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.serial.SerialDevice;
import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.stages.StageDeviceInterface;
import rtlib.stages.devices.smc100.adapters.SMC100EnableAdapter;
import rtlib.stages.devices.smc100.adapters.SMC100HomingAdapter;
import rtlib.stages.devices.smc100.adapters.SMC100MaxPositionAdapter;
import rtlib.stages.devices.smc100.adapters.SMC100MinPositionAdapter;
import rtlib.stages.devices.smc100.adapters.SMC100PositionAdapter;
import rtlib.stages.devices.smc100.adapters.SMC100Protocol;
import rtlib.stages.devices.smc100.adapters.SMC100ReadyAdapter;
import rtlib.stages.devices.smc100.adapters.SMC100ResetAdapter;
import rtlib.stages.devices.smc100.adapters.SMC100StopAdapter;

public class SMC100StageDevice extends SerialDevice implements
																							StageDeviceInterface,
																							Waiting
{

	private BooleanVariable mEnableVariable, mReadyVariable,
			mHomingVariable, mStopVariable, mResetVariable;
	private DoubleVariable mPositionVariable, mMinPositionVariable,
			mMaxPositionVariable;


	public SMC100StageDevice(String pDeviceName, String pPortName)
	{
		super(pDeviceName, pPortName, SMC100Protocol.cBaudRate);

		SerialTextDeviceAdapter lEnableAdapter = new SMC100EnableAdapter();
		mEnableVariable = addSerialBooleanVariable(	pDeviceName + "Enable",
																								lEnableAdapter);

		SerialTextDeviceAdapter lReadyAdapter = new SMC100ReadyAdapter();
		mReadyVariable = addSerialBooleanVariable(pDeviceName + "Ready",
																							lReadyAdapter);

		SerialTextDeviceAdapter lHomingAdapter = new SMC100HomingAdapter();
		mHomingVariable = addSerialBooleanVariable(	pDeviceName + "Homing",
																								lHomingAdapter);
		
		SerialTextDeviceAdapter lMinPositionAdapter = new SMC100MinPositionAdapter();
		mMinPositionVariable = addSerialBooleanVariable(pDeviceName + "MinPosition",
																								lMinPositionAdapter);

		SerialTextDeviceAdapter lMaxPositionAdapter = new SMC100MaxPositionAdapter();
		mMaxPositionVariable = addSerialBooleanVariable(pDeviceName + "MaxPosition",
																								lMaxPositionAdapter);

		SerialTextDeviceAdapter lStopAdapter = new SMC100StopAdapter();
		mStopVariable = addSerialBooleanVariable(	pDeviceName + "Stop",
																							lStopAdapter);

		SerialTextDeviceAdapter lPositionAdapter = new SMC100PositionAdapter(this);
		mPositionVariable = addSerialDoubleVariable(pDeviceName + "PositionDirect",
																											lPositionAdapter);

		SerialTextDeviceAdapter lResetAdapter = new SMC100ResetAdapter();
		mResetVariable = addSerialBooleanVariable(pDeviceName + "Reset",
																							lResetAdapter);

	}


	@Override
	public boolean open()
	{
		boolean lStart = super.open();

		if (lStart)
		{
			home(0);
			waitToBeReady(0, 1, TimeUnit.MINUTES);
			enable(0);
			return true;
		}
		return lStart;
	}


	@Override
	public int getNumberOfDOFs()
	{
		return 1;
	}

	@Override
	public int getDOFIndexByName(String pName)
	{
		return 0;
	}

	@Override
	public String getDOFNameByIndex(int pIndex)
	{
		return getDeviceName();
	}

	@Override
	public DoubleVariable getMinPositionVariable(int pIndex)
	{
		return mMinPositionVariable;
	}

	@Override
	public DoubleVariable getMaxPositionVariable(int pIndex)
	{
		return mMaxPositionVariable;
	}

	@Override
	public DoubleVariable getEnableVariable(int pIndex)
	{
		return mEnableVariable;
	}

	@Override
	public DoubleVariable getHomingVariable(int pIndex)
	{
		return mHomingVariable;
	}

	@Override
	public DoubleVariable getPositionVariable(int pIndex)
	{
		return mPositionVariable;
	}

	@Override
	public BooleanVariable getReadyVariable(int pIndex)
	{
		return mReadyVariable;
	}

	@Override
	public BooleanVariable getStopVariable(int pIndex)
	{
		return mStopVariable;
	}

	private void reset(int pIndex)
	{
		mResetVariable.setEdge(true);
	}

	public void home(int pIndex)
	{
		mHomingVariable.setEdge(true);
	}

	public void enable(int pIndex)
	{
		mEnableVariable.setEdge(true);
	}

	public void setMinimumPosition(int pMinimumPosition)
	{
		mMinPositionVariable.setValue(pMinimumPosition);
	}

	public void setMaximumPosition(int pMinimumPosition)
	{
		mMaxPositionVariable.setValue(pMinimumPosition);
	}

	public double getCurrentPosition(int pIndex)
	{
		return mPositionVariable.getValue();
	}

	public void goToPosition(int pIndex, double pValue)
	{
		mPositionVariable.setValue(pValue);
	}

	public Boolean waitToBeReady(	int pIndex,
																int pTimeOut,
																TimeUnit pTimeUnit)
	{
		System.out.println("waiting...");
		return waitFor(	pTimeOut,
										pTimeUnit,
										() -> mReadyVariable.getBooleanValue());
	}


	@Override
	public String toString()
	{
		return "SMC100StageDevice [mSerial=" + mSerial
						+ ", getNumberOfDOFs()="
						+ getNumberOfDOFs()
						+ ", getDeviceName()="
						+ getDeviceName()
						+ "]";
	}


}
