package rtlib.microscope.lightsheet.illumination;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;

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

	public DoubleVariable getLaserOnOffArrayVariable(int pLaserIndex);

	public BooleanVariable getSIPatternOnOffVariable();

	public int getNumberOfPhases();



}
