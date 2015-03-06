package rtlib.filterwheels;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.doublev.DoubleVariable;

public abstract class FilterWheelDeviceBase	extends
																						NamedVirtualDevice implements
																	FilterWheelDeviceInterface
{
	protected DoubleVariable mFilterPositionVariable = null,
			mFilterSpeedVariable = null;

	public FilterWheelDeviceBase(String pDeviceName)
	{
		super(pDeviceName);
		mFilterPositionVariable = new DoubleVariable(	"FilterWheelPosition",
																									0);
		mFilterSpeedVariable = new DoubleVariable("FilterWheelSpeed",
																									0);
	}

	@Override
	public final DoubleVariable getPositionVariable()
	{
		return mFilterPositionVariable;
	}

	@Override
	public final DoubleVariable getSpeedVariable()
	{
		return mFilterSpeedVariable;
	}

	@Override
	public int getPosition()
	{
		return (int) mFilterPositionVariable.getValue();
	}

	@Override
	public void setPosition(final int pPosition)
	{
		mFilterPositionVariable.setValue(pPosition);
	}

	@Override
	public int getSpeed()
	{
		return (int) mFilterSpeedVariable.getValue();
	}

	@Override
	public void setSpeed(final int pSpeed)
	{
		mFilterSpeedVariable.setValue(pSpeed);
	}

}
