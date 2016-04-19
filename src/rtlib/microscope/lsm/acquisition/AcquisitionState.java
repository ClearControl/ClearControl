package rtlib.microscope.lsm.acquisition;

import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.interpolation.InterpolationTable;
import rtlib.microscope.lsm.component.detection.DetectionArmInterface;
import rtlib.microscope.lsm.component.lightsheet.LightSheetInterface;

public class AcquisitionState implements AcquisitionStateInterface
{
	private LightSheetMicroscope mLightSheetMicroscope;

	private double mTransitionPlaneZ = 0;
	private final InterpolationTable mInterpolationTableDZ;
	private final InterpolationTable mInterpolationTableIX;
	private final InterpolationTable mInterpolationTableIY;
	private final InterpolationTable mInterpolationTableIZ;
	private final InterpolationTable mInterpolationTableIA;
	private final InterpolationTable mInterpolationTableIB;
	private final InterpolationTable mInterpolationTableIW;
	private final InterpolationTable mInterpolationTableIH;
	private final InterpolationTable mInterpolationTableIP;

	private int mNumberOfLightSheetDevices;

	private int mNumberOfDetectionArmDevices;

	public AcquisitionState(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mNumberOfDetectionArmDevices = mLightSheetMicroscope.getDeviceLists()
																														.getNumberOfDevices(DetectionArmInterface.class);
		mNumberOfLightSheetDevices = mLightSheetMicroscope.getDeviceLists()
																													.getNumberOfDevices(LightSheetInterface.class);

		mInterpolationTableDZ = new InterpolationTable(mNumberOfDetectionArmDevices);

		mInterpolationTableIX = new InterpolationTable(mNumberOfLightSheetDevices);
		mInterpolationTableIY = new InterpolationTable(mNumberOfLightSheetDevices);
		mInterpolationTableIZ = new InterpolationTable(mNumberOfLightSheetDevices);

		mInterpolationTableIA = new InterpolationTable(mNumberOfLightSheetDevices);
		mInterpolationTableIB = new InterpolationTable(mNumberOfLightSheetDevices);
		mInterpolationTableIW = new InterpolationTable(mNumberOfLightSheetDevices);
		mInterpolationTableIH = new InterpolationTable(mNumberOfLightSheetDevices);
		mInterpolationTableIP = new InterpolationTable(mNumberOfLightSheetDevices);
	}

	public AcquisitionState(AcquisitionState pCurrentAcquisitionState)
	{
		mTransitionPlaneZ = pCurrentAcquisitionState.mTransitionPlaneZ;

		mLightSheetMicroscope = pCurrentAcquisitionState.mLightSheetMicroscope;
		mInterpolationTableDZ = new InterpolationTable(pCurrentAcquisitionState.mInterpolationTableDZ);
		mInterpolationTableIX = new InterpolationTable(pCurrentAcquisitionState.mInterpolationTableIX);
		mInterpolationTableIY = new InterpolationTable(pCurrentAcquisitionState.mInterpolationTableIY);
		mInterpolationTableIZ = new InterpolationTable(pCurrentAcquisitionState.mInterpolationTableIZ);

		mInterpolationTableIA = new InterpolationTable(pCurrentAcquisitionState.mInterpolationTableIA);
		mInterpolationTableIB = new InterpolationTable(pCurrentAcquisitionState.mInterpolationTableIB);
		mInterpolationTableIW = new InterpolationTable(pCurrentAcquisitionState.mInterpolationTableIW);
		mInterpolationTableIH = new InterpolationTable(pCurrentAcquisitionState.mInterpolationTableIH);
		mInterpolationTableIP = new InterpolationTable(pCurrentAcquisitionState.mInterpolationTableIP);
	}

	public void addControlPlane(double pZ)
	{

		mInterpolationTableDZ.addRow(pZ);

		mInterpolationTableIX.addRow(pZ);
		mInterpolationTableIY.addRow(pZ);
		mInterpolationTableIZ.addRow(pZ);

		mInterpolationTableIA.addRow(pZ);
		mInterpolationTableIB.addRow(pZ);
		mInterpolationTableIW.addRow(pZ);
		mInterpolationTableIH.addRow(pZ);
		mInterpolationTableIP.addRow(pZ);
	}

	public int getNumberOfControlPlanes()
	{
		return mInterpolationTableDZ.getNumberOfRows();
	}

	public InterpolationTable getDZTable()
	{
		return mInterpolationTableDZ;
	}

	public InterpolationTable getIXTable()
	{
		return mInterpolationTableIX;
	}

	public InterpolationTable getIYTable()
	{
		return mInterpolationTableIY;
	}

	public InterpolationTable getIZTable()
	{
		return mInterpolationTableIZ;
	}

