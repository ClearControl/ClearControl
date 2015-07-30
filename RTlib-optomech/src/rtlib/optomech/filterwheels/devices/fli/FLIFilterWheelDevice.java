package rtlib.optomech.filterwheels.devices.fli;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.VariableSetListener;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.optomech.filterwheels.FilterWheelDeviceInterface;
import rtlib.optomech.filterwheels.devices.fli.adapters.FilterWheelPositionDeviceAdapter;
import rtlib.optomech.filterwheels.devices.fli.adapters.FilterWheelSpeedDeviceAdapter;
import rtlib.serial.SerialDevice;

public class FLIFilterWheelDevice	extends
									SerialDevice implements FilterWheelDeviceInterface
{

	private final DoubleVariable mFilterPositionVariable,
		mFilterSpeedVariable;
	private volatile int mCachedPosition, mCachedSpeed;

	public FLIFilterWheelDevice(final int pDeviceIndex)
	{
	this(MachineConfiguration	.getCurrentMachineConfiguration()
								.getSerialDevicePort(	"filterwheel.fli",
														pDeviceIndex,
														"NULL"));
	}

	public FLIFilterWheelDevice(final String pPortName)
	{
	super("FLIFilterWheel", pPortName, 9600);

	final FilterWheelPositionDeviceAdapter lFilterWheelPosition = new FilterWheelPositionDeviceAdapter(this);
	mFilterPositionVariable = addSerialDoubleVariable(	"FilterWheelPosition",
														lFilterWheelPosition);
	mFilterPositionVariable.addSetListener(new VariableSetListener<Double>()
	{
		@Override
		public void setEvent(	final Double pCurrentValue,
								final Double pNewValue)
		{
		updateCache(pNewValue);
		}

		private void updateCache(final Double pNewValue)
		{
		mCachedPosition = (int) (pNewValue == null	? 0
													: pNewValue.doubleValue());
		}
	});

	final FilterWheelSpeedDeviceAdapter lFilterWheelSpeed = new FilterWheelSpeedDeviceAdapter(this);
	mFilterSpeedVariable = addSerialDoubleVariable(	"FilterWheelSpeed",
													lFilterWheelSpeed);
	mFilterSpeedVariable.addSetListener(new VariableSetListener<Double>()
	{
		@Override
		public void setEvent(	final Double pCurrentValue,
								final Double pNewValue)
		{
		updateCache(pNewValue);
		}

		private void updateCache(final Double pNewValue)
		{
		mCachedSpeed = (int) (pNewValue == null	? 0
												: pNewValue.doubleValue());
		}
	});
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

	public int getCachedPosition()
	{
	return mCachedPosition;
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

	public int getCachedSpeed()
	{
	return mCachedSpeed;
	}

	@Override
	public void setSpeed(final int pSpeed)
	{
	mFilterSpeedVariable.setValue(pSpeed);
	}

	@Override
	public boolean open()
	{
	final boolean lIsOpened = super.open();
	setSpeed(1);
	setPosition(0);
	return lIsOpened;
	}

}
