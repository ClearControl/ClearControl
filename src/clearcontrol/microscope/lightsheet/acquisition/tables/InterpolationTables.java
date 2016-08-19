package clearcontrol.microscope.lightsheet.acquisition.tables;

import java.util.ArrayList;

import clearcontrol.core.math.interpolation.SplineInterpolationTable;
import clearcontrol.device.change.ChangeListeningBase;
import clearcontrol.microscope.lightsheet.LightSheetDOF;

public class InterpolationTables extends
																ChangeListeningBase<InterpolationTables>
{
	private final int mNumberOfLightSheetDevices;
	private final int mNumberOfDetectionArmDevices;
	private double mTransitionPlaneZ = 0;
	private ArrayList<SplineInterpolationTable> mInterpolationTableList = new ArrayList<SplineInterpolationTable>();

	public InterpolationTables(	int pNumberOfDetectionArmDevices,
															int pNumberOfLightSheetDevices)
	{
		super();

		mNumberOfDetectionArmDevices = pNumberOfDetectionArmDevices;
		mNumberOfLightSheetDevices = pNumberOfLightSheetDevices;

		SplineInterpolationTable lInterpolationTableDZ = new SplineInterpolationTable(mNumberOfDetectionArmDevices);

		SplineInterpolationTable lInterpolationTableIX = new SplineInterpolationTable(mNumberOfLightSheetDevices);
		SplineInterpolationTable lInterpolationTableIY = new SplineInterpolationTable(mNumberOfLightSheetDevices);
		SplineInterpolationTable lInterpolationTableIZ = new SplineInterpolationTable(mNumberOfLightSheetDevices);

		SplineInterpolationTable lInterpolationTableIA = new SplineInterpolationTable(mNumberOfLightSheetDevices);
		SplineInterpolationTable lInterpolationTableIB = new SplineInterpolationTable(mNumberOfLightSheetDevices);
		SplineInterpolationTable lInterpolationTableIW = new SplineInterpolationTable(mNumberOfLightSheetDevices);
		SplineInterpolationTable lInterpolationTableIH = new SplineInterpolationTable(mNumberOfLightSheetDevices);
		SplineInterpolationTable lInterpolationTableIP = new SplineInterpolationTable(mNumberOfLightSheetDevices);

		mInterpolationTableList.add(lInterpolationTableDZ);
		mInterpolationTableList.add(lInterpolationTableIX);
		mInterpolationTableList.add(lInterpolationTableIY);
		mInterpolationTableList.add(lInterpolationTableIZ);

		mInterpolationTableList.add(lInterpolationTableIA);
		mInterpolationTableList.add(lInterpolationTableIB);
		mInterpolationTableList.add(lInterpolationTableIW);
		mInterpolationTableList.add(lInterpolationTableIH);
		mInterpolationTableList.add(lInterpolationTableIP);
	}

	public InterpolationTables(InterpolationTables pCurrentAcquisitionState)
	{
		mNumberOfDetectionArmDevices = pCurrentAcquisitionState.mNumberOfDetectionArmDevices;
		mNumberOfLightSheetDevices = pCurrentAcquisitionState.mNumberOfLightSheetDevices;

		mTransitionPlaneZ = pCurrentAcquisitionState.mTransitionPlaneZ;

		mInterpolationTableList = new ArrayList<>(pCurrentAcquisitionState.mInterpolationTableList);
	}

	public void addControlPlane(double pZ)
	{
		for (SplineInterpolationTable lSplineInterpolationTable : mInterpolationTableList)
			lSplineInterpolationTable.addRow(pZ);
		notifyListeners(this);
	}

	public int getNumberOfControlPlanes()
	{
		return mInterpolationTableList.get(0).getNumberOfRows();
	}

	public int getNumberOfDevices(LightSheetDOF pLightSheetDOF)
	{
		return getTable(pLightSheetDOF).getNumberOfColumns();
	}

	public double getZ(int pControlPlaneIndex)
	{
		// we are interested in getting the Z position (X in table) _not_ the DZ
		// value!
		double lZ = getTable(LightSheetDOF.DZ).getRow(pControlPlaneIndex)
																					.getX();
		return lZ;
	}

	public double getMinZ()
	{
		return getTable(LightSheetDOF.DZ).getMinX();
	}

	public double getMaxZ()
	{
		return getTable(LightSheetDOF.DZ).getMaxX();
	}

	public double getInterpolated(LightSheetDOF pLightSheetDOF,
																int pDeviceIndex,
																double pZ)
	{
		return getTable(pLightSheetDOF).getInterpolatedValue(	pDeviceIndex,
																													pZ);
	}

	public void set(LightSheetDOF pLightSheetDOF,
									int pControlPlaneIndex,
									int pDeviceIndex,
									double pZ)
	{
		getTable(pLightSheetDOF).setY(pControlPlaneIndex,
																	pDeviceIndex,
																	pZ);
		notifyListeners(this);
	}

	public void add(LightSheetDOF pLightSheetDOF,
									int pControlPlaneIndex,
									int pDeviceIndex,
									double pZ)
	{
		getTable(pLightSheetDOF).addY(pControlPlaneIndex,
																	pDeviceIndex,
																	pZ);
		notifyListeners(this);
	}

	public void set(LightSheetDOF pLightSheetDOF,
									int pControlPlaneIndex,
									double pZ)
	{
		getTable(pLightSheetDOF).setY(pControlPlaneIndex, pZ);
		notifyListeners(this);
	}

	public void set(LightSheetDOF pLightSheetDOF, double pZ)
	{
		getTable(pLightSheetDOF).setY(pZ);
		notifyListeners(this);
	}

	public void setTransitionPlane(double pZ)
	{
		mTransitionPlaneZ = pZ;
		notifyListeners(this);
	}

	public double getTransitionPlane()
	{
		return mTransitionPlaneZ;
	}

	private SplineInterpolationTable getTable(LightSheetDOF pLightSheetDOF)
	{
		return mInterpolationTableList.get(pLightSheetDOF.ordinal());
	}

}
