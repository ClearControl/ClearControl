package rtlib.ao.slms;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.device.NameableInterface;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;

public interface SpatialPhaseModulatorDeviceInterface	extends
																											OpenCloseDeviceInterface,
																											StartStopDeviceInterface,
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
