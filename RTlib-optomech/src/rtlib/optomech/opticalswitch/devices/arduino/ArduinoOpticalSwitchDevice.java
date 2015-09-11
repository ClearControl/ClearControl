package rtlib.optomech.opticalswitch.devices.arduino;

import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.optomech.OpticalSwitchDeviceInterface;
import rtlib.optomech.opticalswitch.devices.arduino.adapters.ArduinoOpticalSwitchPositionAdapter;
import rtlib.serial.SerialDevice;

public class ArduinoOpticalSwitchDevice extends SerialDevice	implements
															OpticalSwitchDeviceInterface
{

	private final DoubleVariable mPositionVariable;

	private static final int cAllClosed = 100;
	private static final int cAllOpened = 200;

	public ArduinoOpticalSwitchDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
									.getSerialDevicePort(	"fiberswitch.optojena",
															pDeviceIndex,
															"NULL"));
	}

	public ArduinoOpticalSwitchDevice(final String pPortName)
	{
		super("ArduinoOpticalSwitch", pPortName, 250000);

		final ArduinoOpticalSwitchPositionAdapter lFiberSwitchPosition = new ArduinoOpticalSwitchPositionAdapter(this);

		mPositionVariable = addSerialDoubleVariable("OpticalSwitchPosition",
													lFiberSwitchPosition);

	}

	@Override
	public boolean open()
	{
		final boolean lIsOpened = super.open();
		setPosition(cAllClosed);

		return lIsOpened;
	}

	@Override
	public boolean close()
	{
		final boolean lIsClosed = super.close();
		setPosition(cAllClosed);

		return lIsClosed;
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
	public void setPosition(int pPosition)
	{
		mPositionVariable.set((double) pPosition);
	}

	@Override
	public int[] getValidPositions()
	{
		return new int[]
		{ 0, 1, 2, 3, cAllClosed, cAllOpened };
	}

}