	public InterpolationTable getIATable()
	{
		return mInterpolationTableIA;
	}

	public InterpolationTable getIBTable()
	{
		return mInterpolationTableIB;
	}

	public InterpolationTable getIWTable()
	{
		return mInterpolationTableIW;
	}

	public InterpolationTable getIHTable()
	{
		return mInterpolationTableIH;
	}

	public InterpolationTable getIPTable()
	{
		return mInterpolationTableIP;
	}

	public int getNumberOfDevicesDZ()
	{
		return mInterpolationTableDZ.getNumberOfColumns();
	}

	public int getNumberOfDevicesIX()
	{
		return mInterpolationTableIX.getNumberOfColumns();
	}

	public int getNumberOfDevicesIY()
	{
		return mInterpolationTableIY.getNumberOfColumns();
	}

	public int getNumberOfDevicesIZ()
	{
		return mInterpolationTableIZ.getNumberOfColumns();
	}

	public int getNumberOfDevicesIA()
	{
		return mInterpolationTableIA.getNumberOfColumns();
	}

	public int getNumberOfDevicesIB()
	{
		return mInterpolationTableIB.getNumberOfColumns();
	}

	public int getNumberOfDevicesIW()
	{
		return mInterpolationTableIW.getNumberOfColumns();
	}

	public int getNumberOfDevicesIH()
	{
		return mInterpolationTableIH.getNumberOfColumns();
	}

	public int getNumberOfDevicesIP()
	{
		return mInterpolationTableIP.getNumberOfColumns();
	}

	public double getZ(int pControlPlaneIndex)
	{
		// we are interested in getting the Z position (X in table) _not_ the DZ
		// value!
		double lZ = mInterpolationTableDZ.getRow(pControlPlaneIndex)
																			.getX();
		return lZ;
	}

	public double getMinZ()
	{
		return mInterpolationTableDZ.getMinX();
	}

	public double getMaxZ()
	{
		return mInterpolationTableDZ.getMaxX();
	}

	/******************************************************/

	public void setDZ(double pValue)
	{
		
		int lNumberOfControlPlanes = getNumberOfControlPlanes();

		for (int l = 0; l < mNumberOfDetectionArmDevices; l++)
			for (int c = 0; c < lNumberOfControlPlanes; c++)
				mInterpolationTableDZ.getRow(c).setY(l, pValue);
	}

	public void setAtControlPlaneDZ(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableDZ.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
	}

	public void setInterpolatedAtControlPlaneDZ(int pControlPlaneIndex,
																							int pDeviceIndex)
	{
		double lZ = getZ(pControlPlaneIndex);
		double lValue = getInterpolatedDZ(pControlPlaneIndex, lZ);
		setAtControlPlaneDZ(pControlPlaneIndex, pDeviceIndex, lValue);
	}

	public void addAtControlPlaneDZ(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pDelta)
	{
		mInterpolationTableDZ.getRow(pControlPlaneIndex)
													.addY(pDeviceIndex, pDelta);
	}

	public double getAtControlPlaneDZ(int pControlPlaneIndex,
																		int pDeviceIndex)
	{
		return mInterpolationTableDZ.getRow(pControlPlaneIndex)
																.getY(pDeviceIndex);
	}

	/******************************************************/

	public void setIX(double pValue)
	{
	
		int lNumberOfControlPlanes = getNumberOfControlPlanes();

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			for (int c = 0; c < lNumberOfControlPlanes; c++)
				mInterpolationTableIX.getRow(c).setY(l, pValue);
	}

	public void setAtControlPlaneIX(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIX.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
	}

	public void setInterpolatedAtControlPlaneIX(int pControlPlaneIndex,
																							int pDeviceIndex)
	{
		double lZ = getZ(pControlPlaneIndex);
		double lValue = getInterpolatedIX(pControlPlaneIndex, lZ);
		setAtControlPlaneIX(pControlPlaneIndex, pDeviceIndex, lValue);
	}

	public void addAtControlPlaneIX(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pDelta)
	{
		mInterpolationTableIX.getRow(pControlPlaneIndex)
													.addY(pDeviceIndex, pDelta);
	}

	public double getAtControlPlaneIX(int pControlPlaneIndex,
																		int pDeviceIndex)
	{
		return mInterpolationTableIX.getRow(pControlPlaneIndex)
																.getY(pDeviceIndex);
	}

	/******************************************************/

	public void setIY(double pValue)
	{

		int lNumberOfControlPlanes = getNumberOfControlPlanes();

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			for (int c = 0; c < lNumberOfControlPlanes; c++)
				mInterpolationTableIY.getRow(c).setY(l, pValue);
	}

	public void setAtControlPlaneIY(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIY.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
	}

