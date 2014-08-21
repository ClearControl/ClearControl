package rtlib.lightsheet;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.device.queue.QueueProvider;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;

public interface LightSheetSignalGeneratorInterface	extends
																										VirtualDeviceInterface,
																										StateQueueDeviceInterface
{

	DoubleVariable getImageHeightVariable();

	DoubleVariable getEffectiveExposureInMicrosecondsVariable();

	DoubleVariable getLineExposureInMicrosecondsVariable();

	DoubleVariable getMarginTimeInMicrosecondsVariable();

	DoubleVariable getReadoutTimeInMicrosecondsPerLineVariable();

	DoubleVariable getLightSheetYInMicronsVariable();

	DoubleVariable getLightSheetZInMicronsVariable();

	DoubleVariable getLightSheetThetaInDegreesVariable();

	DoubleVariable getFocusZVariable();

	DoubleVariable getLightSheetLengthInMicronsVariable();

	DoubleVariable getStageYVariable();

	DoubleVariable getPatternOnOffVariable();

	DoubleVariable getPatternPeriodVariable();

	DoubleVariable getPatternPhaseIndexVariable();

	DoubleVariable getPatternOnLengthVariable();

	DoubleVariable getPatternPhaseIncrementVariable();

	DoubleVariable getLaserOnOffArrayVariable(int pLaserIndex);

	BooleanVariable getLockLightSheetToPifocVariable();

	void requestUpdate();

	boolean isPlaying();

	void setPatterned(boolean pIsPatternOn);

	boolean isPatterned();

	QueueProvider<?> getQueueProviderFor2DContinuousAcquisition();

	long estimatePlayTimeInMilliseconds();

	void finalizeQueueFor3DStackAcquisition();

	int getNumberOfPhases();

}
