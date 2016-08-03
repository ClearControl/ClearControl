package clearcontrol.microscope.lightsheet.acquisition;

import static java.lang.Math.floor;
import static java.lang.Math.round;

import java.util.Iterator;

import clearcontrol.core.variable.Variable;
import clearcontrol.hardware.lasers.LaserDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.state.AcquisitionState;
import clearcontrol.microscope.lightsheet.acquisition.state.gui.AcquisitionStateEvolutionVisualizer;
import clearcontrol.microscope.lightsheet.acquisition.state.gui.AcquisitionStateVisualizer;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;

public class StackAcquisition implements StackAcquisitionInterface
{

	private final LightSheetMicroscope mLightSheetMicroscope;

	private final Variable<Number> mZLow = new Variable<Number>("LowZ",
																															25.0);
	private final Variable<Number> mZHigh = new Variable<Number>(	"HighZ",
																																75.0);

	private final Variable<Number> mZStep = new Variable<Number>(	"ZStep",
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
		mZLow.set(pValue);
	}

	@Override
	public double getMinZ()
	{
		return mZLow.get().doubleValue();
	}

	@Override
	public void setHighZ(double pValue)
	{
		mZHigh.set(pValue);
	}

	@Override
	public double getMaxZ()
	{
		return mZHigh.get().doubleValue();
	}

	@Override
	public void setStepZ(double pValue)
	{
		mZStep.set(pValue);
	}

	@Override
	public double getStepZ()
	{
		return mZStep.get().doubleValue();
	}

	@Override
	public double getStackDepthInMicrons()
	{
		return (mZHigh.get().doubleValue() - mZLow.get().doubleValue());
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
		return (int) floor(getStackDepthInMicrons() / mZStep.get().doubleValue());
	}

	@Override
	public Variable<Number> getStackZLowVariable()
	{
		return mZLow;
	}

	@Override
	public Variable<Number> getStackZHighVariable()
	{
		return mZHigh;
	}
	
	@Override
	public Variable<Number> getStackZStepVariable()
	{
		return mZStep;
	}

	@Override
	public Variable<Number> getStackZMinVariable()
	{
		return getLightSheetMicroscope().getDevice(DetectionArm.class, 0)
																		.getZVariable()
																		.getMinVariable();
	}

	@Override
	public Variable<Number> getStackZMaxVariable()
	{
		return getLightSheetMicroscope().getDevice(DetectionArm.class, 0)
																		.getZVariable()
																		.getMaxVariable();
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
																																		.getNumberOfDevices(DetectionArmInterface.class);

		for (int d = 0; d < lNumberOfDetectionPathDevices; d++)
		{
			mLightSheetMicroscope.setDZ(d, getDZ(pPlaneIndex, d));
		}

		final int lNumberOfLightsheetDevices = mLightSheetMicroscope.getDeviceLists()
																																.getNumberOfDevices(LightSheetInterface.class);

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
																														.getNumberOfDevices(LaserDeviceInterface.class);

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
		final double lZ = mZLow.get().doubleValue() + pPlaneIndex * getStepZ();
		return lZ;
	}

	@Override
	public int getPlaneIndexForZRamp(double pZRampValue)
	{
		final int lIndex = (int) round((pZRampValue - mZLow.get().doubleValue()) / getStepZ());
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
