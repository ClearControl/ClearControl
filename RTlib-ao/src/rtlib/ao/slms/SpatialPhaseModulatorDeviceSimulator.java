package rtlib.ao.slms;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.variable.objectv.ObjectVariable;

public class SpatialPhaseModulatorDeviceSimulator	extends
																									SpatialPhaseModulatorDeviceBase
{

	public SpatialPhaseModulatorDeviceSimulator(String pDeviceName,
																							int pFullMatrixWidthHeight,
																							int pActuatorResolution)
	{
		super(pDeviceName, pFullMatrixWidthHeight, pActuatorResolution);
		mMatrixVariable = new ObjectVariable<DenseMatrix64F>("MatrixReference")
		{
			@Override
			public DenseMatrix64F setEventHook(	final DenseMatrix64F pOldValue,
																					final DenseMatrix64F pNewValue)
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
