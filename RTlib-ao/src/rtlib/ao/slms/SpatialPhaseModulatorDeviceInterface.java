package rtlib.ao.slms;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.device.NameableInterface;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;


public interface SpatialPhaseModulatorDeviceInterface	extends
																											VirtualDeviceInterface,
																											NameableInterface
{

	int getMatrixWidth();

	int getMatrixHeight();

	int getActuatorResolution();

	DoubleVariable getMatrixWidthVariable();

	DoubleVariable getMatrixHeightVariable();

	DoubleVariable getActuatorResolutionVariable();

	DoubleVariable getNumberOfActuatorVariable();

	ObjectVariable<DenseMatrix64F> getMatrixReference();

	void zero();

	void setMode(int u, int v, double pValue);

	long getRelaxationTimeInMilliseconds();



}
