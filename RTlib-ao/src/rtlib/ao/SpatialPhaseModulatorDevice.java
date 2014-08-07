package rtlib.ao;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.kam.memory.ndarray.NDArray;

public abstract class SpatialPhaseModulatorDevice extends
																						SignalStartableDevice	implements
																																	VirtualDeviceInterface
{
	protected DoubleVariable mMatrixWidthVariable;
	protected DoubleVariable mMatrixHeightVariable;
	protected DoubleVariable mActuatorResolutionVariable;
	protected DoubleVariable mNumberOfActuatorsVariable;


	protected ObjectVariable<NDArray> mMatrixVariable;

	public SpatialPhaseModulatorDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

	public DoubleVariable getMatrixWidthVariable()
	{
		return mMatrixWidthVariable;
	}

	public DoubleVariable getMatrixHeightVariable()
	{
		return mMatrixHeightVariable;
	}

	public DoubleVariable getActuatorResolutionVariable()
	{
		return mActuatorResolutionVariable;
	}

	public DoubleVariable getNumberOfActuatorVariable()
	{
		return mNumberOfActuatorsVariable;
	}

	public ObjectVariable<NDArray> getMatrixReference()
	{
		return mMatrixVariable;
	}



}
