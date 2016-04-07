package rtlib.stages;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.objectv.ObjectVariable;

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

	ObjectVariable<Double> getMinPositionVariable(int pDOFIndex);

	ObjectVariable<Double> getMaxPositionVariable(int pDOFIndex);

	ObjectVariable<Boolean> getEnableVariable(int pDOFIndex);

	ObjectVariable<Double> getPositionVariable(int pDOFIndex);

	ObjectVariable<Boolean> getReadyVariable(int pDOFIndex);

	ObjectVariable<Boolean> getHomingVariable(int pDOFIndex);

	ObjectVariable<Boolean> getStopVariable(int pDOFIndex);

}
