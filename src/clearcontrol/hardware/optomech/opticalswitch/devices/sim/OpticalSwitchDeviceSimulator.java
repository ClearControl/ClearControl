package clearcontrol.hardware.optomech.opticalswitch.devices.sim;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.device.sim.SimulationDeviceInterface;
import clearcontrol.hardware.optomech.opticalswitch.OpticalSwitchDeviceInterface;

public class OpticalSwitchDeviceSimulator extends VirtualDevice	implements
																																OpticalSwitchDeviceInterface,
																																LoggingInterface,
																																SimulationDeviceInterface

{

	private final Variable<Boolean>[] mOpticalSwitchOnOffVariableArray;
	private int mNumberOfSwitches;

	@SuppressWarnings("unchecked")
	public OpticalSwitchDeviceSimulator(String pDeviceName,
																			final int pNumberOfSwitches)
	{
		super(pDeviceName);

		mNumberOfSwitches = pNumberOfSwitches;

		mOpticalSwitchOnOffVariableArray = new Variable[mNumberOfSwitches];

		for (int i = 0; i < pNumberOfSwitches; i++)
		{
			mOpticalSwitchOnOffVariableArray[i] = new Variable<Boolean>("Switch" + i,
																																	false);

			final int fi = i;
			mOpticalSwitchOnOffVariableArray[i].addSetListener((o, n) -> {
				if (isSimLogging())
					info(pDeviceName + ": switch " + fi + " new state: " + n);
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
