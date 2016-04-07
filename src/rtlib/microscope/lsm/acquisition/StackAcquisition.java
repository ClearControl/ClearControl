package rtlib.microscope.lsm.acquisition;

import static java.lang.Math.floor;
import static java.lang.Math.round;

import java.util.Iterator;

import rtlib.core.variable.Variable;
import rtlib.microscope.lsm.LightSheetMicroscope;
import rtlib.microscope.lsm.acquisition.gui.AcquisitionStateEvolutionVisualizer;
import rtlib.microscope.lsm.acquisition.gui.AcquisitionStateVisualizer;

public class StackAcquisition implements StackAcquisitionInterface
{

	private final LightSheetMicroscope mLightSheetMicroscope;

	private final Variable<Double> mLowZ = new Variable<Double>("LowZ",
																																					25.0);
	private final Variable<Double> mHighZ = new Variable<Double>(	"HighZ",
																																						75.0);

	private final Variable<Double> mZStep = new Variable<Double>(	"ZStep",
																																						0.5);

	private volatile AcquisitionState mCurrentAcquisitionState;

	private AcquisitionStateEvolutionVisualizer mAcquisitionStateEvolutionVisualizer;
	private AcquisitionStateVisualizer mAcquisitionStateVisualizer;

	public StackAcquisition(LightSheetMicroscope pLightSheetMicroscope)
	{
		super();
		mLightSheetMicroscope = pLightSheetMicroscope;

		mCurrentAcquisitionState = new AcquisitionState(mLightSheetMicroscope);
		mAcquisitionStateEvolutionVisualizer = new AcquisitionStateEvolutionVisualizer();
		mAcquisitionStateVisualizer = new AcquisitionStateVisualizer();
	}

	public LightSheetMicroscope getLightSheetMicroscope()
	{
		return mLightSheetMicroscope;
	}

	public void setup(double pMinZ,
										double pMiddleZ,
										double pMaxZ,
										double pStepZ,
										double pControlPlaneStepZ,
										double pMarginZ)
	{
		setLowZ(pMinZ);
		setHighZ(pMaxZ);
		setStepZ(pStepZ);
		getCurrentState().setTransitionPlane(pMiddleZ);

		for (double z = pMinZ + pMarginZ; z <= pMaxZ - pMarginZ; z += pControlPlaneStepZ)
		{
			mCurrentAcquisitionState.addControlPlane(z);
		}

		mAcquisitionStateEvolutionVisualizer.addState(getCurrentState());
		mAcquisitionStateVisualizer.setState(getCurrentState());
	}

	@Override
	public void setCurrentState(AcquisitionState pNewAcquisitionState)
	{
		mCurrentAcquisitionState = pNewAcquisitionState;
		mAcquisitionStateEvolutionVisualizer.addState(getCurrentState());
		mAcquisitionStateVisualizer.setState(getCurrentState());
	}

	@Override
	public AcquisitionState getCurrentState()
	{
		return mCurrentAcquisitionState;
	}

	@Override
	public void setLowZ(double pValue)
	{
		mLowZ.set(pValue);
	}

	@Override
	public double getMinZ()
	{
		return mLowZ.get();
	}

	@Override
	public void setHighZ(double pValue)
	{
		mHighZ.set(pValue);
	}

	@Override
	public double getMaxZ()
	{
		return mHighZ.get();
	}

	@Override
	public void setStepZ(double pValue)
	{
		mZStep.set(pValue);
	}

	@Override
	public double getStepZ()
	{
		return mZStep.get();
	}

	@Override
	public double getStackDepthInMicrons()
	{
		return (mHighZ.get() - mLowZ.get());
	}

	@Override
	public void setStackDepth(int pNumberOfPlanes)
	{
		double lStepZ = (getStackDepthInMicrons() / (pNumberOfPlanes));

		setHighZ(getMinZ() + pNumberOfPlanes * lStepZ);
		setStepZ(lStepZ);
	}

	@Override
	public int getStackDepth()
	{
		return (int) floor(getStackDepthInMicrons() / mZStep.get());
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

	@Override
	public void setToControlPlane(int pControlPlaneIndex)
	{
		double lControlPlaneZ = getControlPlaneZ(pControlPlaneIndex);
		int lStackPlaneIndex = getPlaneIndexForZRamp(lControlPlaneZ);
		setToStackPlane(lStackPlaneIndex);
	}

	@Override
	public void addStackMargin(int pNumberOfMarginPlanesToAdd)
	{
		addStackMargin(0, pNumberOfMarginPlanesToAdd);
	}

	@Override
	public void addStackMargin(	int pStackPlaneIndex,
															int pNumberOfMarginPlanesToAdd)
	{
		this.setToStackPlane(pStackPlaneIndex);
		mLightSheetMicroscope.setC(false);
		mLightSheetMicroscope.setILO(false);
		for (int i = 0; i < pNumberOfMarginPlanesToAdd; i++)
			mLightSheetMicroscope.addCurrentStateToQueue();
	}

	@Override
	public double getControlPlaneZ(int pControlPlaneIndex)
	{
		return mCurrentAcquisitionState.getZ(pControlPlaneIndex);
	}

	@Override
	public double getZRamp(int pPlaneIndex)
	{
		final double lZ = mLowZ.get() + pPlaneIndex * getStepZ();
		return lZ;
	}

	@Override
	public int getPlaneIndexForZRamp(double pZRampValue)
	{
		final int lIndex = (int) round((pZRampValue - mLowZ.get()) / getStepZ());
		return lIndex;
	}

	@Override
	public double getDZ(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedDZ(	pDeviceIndex,
																																									lRamp);
		return lRamp + lInterpolatedValue;
	}

	@Override
	public double getIX(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIX(	pDeviceIndex,
																																									lRamp);
		return lInterpolatedValue;
	}

	@Override
	public double getIY(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIY(	pDeviceIndex,
																																									lRamp);
		return lInterpolatedValue;
	}

	@Override
	public double getIZ(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIZ(	pDeviceIndex,
																																									lRamp);
		return lRamp + lInterpolatedValue;
	}

	@Override
	public double getIA(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIA(	pDeviceIndex,
																																									lRamp);

		return lInterpolatedValue;
	}

	@Override
	public double getIB(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIB(	pDeviceIndex,
																																									lRamp);

		return lInterpolatedValue;
	}

	@Override
	public double getIW(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIW(	pDeviceIndex,
																																									lRamp);

		return lInterpolatedValue;
	}

	@Override
	public double getIH(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIH(	pDeviceIndex,
																																									lRamp);

		return lInterpolatedValue;
	}

	@Override
	public double getIP(int pPlaneIndex, int pDeviceIndex)
	{
		final double lRamp = getZRamp(pPlaneIndex);
		final double lInterpolatedValue = mCurrentAcquisitionState.getInterpolatedIP(	pDeviceIndex,
																																									lRamp);

		return lInterpolatedValue;
	}

	@Override
	public int getBestDetectionArm(int pControlPlaneIndex)
	{

		double lTransitionPlane = mCurrentAcquisitionState.getTransitionPlane();

		if (getZRamp(pControlPlaneIndex) <= lTransitionPlane)
			return 0;
		else if (getZRamp(pControlPlaneIndex) >= lTransitionPlane)
			return 1;

		return -1;

	}

}
