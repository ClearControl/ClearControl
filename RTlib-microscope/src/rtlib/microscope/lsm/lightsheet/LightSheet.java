package rtlib.microscope.lsm.lightsheet;

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
import rtlib.microscope.lsm.lightsheet.si.BinaryStructuredIlluminationPattern;
import rtlib.microscope.lsm.lightsheet.si.StructuredIlluminationPatternInterface;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.staves.ConstantStave;
import rtlib.symphony.staves.EdgeStave;
import rtlib.symphony.staves.RampSteppingStave;
import rtlib.symphony.staves.StaveInterface;

public class LightSheet extends NamedVirtualDevice implements
																									LightSheetInterface,
																									AsynchronousExecutorServiceAccess
{

	private String mName;

	private final ObjectVariable<UnivariateAffineComposableFunction> mXFunction = new ObjectVariable<>(	"LightSheetXFunction",
																																																			new UnivariateAffineFunction());
	private final ObjectVariable<UnivariateAffineComposableFunction> mYFunction = new ObjectVariable<>(	"LightSheetYFunction",
																																																			new UnivariateAffineFunction());
	private final ObjectVariable<UnivariateAffineComposableFunction> mZFunction = new ObjectVariable<>(	"LightSheetZFunction",
																																																			new UnivariateAffineFunction());

	private final ObjectVariable<UnivariateAffineComposableFunction> mWidthFunction = new ObjectVariable<>(	"LightSheetWidthFunction",
																																																					new UnivariateAffineFunction());
	private final ObjectVariable<UnivariateAffineComposableFunction> mHeightFunction = new ObjectVariable<>("LightSheetHeightFunction",
																																																					new UnivariateAffineFunction());

	private final ObjectVariable<UnivariateAffineComposableFunction> mAlphaFunction = new ObjectVariable<>(	"LightSheetAlphaFunction",
																																																					new UnivariateAffineFunction());
	private final ObjectVariable<UnivariateAffineComposableFunction> mBetaFunction = new ObjectVariable<>("LightSheetBetaFunction",
																																																				new UnivariateAffineFunction());

	private final ObjectVariable<UnivariateAffineComposableFunction> mPowerFunction = new ObjectVariable<>(	"LightSheetPowerFunction",
																																																					new UnivariateAffineFunction());

	private final DoubleVariable mEffectiveExposureInMicrosecondsVariable = new DoubleVariable(	"EffectiveExposureInMicroseconds",
																																															5000);
	private final DoubleVariable mImageHeightVariable = new DoubleVariable(	"ImageHeight",
																																					2 * 1024);
	private final DoubleVariable mReadoutTimeInMicrosecondsPerLineVariable = new DoubleVariable("ReadoutTimeInMicrosecondsPerLine",
																																															9.74);
	private final DoubleVariable mMarginTimeInMicrosecondsVariable = new DoubleVariable("MarginTimeInMicroseconds",
																																											100);
	private final DoubleVariable mFocalLengthInMicronsVariable = new DoubleVariable("FocalLengthInMicrons",
																																									20000);
	private final DoubleVariable mLambdaInMicronsVariable = new DoubleVariable(	"LambdaInMicrons",
																																							594);

	private final DoubleVariable mXVariable = new DoubleVariable(	"LightSheetX",
																																0);
	private final DoubleVariable mYVariable = new DoubleVariable(	"LightSheetY",
																																0);
	private final DoubleVariable mZVariable = new DoubleVariable(	"LightSheetZ",
																																0);

	private final DoubleVariable mAlphaInDegreesVariable = new DoubleVariable("LightSheetAlphaInDegrees",
																																						0);
	private final DoubleVariable mBetaInDegreesVariable = new DoubleVariable(	"LightSheetBetaInDegrees",
																																						0);
	private final DoubleVariable mWidthVariable = new DoubleVariable(	"LightSheetRange",
																																		0);
	private final DoubleVariable mHeightVariable = new DoubleVariable("LightSheetLength",
																																		0);
	private final DoubleVariable mPowerVariable = new DoubleVariable(	"LightSheetLengthPower",
																																		1);

	private final DoubleVariable mLineExposureInMicrosecondsVariable = new DoubleVariable("LineExposureInMicroseconds",
																																												10);

	private final BooleanVariable[] mLaserOnOffVariableArray;

	private final BooleanVariable[] mSIPatternOnOffVariableArray;

	private final ObjectVariable<StructuredIlluminationPatternInterface>[] mStructuredIlluminationPatternVariableArray;

	private Movement mBeforeExposureMovement, mExposureMovement;

	private RampSteppingStave mBeforeExposureZStave,
			mBeforeExposureYStave, mExposureYStave, mExposureZStave;

	private ConstantStave mBeforeExposureXStave, mExposureXStave,
			mBeforeExposureBStave, mExposureBStave, mBeforeExposureWStave,
			mExposureWStave, mBeforeExposureLAStave, mExposureLAStave,
			mNonSIIluminationLaserTriggerStave;

	private EdgeStave mBeforeExposureTStave, mExposureTStave;

	private final int mNumberOfLaserDigitalControls;

	@SuppressWarnings("unchecked")
	public LightSheet(String pName,
										final double pReadoutTimeInMicrosecondsPerLine,
										final int pNumberOfLines,
										final int pNumberOfLaserDigitalControls)
	{
		super(pName);
		mName = pName;

		mNumberOfLaserDigitalControls = pNumberOfLaserDigitalControls;

		final VariableSetListener<Double> lDoubleVariableListener = (u, v) -> {
			update();
		};

		mLaserOnOffVariableArray = new BooleanVariable[mNumberOfLaserDigitalControls];

		mSIPatternOnOffVariableArray = new BooleanVariable[mNumberOfLaserDigitalControls];

		mStructuredIlluminationPatternVariableArray = new ObjectVariable[mNumberOfLaserDigitalControls];

		mReadoutTimeInMicrosecondsPerLineVariable.setValue(pReadoutTimeInMicrosecondsPerLine);
		mImageHeightVariable.setValue(pNumberOfLines);

		mBeforeExposureLAStave = new ConstantStave(	"laser.beforeexp.am",
																								0);
		mExposureLAStave = new ConstantStave("laser.exposure.am", 0);

		mBeforeExposureXStave = new ConstantStave("lightsheet.x.be", 0);
		mBeforeExposureYStave = new RampSteppingStave("lightsheet.y.be");
		mBeforeExposureZStave = new RampSteppingStave("lightsheet.z.be");
		mBeforeExposureBStave = new ConstantStave("lightsheet.b.be", 0);
		mBeforeExposureWStave = new ConstantStave("lightsheet.r.be", 0);
		mBeforeExposureTStave = new EdgeStave("trigger.out.be", 1, 1, 0);

		mExposureXStave = new ConstantStave("lightsheet.x.e", 0);
		mExposureYStave = new RampSteppingStave("lightsheet.y.e");
		mExposureZStave = new RampSteppingStave("lightsheet.z.e");
		mExposureBStave = new ConstantStave("lightsheet.b.e", 0);
		mExposureWStave = new ConstantStave("lightsheet.r.e", 0);
		mExposureTStave = new EdgeStave("trigger.out.e", 1, 0, 0);

		mNonSIIluminationLaserTriggerStave = new ConstantStave(	"trigger.out.e",
																														1);

		for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
		{
			final String lLaserName = "Laser" + i + ".exposure.trig";

			mStructuredIlluminationPatternVariableArray[i] = new ObjectVariable("StructuredIlluminationPattern",
																																					new BinaryStructuredIlluminationPattern());

			mLaserOnOffVariableArray[i] = new BooleanVariable(lLaserName,
																												false);
			mLaserOnOffVariableArray[i].addSetListener(lDoubleVariableListener);

			mSIPatternOnOffVariableArray[i] = new BooleanVariable(lLaserName + "SIPatternOnOff",
																														false);
			mSIPatternOnOffVariableArray[i].addSetListener(lDoubleVariableListener);
		}

		mReadoutTimeInMicrosecondsPerLineVariable.addSetListener(lDoubleVariableListener);
		mMarginTimeInMicrosecondsVariable.addSetListener(lDoubleVariableListener);
		mEffectiveExposureInMicrosecondsVariable.addSetListener(lDoubleVariableListener);
		mImageHeightVariable.addSetListener(lDoubleVariableListener);

		mXVariable.addSetListener(lDoubleVariableListener);
		mXVariable.addSetListener(lDoubleVariableListener);
		mZVariable.addSetListener(lDoubleVariableListener);
		mBetaInDegreesVariable.addSetListener(lDoubleVariableListener);
		mAlphaInDegreesVariable.addSetListener(lDoubleVariableListener);
		mHeightVariable.addSetListener(lDoubleVariableListener);

		for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
		{
			mStructuredIlluminationPatternVariableArray[i].addSetListener((	u,
																																			v) -> {
				update();
			});
		}

		resetFunctions();

	}

	public void resetFunctions()
	{
		mXFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																				.getUnivariateAffineFunction("device.lsm.lighsheet." + mName
																																			+ ".x"));

		mYFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																				.getUnivariateAffineFunction("device.lsm.lighsheet." + mName
																																			+ ".y"));

		mZFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																				.getUnivariateAffineFunction("device.lsm.lighsheet." + mName
																																			+ ".z"));

		mWidthFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.lighsheet." + mName
																																					+ ".w"));

		mHeightFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.lighsheet." + mName
																																					+ ".h"));

		mAlphaFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.lighsheet." + mName
																																					+ ".a"));

		mBetaFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																					.getUnivariateAffineFunction("device.lsm.lighsheet." + mName
																																				+ ".b"));

		mPowerFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.lighsheet." + mName
																																					+ ".p"));
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
		mBeforeExposureXStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".index.x",
																																																										2),
																																		mBeforeExposureXStave);

		mBeforeExposureYStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".index.y",
																																																										3),
																																		mBeforeExposureYStave);

		mBeforeExposureZStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".index.z",
																																																										4),
																																		mBeforeExposureZStave);

		mBeforeExposureBStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".index.b",
																																																										5),
																																		mBeforeExposureBStave);

		mBeforeExposureWStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".index.w",
																																																										6),
																																		mBeforeExposureWStave);

		mBeforeExposureLAStave = pBeforeExposureMovement.ensureSetStave(lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".index.la",
																																																										7),
																																		mBeforeExposureLAStave);

		mBeforeExposureTStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".index.t",
																																																										8 + 7),
																																		mBeforeExposureTStave);

	}

	private void ensureStavesAddedToExposureMovement(Movement pExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs at exposure:

		mExposureXStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".index.x",
																																																				2),
																												mExposureXStave);

		mExposureYStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".index.y",
																																																				3),
																												mExposureYStave);

		mExposureZStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".index.z",
																																																				4),
																												mExposureZStave);

		mExposureBStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".index.b",
																																																				5),
																												mExposureBStave);

		mExposureWStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".index.w",
																																																				6),
																												mExposureWStave);

		mExposureLAStave = pExposureMovement.ensureSetStave(lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".index.la",
																																																				7),
																												mExposureLAStave);

		mExposureTStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".index.t",
																																																				8 + 7),
																												mExposureTStave);

		for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
			mNonSIIluminationLaserTriggerStave = setLaserDigitalTriggerStave(	pExposureMovement,
																																				i,
																																				mNonSIIluminationLaserTriggerStave);

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
			mLineExposureInMicrosecondsVariable.setValue(lLineExposureTimeInMicroseconds);

			final double lGalvoYOffsetBeforeRotation = mYVariable.getValue();
			final double lGalvoZOffsetBeforeRotation = mZVariable.getValue();

			final double lGalvoYOffset = galvoRotateY(lGalvoYOffsetBeforeRotation,
																								lGalvoZOffsetBeforeRotation);
			final double lGalvoZOffset = galvoRotateZ(lGalvoYOffsetBeforeRotation,
																								lGalvoZOffsetBeforeRotation);

			final double lLightSheetHeight = mHeightFunction.get()
																											.value(mHeightVariable.getValue());
			final double lGalvoAmplitudeY = galvoRotateY(	lLightSheetHeight,
																										0);
			final double lGalvoAmplitudeZ = galvoRotateZ(	lLightSheetHeight,
																										0);

			final double lGalvoYLowValue = getYFunction().get()
																										.value(lGalvoYOffset - lGalvoAmplitudeY);
			final double lGalvoYHighValue = getYFunction().get()
																										.value(lGalvoYOffset + lGalvoAmplitudeY);

			final double lGalvoZLowValue = getZFunction().get()
																										.value(lGalvoZOffset - lGalvoAmplitudeZ);
			final double lGalvoZHighValue = getZFunction().get()
																										.value(lGalvoZOffset + lGalvoAmplitudeZ);

			mBeforeExposureYStave.setSyncStart(0);
			mBeforeExposureYStave.setSyncStop(1);
			mBeforeExposureYStave.setStartValue((float) lGalvoYHighValue);
			mBeforeExposureYStave.setStopValue((float) lGalvoYLowValue);

			mBeforeExposureZStave.setSyncStart(0);
			mBeforeExposureZStave.setSyncStop(1);
			mBeforeExposureZStave.setStartValue((float) lGalvoZHighValue);
			mBeforeExposureZStave.setStopValue((float) lGalvoZLowValue);

			mExposureYStave.setSyncStart(0);
			mExposureYStave.setSyncStop(1);
			mExposureYStave.setStartValue((float) lGalvoYLowValue);
			mExposureYStave.setStopValue((float) lGalvoYHighValue);
			mExposureYStave.setOutsideValue((float) lGalvoYHighValue);
			mExposureYStave.setNoJump(true);

			mExposureZStave.setSyncStart(0);
			mExposureZStave.setSyncStop(1);
			mExposureZStave.setStartValue((float) lGalvoZLowValue);
			mExposureZStave.setStopValue((float) lGalvoZHighValue);
			mExposureZStave.setOutsideValue((float) lGalvoZHighValue);
			mExposureZStave.setNoJump(true);

			mBeforeExposureXStave.setValue((float) getXFunction().get()
																														.value(mXVariable.getValue()));
			mExposureXStave.setValue((float) getXFunction().get()
																											.value(mXVariable.getValue()));

			mBeforeExposureBStave.setValue((float) getBetaFunction().get()
																															.value(mBetaInDegreesVariable.getValue()));
			mExposureBStave.setValue((float) getBetaFunction().get()
																												.value(mBetaInDegreesVariable.getValue()));

			/*final double lFocalLength = mFocalLengthInMicronsVariable.get();
			final double lLambdaInMicrons = mLambdaInMicronsVariable.get();
			final double lLightSheetRangeInMicrons = mWidthVariable.getValue();

			final double lIrisDiameterInMm = GaussianBeamGeometry.getBeamIrisDiameter(lFocalLength,
																																								lLambdaInMicrons,
																																								lLightSheetRangeInMicrons);/**/

			mBeforeExposureWStave.setValue((float) getWidthFunction().get()
																																.value(mWidthVariable.getValue()));
			mExposureWStave.setValue(mBeforeExposureWStave.getConstantValue());

			final double lMarginTimeInMicroseconds = mMarginTimeInMicrosecondsVariable.getValue();
			final double lMarginTimeRelativeUnits = microsecondsToRelative(	lExposureMovementTimeInMicroseconds,
																																			lMarginTimeInMicroseconds);

			boolean lIsStepping = true;
			for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
				lIsStepping &= mSIPatternOnOffVariableArray[i].getBooleanValue();

			mBeforeExposureYStave.setStepping(lIsStepping);
			mExposureYStave.setStepping(lIsStepping);
			mBeforeExposureZStave.setStepping(lIsStepping);
			mExposureZStave.setStepping(lIsStepping);

			for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
			{

				final BooleanVariable lLaserBooleanVariable = mLaserOnOffVariableArray[i];
				final StructuredIlluminationPatternInterface lStructuredIlluminatioPatternInterface = mStructuredIlluminationPatternVariableArray[i].get();
				final StaveInterface lLaserTriggerStave = lStructuredIlluminatioPatternInterface.getStave(lMarginTimeRelativeUnits);
				lLaserTriggerStave.setEnabled(lLaserBooleanVariable.getBooleanValue());

				if (mSIPatternOnOffVariableArray[i].getBooleanValue())
					setLaserDigitalTriggerStave(mExposureMovement,
																			i,
																			lLaserTriggerStave);

				else
					setLaserDigitalTriggerStave(mExposureMovement,
																			i,
																			mNonSIIluminationLaserTriggerStave);

			}

			mExposureLAStave.setValue((float) mPowerFunction.get()
																											.value(mPowerVariable.getValue()));
			mBeforeExposureLAStave.setValue((float) mPowerFunction.get()
																														.value(mPowerVariable.getValue()));

		}
	}

	public long getExposureMovementDuration(TimeUnit pTimeUnit)
	{
		return pTimeUnit.convert(	(long) mEffectiveExposureInMicrosecondsVariable.getValue(),
															TimeUnit.MICROSECONDS);
	}

	public long getBeforeExposureMovementDuration(TimeUnit pTimeUnit)
	{
		return pTimeUnit.convert(	(long) (mReadoutTimeInMicrosecondsPerLineVariable.getValue() * mImageHeightVariable.getValue() / 2),
															TimeUnit.MICROSECONDS);
	}

	private double galvoRotateY(double pY, double pZ)
	{
		final double lAlpha = Math.toRadians(mAlphaFunction.get()
																												.value(mAlphaInDegreesVariable.getValue()));
		return pY * cos(lAlpha) - pZ * sin(lAlpha);
	}

	private double galvoRotateZ(double pY, double pZ)
	{
		final double lAlpha = Math.toRadians(mAlphaFunction.get()
																												.value(mAlphaInDegreesVariable.getValue()));
		return pY * sin(lAlpha) + pZ * cos(lAlpha);
	}

	@Override
	public DoubleVariable getImageHeightVariable()
	{
		return mImageHeightVariable;
	}

	public void setEffectiveExposureInMicroseconds(final int pEffectiveExposureInMicroseconds)
	{
		mEffectiveExposureInMicrosecondsVariable.setValue(pEffectiveExposureInMicroseconds);
	}

	@Override
	public DoubleVariable getEffectiveExposureInMicrosecondsVariable()
	{
		return mEffectiveExposureInMicrosecondsVariable;
	}

	@Override
	public DoubleVariable getLineExposureInMicrosecondsVariable()
	{
		return mLineExposureInMicrosecondsVariable;
	}

	@Override
	public DoubleVariable getMarginTimeInMicrosecondsVariable()
	{
		return mMarginTimeInMicrosecondsVariable;
	}

	@Override
	public DoubleVariable getReadoutTimeInMicrosecondsPerLineVariable()
	{
		return mReadoutTimeInMicrosecondsPerLineVariable;
	}

	@Override
	public DoubleVariable getXVariable()
	{
		return mXVariable;
	}

	@Override
	public DoubleVariable getYVariable()
	{
		return mYVariable;
	}

	@Override
	public DoubleVariable getZVariable()
	{
		return mZVariable;
	}

	@Override
	public DoubleVariable getAlphaInDegreesVariable()
	{
		return mAlphaInDegreesVariable;
	}

	@Override
	public DoubleVariable getBetaInDegreesVariable()
	{
		return mBetaInDegreesVariable;
	}

	@Override
	public DoubleVariable getWidthVariable()
	{
		return mWidthVariable;
	}

	@Override
	public DoubleVariable getHeightVariable()
	{
		return mHeightVariable;
	}

	@Override
	public DoubleVariable getPowerVariable()
	{
		return mPowerVariable;
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
		return mSIPatternOnOffVariableArray[pLaserIndex];
	}

	@Override
	public DoubleVariable getLaserOnOffArrayVariable(int pLaserIndex)
	{
		return mLaserOnOffVariableArray[pLaserIndex];
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getXFunction()
	{
		return mXFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getYFunction()
	{
		return mYFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getZFunction()
	{
		return mZFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getWidthFunction()
	{
		return mWidthFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getHeightFunction()
	{
		return mHeightFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getAlphaFunction()
	{
		return mAlphaFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getBetaFunction()
	{
		return mBetaFunction;
	}

	@Override
	public ObjectVariable<UnivariateAffineComposableFunction> getPowerFunction()
	{
		return mPowerFunction;
	}

	public RampSteppingStave getGalvoScannerStaveBeforeExposureZ()
	{
		return mBeforeExposureZStave;
	}

	public RampSteppingStave getGalvoScannerStaveBeforeExposureY()
	{
		return mBeforeExposureYStave;
	}

	public ConstantStave getIllumPifocStaveBeforeExposureX()
	{
		return mBeforeExposureXStave;
	}

	public RampSteppingStave getGalvoScannerStaveExposureZ()
	{
		return mExposureZStave;
	}

	public RampSteppingStave getGalvoScannerStaveExposureY()
	{
		return mExposureYStave;
	}

	public ConstantStave getIllumPifocStaveExposureX()
	{
		return mExposureXStave;
	}

	public EdgeStave getTriggerOutStaveBeforeExposure()
	{
		return mBeforeExposureTStave;
	}

	public EdgeStave getTriggerOutStaveExposure()
	{
		return mExposureTStave;
	}

	public ConstantStave getLaserAnalogModulationBeforeExposure()
	{
		return mBeforeExposureLAStave;
	}

	public ConstantStave getLaserAnalogModulationExposure()
	{
		return mExposureLAStave;
	}

	private static double microsecondsToRelative(	final double pTotalTime,
																								final double pSubTime)
	{
		return pSubTime / pTotalTime;
	}

}
