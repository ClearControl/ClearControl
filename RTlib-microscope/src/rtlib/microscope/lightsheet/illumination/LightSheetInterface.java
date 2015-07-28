package rtlib.microscope.lightsheet.illumination;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.microscope.lightsheet.illumination.si.StructuredIlluminatioPatternInterface;

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

	public ObjectVariable<StructuredIlluminatioPatternInterface> getSIPatternVariable(int pLaserIndex);

	public int getNumberOfPhases(int pLaserIndex);



}
