package rtlib.optomech.filterwheels;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.Variable;

public abstract class FilterWheelDeviceBase	extends
																						NamedVirtualDevice implements
																															FilterWheelDeviceInterface
{
	protected Variable<Integer> mFilterPositionVariable = null,
			mFilterSpeedVariable = null;

	public FilterWheelDeviceBase(String pDeviceName)
	{
		super(pDeviceName);
		mFilterPositionVariable = new Variable<Integer>("FilterWheelPosition",
																													0);
		mFilterSpeedVariable = new Variable<Integer>(	"FilterWheelSpeed",
																												0);
	}

	@Override
	public final Variable<Integer> getPositionVariable()
	{
		return mFilterPositionVariable;
	}

	@Override
	public final Variable<Integer> getSpeedVariable()
	{
		return mFilterSpeedVariable;
	}

	@Override
	public int getPosition()
	{
		return mFilterPositionVariable.get();
	}

	@Override
	public void setPosition(final int pPosition)
	{
		mFilterPositionVariable.set(pPosition);
	}

	@Override
	public int getSpeed()
	{
		return mFilterSpeedVariable.get();
	}

	@Override
	public void setSpeed(final int pSpeed)
	{
		mFilterSpeedVariable.set(pSpeed);
	}

}
