package rtlib.serialdevice.filterwheel.fli;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.VariableListenerAdapter;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.serial.SerialDevice;
import rtlib.serialdevice.filterwheel.fli.adapters.FilterWheelPositionDeviceAdapter;
import rtlib.serialdevice.filterwheel.fli.adapters.FilterWheelSpeedDeviceAdapter;

public class FLIFilterWheelDevice extends SerialDevice
{

	private final DoubleVariable mFilterPositionVariable,
			mFilterSpeedVariable;
	private volatile int mCachedPosition, mCachedSpeed;

	public FLIFilterWheelDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"filterwheel.fli",
																										pDeviceIndex,
																										"NULL"));
	}

	public FLIFilterWheelDevice(final String pPortName)
	{
		super("FLIFilterWheel", pPortName, 9600);

		final FilterWheelPositionDeviceAdapter lFilterWheelPosition = new FilterWheelPositionDeviceAdapter(this);
		mFilterPositionVariable = addSerialDoubleVariable("FilterWheelPosition",
																											lFilterWheelPosition);
		mFilterPositionVariable.addListener(new VariableListenerAdapter<Double>()
		{
			@Override
			public void setEvent(	final Double pCurrentValue,
														final Double pNewValue)
			{
				updateCache(pNewValue);
			}

			private void updateCache(final Double pNewValue)
			{
				mCachedPosition = (int) (pNewValue == null ? 0
																									: pNewValue.doubleValue());
			}
		});

		final FilterWheelSpeedDeviceAdapter lFilterWheelSpeed = new FilterWheelSpeedDeviceAdapter(this);
		mFilterSpeedVariable = addSerialDoubleVariable(	"FilterWheelSpeed",
																										lFilterWheelSpeed);
		mFilterSpeedVariable.addListener(new VariableListenerAdapter<Double>()
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

	public final DoubleVariable getPositionVariable()
	{
		return mFilterPositionVariable;
	}

	public final DoubleVariable getSpeedVariable()
	{
		return mFilterSpeedVariable;
	}

	public int getPosition()
	{
		return (int) mFilterPositionVariable.getValue();
	}

	public int getCachedPosition()
	{
		return mCachedPosition;
	}

	public void setPosition(final int pPosition)
	{
		mFilterPositionVariable.setValue(pPosition);
	}

	public int getSpeed()
	{
		return (int) mFilterSpeedVariable.getValue();
	}

	public int getCachedSpeed()
	{
		return mCachedSpeed;
	}

	public void setSpeed(final int pSpeed)
	{
		mFilterSpeedVariable.setValue(pSpeed);
	}

	@Override
	public boolean open()
	{
		boolean lIsOpened = super.open();
		setPosition(0);
		return lIsOpened;
	}

}
