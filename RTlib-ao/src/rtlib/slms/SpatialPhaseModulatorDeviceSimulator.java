package rtlib.slms;

import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.kam.memory.ndarray.NDArrayTyped;

public class SpatialPhaseModulatorDeviceSimulator	extends
																									SpatialPhaseModulatorDeviceBase
{

	public SpatialPhaseModulatorDeviceSimulator(String pDeviceName,
																							int pFullMatrixWidthHeight,
																							int pActuatorResolution)
	{
		super(pDeviceName, pFullMatrixWidthHeight, pActuatorResolution);
		mMatrixVariable = new ObjectVariable<NDArrayTyped<Double>>("MatrixReference")
		{
			@Override
			public NDArrayTyped<Double> setEventHook(	final NDArrayTyped<Double> pOldValue,
																								final NDArrayTyped<Double> pNewValue)
			{
				System.out.format("Device: %s received new data: %s",
													getName(),
													pNewValue);

				return super.setEventHook(pOldValue, pNewValue);
			}

		};
	}

	@Override
	public void zero()
	{

	}

	@Override
	public long getRelaxationTimeInMilliseconds()
	{
		return 1;
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

}
