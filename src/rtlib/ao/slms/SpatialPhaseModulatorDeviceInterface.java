package rtlib.ao.slms;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.device.NameableInterface;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.ObjectVariable;

public interface SpatialPhaseModulatorDeviceInterface	extends
																											OpenCloseDeviceInterface,
																											StartStopDeviceInterface,
																											NameableInterface
{

	int getMatrixWidth();

	int getMatrixHeight();

	int getActuatorResolution();

	ObjectVariable<Double> getMatrixWidthVariable();

	ObjectVariable<Double> getMatrixHeightVariable();

	ObjectVariable<Double> getActuatorResolutionVariable();

	ObjectVariable<Double> getNumberOfActuatorVariable();

	ObjectVariable<DenseMatrix64F> getMatrixReference();

	void zero();

	void setMode(int u, int v, double pValue);

	long getRelaxationTimeInMilliseconds();

}
