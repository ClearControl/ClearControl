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
import rtlib.symphony.movement.Movement;
import rtlib.symphony.staves.BinaryPatternSteppingStave;
import rtlib.symphony.staves.ConstantStave;
import rtlib.symphony.staves.RampSteppingStave;

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
	private final BooleanVariable mPatternOnOff = new BooleanVariable("PatternOnOff",
																																		false);
	private final DoubleVariable mPatternPeriod = new DoubleVariable(	"PatternPeriod",
																																		2);
	private final DoubleVariable mPatternPhaseIndex = new DoubleVariable(	"PatternPhaseIndex",
																																				0);
	private final DoubleVariable mPatternOnLength = new DoubleVariable(	"PatternOnLength",
																																			1);
	private final DoubleVariable mPatternPhaseIncrement = new DoubleVariable(	"PatternPhaseIncrement",
																																						1);

	private final DoubleVariable mNumberOfPhasesPerPlane = new DoubleVariable("NumberOfPhases",
																																						2);

	private final RampSteppingStave mLightSheetStaveBeforeExposureZ,
			mLightSheetStaveBeforeExposureY, mLightSheetStaveExposureZ,
			mLightSheetStaveExposureY;
	private final ConstantStave mLightSheetStaveBeforeExposureX,
			mLightSheetStaveExposureX, mLightSheetStaveBeforeExposureB,
			mLightSheetStaveExposureB, mLightSheetStaveBeforeExposureR,
			mLightSheetStaveExposureR;
	private final ConstantStave mLightSheetStaveBeforeExposureT,
			mLightSheetStaveExposureT;
	private final ConstantStave mLightSheetStaveBeforeExposureLA,
			mLightSheetStaveExposureLA;
	private final BinaryPatternSteppingStave[] mLightSheetStaveLaserLD;

	private final int mNumberOfLaserDigitalControls;

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

		final VariableSetListener<Object> lObjectVariableListener = (u, v) -> {
			update();
		};


		mLaserOnOffArray = new BooleanVariable[mNumberOfLaserDigitalControls];

		mLightSheetStaveLaserLD = new BinaryPatternSteppingStave[mNumberOfLaserDigitalControls];

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

		for (int i = 0; i < mLaserOnOffArray.length; i++)
		{
			final String lLaserName = "Laser" + i + ".exposure.trig";
			mLightSheetStaveLaserLD[i] = new BinaryPatternSteppingStave(lLaserName);

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

		mPatternOnOff.addSetListener(lDoubleVariableListener);
		mPatternPeriod.addSetListener(lDoubleVariableListener);
		mPatternPhaseIndex.addSetListener(lDoubleVariableListener);
		mPatternOnLength.addSetListener(lDoubleVariableListener);

	}

	public void addStavesToBeforeExposureMovement(Movement pBeforeExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs before exposure:
		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.x",
																																											1),
																			mLightSheetStaveBeforeExposureX);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.y",
																																											2),
																			mLightSheetStaveBeforeExposureY);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.y",
																																											3),
																			mLightSheetStaveBeforeExposureZ);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.b",
																																											4),
																			mLightSheetStaveBeforeExposureB);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.r",
																																											5),
																			mLightSheetStaveBeforeExposureR);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.la",
																																											6),
																			mLightSheetStaveBeforeExposureLA);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																													+ ".index.t",
																																											8 + 7),
																			mLightSheetStaveBeforeExposureT);

	}

	public void addStavesToExposureMovement(Movement pExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs at exposure:

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.x",
																																								1),
																mLightSheetStaveExposureX);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.y",
																																								2),
																mLightSheetStaveExposureY);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.y",
																																								3),
																mLightSheetStaveExposureZ);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.b",
																																								4),
																mLightSheetStaveExposureB);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.r",
																																								5),
																mLightSheetStaveExposureR);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.la",
																																								6),
																mLightSheetStaveExposureLA);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.t",
																																								8 + 7),
																mLightSheetStaveExposureT);

		for (int i = 0; i < mLaserOnOffArray.length; i++)
		{
			final int lLaserDigitalLineIndex = lCurrentMachineConfiguration.getIntegerProperty(	"device.lsm.lightsheet." + getName().toLowerCase()
																																															+ ".index.ld"
																																															+ i,
																																													8 + i);
			pExposureMovement.setStave(	lLaserDigitalLineIndex,
																	mLightSheetStaveLaserLD[i]);
		}

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

			mLightSheetStaveBeforeExposureY.setStepping(mPatternOnOff.getBooleanValue());
			mLightSheetStaveExposureY.setStepping(mPatternOnOff.getBooleanValue());

			mLightSheetStaveBeforeExposureZ.setStepping(mPatternOnOff.getBooleanValue());
			mLightSheetStaveExposureZ.setStepping(mPatternOnOff.getBooleanValue());

			for (int i = 0; i < mLaserOnOffArray.length; i++)
			{
				final BinaryPatternSteppingStave lLaserTriggerStave = mLightSheetStaveLaserLD[i];
				final BooleanVariable lLaserBooleanVariable = mLaserOnOffArray[i];

				lLaserTriggerStave.setEnabled(lLaserBooleanVariable.getBooleanValue());
				lLaserTriggerStave.setSyncStart((float) clamp01(lMarginTimeRelativeUnits));
				lLaserTriggerStave.setSyncStop((float) clamp01(1 - lMarginTimeRelativeUnits));
				lLaserTriggerStave.setEnabled(mPatternOnOff.getBooleanValue());
				lLaserTriggerStave.setPatternPeriod((int) mPatternPeriod.getValue());
				lLaserTriggerStave.setPatternPhaseIndex((int) mPatternPhaseIndex.getValue());
				lLaserTriggerStave.setPatternOnLength((int) mPatternOnLength.getValue());
				lLaserTriggerStave.setPatternPhaseIncrement((int) mPatternPhaseIncrement.getValue());
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
	public void setPatterned(final boolean pIsPatternOn)
	{
		mPatternOnOff.setValue(pIsPatternOn);
	}

	@Override
	public boolean isPatterned()
	{
		return mPatternOnOff.getBooleanValue();
	}

	@Override
	public DoubleVariable getImageHeightVariable()
	{
		return mImageHeight;
	}

	@Override
	public int getNumberOfPhases()
	{
		final boolean lIsPatterned = isPatterned();
		final double lPatternPeriod = lIsPatterned ? mPatternPeriod.getValue()
																							: 1;
		final double lPatternPhaseIncrement = lIsPatterned ? mPatternPhaseIncrement.getValue()
																											: 1;

		final int lNumberOfPhases = (int) (lPatternPeriod / lPatternPhaseIncrement);

		return lNumberOfPhases;
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
	public DoubleVariable getLightSheetLengthInMicronsVariable()
	{
		return mLightSheetLengthInMicrons;
	}

	@Override
	public DoubleVariable getPatternOnOffVariable()
	{
		return mPatternOnOff;
	}

	@Override
	public DoubleVariable getPatternPeriodVariable()
	{
		return mPatternPeriod;
	}

	@Override
	public DoubleVariable getPatternPhaseIndexVariable()
	{
		return mPatternPhaseIndex;
	}

	@Override
	public DoubleVariable getPatternOnLengthVariable()
	{
		return mPatternOnOff;
	}

	@Override
	public DoubleVariable getPatternPhaseIncrementVariable()
	{
		return mPatternPhaseIncrement;
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

	private static final double clamp01(final double x)
	{
		return Math.max(0, Math.min(1, x));
	}

	private static double microsecondsToRelative(	final double pTotalTime,
																								final double pSubTime)
	{
		return pSubTime / pTotalTime;
	}

}
