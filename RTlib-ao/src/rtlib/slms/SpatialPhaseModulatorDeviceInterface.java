package rtlib.slms;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;


public interface SpatialPhaseModulatorDeviceInterface	extends
																											VirtualDeviceInterface
{

	DoubleVariable getMatrixWidthVariable();

	DoubleVariable getMatrixHeightVariable();

	DoubleVariable getActuatorResolutionVariable();

	DoubleVariable getNumberOfActuatorVariable();

	ObjectVariable<DenseMatrix64F> getMatrixReference();

	void zero();

	long getRelaxationTimeInMilliseconds();

}
