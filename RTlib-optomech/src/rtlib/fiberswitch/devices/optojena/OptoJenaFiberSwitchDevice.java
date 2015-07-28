package rtlib.fiberswitch.devices.optojena;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.VariableSetListener;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.longv.LongVariable;
import rtlib.fiberswitch.FiberSwitchDeviceInterface;
import rtlib.fiberswitch.devices.optojena.adapters.FiberSwitchPositionDeviceAdapter;
import rtlib.filterwheels.FilterWheelDeviceInterface;
import rtlib.filterwheels.devices.fli.adapters.FilterWheelPositionDeviceAdapter;
import rtlib.filterwheels.devices.fli.adapters.FilterWheelSpeedDeviceAdapter;
import rtlib.serial.SerialDevice;

public class OptoJenaFiberSwitchDevice extends
	SerialDevice implements FiberSwitchDeviceInterface
{

	private final DoubleVariable	mPositionVariable;

	public OptoJenaFiberSwitchDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration
			.getCurrentMachineConfiguration()
			.getSerialDevicePort(
				"fiberswitch.optojena",
				pDeviceIndex,
				"NULL"));
	}

	public OptoJenaFiberSwitchDevice(final String pPortName)
	{
		super("OptoJenaFiberSwitch", pPortName, 9600);

		final FiberSwitchPositionDeviceAdapter lFiberSwitchPosition = new FiberSwitchPositionDeviceAdapter(this);

		mPositionVariable = addSerialDoubleVariable(
			"FiberSwitchPosition",
			lFiberSwitchPosition);

	}

	@Override
	public boolean open()
	{
		final boolean lIsOpened = super.open();
		setSwitchPosition(0);
		
		return lIsOpened;
	}

	@Override
	public DoubleVariable getPositionVariable()
	{
		return mPositionVariable;
	}

	@Override
	public int getPosition()
	{
		return (int) mPositionVariable.getValue();
	}

	@Override
	public void setSwitchPosition(int pPosition)
	{
			mPositionVariable.set((double) pPosition);
	}

}
