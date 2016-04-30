package clearcontrol.hardware.stages;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

public interface StageDeviceInterface	extends
																			NameableInterface,
																			OpenCloseDeviceInterface
{
	StageType getStageType();

	int getNumberOfDOFs();

	int getDOFIndexByName(String pName);

	String getDOFNameByIndex(int pDOFIndex);

	void reset(int pDOFIndex);

	void home(int pDOFIndex);

	void enable(int pDOFIndex);

	void setTargetPosition(int pIndex, double pPosition);

	double getTargetPosition(int pIndex);

	double getCurrentPosition(int pDOFIndex);

	Boolean waitToBeReady(int pDOFIndex,
												int pTimeOut,
												TimeUnit pTimeUnit);

	Variable<Double> getMinPositionVariable(int pDOFIndex);

	Variable<Double> getMaxPositionVariable(int pDOFIndex);

	Variable<Boolean> getEnableVariable(int pDOFIndex);

	Variable<Double> getTargetPositionVariable(int pDOFIndex);

	Variable<Double> getCurrentPositionVariable(int pDOFIndex);

	Variable<Boolean> getReadyVariable(int pDOFIndex);

	Variable<Boolean> getHomingVariable(int pDOFIndex);

	Variable<Boolean> getStopVariable(int pDOFIndex);

}
