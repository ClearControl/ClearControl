package rtlib.hardware.slm.slms;

import org.ejml.data.DenseMatrix64F;

import rtlib.core.variable.Variable;
import rtlib.device.name.NameableInterface;
import rtlib.device.openclose.OpenCloseDeviceInterface;
import rtlib.device.startstop.StartStopDeviceInterface;

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
