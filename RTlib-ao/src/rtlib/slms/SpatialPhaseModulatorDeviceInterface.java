package rtlib.slms;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.kam.memory.ndarray.NDArrayTyped;


public interface SpatialPhaseModulatorDeviceInterface	extends
																											VirtualDeviceInterface
{

	DoubleVariable getMatrixWidthVariable();

	DoubleVariable getMatrixHeightVariable();

	DoubleVariable getActuatorResolutionVariable();

	DoubleVariable getNumberOfActuatorVariable();

	ObjectVariable<NDArrayTyped<Double>> getMatrixReference();

	void zero();

	long getRelaxationTimeInMilliseconds();

}
