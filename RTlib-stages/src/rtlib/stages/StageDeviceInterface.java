package rtlib.stages;

import java.util.concurrent.TimeUnit;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;

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
	

	Boolean waitToBeReady(int pDOFIndex, int pTimeOut, TimeUnit pTimeUnit);

	DoubleVariable getMinPositionVariable(int pDOFIndex);

	DoubleVariable getMaxPositionVariable(int pDOFIndex);

	DoubleVariable getEnableVariable(int pDOFIndex);

	DoubleVariable getPositionVariable(int pDOFIndex);

	DoubleVariable getReadyVariable(int pDOFIndex);

	DoubleVariable getHomingVariable(int pDOFIndex);

	BooleanVariable getStopVariable(int pDOFIndex);



}
