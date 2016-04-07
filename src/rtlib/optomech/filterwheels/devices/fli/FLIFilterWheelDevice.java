package rtlib.optomech.filterwheels.devices.fli;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.ObjectVariable;
import rtlib.core.variable.VariableSetListener;
import rtlib.optomech.filterwheels.FilterWheelDeviceInterface;
import rtlib.optomech.filterwheels.devices.fli.adapters.FilterWheelPositionDeviceAdapter;
import rtlib.optomech.filterwheels.devices.fli.adapters.FilterWheelSpeedDeviceAdapter;
import rtlib.serial.SerialDevice;

public class FLIFilterWheelDevice extends SerialDevice implements
																											FilterWheelDeviceInterface
{

	private final ObjectVariable<Integer> mFilterPositionVariable,
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
		mFilterPositionVariable = addSerialVariable("FilterWheelPosition",
																								lFilterWheelPosition);
		mFilterPositionVariable.addSetListener(new VariableSetListener<Integer>()
		{
			@Override
			public void setEvent(	final Integer pCurrentValue,
														final Integer pNewValue)
			{
				updateCache(pNewValue);
			}

			private void updateCache(final Integer pNewValue)
			{
				mCachedPosition = (int) (pNewValue == null ? 0
																									: pNewValue.doubleValue());
			}
		});

		final FilterWheelSpeedDeviceAdapter lFilterWheelSpeed = new FilterWheelSpeedDeviceAdapter(this);
		mFilterSpeedVariable = addSerialVariable(	"FilterWheelSpeed",
																							lFilterWheelSpeed);
		mFilterSpeedVariable.addSetListener(new VariableSetListener<Integer>()
		{
			@Override
			public void setEvent(	final Integer pCurrentValue,
														final Integer pNewValue)
			{
				updateCache(pNewValue);
			}

			private void updateCache(final Integer pNewValue)
			{
				mCachedSpeed = (int) (pNewValue == null	? 0
																								: pNewValue.doubleValue());
			}
		});
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

	public int getCachedPosition()
	{
		return mCachedPosition;
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

	public int getCachedSpeed()
	{
		return mCachedSpeed;
	}

	@Override
	public void setSpeed(final int pSpeed)
	{
		mFilterSpeedVariable.set(pSpeed);
	}

	@Override
	public boolean open()
	{
		final boolean lIsOpened = super.open();
		setSpeed(1);
		setPosition(0);
		return lIsOpened;
	}

	@Override
	public int[] getValidPositions()
	{
		return new int[]
		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	}

}
