package rtlib.optomech.fiberswitch;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.types.doublev.DoubleVariable;

public abstract class OpticalSwitchDeviceBase	extends
												NamedVirtualDevice	implements
																	OpticalSwitchDeviceInterface
{
	protected DoubleVariable mSwitchPositionVariable = null;

	public OpticalSwitchDeviceBase(String pDeviceName)
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
	public void setPosition(final int pPosition)
	{
		mSwitchPositionVariable.setValue(pPosition);
	}

}
