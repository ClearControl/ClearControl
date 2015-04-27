package rtlib.lightsheet;

import org.apache.commons.math3.analysis.UnivariateFunction;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.device.queue.QueueProvider;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;

public interface LightSheetSignalGeneratorInterface<M extends UnivariateFunction> extends
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

	DoubleVariable getLightSheetAlphaInDegreesVariable();

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

	DoubleVariable getMicronsToNormGalvoUnitVariable();

	ObjectVariable<M> getPifoc2LightSheetModelVariable();

	void requestUpdate();

	boolean isPlaying();

	void setPatterned(boolean pIsPatternOn);

	boolean isPatterned();

	QueueProvider<?> getQueueProviderFor2DContinuousAcquisition();

	long estimatePlayTimeInMilliseconds();

	void finalizeQueueFor3DStackAcquisition();

	int getNumberOfPhases();



}
