package rtlib.microscope.lsm.acquisition;

public interface StackAcquisitionInterface extends Iterable<Integer>
{

	void setStackDepth(int pStackDepth);

	void setLowZ(double pValue);

	void setHighZ(double pValue);

	double getMinZ();

	double getMaxZ();

	void setStepZ(double pValue);

	double getStepZ();

	int getStackDepth();

	double getStackDepthInMicrons();

	void setCurrentState(AcquisitionState pNewAcquisitionState);

	AcquisitionState getCurrentState();

	void setToStackPlane(int pPlaneIndex);

	void setToControlPlane(int pControlPlaneIndex);

	int getBestDetectionArm(int pControlPlaneIndex);

	double getControlPlaneZ(int pControlPlaneIndex);

	double getZRamp(int pPlaneIndex);

	int getPlaneIndexForZRamp(double pZRampValue);

	double getDZ(int pPlaneIndex, int pDeviceIndex);

	double getIX(int pPlaneIndex, int pDeviceIndex);

	double getIY(int pPlaneIndex, int pDeviceIndex);

	double getIZ(int pPlaneIndex, int pDeviceIndex);

	double getIA(int pPlaneIndex, int pDeviceIndex);

	double getIB(int pPlaneIndex, int pDeviceIndex);

	double getIW(int pPlaneIndex, int pDeviceIndex);

	double getIH(int pPlaneIndex, int pDeviceIndex);

	double getIP(int pPlaneIndex, int pDeviceIndex);

	void addStackMargin(int pZPlaneIndex, int pNumber);

	void addStackMargin(int pNumber);

}
