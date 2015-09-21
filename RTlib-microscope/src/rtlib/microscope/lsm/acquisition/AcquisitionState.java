package rtlib.microscope.lsm.acquisition;

import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.interpolation.InterpolationTable;

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

	public AcquisitionState(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;
		mInterpolationTableDZ = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																																				.getNumberOfDetectionArmDevices());

		mInterpolationTableIX = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																																				.getNumberOfLightSheetDevices());
		mInterpolationTableIY = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																																				.getNumberOfLightSheetDevices());
		mInterpolationTableIZ = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																																				.getNumberOfLightSheetDevices());

		mInterpolationTableIA = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																																				.getNumberOfLightSheetDevices());
		mInterpolationTableIB = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																																				.getNumberOfLightSheetDevices());
		mInterpolationTableIW = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																																				.getNumberOfLightSheetDevices());
		mInterpolationTableIH = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																																				.getNumberOfLightSheetDevices());
		mInterpolationTableIP = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																																				.getNumberOfLightSheetDevices());
	}

	public AcquisitionState(AcquisitionState pCurrentAcquisitionState)
	{
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
	public void setAtControlPlaneDZ(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableDZ.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
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
	public void setAtControlPlaneIX(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIX.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
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
	public void setAtControlPlaneIY(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIY.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
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
	public void setAtControlPlaneIZ(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIZ.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
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
	public void setAtControlPlaneIA(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIA.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
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
	public void setAtControlPlaneIB(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIB.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
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
	public void setAtControlPlaneIW(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIW.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
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
	public void setAtControlPlaneIH(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIH.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
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
	public void setAtControlPlaneIP(int pControlPlaneIndex,
																	int pDeviceIndex,
																	double pValue)
	{
		mInterpolationTableIP.getRow(pControlPlaneIndex)
													.setY(pDeviceIndex, pValue);
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

	public double getInterpolatedDZ(int pDeviceIndex, double pX)
	{
		return mInterpolationTableDZ.getInterpolatedValue(pDeviceIndex,
																											pX);
	}

	public double getInterpolatedIX(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIX.getInterpolatedValue(pDeviceIndex,
																											pX);
	}

	public double getInterpolatedIY(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIY.getInterpolatedValue(pDeviceIndex,
																											pX);
	}

	public double getInterpolatedIZ(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIZ.getInterpolatedValue(pDeviceIndex,
																											pX);
	}

	public double getInterpolatedIA(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIA.getInterpolatedValue(pDeviceIndex,
																											pX);
	}

	public double getInterpolatedIB(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIB.getInterpolatedValue(pDeviceIndex,
																											pX);
	}

	public double getInterpolatedIW(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIW.getInterpolatedValue(pDeviceIndex,
																											pX);
	}

	public double getInterpolatedIH(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIH.getInterpolatedValue(pDeviceIndex,
																											pX);
	}

	public double getInterpolatedIP(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIP.getInterpolatedValue(pDeviceIndex,
																											pX);
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
