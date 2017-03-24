package clearcontrol.microscope.lightsheet.component.lightsheet;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.queue.HasVariableStateQueues;
import clearcontrol.core.device.queue.StateQueueDeviceInterface;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

/**
 *
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public interface LightSheetInterface extends
                                     NameableInterface,
                                     OpenCloseDeviceInterface,
                                     StateQueueDeviceInterface,
                                     HasVariableStateQueues,
                                     HasChangeListenerInterface<VirtualDevice>
{

  // These variables should be synced with camera variables:

  public Variable<Long> getImageHeightVariable();

  public BoundedVariable<Number> getEffectiveExposureInMicrosecondsVariable();

  // public BoundedVariable<Double> getLineExposureInMicrosecondsVariable();

  public BoundedVariable<Number> getOverScanVariable();

  public BoundedVariable<Number> getReadoutTimeInMicrosecondsPerLineVariable();

  // Below are variables that can be adjusted freely:

  public BoundedVariable<Number> getXVariable();

  public BoundedVariable<Number> getYVariable();

  public BoundedVariable<Number> getZVariable();

  public BoundedVariable<Number> getAlphaInDegreesVariable();

  public BoundedVariable<Number> getBetaInDegreesVariable();

  public BoundedVariable<Number> getWidthVariable();

  public BoundedVariable<Number> getHeightVariable();

  public BoundedVariable<Number> getPowerVariable();

  public Variable<Boolean> getAdaptPowerToWidthHeightVariable();

  public Variable<Boolean> getLaserOnOffArrayVariable(int pLaserIndex);

  public Variable<Boolean> getSIPatternOnOffVariable(int pLaserIndex);

  public Variable<StructuredIlluminationPatternInterface> getSIPatternVariable(int pLaserIndex);

  public Variable<UnivariateAffineFunction> getXFunction();

  public Variable<UnivariateAffineFunction> getYFunction();

  public Variable<UnivariateAffineFunction> getZFunction();

  public Variable<UnivariateAffineFunction> getWidthFunction();

  public Variable<UnivariateAffineFunction> getHeightFunction();

  public Variable<UnivariateAffineFunction> getAlphaFunction();

  public Variable<UnivariateAffineFunction> getBetaFunction();

  public Variable<UnivariateAffineFunction> getPowerFunction();

  public Variable<PolynomialFunction> getWidthPowerFunction();

  public Variable<PolynomialFunction> getHeightPowerFunction();

  // Convenience methods:

  public int getNumberOfPhases(int pLaserIndex);

  public int getNumberOfLaserDigitalControls();

  // Resetting and updating:

  public void resetFunctions();

  public void resetBounds();

  public void update();

}
