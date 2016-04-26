package rtlib.microscope.lightsheet.component.lightsheet;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.Variable;
import rtlib.core.variable.bounded.BoundedVariable;
import rtlib.device.name.NameableInterface;
import rtlib.device.openclose.OpenCloseDeviceInterface;
import rtlib.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

public interface LightSheetInterface extends
																		NameableInterface,
																		OpenCloseDeviceInterface
{

	public Variable<Long> getImageHeightVariable();

	public BoundedVariable<Double> getEffectiveExposureInMicrosecondsVariable();

	public BoundedVariable<Double> getLineExposureInMicrosecondsVariable();

	public BoundedVariable<Double> getOverScanVariable();

	public BoundedVariable<Double> getReadoutTimeInMicrosecondsPerLineVariable();

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

	public Variable<UnivariateAffineComposableFunction> getXFunction();

	public Variable<UnivariateAffineComposableFunction> getYFunction();

	public Variable<UnivariateAffineComposableFunction> getZFunction();

	public Variable<UnivariateAffineComposableFunction> getWidthFunction();

	public Variable<UnivariateAffineComposableFunction> getHeightFunction();

	public Variable<UnivariateAffineComposableFunction> getAlphaFunction();

	public Variable<UnivariateAffineComposableFunction> getBetaFunction();

	public Variable<UnivariateAffineComposableFunction> getPowerFunction();

	public Variable<PolynomialFunction> getWidthPowerFunction();

	public Variable<PolynomialFunction> getHeightPowerFunction();

	public int getNumberOfPhases(int pLaserIndex);

	public int getNumberOfLaserDigitalControls();

	public void resetFunctions();

	void update();

}
