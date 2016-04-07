package rtlib.stages.devices.smc100;

import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.timing.Waiting;
import rtlib.core.variable.ObjectVariable;
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

public class SMC100StageDevice extends SerialDevice	implements
																										StageDeviceInterface,
																										Waiting
{

	private final ObjectVariable<Boolean> mEnableVariable,
			mReadyVariable, mHomingVariable, mStopVariable, mResetVariable;
	private final ObjectVariable<Double> mPositionVariable,
			mMinPositionVariable, mMaxPositionVariable;

	public SMC100StageDevice(String pDeviceName, String pPortName)
	{
		super(pDeviceName, pPortName, SMC100Protocol.cBaudRate);

		final SerialTextDeviceAdapter lEnableAdapter = new SMC100EnableAdapter();
		mEnableVariable = addSerialVariable(pDeviceName + "Enable",
																				lEnableAdapter);

		final SerialTextDeviceAdapter lReadyAdapter = new SMC100ReadyAdapter();
		mReadyVariable = addSerialVariable(	pDeviceName + "Ready",
																				lReadyAdapter);

		final SerialTextDeviceAdapter lHomingAdapter = new SMC100HomingAdapter();
		mHomingVariable = addSerialVariable(pDeviceName + "Homing",
																				lHomingAdapter);

		final SerialTextDeviceAdapter lMinPositionAdapter = new SMC100MinPositionAdapter();
		mMinPositionVariable = addSerialVariable(	pDeviceName + "MinPosition",
																							lMinPositionAdapter);

		final SerialTextDeviceAdapter lMaxPositionAdapter = new SMC100MaxPositionAdapter();
		mMaxPositionVariable = addSerialVariable(	pDeviceName + "MaxPosition",
																							lMaxPositionAdapter);

		final SerialTextDeviceAdapter lStopAdapter = new SMC100StopAdapter();
		mStopVariable = addSerialVariable(pDeviceName + "Stop",
																			lStopAdapter);

		final SerialTextDeviceAdapter lPositionAdapter = new SMC100PositionAdapter(this);
		mPositionVariable = addSerialVariable(pDeviceName + "PositionDirect",
																					lPositionAdapter);

		final SerialTextDeviceAdapter lResetAdapter = new SMC100ResetAdapter();
		mResetVariable = addSerialVariable(	pDeviceName + "Reset",
																				lResetAdapter);

	}

	@Override
	public boolean open()
	{
		final boolean lStart = super.open();

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
		return getName();
	}

	@Override
	public ObjectVariable<Double> getMinPositionVariable(int pIndex)
	{
		return mMinPositionVariable;
	}

	@Override
	public ObjectVariable<Double> getMaxPositionVariable(int pIndex)
	{
		return mMaxPositionVariable;
	}

	@Override
	public ObjectVariable<Boolean> getEnableVariable(int pIndex)
	{
		return mEnableVariable;
	}

	@Override
	public ObjectVariable<Boolean> getHomingVariable(int pIndex)
	{
		return mHomingVariable;
	}

	@Override
	public ObjectVariable<Double> getPositionVariable(int pIndex)
	{
		return mPositionVariable;
	}

	@Override
	public ObjectVariable<Boolean> getReadyVariable(int pIndex)
	{
		return mReadyVariable;
	}

	@Override
	public ObjectVariable<Boolean> getStopVariable(int pIndex)
	{
		return mStopVariable;
	}

	@Override
	public void reset(int pIndex)
	{
		mResetVariable.set(false);
		mResetVariable.set(true);
	}

	@Override
	public void home(int pIndex)
	{
		mResetVariable.set(false);
		mResetVariable.set(true);
	}

	@Override
	public void enable(int pIndex)
	{
		mResetVariable.set(false);
		mResetVariable.set(true);
	}

	public void setMinimumPosition(double pMinimumPosition)
	{
		mMinPositionVariable.set(pMinimumPosition);
	}

	public void setMaximumPosition(double pMinimumPosition)
	{
		mMaxPositionVariable.set(pMinimumPosition);
	}

	@Override
	public double getCurrentPosition(int pIndex)
	{
		return mPositionVariable.get();
	}

	@Override
	public void goToPosition(int pIndex, double pValue)
	{
		mPositionVariable.set(pValue);
	}

	@Override
	public Boolean waitToBeReady(	int pIndex,
																int pTimeOut,
																TimeUnit pTimeUnit)
	{
		// System.out.println("waiting...");
		return waitFor(pTimeOut, pTimeUnit, () -> mReadyVariable.get());
	}

	@Override
	public String toString()
	{
		return "SMC100StageDevice [mSerial=" + getSerial()
						+ ", getNumberOfDOFs()="
						+ getNumberOfDOFs()
						+ ", getDeviceName()="
						+ getName()
						+ "]";
	}

}
