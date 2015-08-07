package rtlib.microscope.lightsheet.acquisition;

import static java.lang.Math.floor;

import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.microscope.lightsheet.LightSheetMicroscope;
import rtlib.microscope.lightsheet.acquisition.interpolation.InterpolationTable;

public class StackAcquisition
{

	private final LightSheetMicroscope mLightSheetMicroscope;

	private final DoubleVariable mLowZ = new DoubleVariable("LowZ",
															25);
	private final DoubleVariable mHighZ = new DoubleVariable(	"HighZ",
																75);

	private final DoubleVariable mZStep = new DoubleVariable(	"ZStep",
																0.5);

	private final InterpolationTable mInterpolationTableDZ;
	private final InterpolationTable mInterpolationTableIZ;
	private final InterpolationTable mInterpolationTableIY;
	private final InterpolationTable mInterpolationTableIA;
	private final InterpolationTable mInterpolationTableIB;
	private final InterpolationTable mInterpolationTableIR;
	private final InterpolationTable mInterpolationTableIL;
	private final InterpolationTable mInterpolationTableIP;

	public StackAcquisition(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;
		mInterpolationTableDZ = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																			.getNumberOfDetectionArmDevices());
		mInterpolationTableIZ = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																			.getNumberOfLightSheetDevices());
		mInterpolationTableIY = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																			.getNumberOfLightSheetDevices());
		mInterpolationTableIA = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																			.getNumberOfLightSheetDevices());
		mInterpolationTableIB = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																			.getNumberOfLightSheetDevices());
		mInterpolationTableIR = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																			.getNumberOfLightSheetDevices());
		mInterpolationTableIL = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																			.getNumberOfLightSheetDevices());
		mInterpolationTableIP = new InterpolationTable(mLightSheetMicroscope.getDeviceLists()
																			.getNumberOfLightSheetDevices());
	}

	public void addControlPlane(double pZ)
	{
		mInterpolationTableDZ.addRow(pZ);
		mInterpolationTableIZ.addRow(pZ);
		mInterpolationTableIY.addRow(pZ);
		mInterpolationTableIA.addRow(pZ);
		mInterpolationTableIB.addRow(pZ);
		mInterpolationTableIR.addRow(pZ);
		mInterpolationTableIL.addRow(pZ);
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

	public void setPlane(int pPlaneIndex)
	{
		final int lNumberOfDetectionPathDevices = mLightSheetMicroscope.getDeviceLists()
																		.getNumberOfDetectionArmDevices();

		for (int i = 0; i < lNumberOfDetectionPathDevices; i++)
		{
			mLightSheetMicroscope.setDZ(i, getDZ(pPlaneIndex, i));
		}

		final int lNumberOfLightsheetDevices = mLightSheetMicroscope.getDeviceLists()
																	.getNumberOfLightSheetDevices();

		for (int i = 0; i < lNumberOfLightsheetDevices; i++)
		{
			mLightSheetMicroscope.setIZ(i, getIZ(pPlaneIndex, i));
			mLightSheetMicroscope.setIY(i, getIY(pPlaneIndex, i));
			mLightSheetMicroscope.setIA(i, getIA(pPlaneIndex, i));
			mLightSheetMicroscope.setIB(i, getIB(pPlaneIndex, i));
			mLightSheetMicroscope.setIW(i, getIR(pPlaneIndex, i));
			mLightSheetMicroscope.setIH(i, getIL(pPlaneIndex, i));
			mLightSheetMicroscope.setIP(i, getIP(pPlaneIndex, i));
		}

		final int lNumberOfLaserDevices = mLightSheetMicroscope.getDeviceLists()
																.getNumberOfLaserDevices();

		for (int i = 0; i < lNumberOfLaserDevices; i++)
		{
			mLightSheetMicroscope.setIP(i, getIP(pPlaneIndex, i));
		}

	}

	private double getRamp(int pPlaneIndex)
	{
		final double lNormalizedZ = (1.0 * pPlaneIndex) / (getNumberOfPlanes() - 1);

		final double lZstart = mLowZ.getValue();
		final double lZstop = mHighZ.getValue();
		final double lZ = mLowZ.getValue() + lNormalizedZ
							* (lZstop - lZstart);

		return lZ;
	}

	public double getDZ(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableDZ.getInterpolatedValue(	pDeviceIndex,
																						lRamp);
		return lRamp + lInterpolatedValue;
	}

	public double getIZ(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIZ.getInterpolatedValue(	pDeviceIndex,
																						lRamp);
		return lRamp + lInterpolatedValue;
	}

	public double getIY(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIY.getInterpolatedValue(	pDeviceIndex,
																						lRamp);
		return lInterpolatedValue;
	}

	public double getIA(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIA.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIB(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIB.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIR(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIR.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIL(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIL.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIP(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getRamp(pPlaneIndex);
		final double lInterpolatedValue = mInterpolationTableIP.getInterpolatedValue(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

}
