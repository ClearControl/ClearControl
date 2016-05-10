package clearcontrol.microscope.lightsheet.component.lightsheet;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.device.change.HasChangeListenerInterface;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

public interface LightSheetInterface extends
																		NameableInterface,
																		OpenCloseDeviceInterface,
																		HasChangeListenerInterface
{

	// These variables should be synced with camera variables:

	public Variable<Long> getImageHeightVariable();

	public BoundedVariable<Double> getEffectiveExposureInMicrosecondsVariable();

	public BoundedVariable<Double> getLineExposureInMicrosecondsVariable();

	public BoundedVariable<Double> getOverScanVariable();

	public BoundedVariable<Double> getReadoutTimeInMicrosecondsPerLineVariable();

	// Below are variables that can be adjusted freely:

	public BoundedVariable<Double> getXVariable();

	public BoundedVariable<Double> getYVariable();

	public BoundedVariable<Number> getZVariable();

	public BoundedVariable<Double> getAlphaInDegreesVariable();

	public BoundedVariable<Double> getBetaInDegreesVariable();

	public BoundedVariable<Double> getWidthVariable();

	public BoundedVariable<Double> getHeightVariable();

	public BoundedVariable<Double> getPowerVariable();

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
