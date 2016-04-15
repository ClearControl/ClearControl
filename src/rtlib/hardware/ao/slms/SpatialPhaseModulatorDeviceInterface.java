package rtlib.hardware.ao.slms;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.device.NameableInterface;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.variable.Variable;

public interface SpatialPhaseModulatorDeviceInterface	extends
																											OpenCloseDeviceInterface,
																											StartStopDeviceInterface,
																											NameableInterface
{

	int getMatrixWidth();

	int getMatrixHeight();

	int getActuatorResolution();

	Variable<Double> getMatrixWidthVariable();

	Variable<Double> getMatrixHeightVariable();

	Variable<Double> getActuatorResolutionVariable();

	Variable<Double> getNumberOfActuatorVariable();

	Variable<DenseMatrix64F> getMatrixReference();

	void zero();

	void setMode(int u, int v, double pValue);

	long getRelaxationTimeInMilliseconds();

}
