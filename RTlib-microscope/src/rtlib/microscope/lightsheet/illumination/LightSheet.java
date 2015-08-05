package rtlib.microscope.lightsheet.illumination;

import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.math.functions.UnivariateAffineFunction;
import rtlib.core.variable.VariableSetListener;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.microscope.lightsheet.illumination.si.BinaryStructuredIlluminationPattern;
import rtlib.microscope.lightsheet.illumination.si.StructuredIlluminationPatternInterface;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.staves.ConstantStave;
import rtlib.symphony.staves.EdgeStave;
import rtlib.symphony.staves.RampSteppingStave;
import rtlib.symphony.staves.StaveInterface;

public class LightSheet extends NamedVirtualDevice implements
																									LightSheetInterface,
																									AsynchronousExecutorServiceAccess
{


	private final ObjectVariable<UnivariateAffineComposableFunction> mLightSheetXFunction = new ObjectVariable<>(	"LightSheetXFunction",
																																																									new UnivariateAffineFunction(	1,
																																																																								0));
	private final ObjectVariable<UnivariateAffineComposableFunction> mLightSheetYFunction = new ObjectVariable<>("LightSheetYFunction",
																																																									new UnivariateAffineFunction(	1,
																																																																								0));
	private final ObjectVariable<UnivariateAffineComposableFunction> mLightSheetZFunction = new ObjectVariable<>("LightSheetZFunction",
																																																									new UnivariateAffineFunction(	1,
																																																																								0));
	private final ObjectVariable<UnivariateAffineComposableFunction> mLightSheetBetaFunction = new ObjectVariable<>(	"LightSheetBetaFunction",
																																																										new UnivariateAffineFunction(	1,
																																																																									0));
	private final ObjectVariable<UnivariateAffineComposableFunction> mLightSheetIrisDiameterFunction = new ObjectVariable<>(	"LightSheetIrisDiameterFunction",
																																																														new UnivariateAffineFunction(	1,
																																																																													0));

	private final ObjectVariable<UnivariateAffineComposableFunction> mLightSheetPowerFunction = new ObjectVariable<>("LightSheetIrisDiameterFunction",
																																																											new UnivariateAffineFunction(	0.01,
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
	private final DoubleVariable mLightSheetPowerInmW = new DoubleVariable(	"LightSheetLengthPowerInmW ",
																																					100);

	private final DoubleVariable mLineExposureInMicroseconds = new DoubleVariable("LineExposureInMicroseconds",
																																								10);

	private final BooleanVariable[] mLaserOnOffArray;

	private final BooleanVariable[] mSIPatternOnOff;

	private final ObjectVariable<StructuredIlluminationPatternInterface>[] mStructuredIlluminationPatternVariableArray;

	private Movement mBeforeExposureMovement, mExposureMovement;

	private RampSteppingStave mLightSheetStaveBeforeExposureZ,
			mLightSheetStaveBeforeExposureX, mLightSheetStaveExposureX,
			mLightSheetStaveExposureZ;
	private ConstantStave mLightSheetStaveBeforeExposureY,
			mLightSheetStaveExposureY, mLightSheetStaveBeforeExposureB,
			mLightSheetStaveExposureB, mLightSheetStaveBeforeExposureR,
			mLightSheetStaveExposureR,
			mLightSheetStaveBeforeExposureLA, mLightSheetStaveExposureLA,
			mNonSIIluminationLaserTrigger;

	private EdgeStave mLightSheetStaveBeforeExposureT,
			mLightSheetStaveExposureT;

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

		mSIPatternOnOff = new BooleanVariable[mNumberOfLaserDigitalControls];

		mStructuredIlluminationPatternVariableArray = new ObjectVariable[mNumberOfLaserDigitalControls];

		mReadoutTimeInMicrosecondsPerLine.setValue(pReadoutTimeInMicrosecondsPerLine);
		mImageHeight.setValue(pNumberOfLines);

		mLightSheetStaveBeforeExposureLA = new ConstantStave(	"laser.beforeexp.am",
																													0);
		mLightSheetStaveExposureLA = new ConstantStave(	"laser.exposure.am",
																										0);

		mLightSheetStaveBeforeExposureX = new RampSteppingStave("lightsheet.x.be");
		mLightSheetStaveBeforeExposureY = new ConstantStave("lightsheet.y.be",
																												0);
		mLightSheetStaveBeforeExposureZ = new RampSteppingStave("lightsheet.z.be");
		mLightSheetStaveBeforeExposureB = new ConstantStave("lightsheet.b.be",
																												0);
		mLightSheetStaveBeforeExposureR = new ConstantStave("lightsheet.r.be",
																												0);
		mLightSheetStaveBeforeExposureT = new EdgeStave("trigger.out.be",
																										1,
																										1,
																										0);

		mLightSheetStaveExposureX = new RampSteppingStave("lightsheet.x.e");
		mLightSheetStaveExposureY = new ConstantStave("lightsheet.y.e", 0);
		mLightSheetStaveExposureZ = new RampSteppingStave("lightsheet.z.e");
		mLightSheetStaveExposureB = new ConstantStave("lightsheet.b.e", 0);
		mLightSheetStaveExposureR = new ConstantStave("lightsheet.r.e", 0);
		mLightSheetStaveExposureT = new EdgeStave("trigger.out.e",
																							1,
																							0,
																							0);

		mNonSIIluminationLaserTrigger = new ConstantStave("trigger.out.e",
																											1);

		for (int i = 0; i < mLaserOnOffArray.length; i++)
		{
			final String lLaserName = "Laser" + i + ".exposure.trig";

			mStructuredIlluminationPatternVariableArray[i] = new ObjectVariable("StructuredIlluminationPattern",
																																					new BinaryStructuredIlluminationPattern());

			mLaserOnOffArray[i] = new BooleanVariable(lLaserName, false);
			mLaserOnOffArray[i].addSetListener(lDoubleVariableListener);

			mSIPatternOnOff[i] = new BooleanVariable(	lLaserName + "SIPatternOnOff",
																								false);
			mSIPatternOnOff[i].addSetListener(lDoubleVariableListener);
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

		mLightSheetXFunction.set(new UnivariateAffineFunction(MachineConfiguration.getCurrentMachineConfiguration()
																																							.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																			+ ".x.sa",
																																																	1),
																													MachineConfiguration.getCurrentMachineConfiguration()
																																							.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																			+ ".x.sb",
																																																	0)));

		mLightSheetYFunction.set(new UnivariateAffineFunction(MachineConfiguration.getCurrentMachineConfiguration()
																																							.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																			+ ".y.sa",
																																																	1),
																													MachineConfiguration.getCurrentMachineConfiguration()
																																							.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																			+ ".y.sb",
																																																	0)));

		mLightSheetZFunction.set(new UnivariateAffineFunction(MachineConfiguration.getCurrentMachineConfiguration()
																																							.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																			+ ".z.sa",
																																																	1),
																													MachineConfiguration.getCurrentMachineConfiguration()
																																							.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																			+ ".z.sb",
																																																	0)));

		mLightSheetBetaFunction.set(new UnivariateAffineFunction(	MachineConfiguration.getCurrentMachineConfiguration()
																																									.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																					+ ".beta.sa",
																																																			1),
																															MachineConfiguration.getCurrentMachineConfiguration()
																																									.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																					+ ".beta.sb",
																																																			0)));

		mLightSheetIrisDiameterFunction.set(new UnivariateAffineFunction(	MachineConfiguration.getCurrentMachineConfiguration()
																																													.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																									+ ".irisd.sa",
																																																							1),
																																			MachineConfiguration.getCurrentMachineConfiguration()
																																													.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																									+ ".irisd.sb",
																																																							0)));

		mLightSheetPowerFunction.set(new UnivariateAffineFunction(MachineConfiguration.getCurrentMachineConfiguration()
																																									.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																					+ ".p.sa",
																																																			1),
																															MachineConfiguration.getCurrentMachineConfiguration()
																																									.getDoubleProperty(	"device.lsm.lighsheet." + pName
																																																					+ ".p.sb",
																																																			0)));

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
		mLightSheetStaveBeforeExposureX = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																																	+ ".index.x",
																																																															2),
																																							mLightSheetStaveBeforeExposureX);

		mLightSheetStaveBeforeExposureY = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																																	+ ".index.y",
																																																															3),
																																							mLightSheetStaveBeforeExposureY);

		mLightSheetStaveBeforeExposureZ = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																																	+ ".index.y",
																																																															4),
																																							mLightSheetStaveBeforeExposureZ);

		mLightSheetStaveBeforeExposureB = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																																	+ ".index.b",
																																																															5),
																																							mLightSheetStaveBeforeExposureB);

		mLightSheetStaveBeforeExposureR = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																																	+ ".index.r",
																																																															6),
																																							mLightSheetStaveBeforeExposureR);

		mLightSheetStaveBeforeExposureLA = pBeforeExposureMovement.ensureSetStave(lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																																	+ ".index.la",
																																																															7),
																																							mLightSheetStaveBeforeExposureLA);

		mLightSheetStaveBeforeExposureT = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																																	+ ".index.t",
																																																															8 + 7),
																																							mLightSheetStaveBeforeExposureT);

	}

	private void ensureStavesAddedToExposureMovement(Movement pExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs at exposure:

		mLightSheetStaveExposureX = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																											+ ".index.x",
																																																									2),
																																	mLightSheetStaveExposureX);

		mLightSheetStaveExposureY = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																											+ ".index.y",
																																																									3),
																																	mLightSheetStaveExposureY);

		mLightSheetStaveExposureZ = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																											+ ".index.y",
																																																									4),
																																	mLightSheetStaveExposureZ);

		mLightSheetStaveExposureB = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																											+ ".index.b",
																																																									5),
																																	mLightSheetStaveExposureB);

		mLightSheetStaveExposureR = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																											+ ".index.r",
																																																									6),
																																	mLightSheetStaveExposureR);

		mLightSheetStaveExposureLA = pExposureMovement.ensureSetStave(lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																											+ ".index.la",
																																																									7),
																																	mLightSheetStaveExposureLA);

		mLightSheetStaveExposureT = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																											+ ".index.t",
																																																									8 + 7),
																																	mLightSheetStaveExposureT);

		for (int i = 0; i < mLaserOnOffArray.length; i++)
			mNonSIIluminationLaserTrigger = setLaserDigitalTriggerStave(pExposureMovement,
																																	i,
																																	mNonSIIluminationLaserTrigger);

	}

	private <O extends StaveInterface> O setLaserDigitalTriggerStave(	Movement pExposureMovement,
																																		int i,
																																		O pStave)
	{
		final int lLaserDigitalLineIndex = MachineConfiguration.getCurrentMachineConfiguration()
																														.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																										+ ".index.ld"
																																										+ i,
																																								8 + i);
		return mExposureMovement.ensureSetStave(lLaserDigitalLineIndex,
																						pStave);
	}

	public void update()
	{
		synchronized (this)
		{
			// System.out.println("Updating: " + getName());
			final double lReadoutTimeInMicroseconds = getBeforeExposureMovementDuration(TimeUnit.MICROSECONDS);
			final double lExposureMovementTimeInMicroseconds = getExposureMovementDuration(TimeUnit.MICROSECONDS);

			mBeforeExposureMovement.setDuration((long) round(lReadoutTimeInMicroseconds),
																					TimeUnit.MICROSECONDS);
			mExposureMovement.setDuration((long) round(lExposureMovementTimeInMicroseconds),
																		TimeUnit.MICROSECONDS);

			final double lLineExposureTimeInMicroseconds = lReadoutTimeInMicroseconds + lExposureMovementTimeInMicroseconds;
			mLineExposureInMicroseconds.setValue(lLineExposureTimeInMicroseconds);

			final double lGalvoXOffsetBeforeRotation = mLightSheetYInMicrons.getValue();
			final double lGalvoZOffsetBeforeRotation = mLightSheetZInMicrons.getValue();

			final double lGalvoXOffset = galvoRotateX(lGalvoXOffsetBeforeRotation,
																								lGalvoZOffsetBeforeRotation);
			final double lGalvoZOffset = galvoRotateZ(lGalvoXOffsetBeforeRotation,
																								lGalvoZOffsetBeforeRotation);

			final double lLightSheetlength = mLightSheetLengthInMicrons.getValue();
			final double lGalvoAmplitudeX = galvoRotateX(	lLightSheetlength,
																										0);
			final double lGalvoAmplitudeZ = galvoRotateZ(	lLightSheetlength,
																										0);

			final double lGalvoYLowValue = getLightSheetYFunction().get()
																															.value(lGalvoXOffset - lGalvoAmplitudeX);
			final double lGalvoYHighValue = getLightSheetYFunction().get()
																															.value(lGalvoXOffset + lGalvoAmplitudeX);

			final double lGalvoZLowValue = getLightSheetZFunction().get()
																															.value(lGalvoZOffset - lGalvoAmplitudeZ);
			final double lGalvoZHighValue = getLightSheetZFunction().get()
																															.value(lGalvoZOffset + lGalvoAmplitudeZ);

			mLightSheetStaveBeforeExposureX.setSyncStart(0);
			mLightSheetStaveBeforeExposureX.setSyncStop(1);
			mLightSheetStaveBeforeExposureX.setStartValue((float) lGalvoYHighValue);
			mLightSheetStaveBeforeExposureX.setStopValue((float) lGalvoYLowValue);

			mLightSheetStaveBeforeExposureZ.setSyncStart(0);
			mLightSheetStaveBeforeExposureZ.setSyncStop(1);
			mLightSheetStaveBeforeExposureZ.setStartValue((float) lGalvoZHighValue);
			mLightSheetStaveBeforeExposureZ.setStopValue((float) lGalvoZLowValue);

			mLightSheetStaveExposureX.setSyncStart(0);
			mLightSheetStaveExposureX.setSyncStop(1);
			mLightSheetStaveExposureX.setStartValue((float) lGalvoYLowValue);
			mLightSheetStaveExposureX.setStopValue((float) lGalvoYHighValue);
			mLightSheetStaveExposureX.setOutsideValue((float) lGalvoYHighValue);
			mLightSheetStaveExposureX.setNoJump(true);

			mLightSheetStaveExposureZ.setSyncStart(0);
			mLightSheetStaveExposureZ.setSyncStop(1);
			mLightSheetStaveExposureZ.setStartValue((float) lGalvoZLowValue);
			mLightSheetStaveExposureZ.setStopValue((float) lGalvoZHighValue);
			mLightSheetStaveExposureZ.setOutsideValue((float) lGalvoZHighValue);
			mLightSheetStaveExposureZ.setNoJump(true);

			mLightSheetStaveBeforeExposureY.setValue((float) getLightSheetXFunction().get()
																																								.value(mLightSheetXInMicrons.getValue()));
			mLightSheetStaveExposureY.setValue((float) getLightSheetXFunction().get()
																																					.value(mLightSheetXInMicrons.getValue()));

			mLightSheetStaveBeforeExposureB.setValue((float) getLightSheetBetaFunction().get()
																																									.value(mLightSheetBetaInDegrees.getValue()));
			mLightSheetStaveExposureB.setValue((float) getLightSheetBetaFunction().get()
																																						.value(mLightSheetBetaInDegrees.getValue()));

			final double lFocalLength = mFocalLengthInMicronsVariable.get();
			final double lLambdaInMicrons = mLambdaInMicronsVariable.get();
			final double lLightSheetRangeInMicrons = mLightSheetRangeInMicrons.getValue();

			final double lIrisDiameterInMm = GaussianBeamGeometry.getBeamIrisDiameter(lFocalLength,
																																								lLambdaInMicrons,
																																								lLightSheetRangeInMicrons);

			mLightSheetStaveBeforeExposureR.setValue((float) getLightSheetIrisDiameterFunction().get()
																																													.value(lIrisDiameterInMm));
			mLightSheetStaveExposureR.setValue(mLightSheetStaveBeforeExposureR.getConstantValue());

			final double lMarginTimeInMicroseconds = mMarginTimeInMicroseconds.getValue();
			final double lMarginTimeRelativeUnits = microsecondsToRelative(	lExposureMovementTimeInMicroseconds,
																																			lMarginTimeInMicroseconds);

			boolean lIsStepping = true;
			for (int i = 0; i < mLaserOnOffArray.length; i++)
				lIsStepping &= mSIPatternOnOff[i].getBooleanValue();

			mLightSheetStaveBeforeExposureX.setStepping(lIsStepping);
			mLightSheetStaveExposureX.setStepping(lIsStepping);
			mLightSheetStaveBeforeExposureZ.setStepping(lIsStepping);
			mLightSheetStaveExposureZ.setStepping(lIsStepping);

			for (int i = 0; i < mLaserOnOffArray.length; i++)
			{

				final BooleanVariable lLaserBooleanVariable = mLaserOnOffArray[i];
				final StructuredIlluminationPatternInterface lStructuredIlluminatioPatternInterface = mStructuredIlluminationPatternVariableArray[i].get();
				final StaveInterface lLaserTriggerStave = lStructuredIlluminatioPatternInterface.getStave(lMarginTimeRelativeUnits);
				lLaserTriggerStave.setEnabled(lLaserBooleanVariable.getBooleanValue());

				if (mSIPatternOnOff[i].getBooleanValue())
					setLaserDigitalTriggerStave(mExposureMovement,
																			i,
																			lLaserTriggerStave);

				else
					setLaserDigitalTriggerStave(mExposureMovement,
																			i,
																			mNonSIIluminationLaserTrigger);

			}

			mLightSheetStaveExposureLA.setValue((float) mLightSheetPowerFunction.get()
																																					.value(mLightSheetPowerInmW.getValue()));
			mLightSheetStaveBeforeExposureLA.setValue((float) mLightSheetPowerFunction.get()
																																								.value(mLightSheetPowerInmW.getValue()));

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

	private double galvoRotateX(double pY, double pZ)
	{
		final double lAlpha = Math.toRadians(mLightSheetAlphaInDegrees.getValue());
		return pY * cos(lAlpha) - pZ * sin(lAlpha);
	}

	private double galvoRotateZ(double pY, double pZ)
	{
		final double lAlpha = Math.toRadians(mLightSheetAlphaInDegrees.getValue());
		return pY * sin(lAlpha) + pZ * cos(lAlpha);
	}

	@Override
	public DoubleVariable getImageHeightVariable()
	{
		return mImageHeight;
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
	public DoubleVariable getLightSheetPoweInmWVariable()
	{
		return mLightSheetPowerInmW;
	}

	@Override
	public ObjectVariable<StructuredIlluminationPatternInterface> getSIPatternVariable(int pLaserIndex)
	{
		return mStructuredIlluminationPatternVariableArray[pLaserIndex];
	}

	@Override
	public int getNumberOfPhases(int pLaserIndex)
	{
		return mStructuredIlluminationPatternVariableArray[pLaserIndex].get()
																																		.getNumberOfPhases();
	}

	@Override
	public BooleanVariable getSIPatternOnOffVariable(int pLaserIndex)
	{
		return mSIPatternOnOff[pLaserIndex];
	}

	@Override
	public DoubleVariable getLaserOnOffArrayVariable(int pLaserIndex)
	{
		return mLaserOnOffArray[pLaserIndex];
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetXFunction()
	{
		return mLightSheetXFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetYFunction()
	{
		return mLightSheetYFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetZFunction()
	{
		return mLightSheetZFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetBetaFunction()
	{
		return mLightSheetBetaFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetIrisDiameterFunction()
	{
		return mLightSheetIrisDiameterFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getLightSheetLaserPowerFunction()
	{
		return mLightSheetPowerFunction;
	}

	public RampSteppingStave getGalvoScannerStaveBeforeExposureZ()
	{
		return mLightSheetStaveBeforeExposureZ;
	}

	public ConstantStave getGalvoScannerStaveBeforeExposureY()
	{
		return mLightSheetStaveBeforeExposureY;
	}

	public RampSteppingStave getIllumPifocStaveBeforeExposureX()
	{
		return mLightSheetStaveBeforeExposureX;
	}

	public RampSteppingStave getGalvoScannerStaveExposureZ()
	{
		return mLightSheetStaveExposureZ;
	}

	public ConstantStave getGalvoScannerStaveExposureY()
	{
		return mLightSheetStaveExposureY;
	}

	public RampSteppingStave getIllumPifocStaveExposureX()
	{
		return mLightSheetStaveExposureX;
	}

	public EdgeStave getTriggerOutStaveBeforeExposure()
	{
		return mLightSheetStaveBeforeExposureT;
	}

	public EdgeStave getTriggerOutStaveExposure()
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
