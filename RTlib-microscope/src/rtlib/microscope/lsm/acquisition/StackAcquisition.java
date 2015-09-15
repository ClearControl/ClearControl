package rtlib.microscope.lsm.acquisition;

import static java.lang.Math.floor;

import java.util.ArrayList;
import java.util.Iterator;

import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.interpolation.InterpolationTable;

public class StackAcquisition implements Iterable<Integer>
{

	private final LightSheetMicroscope mLightSheetMicroscope;

	private final DoubleVariable mLowZ = new DoubleVariable("LowZ",
															25);
	private final DoubleVariable mHighZ = new DoubleVariable(	"HighZ",
																75);

	private final DoubleVariable mZStep = new DoubleVariable(	"ZStep",
																0.5);

	private final InterpolationTable mInterpolationTableDZ;
	private final InterpolationTable mInterpolationTableIX;
	private final InterpolationTable mInterpolationTableIY;
	private final InterpolationTable mInterpolationTableIZ;
	private final InterpolationTable mInterpolationTableIA;
	private final InterpolationTable mInterpolationTableIB;
	private final InterpolationTable mInterpolationTableIW;
	private final InterpolationTable mInterpolationTableIH;
	private final InterpolationTable mInterpolationTableIP;

	public StackAcquisition(LightSheetMicroscope pLightSheetMicroscope)
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

	public double getDepthInMicrons()
	{
		return (mHighZ.getValue() - mLowZ.getValue());
	}

	public int getNumberOfPlanes()
	{
		return (int) floor(getDepthInMicrons() / mZStep.getValue());
	}

	@Override
	public Iterator<Integer> iterator()
	{
		Iterator<Integer> lIterator = new Iterator<Integer>()
		{
			int mZIndex = 0;

			@Override
			public boolean hasNext()
			{
				return mZIndex < getNumberOfPlanes();
			}

			@Override
			public Integer next()
			{
				int lZIndex = mZIndex;
				setPlane(lZIndex);
				mZIndex++;
				return lZIndex;
			}
		};

		return lIterator;
	}

	public void setPlane(int pPlaneIndex)
	{
		final int lNumberOfDetectionPathDevices = mLightSheetMicroscope.getDeviceLists()
																		.getNumberOfDetectionArmDevices();

		for (int d = 0; d < lNumberOfDetectionPathDevices; d++)
		{
			mLightSheetMicroscope.setDZ(d, getDZ(pPlaneIndex, d));
		}

		final int lNumberOfLightsheetDevices = mLightSheetMicroscope.getDeviceLists()
																	.getNumberOfLightSheetDevices();

		for (int l = 0; l < lNumberOfLightsheetDevices; l++)
		{
			mLightSheetMicroscope.setIX(l, getIX(pPlaneIndex, l));
			mLightSheetMicroscope.setIY(l, getIY(pPlaneIndex, l));
			mLightSheetMicroscope.setIZ(l, getIZ(pPlaneIndex, l));

			mLightSheetMicroscope.setIA(l, getIA(pPlaneIndex, l));
			mLightSheetMicroscope.setIB(l, getIB(pPlaneIndex, l));
			mLightSheetMicroscope.setIW(l, getIW(pPlaneIndex, l));
			mLightSheetMicroscope.setIH(l, getIH(pPlaneIndex, l));
			mLightSheetMicroscope.setIP(l, getIP(pPlaneIndex, l));
		}

		final int lNumberOfLaserDevices = mLightSheetMicroscope.getDeviceLists()
																.getNumberOfLaserDevices();

		for (int i = 0; i < lNumberOfLaserDevices; i++)
		{
			mLightSheetMicroscope.setIP(i, getIP(pPlaneIndex, i));
		}

	}

	private double getZRamp(int pPlaneIndex)
	{
		final double lNormalizedZ = (1.0 * pPlaneIndex) / (getNumberOfPlanes() - 1);

		final double lZstart = mLowZ.getValue();
		final double lZstop = mHighZ.getValue();
		final double lZ = mLowZ.getValue() + lNormalizedZ
							* (lZstop - lZstart);

		return lZ;
	}

	public void setDZ(	int pReferencePlaneIndex,
						int pDeviceIndex,
						double pValue)
	{
		mInterpolationTableDZ.getRow(pReferencePlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public void setIX(	int pReferencePlaneIndex,
						int pDeviceIndex,
						double pValue)
	{
		mInterpolationTableIX.getRow(pReferencePlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public void setIY(	int pReferencePlaneIndex,
						int pDeviceIndex,
						double pValue)
	{
		mInterpolationTableIY.getRow(pReferencePlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public void setIZ(	int pReferencePlaneIndex,
						int pDeviceIndex,
						double pValue)
	{
		mInterpolationTableIZ.getRow(pReferencePlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public void setIA(	int pReferencePlaneIndex,
						int pDeviceIndex,
						double pValue)
	{
		mInterpolationTableIA.getRow(pReferencePlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public void setIB(	int pReferencePlaneIndex,
						int pDeviceIndex,
						double pValue)
	{
		mInterpolationTableIB.getRow(pReferencePlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public void setIW(	int pReferencePlaneIndex,
						int pDeviceIndex,
						double pValue)
	{
		mInterpolationTableIW.getRow(pReferencePlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public void setIH(	int pReferencePlaneIndex,
						int pDeviceIndex,
						double pValue)
	{
		mInterpolationTableIH.getRow(pReferencePlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public void setIP(	int pReferencePlaneIndex,
						int pDeviceIndex,
						double pValue)
	{
		mInterpolationTableIP.getRow(pReferencePlaneIndex)
								.setY(pDeviceIndex, pValue);
	}

	public double getDZ(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableDZ.getInterpolatedValue(	pDeviceIndex,
																						lRamp);
		return lRamp + lInterpolatedValue;
	}

	public double getIX(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIX.getInterpolatedValue(	pDeviceIndex,
																						lRamp);
		return lInterpolatedValue;
	}

	public double getIY(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIY.getInterpolatedValue(	pDeviceIndex,
																						lRamp);
		return lInterpolatedValue;
	}

	public double getIZ(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIZ.getInterpolatedValue(	pDeviceIndex,
																						lRamp);
		return lRamp + lInterpolatedValue;
	}

	public double getIA(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIA.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIB(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIB.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIW(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIW.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIH(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIH.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIP(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIP.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

}
