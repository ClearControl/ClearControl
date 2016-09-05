package clearcontrol.hardware.optomech.filterwheels.devices.sim;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.device.sim.SimulationDeviceInterface;
import clearcontrol.hardware.optomech.filterwheels.FilterWheelDeviceBase;
import clearcontrol.hardware.optomech.filterwheels.FilterWheelDeviceInterface;

public class FilterWheelDeviceSimulator extends FilterWheelDeviceBase	implements
																																			FilterWheelDeviceInterface,
																																			LoggingInterface,
																																			SimulationDeviceInterface
{

	public FilterWheelDeviceSimulator(String pDeviceName,
																		int... pValidPositions)
	{
		super(pDeviceName, pValidPositions);

		mPositionVariable.addSetListener((o, n) -> {
			if (isSimLogging())
			{
				String lMessage = String.format("%s: new position: %d corresponding to filter '%s' \n",
																				pDeviceName,
																				n,
																				getPositionName(n));

				info(lMessage);
			}
		});
	}

	@Override
	public boolean open()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		return true;
	}

}
