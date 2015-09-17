package rtlib.microscope.lsm.acquisition;

import static java.lang.Math.floor;

import java.util.Iterator;

import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.microscope.lsm.LightSheetMicroscope;

public class StackAcquisition implements StackAcquisitionInterface
{

	private final LightSheetMicroscope mLightSheetMicroscope;

	private final DoubleVariable mLowZ = new DoubleVariable("LowZ",
															25);
	private final DoubleVariable mHighZ = new DoubleVariable(	"HighZ",
																75);

	private final DoubleVariable mZStep = new DoubleVariable(	"ZStep",
																0.5);

	private volatile AcquisitionState mCurrentAcquisitionState;

	public StackAcquisition(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;
		
		mCurrentAcquisitionState = new AcquisitionState(mLightSheetMicroscope);
	}

	@Override
	public void setCurrentAcquisitionState(AcquisitionState pNewAcquisitionState)
	{
		mCurrentAcquisitionState = pNewAcquisitionState;
	}
	
	@Override
	public AcquisitionState getCurrentAcquisitionState()
	{
		return mCurrentAcquisitionState;
	}

	@Override
	public void setLowZ(double pValue)
	{
		mLowZ.set(pValue);
	}

	@Override
	public double getLowZ()
	{
		return mLowZ.getValue();
	}

	@Override
	public void setHighZ(double pValue)
	{
		mHighZ.set(pValue);
	}

	@Override
	public double getHighZ()
	{
		return mHighZ.getValue();
	}

	@Override
	public void setStepZ(double pValue)
	{
		mZStep.set(pValue);
	}

	@Override
	public double getStepZ()
	{
		return mZStep.getValue();
	}

	@Override
	public double getStackDepthInMicrons()
	{
		return (mHighZ.getValue() - mLowZ.getValue());
	}

	@Override
	public void setStackDepth(int pNumberOfPlanes)
	{
		double lStepZ = (getStackDepthInMicrons() / pNumberOfPlanes);

		setHighZ(getLowZ() + pNumberOfPlanes * lStepZ);
		setStepZ(lStepZ);
	}

	@Override
	public int getStackDepth()
	{
		return (int) floor(getStackDepthInMicrons() / mZStep.getValue());
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
				return mZIndex < getStackDepth();
			}

			@Override
			public Integer next()
			{
				int lZIndex = mZIndex;
				setToStackPlane(lZIndex);
				mZIndex++;
				return lZIndex;
			}
		};

		return lIterator;
	}

	@Override
	public void setToStackPlane(int pPlaneIndex)
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
			mLightSheetMicroscope.setIZ(l, getIW(pPlaneIndex, l));
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

	@Override
	public void setToControlPlane(int pControlPlaneIndex)
	{
		final int lNumberOfDetectionPathDevices = mLightSheetMicroscope.getDeviceLists()
																		.getNumberOfDetectionArmDevices();

		final int lNumberOfLightsheetDevices = mLightSheetMicroscope.getDeviceLists()
																	.getNumberOfLightSheetDevices();

		final int lNumberOfLaserDevices = mLightSheetMicroscope.getDeviceLists()
																.getNumberOfLaserDevices();

		for (int d = 0; d < lNumberOfDetectionPathDevices; d++)
		{
			mLightSheetMicroscope.setDZ(d,
										mCurrentAcquisitionState.getAtControlPlaneDZ(	pControlPlaneIndex,
																						d));
		}

		for (int l = 0; l < lNumberOfLightsheetDevices; l++)
		{
			mLightSheetMicroscope.setIX(l,
										mCurrentAcquisitionState.getAtControlPlaneIX(	pControlPlaneIndex,
																						l));
			mLightSheetMicroscope.setIY(l,
										mCurrentAcquisitionState.getAtControlPlaneIY(	pControlPlaneIndex,
																						l));
			mLightSheetMicroscope.setIZ(l,
										mCurrentAcquisitionState.getAtControlPlaneIZ(	pControlPlaneIndex,
																						l));

			mLightSheetMicroscope.setIA(l,
										mCurrentAcquisitionState.getAtControlPlaneIA(	pControlPlaneIndex,
																						l));
			mLightSheetMicroscope.setIB(l,
										mCurrentAcquisitionState.getAtControlPlaneIB(	pControlPlaneIndex,
																						l));
			mLightSheetMicroscope.setIZ(l,
										mCurrentAcquisitionState.getAtControlPlaneIW(	pControlPlaneIndex,
																						l));
			mLightSheetMicroscope.setIH(l,
										mCurrentAcquisitionState.getAtControlPlaneIH(	pControlPlaneIndex,
																						l));
			mLightSheetMicroscope.setIP(l,
										mCurrentAcquisitionState.getAtControlPlaneIP(	pControlPlaneIndex,
																						l));
		}

		for (int i = 0; i < lNumberOfLaserDevices; i++)
		{
			mLightSheetMicroscope.setIP(i,
										mCurrentAcquisitionState.getAtControlPlaneIP(	pControlPlaneIndex,
																						i));
		}

	}

	private double getZRamp(int pPlaneIndex)
	{
		final double lZ = mLowZ.getValue() + pPlaneIndex * getStepZ();
		return lZ;
	}

	public double getDZ(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedDZ(	pDeviceIndex,
																						lRamp);
		return lRamp + lInterpolatedValue;
	}

	public double getIX(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIX(	pDeviceIndex,
																						lRamp);
		return lInterpolatedValue;
	}

	public double getIY(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIY(	pDeviceIndex,
																						lRamp);
		return lInterpolatedValue;
	}

	public double getIZ(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIZ(	pDeviceIndex,
																						lRamp);
		return lRamp + lInterpolatedValue;
	}

	public double getIA(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIA(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIB(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIB(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIW(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIW(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIH(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIH(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	public double getIP(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIP(	pDeviceIndex,
																						lRamp);

		return lInterpolatedValue;
	}

	@Override
	public int getBestDetectioArm(int pControlPlaneIndex)
	{
		int lNumberOfControlPlanes = mCurrentAcquisitionState.getNumberOfControlPlanes();

		int lFirstTransitionPlane = 0;
		int lLastTransitionPlane = 0;

		for (int i = 0; i < lNumberOfControlPlanes; i++)
			if (mCurrentAcquisitionState.isTransitionCtrlPlane(pControlPlaneIndex))
				lFirstTransitionPlane = i;

		for (int i = lNumberOfControlPlanes - 1; i >= 0; i--)
			if (mCurrentAcquisitionState.isTransitionCtrlPlane(pControlPlaneIndex))
				lLastTransitionPlane = i;

		double lMiddleTransitionPlane = (lFirstTransitionPlane + lLastTransitionPlane) / 2;

		if (pControlPlaneIndex <= lMiddleTransitionPlane)
			return 0;

		if (pControlPlaneIndex >= lMiddleTransitionPlane)
			return 1;

		return -1;

	}



}
