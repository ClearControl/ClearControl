package rtlib.microscope.lightsheetmicroscope.illumination;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;

public interface LightSheetInterface extends VirtualDeviceInterface
{

	DoubleVariable getImageHeightVariable();

	DoubleVariable getEffectiveExposureInMicrosecondsVariable();

	DoubleVariable getLineExposureInMicrosecondsVariable();

	DoubleVariable getMarginTimeInMicrosecondsVariable();

	DoubleVariable getReadoutTimeInMicrosecondsPerLineVariable();

	DoubleVariable getLightSheetYInMicronsVariable();

	DoubleVariable getLightSheetZInMicronsVariable();

	DoubleVariable getLightSheetAlphaInDegreesVariable();

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
