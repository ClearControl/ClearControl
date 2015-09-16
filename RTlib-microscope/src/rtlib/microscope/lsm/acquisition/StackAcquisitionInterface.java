package rtlib.microscope.lsm.acquisition;

public interface StackAcquisitionInterface extends Iterable<Integer>
{

	void setStackDepth(int pStackDepth);

	void setLowZ(double pValue);

	void setHighZ(double pValue);

	double getLowZ();

	double getHighZ();

	void setStepZ(double pValue);

	double getStepZ();

	int getStackDepth();

	double getStackDepthInMicrons();

	void setCurrentAcquisitionState(AcquisitionState pNewAcquisitionState);

	AcquisitionState getCurrentAcquisitionState();

	void setToStackPlane(int pPlaneIndex);

	void setToControlPlane(int pControlPlaneIndex);

	int getBestDetectioArm(int pControlPlaneIndex);

}
