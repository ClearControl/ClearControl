package rtlib.stages;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.Variable;

public interface StageDeviceInterface	extends
																			OpenCloseDeviceInterface
{
	int getNumberOfDOFs();

	int getDOFIndexByName(String pName);

	String getDOFNameByIndex(int pDOFIndex);

	void reset(int pDOFIndex);

	void home(int pDOFIndex);

	void enable(int pDOFIndex);

	double getCurrentPosition(int pDOFIndex);

	void goToPosition(int pDOFIndex, double pValue);

	Boolean waitToBeReady(int pDOFIndex,
												int pTimeOut,
												TimeUnit pTimeUnit);

	Variable<Double> getMinPositionVariable(int pDOFIndex);

	Variable<Double> getMaxPositionVariable(int pDOFIndex);

	Variable<Boolean> getEnableVariable(int pDOFIndex);

	Variable<Double> getPositionVariable(int pDOFIndex);

	Variable<Boolean> getReadyVariable(int pDOFIndex);

	Variable<Boolean> getHomingVariable(int pDOFIndex);

	Variable<Boolean> getStopVariable(int pDOFIndex);

}
