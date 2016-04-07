package rtlib.microscope.lsm.component.lightsheet;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.Variable;
import rtlib.microscope.lsm.component.lightsheet.si.StructuredIlluminationPatternInterface;

public interface LightSheetInterface extends OpenCloseDeviceInterface
{

	public Variable<Long> getImageHeightVariable();

	public Variable<Double> getEffectiveExposureInMicrosecondsVariable();

	public Variable<Double> getLineExposureInMicrosecondsVariable();

	public Variable<Double> getOverScanVariable();

	public Variable<Double> getReadoutTimeInMicrosecondsPerLineVariable();

	public Variable<Double> getXVariable();

	public Variable<Double> getYVariable();

	public Variable<Double> getZVariable();

	public Variable<Double> getAlphaInDegreesVariable();

	public Variable<Double> getBetaInDegreesVariable();

	public Variable<Double> getWidthVariable();

	public Variable<Double> getHeightVariable();

	public Variable<Double> getPowerVariable();

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
