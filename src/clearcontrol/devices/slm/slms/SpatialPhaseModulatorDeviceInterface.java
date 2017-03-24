package clearcontrol.devices.slm.slms;

import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.startstop.StartStopDeviceInterface;
import clearcontrol.core.variable.Variable;

import org.ejml.data.DenseMatrix64F;

public interface SpatialPhaseModulatorDeviceInterface extends
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
