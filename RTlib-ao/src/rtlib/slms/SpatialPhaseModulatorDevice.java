package rtlib.slms;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.kam.memory.ndarray.NDArrayTyped;

public abstract class SpatialPhaseModulatorDevice	extends
																									SignalStartableDevice	implements
																																				VirtualDeviceInterface
{
	protected DoubleVariable mMatrixWidthVariable;
	protected DoubleVariable mMatrixHeightVariable;
	protected DoubleVariable mActuatorResolutionVariable;
	protected DoubleVariable mNumberOfActuatorsVariable;

	protected ObjectVariable<NDArrayTyped<Double>> mMatrixVariable;

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

	public ObjectVariable<NDArrayTyped<Double>> getMatrixReference()
	{
		return mMatrixVariable;
	}

	public abstract void zero();

	public abstract long getRelaxationTimeInMilliseconds();

}
