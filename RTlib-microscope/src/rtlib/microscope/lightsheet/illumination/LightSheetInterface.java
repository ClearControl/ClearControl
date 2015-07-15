package rtlib.microscope.lightsheet.illumination;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.doublev.DoubleVariable;

public interface LightSheetInterface extends OpenCloseDeviceInterface
{

	DoubleVariable getImageHeightVariable();

	DoubleVariable getEffectiveExposureInMicrosecondsVariable();

	DoubleVariable getLineExposureInMicrosecondsVariable();

	DoubleVariable getMarginTimeInMicrosecondsVariable();

	DoubleVariable getReadoutTimeInMicrosecondsPerLineVariable();

	DoubleVariable getLightSheetYInMicronsVariable();

	DoubleVariable getLightSheetZInMicronsVariable();

	DoubleVariable getLightSheetAlphaInDegreesVariable();

	DoubleVariable getLightSheetBetaInDegreesVariable();

	DoubleVariable getLightSheetRangeInMicronsVariable();

	DoubleVariable getLightSheetLengthInMicronsVariable();

	DoubleVariable getPatternOnOffVariable();

	DoubleVariable getPatternPeriodVariable();

	DoubleVariable getPatternPhaseIndexVariable();

	DoubleVariable getPatternOnLengthVariable();

	DoubleVariable getPatternPhaseIncrementVariable();

	DoubleVariable getLaserOnOffArrayVariable(int pLaserIndex);

	void setPatterned(boolean pIsPatternOn);

	boolean isPatterned();

	int getNumberOfPhases();



}
