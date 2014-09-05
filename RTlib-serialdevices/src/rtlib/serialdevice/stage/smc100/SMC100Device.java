package rtlib.serialdevice.stage.smc100;

import rtlib.core.device.PositioningDevice;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.serial.SerialDevice;
import rtlib.serial.adapters.SerialTextDeviceAdapter;
import rtlib.serialdevice.stage.smc100.adapters.SMC100HomingAdapter;
import rtlib.serialdevice.stage.smc100.adapters.SMC100PositionAdapter;
import rtlib.serialdevice.stage.smc100.adapters.SMC100Protocol;

public class SMC100Device extends SerialDevice implements
																							PositioningDevice
{

	private BooleanVariable mHomingVariable;
	private DoubleVariable mPositionVariable;

	public SMC100Device(String pDeviceName, String pPortName)
	{
		super(pDeviceName, pPortName, SMC100Protocol.cBaudRate);

		SerialTextDeviceAdapter lHomingAdapter = new SMC100HomingAdapter();
		mHomingVariable = addSerialBooleanVariable(	pDeviceName + "Homing",
																								lHomingAdapter);

		SerialTextDeviceAdapter lPositionAdapter = new SMC100PositionAdapter();
		mPositionVariable = addSerialDoubleVariable(pDeviceName + "Position",
																								lPositionAdapter);
	}

	@Override
	public int getNumberOfDOFs()
	{
		return 1;
	}

	@Override
	public DoubleVariable getPositionVariable(int pIndex)
	{
		return mPositionVariable;
	}

	public void home()
	{
		mHomingVariable.setEdge(true);
	}

	public void goToPosition(int pValue)
	{
		mPositionVariable.setValue(pValue);
	}

}
