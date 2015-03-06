package rtlib.slms.dms;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.kam.memory.ndarray.NDArrayTyped;

public abstract class SpatialPhaseModulatorDeviceBase	extends
																											SignalStartableDevice	implements
																																						VirtualDeviceInterface
{
	protected DoubleVariable mMatrixWidthVariable;
	protected DoubleVariable mMatrixHeightVariable;
	protected DoubleVariable mActuatorResolutionVariable;
	protected DoubleVariable mNumberOfActuatorsVariable;

	protected ObjectVariable<NDArrayTyped<Double>> mMatrixVariable;

	public SpatialPhaseModulatorDeviceBase(	String pDeviceName,
																					int pFullMatrixWidthHeight,
																					int pActuatorResolution)
	{
		super(pDeviceName);

		mMatrixWidthVariable = new DoubleVariable("MatrixWidth",
																							pFullMatrixWidthHeight);
		mMatrixHeightVariable = new DoubleVariable(	"MatrixHeight",
																								pFullMatrixWidthHeight);
		mActuatorResolutionVariable = new DoubleVariable(	"ActuatorResolution",
																											pActuatorResolution);

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