	public void setInterpolatedAtControlPlaneIY(int pControlPlaneIndex,
																							int pDeviceIndex)
	{
		double lZ = getZ(pControlPlaneIndex);
		double lValue = getInterpolatedIY(pControlPlaneIndex, lZ);
		setAtControlPlaneIY(pControlPlaneIndex, pDeviceIndex, lValue);
	}

	public void addAtControlPlaneIY(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pDelta)
	{
		mInterpolationTableIY.getRow(pControlPlaneIndex)
													.addY(pDeviceIndex, pDelta);
	}

	public double getAtControlPlaneIY(int pControlPlaneIndex,
																		int pDeviceIndex)
	{
		return mInterpolationTableIY.getRow(pControlPlaneIndex)
																.getY(pDeviceIndex);
	}

	/******************************************************/

	public void setIZ(double pValue)
	{
	
		int lNumberOfControlPlanes = getNumberOfControlPlanes();

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			for (int c = 0; c < lNumberOfControlPlanes; c++)
				mInterpolationTableIZ.getRow(c).setY(l, pValue);
	}

	public void setAtControlPlaneIZ(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIZ.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
	}

	public void setInterpolatedAtControlPlaneIZ(int pControlPlaneIndex,
																							int pDeviceIndex)
	{
		double lZ = getZ(pControlPlaneIndex);
		double lValue = getInterpolatedIZ(pControlPlaneIndex, lZ);
		setAtControlPlaneIZ(pControlPlaneIndex, pDeviceIndex, lValue);
	}

	public void addAtControlPlaneIZ(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pDelta)
	{
		mInterpolationTableIZ.getRow(pControlPlaneIndex)
													.addY(pDeviceIndex, pDelta);
	}

	public double getAtControlPlaneIZ(int pControlPlaneIndex,
																		int pDeviceIndex)
	{
		return mInterpolationTableIZ.getRow(pControlPlaneIndex)
																.getY(pDeviceIndex);
	}

	/******************************************************/

	public void setIA(double pValue)
	{

		int lNumberOfControlPlanes = getNumberOfControlPlanes();

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			for (int c = 0; c < lNumberOfControlPlanes; c++)
				mInterpolationTableIA.getRow(c).setY(l, pValue);
	}

	public void setAtControlPlaneIA(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIA.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
	}

	public void setInterpolatedAtControlPlaneIA(int pControlPlaneIndex,
																							int pDeviceIndex)
	{
		double lZ = getZ(pControlPlaneIndex);
		double lValue = getInterpolatedIA(pControlPlaneIndex, lZ);
		setAtControlPlaneIA(pControlPlaneIndex, pDeviceIndex, lValue);
	}

	public void addAtControlPlaneIA(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pDelta)
	{
		mInterpolationTableIA.getRow(pControlPlaneIndex)
													.addY(pDeviceIndex, pDelta);
	}

	public double getAtControlPlaneIA(int pControlPlaneIndex,
																		int pDeviceIndex)
	{
		return mInterpolationTableIA.getRow(pControlPlaneIndex)
																.getY(pDeviceIndex);
	}

	/******************************************************/

	public void setIB(double pValue)
	{
	
		int lNumberOfControlPlanes = getNumberOfControlPlanes();

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			for (int c = 0; c < lNumberOfControlPlanes; c++)
				mInterpolationTableIB.getRow(c).setY(l, pValue);
	}

	public void setAtControlPlaneIB(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIB.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
	}

	public void setInterpolatedAtControlPlaneIB(int pControlPlaneIndex,
																							int pDeviceIndex)
	{
		double lZ = getZ(pControlPlaneIndex);
		double lValue = getInterpolatedIB(pControlPlaneIndex, lZ);
		setAtControlPlaneIB(pControlPlaneIndex, pDeviceIndex, lValue);
	}

	public void addAtControlPlaneIB(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pDelta)
	{
		mInterpolationTableIB.getRow(pControlPlaneIndex)
													.addY(pDeviceIndex, pDelta);
	}

	public double getAtControlPlaneIB(int pControlPlaneIndex,
																		int pDeviceIndex)
	{
		return mInterpolationTableIB.getRow(pControlPlaneIndex)
																.getY(pDeviceIndex);
	}

	/******************************************************/

	public void setIW(double pValue)
	{
	
		int lNumberOfControlPlanes = getNumberOfControlPlanes();

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			for (int c = 0; c < lNumberOfControlPlanes; c++)
				mInterpolationTableIW.getRow(c).setY(l, pValue);
	}

	public void setAtControlPlaneIW(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIW.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
	}

	public void setInterpolatedAtControlPlaneIW(int pControlPlaneIndex,
																							int pDeviceIndex)
	{
		double lZ = getZ(pControlPlaneIndex);
		double lValue = getInterpolatedIW(pControlPlaneIndex, lZ);
		setAtControlPlaneIW(pControlPlaneIndex, pDeviceIndex, lValue);
	}

