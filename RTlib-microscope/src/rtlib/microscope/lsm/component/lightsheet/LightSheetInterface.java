package rtlib.microscope.lsm.component.lightsheet;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.microscope.lsm.component.lightsheet.si.StructuredIlluminationPatternInterface;

public interface LightSheetInterface extends OpenCloseDeviceInterface
{

	public DoubleVariable getImageHeightVariable();

	public DoubleVariable getEffectiveExposureInMicrosecondsVariable();

	public DoubleVariable getLineExposureInMicrosecondsVariable();

	public DoubleVariable getOverScanVariable();

	public DoubleVariable getReadoutTimeInMicrosecondsPerLineVariable();

	public DoubleVariable getXVariable();

	public DoubleVariable getYVariable();

	public DoubleVariable getZVariable();

	public DoubleVariable getAlphaInDegreesVariable();

	public DoubleVariable getBetaInDegreesVariable();

	public DoubleVariable getWidthVariable();

	public DoubleVariable getHeightVariable();

	public DoubleVariable getPowerVariable();

	public BooleanVariable getAdaptPowerToWidthHeightVariable();

	public BooleanVariable getLaserOnOffArrayVariable(int pLaserIndex);

	public BooleanVariable getSIPatternOnOffVariable(int pLaserIndex);

	public ObjectVariable<StructuredIlluminationPatternInterface> getSIPatternVariable(int pLaserIndex);

	public ObjectVariable<UnivariateAffineComposableFunction> getXFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getYFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getZFunction();
	
	public ObjectVariable<UnivariateAffineComposableFunction> getWidthFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getHeightFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getAlphaFunction();
	
	public ObjectVariable<UnivariateAffineComposableFunction> getBetaFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getPowerFunction();
	
	public ObjectVariable<PolynomialFunction> getWidthPowerFunction();
	
	public ObjectVariable<PolynomialFunction> getHeightPowerFunction();

	public int getNumberOfPhases(int pLaserIndex);

	public int getNumberOfLaserDigitalControls();

	public void resetFunctions();

	void update();




}
