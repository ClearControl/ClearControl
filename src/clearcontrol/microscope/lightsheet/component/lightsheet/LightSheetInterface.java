package clearcontrol.microscope.lightsheet.component.lightsheet;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.device.change.HasChangeListenerInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.device.queue.QueueDeviceInterface;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;

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
                                     QueueDeviceInterface<LightSheetQueue>,
                                     HasChangeListenerInterface<VirtualDevice>,
                                     LightSheetParameterInterface
{

  // Below are function variables:

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

}
