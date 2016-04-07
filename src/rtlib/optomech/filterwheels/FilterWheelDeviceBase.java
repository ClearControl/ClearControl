package rtlib.optomech.filterwheels;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.ObjectVariable;

public abstract class FilterWheelDeviceBase	extends
																						NamedVirtualDevice implements
																															FilterWheelDeviceInterface
{
	protected ObjectVariable<Integer> mFilterPositionVariable = null,
			mFilterSpeedVariable = null;

	public FilterWheelDeviceBase(String pDeviceName)
	{
		super(pDeviceName);
		mFilterPositionVariable = new ObjectVariable<Integer>("FilterWheelPosition",
																													0);
		mFilterSpeedVariable = new ObjectVariable<Integer>(	"FilterWheelSpeed",
																												0);
	}

	@Override
	public final ObjectVariable<Integer> getPositionVariable()
	{
		return mFilterPositionVariable;
	}

	@Override
	public final ObjectVariable<Integer> getSpeedVariable()
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