	public void addAtControlPlaneIW(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pDelta)
	{
		mInterpolationTableIW.getRow(pControlPlaneIndex)
													.addY(pDeviceIndex, pDelta);
	}

	public double getAtControlPlaneIW(int pControlPlaneIndex,
																		int pDeviceIndex)
	{
		return mInterpolationTableIW.getRow(pControlPlaneIndex)
																.getY(pDeviceIndex);
	}

	/******************************************************/

	public void setIH(double pValue)
	{
	
		int lNumberOfControlPlanes = getNumberOfControlPlanes();

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			for (int c = 0; c < lNumberOfControlPlanes; c++)
				mInterpolationTableIH.getRow(c).setY(l, pValue);
	}

	public void setAtControlPlaneIH(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIH.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
	}

	public void setInterpolatedAtControlPlaneIH(int pControlPlaneIndex,
																							int pDeviceIndex)
	{
		double lZ = getZ(pControlPlaneIndex);
		double lValue = getInterpolatedIH(pControlPlaneIndex, lZ);
		setAtControlPlaneIH(pControlPlaneIndex, pDeviceIndex, lValue);
	}

	public void addAtControlPlaneIH(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pDelta)
	{
		mInterpolationTableIH.getRow(pControlPlaneIndex)
													.addY(pDeviceIndex, pDelta);
	}

	public double getAtControlPlaneIH(int pControlPlaneIndex,
																		int pDeviceIndex)
	{
		return mInterpolationTableIH.getRow(pControlPlaneIndex)
																.getY(pDeviceIndex);
	}

	/******************************************************/

	public void setIP(double pValue)
	{
	
		int lNumberOfControlPlanes = getNumberOfControlPlanes();

		for (int l = 0; l < mNumberOfLightSheetDevices; l++)
			for (int c = 0; c < lNumberOfControlPlanes; c++)
				mInterpolationTableIP.getRow(c).setY(l, pValue);
	}

	public void setAtControlPlaneIP(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIP.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
	}

	public void setInterpolatedAtControlPlaneIP(int pControlPlaneIndex,
																							int pDeviceIndex)
	{
		double lZ = getZ(pControlPlaneIndex);
		double lValue = getInterpolatedIP(pControlPlaneIndex, lZ);
		setAtControlPlaneIP(pControlPlaneIndex, pDeviceIndex, lValue);
	}

	public void addAtControlPlaneIP(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pDelta)
	{
		mInterpolationTableIP.getRow(pControlPlaneIndex)
													.addY(pDeviceIndex, pDelta);
	}

	public double getAtControlPlaneIP(int pControlPlaneIndex,
																		int pDeviceIndex)
	{
		return mInterpolationTableIP.getRow(pControlPlaneIndex)
																.getY(pDeviceIndex);
	}

	public double getInterpolatedDZ(int pDeviceIndex, double pZ)
	{
		return mInterpolationTableDZ.getInterpolatedValue(pDeviceIndex,
																											pZ);
	}

	public double getInterpolatedIX(int pDeviceIndex, double pZ)
	{
		return mInterpolationTableIX.getInterpolatedValue(pDeviceIndex,
																											pZ);
	}

	public double getInterpolatedIY(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIY.getInterpolatedValue(pDeviceIndex,
																											pX);
	}

	public double getInterpolatedIZ(int pDeviceIndex, double pZ)
	{
		return mInterpolationTableIZ.getInterpolatedValue(pDeviceIndex,
																											pZ);
	}

	public double getInterpolatedIA(int pDeviceIndex, double pZ)
	{
		return mInterpolationTableIA.getInterpolatedValue(pDeviceIndex,
																											pZ);
	}

	public double getInterpolatedIB(int pDeviceIndex, double pZ)
	{
		return mInterpolationTableIB.getInterpolatedValue(pDeviceIndex,
																											pZ);
	}

	public double getInterpolatedIW(int pDeviceIndex, double pZ)
	{
		return mInterpolationTableIW.getInterpolatedValue(pDeviceIndex,
																											pZ);
	}

	public double getInterpolatedIH(int pDeviceIndex, double pZ)
	{
		return mInterpolationTableIH.getInterpolatedValue(pDeviceIndex,
																											pZ);
	}

	public double getInterpolatedIP(int pDeviceIndex, double pZ)
	{
		return mInterpolationTableIP.getInterpolatedValue(pDeviceIndex,
																											pZ);
	}

	public void setTransitionPlane(double pZ)
	{
		mTransitionPlaneZ = pZ;
	}

	public double getTransitionPlane()
	{
		return mTransitionPlaneZ;
	}

}
