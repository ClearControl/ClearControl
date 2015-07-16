package rtlib.microscope.lightsheet.illumination;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.analysis.UnivariateFunction;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.math.regression.linear.UnivariateAffineFunction;
import rtlib.core.variable.VariableSetListener;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.microscope.lightsheet.illumination.si.BinaryStructuredIlluminationPattern;
import rtlib.microscope.lightsheet.illumination.si.StructuredIlluminatioPatternInterface;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.staves.ConstantStave;
import rtlib.symphony.staves.RampSteppingStave;
import rtlib.symphony.staves.StaveInterface;

public class LightSheet extends NamedVirtualDevice implements
																									LightSheetInterface,
																									AsynchronousExecutorServiceAccess
{

	private static final double cMicronsToNormGalvoUnits = -0.003026;

	private final ObjectVariable<UnivariateFunction> mLightSheetXFunction = new ObjectVariable<UnivariateFunction>(	"LightSheetXFunction",
																																																									new UnivariateAffineFunction());
	private final ObjectVariable<UnivariateFunction> mLightSheetYFunction = new ObjectVariable<UnivariateFunction>(	"LightSheetYFunction",
																																																									new UnivariateAffineFunction(	cMicronsToNormGalvoUnits,
																																																																								0));
	private final ObjectVariable<UnivariateFunction> mLightSheetZFunction = new ObjectVariable<UnivariateFunction>(	"LightSheetZFunction",
																																																									new UnivariateAffineFunction(	cMicronsToNormGalvoUnits,
																																																																								0));
	private final ObjectVariable<UnivariateFunction> mLightSheetBetaFunction = new ObjectVariable<UnivariateFunction>("LightSheetBetaFunction",
																																																										new UnivariateAffineFunction(	1,
																																																																									0));
	private final ObjectVariable<UnivariateFunction> mLightSheetIrisDiameterFunction = new ObjectVariable<UnivariateFunction>("LightSheetIrisDiameterFunction",
																																																														new UnivariateAffineFunction(	1,
																																																																													0));

	private final DoubleVariable mEffectiveExposureInMicroseconds = new DoubleVariable(	"EffectiveExposureInMicroseconds",
																																											5000);
	private final DoubleVariable mImageHeight = new DoubleVariable(	"ImageHeight",
																																	2 * 1024);
	private final DoubleVariable mReadoutTimeInMicrosecondsPerLine = new DoubleVariable("ReadoutTimeInMicrosecondsPerLine",
																																											9.74);
	private final DoubleVariable mMarginTimeInMicroseconds = new DoubleVariable("MarginTimeInMicroseconds",
																																							100);
	private final DoubleVariable mFocalLengthInMicronsVariable = new DoubleVariable("FocalLengthInMicrons",
																																									20000);
	private final DoubleVariable mLambdaInMicronsVariable = new DoubleVariable(	"LambdaInMicrons",
																																							594);

	private final DoubleVariable mLightSheetXInMicrons = new DoubleVariable("LightSheetXInMicrons",
																																					0);
	private final DoubleVariable mLightSheetYInMicrons = new DoubleVariable("LightSheetYInMicrons",
																																					0);
	private final DoubleVariable mLightSheetZInMicrons = new DoubleVariable("LightSheetZInMicrons",
																																					0);
	private final DoubleVariable mLightSheetAlphaInDegrees = new DoubleVariable("LightSheetAlphaInDegrees",
																																							0);
	private final DoubleVariable mLightSheetBetaInDegrees = new DoubleVariable(	"LightSheetBetaInDegrees",
																																							0);
	private final DoubleVariable mLightSheetRangeInMicrons = new DoubleVariable("LightSheetRangeInMicrons",
																																							0);
	private final DoubleVariable mLightSheetLengthInMicrons = new DoubleVariable(	"LightSheetLengthInMicrons",
																																								100);

	private final DoubleVariable mLineExposureInMicroseconds = new DoubleVariable("LineExposureInMicroseconds",
																																								10);

	private final BooleanVariable[] mLaserOnOffArray;

	private final BooleanVariable mSIPatternOnOff = new BooleanVariable("SIPatternOnOff",
																																			false);

	private final DoubleVariable mNumberOfPhasesPerPlane = new DoubleVariable("NumberOfPhases",
																																						1);

	private final ObjectVariable<StructuredIlluminatioPatternInterface>[] mStructuredIlluminationPatternVariableArray;

	private Movement mBeforeExposureMovement, mExposureMovement;

	private final RampSteppingStave mLightSheetStaveBeforeExposureZ,
			mLightSheetStaveBeforeExposureY, mLightSheetStaveExposureZ,
			mLightSheetStaveExposureY;
	private final ConstantStave mLightSheetStaveBeforeExposureX,
			mLightSheetStaveExposureX, mLightSheetStaveBeforeExposureB,
			mLightSheetStaveExposureB, mLightSheetStaveBeforeExposureR,
			mLightSheetStaveExposureR, mLightSheetStaveBeforeExposureT,
			mLightSheetStaveExposureT, mLightSheetStaveBeforeExposureLA,
			mLightSheetStaveExposureLA, mNonSIIluminationLaserTrigger;

	private final int mNumberOfLaserDigitalControls;

	@SuppressWarnings("unchecked")
	public LightSheet(String pName,
										final double pReadoutTimeInMicrosecondsPerLine,
										final int pNumberOfLines,
										final int pNumberOfLaserDigitalControls)
	{
		super(pName);

		mNumberOfLaserDigitalControls = pNumberOfLaserDigitalControls;

		final VariableSetListener<Double> lDoubleVariableListener = (u, v) -> {
			update();
		};

		mLaserOnOffArray = new BooleanVariable[mNumberOfLaserDigitalControls];

		mStructuredIlluminationPatternVariableArray = new ObjectVariable[mNumberOfLaserDigitalControls];

		mReadoutTimeInMicrosecondsPerLine.setValue(pReadoutTimeInMicrosecondsPerLine);
		mImageHeight.setValue(pNumberOfLines);

		mLightSheetStaveBeforeExposureLA = new ConstantStave(	"laser.beforeexp.am",
																													0);
		mLightSheetStaveExposureLA = new ConstantStave(	"laser.exposure.am",
																										0);

		mLightSheetStaveBeforeExposureZ = new RampSteppingStave("lightsheet.z.be");
		mLightSheetStaveBeforeExposureY = new RampSteppingStave("lightsheet.y.be");
		mLightSheetStaveBeforeExposureX = new ConstantStave("lightsheet.x.be",
																												0);
		mLightSheetStaveBeforeExposureB = new ConstantStave("lightsheet.b.be",
																												0);
		mLightSheetStaveBeforeExposureR = new ConstantStave("lightsheet.r.be",
																												0);
		mLightSheetStaveBeforeExposureT = new ConstantStave("trigger.out.be",
																												1);

		mLightSheetStaveExposureZ = new RampSteppingStave("lightsheet.z.e");
		mLightSheetStaveExposureY = new RampSteppingStave("lightsheet.y.e");
		mLightSheetStaveExposureX = new ConstantStave("lightsheet.x.e", 0);
		mLightSheetStaveExposureB = new ConstantStave("lightsheet.b.e", 0);
		mLightSheetStaveExposureR = new ConstantStave("lightsheet.r.e", 0);
		mLightSheetStaveExposureT = new ConstantStave("trigger.out.e", 0);

		mNonSIIluminationLaserTrigger = new ConstantStave("trigger.out.e",
																											1);

		for (int i = 0; i < mLaserOnOffArray.length; i++)
		{
			final String lLaserName = "Laser" + i + ".exposure.trig";

			mStructuredIlluminationPatternVariableArray[i] = new ObjectVariable("StructuredIlluminationPattern",
																																					new BinaryStructuredIlluminationPattern());

			mLaserOnOffArray[i] = new BooleanVariable(lLaserName, false);
			mLaserOnOffArray[i].addSetListener(lDoubleVariableListener);
		}

		mReadoutTimeInMicrosecondsPerLine.addSetListener(lDoubleVariableListener);
		mMarginTimeInMicroseconds.addSetListener(lDoubleVariableListener);
		mEffectiveExposureInMicroseconds.addSetListener(lDoubleVariableListener);
		mImageHeight.addSetListener(lDoubleVariableListener);

		mLightSheetXInMicrons.addSetListener(lDoubleVariableListener);
		mLightSheetYInMicrons.addSetListener(lDoubleVariableListener);
		mLightSheetZInMicrons.addSetListener(lDoubleVariableListener);
		mLightSheetBetaInDegrees.addSetListener(lDoubleVariableListener);
		mLightSheetAlphaInDegrees.addSetListener(lDoubleVariableListener);
		mLightSheetLengthInMicrons.addSetListener(lDoubleVariableListener);

		for (int i = 0; i < mLaserOnOffArray.length; i++)
		{
			mStructuredIlluminationPatternVariableArray[i].addSetListener((	u,
																																			v) -> {
				update();
			});

		}

	}

	public void setBeforeExposureMovement(Movement pBeforeExposureMovement)
	{
		mBeforeExposureMovement = pBeforeExposureMovement;
		ensureStavesAddedToBeforeExposureMovement(mBeforeExposureMovement);
	}

	public void setExposureMovement(Movement pExposureMovement)
	{
		mExposureMovement = pExposureMovement;
		ensureStavesAddedToExposureMovement(mExposureMovement);
	}

	private void ensureStavesAddedToBeforeExposureMovement(Movement pBeforeExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs before exposure:
		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.x",
																																											2),
																			mLightSheetStaveBeforeExposureX);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.y",
																																											3),
																			mLightSheetStaveBeforeExposureY);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.y",
																																											4),
																			mLightSheetStaveBeforeExposureZ);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.b",
																																											5),
																			mLightSheetStaveBeforeExposureB);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.r",
																																											6),
																			mLightSheetStaveBeforeExposureR);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.la",
																																											7),
																			mLightSheetStaveBeforeExposureLA);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.t",
																																											8 + 7),
																			mLightSheetStaveBeforeExposureT);

	}

	private void ensureStavesAddedToExposureMovement(Movement pExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs at exposure:

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.x",
																																								2),
																mLightSheetStaveExposureX);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.y",
																																								3),
																mLightSheetStaveExposureY);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.y",
																																								4),
																mLightSheetStaveExposureZ);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.b",
																																								5),
																mLightSheetStaveExposureB);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.r",
																																								6),
																mLightSheetStaveExposureR);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.la",
																																								7),
																mLightSheetStaveExposureLA);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.t",
																																								8 + 7),
																mLightSheetStaveExposureT);

		for (int i = 0; i < mLaserOnOffArray.length; i++)
			setLaserDigitalTriggerStave(pExposureMovement,
																	i,
																	mNonSIIluminationLaserTrigger);

	}

	private void setLaserDigitalTriggerStave(	Movement pExposureMovement,
																						int i,
																						StaveInterface pStave)
	{
		final int lLaserDigitalLineIndex = MachineConfiguration.getCurrentMachineConfiguration()
																														.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.ld"
																																										+ i,
																																								8 + i);
		mExposureMovement.setStave(lLaserDigitalLineIndex, pStave);
	}

	public void update()
	{
		synchronized (this)
		{

			mNumberOfPhasesPerPlane.setValue(getNumberOfPhases());

			final double lReadoutTimeInMicroseconds = getBeforeExposureMovementDuration(TimeUnit.MICROSECONDS);
			final double lExposureMovementTimeInMicroseconds = getExposureMovementDuration(TimeUnit.MICROSECONDS);

			final double lLineExposureTimeInMicroseconds = lReadoutTimeInMicroseconds + lExposureMovementTimeInMicroseconds;
			mLineExposureInMicroseconds.setValue(lLineExposureTimeInMicroseconds);

			final double lMarginTimeInMicroseconds = mMarginTimeInMicroseconds.getValue();

			final double lLightSheetlength = mLightSheetLengthInMicrons.getValue();

			final double lLightSheetZInMicrons = mLightSheetZInMicrons.getValue();

			final double lGalvoYOffsetBeforeRotation = mLightSheetYInMicrons.getValue();
			final double lGalvoYOffsetToY = galvoRotateY(lGalvoYOffsetBeforeRotation);
			final double lGalvoYOffsetToZ = galvoRotateZ(lGalvoYOffsetBeforeRotation);

			final double lGalvoZOffsetBeforeRotation = lLightSheetZInMicrons;
			final double lGalvoZOffsetToY = galvoRotateY(lGalvoZOffsetBeforeRotation);
			final double lGalvoZOffsetToZ = galvoRotateZ(lGalvoZOffsetBeforeRotation);

			final double lGalvoYOffset = lGalvoYOffsetToY + lGalvoZOffsetToY;
			final double lGalvoZOffset = lGalvoYOffsetToZ + lGalvoZOffsetToZ;

			final double lGalvoAmplitudeY = galvoRotateY(lLightSheetlength);
			final double lGalvoAmplitudeZ = galvoRotateZ(lLightSheetlength);

			final double lGalvoYLowValue = getLightSheetYConversion().get()
																																.value(lGalvoYOffset - lGalvoAmplitudeY);
			final double lGalvoYHighValue = getLightSheetYConversion().get()
																																.value(lGalvoYOffset + lGalvoAmplitudeY);

			final double lGalvoZLowValue = getLightSheetZConversion().get()
																																.value(lGalvoZOffset - lGalvoAmplitudeZ);
			final double lGalvoZHighValue = getLightSheetZConversion().get()
																																.value(lGalvoZOffset + lGalvoAmplitudeZ);

			mLightSheetStaveBeforeExposureY.setSyncStart(0);
			mLightSheetStaveBeforeExposureY.setSyncStop(1);
			mLightSheetStaveBeforeExposureY.setStartValue((float) lGalvoYHighValue);
			mLightSheetStaveBeforeExposureY.setStopValue((float) lGalvoYLowValue);

			mLightSheetStaveBeforeExposureZ.setSyncStart(0);
			mLightSheetStaveBeforeExposureZ.setSyncStop(1);
			mLightSheetStaveBeforeExposureZ.setStartValue((float) lGalvoZHighValue);
			mLightSheetStaveBeforeExposureZ.setStopValue((float) lGalvoZLowValue);

			mLightSheetStaveExposureY.setSyncStart(0);
			mLightSheetStaveExposureY.setSyncStop(1);
			mLightSheetStaveExposureY.setStartValue((float) lGalvoYLowValue);
			mLightSheetStaveExposureY.setStopValue((float) lGalvoYHighValue);
			mLightSheetStaveExposureY.setOutsideValue((float) lGalvoYHighValue);
			mLightSheetStaveExposureY.setNoJump(true);

			mLightSheetStaveExposureZ.setSyncStart(0);
			mLightSheetStaveExposureZ.setSyncStop(1);
			mLightSheetStaveExposureZ.setStartValue((float) lGalvoZLowValue);
			mLightSheetStaveExposureZ.setStopValue((float) lGalvoZHighValue);
			mLightSheetStaveExposureZ.setOutsideValue((float) lGalvoZHighValue);
			mLightSheetStaveExposureZ.setNoJump(true);

			mLightSheetStaveBeforeExposureX.setValue((float) getLightSheetXFunction().get()
																																								.value(mLightSheetXInMicrons.getValue()));
			mLightSheetStaveExposureX.setValue((float) getLightSheetXFunction().get()
																																					.value(mLightSheetXInMicrons.getValue()));

			mLightSheetStaveBeforeExposureB.setValue((float) getLightSheetBetaConversion().get()
																																										.value(mLightSheetBetaInDegrees.getValue()));
			mLightSheetStaveExposureB.setValue((float) getLightSheetBetaConversion().get()
																																							.value(mLightSheetBetaInDegrees.getValue()));

			final double lFocalLength = mFocalLengthInMicronsVariable.get();
			final double lLambdaInMicrons = mLambdaInMicronsVariable.get();
			final double lLightSheetRangeInMicrons = mLightSheetRangeInMicrons.getValue();

			final double lIrisDiameterInMm = GaussianBeamGeometry.getBeamIrisDiameter(lFocalLength,
																																								lLambdaInMicrons,
																																								lLightSheetRangeInMicrons);

			mLightSheetStaveBeforeExposureR.setValue((float) getLightSheetIrisDiameterConversion().get()
																																														.value(lIrisDiameterInMm));
			mLightSheetStaveExposureR.setValue(mLightSheetStaveBeforeExposureR.getConstantValue());

			final double lMarginTimeRelativeUnits = microsecondsToRelative(	lExposureMovementTimeInMicroseconds,
																																			lMarginTimeInMicroseconds);

			mLightSheetStaveBeforeExposureY.setStepping(mSIPatternOnOff.getBooleanValue());
			mLightSheetStaveExposureY.setStepping(mSIPatternOnOff.getBooleanValue());

			mLightSheetStaveBeforeExposureZ.setStepping(mSIPatternOnOff.getBooleanValue());
			mLightSheetStaveExposureZ.setStepping(mSIPatternOnOff.getBooleanValue());

			for (int i = 0; i < mLaserOnOffArray.length; i++)
			{
				final BooleanVariable lLaserBooleanVariable = mLaserOnOffArray[i];
				final StructuredIlluminatioPatternInterface lStructuredIlluminatioPatternInterface = mStructuredIlluminationPatternVariableArray[i].get();
				final StaveInterface lLaserTriggerStave = lStructuredIlluminatioPatternInterface.getStave(lMarginTimeRelativeUnits);
				lLaserTriggerStave.setEnabled(lLaserBooleanVariable.getBooleanValue());

				if (mSIPatternOnOff.getBooleanValue())
					setLaserDigitalTriggerStave(mExposureMovement,
																			i,
																			lLaserTriggerStave);

				else
					setLaserDigitalTriggerStave(mExposureMovement,
																			i,
																			mNonSIIluminationLaserTrigger);

			}

			mLightSheetStaveExposureLA.setValue(1);
			mLightSheetStaveBeforeExposureLA.setValue(1);

		}
	}

	public long getExposureMovementDuration(TimeUnit pTimeUnit)
	{
		return pTimeUnit.convert(	(long) mEffectiveExposureInMicroseconds.getValue(),
															TimeUnit.MICROSECONDS);
	}

	public long getBeforeExposureMovementDuration(TimeUnit pTimeUnit)
	{
		return pTimeUnit.convert(	(long) (mReadoutTimeInMicrosecondsPerLine.getValue() * mImageHeight.getValue() / 2),
															TimeUnit.MICROSECONDS);
	}

	private double galvoRotateY(double pY)
	{
		final double lAlpha = Math.toRadians(mLightSheetAlphaInDegrees.getValue());
		return pY * cos(lAlpha);
	}

	private double galvoRotateZ(double pZ)
	{
		final double lAlpha = Math.toRadians(mLightSheetAlphaInDegrees.getValue());
		return -pZ * sin(lAlpha);
	}

	@Override
	public DoubleVariable getImageHeightVariable()
	{
		return mImageHeight;
	}

	@Override
	public int getNumberOfPhases()
	{
		return mStructuredIlluminationPatternVariableArray[0].get()
																													.getNumberOfPhases();
	}

	public void setEffectiveExposureInMicroseconds(final int pEffectiveExposureInMicroseconds)
	{
		mEffectiveExposureInMicroseconds.setValue(pEffectiveExposureInMicroseconds);
	}

	@Override
	public DoubleVariable getEffectiveExposureInMicrosecondsVariable()
	{
		return mEffectiveExposureInMicroseconds;
	}

	@Override
	public DoubleVariable getLineExposureInMicrosecondsVariable()
	{
		return mLineExposureInMicroseconds;
	}

	@Override
	public DoubleVariable getMarginTimeInMicrosecondsVariable()
	{
		return mMarginTimeInMicroseconds;
	}

	@Override
	public DoubleVariable getReadoutTimeInMicrosecondsPerLineVariable()
	{
		return mReadoutTimeInMicrosecondsPerLine;
	}

	@Override
	public DoubleVariable getLightSheetYInMicronsVariable()
	{
		return mLightSheetYInMicrons;
	}

	@Override
	public DoubleVariable getLightSheetZInMicronsVariable()
	{
		return mLightSheetZInMicrons;
	}

	@Override
	public DoubleVariable getLightSheetAlphaInDegreesVariable()
	{
		return mLightSheetAlphaInDegrees;
	}

	@Override
	public DoubleVariable getLightSheetBetaInDegreesVariable()
	{
		return mLightSheetBetaInDegrees;
	}

	@Override
	public DoubleVariable getLightSheetRangeInMicronsVariable()
	{
		return mLightSheetRangeInMicrons;
	}

	@Override
	public DoubleVariable getLightSheetLengthInMicronsVariable()
	{
		return mLightSheetLengthInMicrons;
	}

	@Override
	public BooleanVariable getSIPatternOnOffVariable()
	{
		return mSIPatternOnOff;
	}

	@Override
	public DoubleVariable getLaserOnOffArrayVariable(int pLaserIndex)
	{
		return mLaserOnOffArray[pLaserIndex];
	}

	public ObjectVariable<UnivariateFunction> getLightSheetXFunction()
	{
		return mLightSheetXFunction;
	}

	public ObjectVariable<UnivariateFunction> getLightSheetYConversion()
	{
		return mLightSheetYFunction;
	}

	public ObjectVariable<UnivariateFunction> getLightSheetZConversion()
	{
		return mLightSheetZFunction;
	}

	public ObjectVariable<UnivariateFunction> getLightSheetBetaConversion()
	{
		return mLightSheetBetaFunction;
	}

	public ObjectVariable<UnivariateFunction> getLightSheetIrisDiameterConversion()
	{
		return mLightSheetIrisDiameterFunction;
	}

	public RampSteppingStave getGalvoScannerStaveBeforeExposureZ()
	{
		return mLightSheetStaveBeforeExposureZ;
	}

	public RampSteppingStave getGalvoScannerStaveBeforeExposureY()
	{
		return mLightSheetStaveBeforeExposureY;
	}

	public ConstantStave getIllumPifocStaveBeforeExposureX()
	{
		return mLightSheetStaveBeforeExposureX;
	}

	public RampSteppingStave getGalvoScannerStaveExposureZ()
	{
		return mLightSheetStaveExposureZ;
	}

	public RampSteppingStave getGalvoScannerStaveExposureY()
	{
		return mLightSheetStaveExposureY;
	}

	public ConstantStave getIllumPifocStaveExposureX()
	{
		return mLightSheetStaveExposureX;
	}

	public ConstantStave getTriggerOutStaveBeforeExposure()
	{
		return mLightSheetStaveBeforeExposureT;
	}

	public ConstantStave getTriggerOutStaveExposure()
	{
		return mLightSheetStaveExposureT;
	}

	public ConstantStave getLaserAnalogModulationBeforeExposure()
	{
		return mLightSheetStaveBeforeExposureLA;
	}

	public ConstantStave getLaserAnalogModulationExposure()
	{
		return mLightSheetStaveExposureLA;
	}



	private static double microsecondsToRelative(	final double pTotalTime,
																								final double pSubTime)
	{
		return pSubTime / pTotalTime;
	}

}
