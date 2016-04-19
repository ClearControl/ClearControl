package rtlib.hardware.optomech.opticalswitch.devices.optojena;

import java.util.concurrent.ConcurrentHashMap;

import rtlib.com.serial.SerialDevice;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.variable.Variable;
import rtlib.device.position.PositionDeviceInterface;
import rtlib.hardware.optomech.OptoMechDeviceInterface;
import rtlib.hardware.optomech.opticalswitch.devices.optojena.adapters.FiberSwitchPositionAdapter;

public class OptoJenaFiberSwitchDevice extends SerialDevice	implements
																														PositionDeviceInterface,
																														OptoMechDeviceInterface
{

	private Variable<Integer> mPositionVariable;
	private ConcurrentHashMap<Integer, String> mFilterPositionToNameMap;

	public OptoJenaFiberSwitchDevice(final int pDeviceIndex)
	{
		this(MachineConfiguration.getCurrentMachineConfiguration()
															.getSerialDevicePort(	"fiberswitch.optojena",
																										pDeviceIndex,
																										"NULL"));

		mPositionVariable = new Variable<Integer>("SwitchPosition", 0);
	}

	public OptoJenaFiberSwitchDevice(final String pPortName)
	{
		super("OptoJenaFiberSwitch", pPortName, 76800);

		final FiberSwitchPositionAdapter lFiberSwitchPosition = new FiberSwitchPositionAdapter(this);

		mPositionVariable = addSerialVariable("OpticalSwitchPosition",
																					lFiberSwitchPosition);

	}

	@Override
	public boolean open()
	{
		final boolean lIsOpened = super.open();
		setPosition(0);

		return lIsOpened;
	}

	@Override
	public Variable<Integer> getPositionVariable()
	{
		return mPositionVariable;
	}

	@Override
	public int getPosition()
	{
		return mPositionVariable.get();
	}

	@Override
	public void setPosition(int pPosition)
	{
		mPositionVariable.set(pPosition);
	}

	@Override
	public int[] getValidPositions()
	{
		return new int[]
		{ 1, 2, 3, 4, 5, 6 };
	}

	@Override
	public void setPositionName(int pPositionIndex, String pPositionName)
	{
		mFilterPositionToNameMap.put(pPositionIndex, pPositionName);
	}

	@Override
	public String getPositionName(int pPositionIndex)
	{
		return mFilterPositionToNameMap.get(mPositionVariable);
	}

}
