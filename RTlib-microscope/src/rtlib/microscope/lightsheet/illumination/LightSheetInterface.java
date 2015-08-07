package rtlib.microscope.lightsheet.illumination;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.microscope.lightsheet.illumination.si.StructuredIlluminationPatternInterface;

public interface LightSheetInterface extends OpenCloseDeviceInterface
{

	public DoubleVariable getImageHeightVariable();

	public DoubleVariable getEffectiveExposureInMicrosecondsVariable();

	public DoubleVariable getLineExposureInMicrosecondsVariable();

	public DoubleVariable getMarginTimeInMicrosecondsVariable();

	public DoubleVariable getReadoutTimeInMicrosecondsPerLineVariable();

	public DoubleVariable getLightSheetYInMicronsVariable();

	public DoubleVariable getLightSheetZInMicronsVariable();

	public DoubleVariable getLightSheetAlphaInDegreesVariable();

	public DoubleVariable getLightSheetBetaInDegreesVariable();

	public DoubleVariable getLightSheetRangeInMicronsVariable();

	public DoubleVariable getLightSheetLengthInMicronsVariable();

	public DoubleVariable getLightSheetPoweInmWVariable();

	public DoubleVariable getLaserOnOffArrayVariable(int pLaserIndex);

	public BooleanVariable getSIPatternOnOffVariable(int pLaserIndex);

	public ObjectVariable<StructuredIlluminationPatternInterface> getSIPatternVariable(int pLaserIndex);

	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetXFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetYFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetZFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetBetaFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetIrisDiameterFunction();

	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetLaserPowerFunction();

	public int getNumberOfPhases(int pLaserIndex);

}
