package rtlib.microscope.lsm.acquisition;

import gnu.trove.list.array.TIntArrayList;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.interpolation.InterpolationTable;

public class AcquisitionState implements AcquisitionStateInterface
{
	private LightSheetMicroscope mLightSheetMicroscope;

	private final TIntArrayList mTransitionPlanes = new TIntArrayList();
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
		mTransitionPlanes.add(0);

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

	public void setTransitionCtrlPlane(	int pControlPlaneIndex,
										boolean pBestDetectionArm)
	{
		mTransitionPlanes.set(	pControlPlaneIndex,
								pBestDetectionArm ? 1 : 0);
	}

	public boolean isTransitionCtrlPlane(int pControlPlaneIndex)
	{
		return mTransitionPlanes.get(pControlPlaneIndex) == 1;
	}

	public void setAtControlPlaneDZ(	int pControlPlaneIndex,
										int pDeviceIndex,
										double pValue)
	{
		mInterpolationTableDZ.getRow(pControlPlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getAtControlPlaneDZ(int pControlPlaneIndex,
										int pDeviceIndex)
	{
		return mInterpolationTableDZ.getRow(pControlPlaneIndex)
									.getY(pDeviceIndex);
	}

	public void setAtControlPlaneIX(	int pControlPlaneIndex,
										int pDeviceIndex,
										double pValue)
	{
		mInterpolationTableIX.getRow(pControlPlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getAtControlPlaneIX(int pControlPlaneIndex,
										int pDeviceIndex)
	{
		return mInterpolationTableIX.getRow(pControlPlaneIndex)
									.getY(pDeviceIndex);
	}

	public void setAtControlPlaneIY(	int pControlPlaneIndex,
										int pDeviceIndex,
										double pValue)
	{
		mInterpolationTableIY.getRow(pControlPlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getAtControlPlaneIY(int pControlPlaneIndex,
										int pDeviceIndex)
	{
		return mInterpolationTableIY.getRow(pControlPlaneIndex)
									.getY(pDeviceIndex);
	}

	public void setAtControlPlaneIZ(	int pControlPlaneIndex,
										int pDeviceIndex,
										double pValue)
	{
		mInterpolationTableIZ.getRow(pControlPlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getAtControlPlaneIZ(int pControlPlaneIndex,
										int pDeviceIndex)
	{
		return mInterpolationTableIZ.getRow(pControlPlaneIndex)
									.getY(pDeviceIndex);
	}

	public void setAtControlPlaneIA(	int pControlPlaneIndex,
										int pDeviceIndex,
										double pValue)
	{
		mInterpolationTableIA.getRow(pControlPlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getAtControlPlaneIA(int pControlPlaneIndex,
										int pDeviceIndex)
	{
		return mInterpolationTableIA.getRow(pControlPlaneIndex)
									.getY(pDeviceIndex);
	}

	public void setAtControlPlaneIB(	int pControlPlaneIndex,
										int pDeviceIndex,
										double pValue)
	{
		mInterpolationTableIB.getRow(pControlPlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getAtControlPlaneIB(int pControlPlaneIndex,
										int pDeviceIndex)
	{
		return mInterpolationTableIB.getRow(pControlPlaneIndex)
									.getY(pDeviceIndex);
	}

	public void setAtControlPlaneIW(	int pControlPlaneIndex,
										int pDeviceIndex,
										double pValue)
	{
		mInterpolationTableIW.getRow(pControlPlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getAtControlPlaneIW(int pControlPlaneIndex,
										int pDeviceIndex)
	{
		return mInterpolationTableIW.getRow(pControlPlaneIndex)
									.getY(pDeviceIndex);
	}

	public void setAtControlPlaneIH(	int pControlPlaneIndex,
										int pDeviceIndex,
										double pValue)
	{
		mInterpolationTableIH.getRow(pControlPlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getAtControlPlaneIH(int pControlPlaneIndex,
										int pDeviceIndex)
	{
		return mInterpolationTableIH.getRow(pControlPlaneIndex)
									.getY(pDeviceIndex);
	}

	public void setAtControlPlaneIP(	int pControlPlaneIndex,
										int pDeviceIndex,
										double pValue)
	{
		mInterpolationTableIP.getRow(pControlPlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getAtControlPlaneIP(int pControlPlaneIndex,
										int pDeviceIndex)
	{
		return mInterpolationTableIP.getRow(pControlPlaneIndex)
									.getY(pDeviceIndex);
	}

	public double getInterpolatedDZ(int pDeviceIndex, double pX)
	{
		return mInterpolationTableDZ.getInterpolatedValue(pDeviceIndex, pX);
	}

	public double getInterpolatedIX(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIX.getInterpolatedValue(pDeviceIndex, pX);
	}

	public double getInterpolatedIY(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIY.getInterpolatedValue(pDeviceIndex, pX);
	}

	public double getInterpolatedIZ(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIZ.getInterpolatedValue(pDeviceIndex, pX);
	}

	public double getInterpolatedIA(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIA.getInterpolatedValue(pDeviceIndex, pX);
	}

	public double getInterpolatedIB(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIB.getInterpolatedValue(pDeviceIndex, pX);
	}

	public double getInterpolatedIW(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIW.getInterpolatedValue(pDeviceIndex, pX);
	}

	public double getInterpolatedIH(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIH.getInterpolatedValue(pDeviceIndex, pX);
	}

	public double getInterpolatedIP(int pDeviceIndex, double pX)
	{
		return mInterpolationTableIP.getInterpolatedValue(pDeviceIndex, pX);
	}




}
