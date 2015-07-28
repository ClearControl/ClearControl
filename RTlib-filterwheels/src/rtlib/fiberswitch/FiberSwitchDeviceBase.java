package rtlib.fiberswitch;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.longv.LongVariable;

public abstract class FiberSwitchDeviceBase extends NamedVirtualDevice
											implements
											FiberSwitchDeviceInterface
{
	protected DoubleVariable mSwitchPositionVariable = null;

	public FiberSwitchDeviceBase(String pDeviceName)
	{
	super(pDeviceName);
	mSwitchPositionVariable = new DoubleVariable(	"FilterWheelPosition",
												0);
	}

	@Override
	public final DoubleVariable getPositionVariable()
	{
	return mSwitchPositionVariable;
	}

	@Override
	public int getPosition()
	{
	return (int) mSwitchPositionVariable.getValue();
	}

	@Override
	public void setSwitchPosition(final int pPosition)
	{
	mSwitchPositionVariable.setValue(pPosition);
	}

}
