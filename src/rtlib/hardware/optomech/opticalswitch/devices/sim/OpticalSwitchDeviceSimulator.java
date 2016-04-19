package rtlib.hardware.optomech.opticalswitch.devices.sim;

import rtlib.core.variable.Variable;
import rtlib.device.name.NamedVirtualDevice;
import rtlib.hardware.optomech.opticalswitch.OpticalSwitchDeviceInterface;

public class OpticalSwitchDeviceSimulator extends NamedVirtualDevice implements
																																		OpticalSwitchDeviceInterface

{

	private final Variable<Boolean>[] mOpticalSwitchOnOffVariableArray;
	private int mNumberOfSwitches;

	@SuppressWarnings("unchecked")
	public OpticalSwitchDeviceSimulator(String pDeviceName,
																			final int pNumberOfSwitches)
	{
		super(pDeviceName);

		mNumberOfSwitches = pNumberOfSwitches;

		mOpticalSwitchOnOffVariableArray = (Variable<Boolean>[]) new Variable[mNumberOfSwitches];

		for (int i = 0; i < pNumberOfSwitches; i++)
		{
			mOpticalSwitchOnOffVariableArray[i] = new Variable<Boolean>("Switch" + i,
																																	false);

			final int fi = i;
			mOpticalSwitchOnOffVariableArray[i].addSetListener((o, n) -> {
				System.out.println(pDeviceName + ": switch "
														+ fi
														+ " new state: "
														+ n);
			});
		}

	}

	@Override
	public boolean open()
	{
		for (Variable<Boolean> lSwitchVariable : mOpticalSwitchOnOffVariableArray)
			lSwitchVariable.set(true);

		return true;
	}

	@Override
	public boolean close()
	{
		for (Variable<Boolean> lSwitchVariable : mOpticalSwitchOnOffVariableArray)
			lSwitchVariable.set(false);

		return true;
	}

	@Override
	public int getNumberOfSwitches()
	{
		return mNumberOfSwitches;
	}

	@Override
	public Variable<Boolean> getSwitchVariable(int pSwitchIndex)
	{
		return mOpticalSwitchOnOffVariableArray[pSwitchIndex];
	}

	@Override
	public String getSwitchName(int pSwitchIndex)
	{
		return "optical switch " + pSwitchIndex;
	}

}
