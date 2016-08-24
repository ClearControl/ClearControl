package clearcontrol.microscope.lightsheet.component.lightsheet;

import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.hardware.signalgen.movement.Movement;
import clearcontrol.hardware.signalgen.staves.ConstantStave;
import clearcontrol.hardware.signalgen.staves.EdgeStave;
import clearcontrol.hardware.signalgen.staves.IntervalStave;
import clearcontrol.hardware.signalgen.staves.RampSteppingStave;
import clearcontrol.hardware.signalgen.staves.StaveInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.BinaryStructuredIlluminationPattern;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

public class LightSheet extends VirtualDevice	implements
																							LightSheetInterface,
																							AsynchronousExecutorServiceAccess
{

	private final Variable<UnivariateAffineFunction> mXFunction = new Variable<>(	"LightSheetXFunction",
																																								new UnivariateAffineFunction());
	private final Variable<UnivariateAffineFunction> mYFunction = new Variable<>(	"LightSheetYFunction",
																																								new UnivariateAffineFunction());
	private final Variable<UnivariateAffineFunction> mZFunction = new Variable<>(	"LightSheetZFunction",
																																								new UnivariateAffineFunction());

	private final Variable<UnivariateAffineFunction> mWidthFunction = new Variable<>(	"LightSheetWidthFunction",
																																										new UnivariateAffineFunction());
	private final Variable<UnivariateAffineFunction> mHeightFunction = new Variable<>("LightSheetHeightFunction",
																																										new UnivariateAffineFunction());

	private final Variable<UnivariateAffineFunction> mAlphaFunction = new Variable<>(	"LightSheetAlphaFunction",
																																										new UnivariateAffineFunction());
	private final Variable<UnivariateAffineFunction> mBetaFunction = new Variable<>("LightSheetBetaFunction",
																																									new UnivariateAffineFunction());

	private final Variable<UnivariateAffineFunction> mPowerFunction = new Variable<>(	"LightSheetPowerFunction",
																																										new UnivariateAffineFunction());

	private final Variable<PolynomialFunction> mWidthPowerFunction = new Variable<>("LightSheetWidthPowerFunction",
																																									new PolynomialFunction(new double[]
																																									{ 1,
																																										0 }));

	private final Variable<PolynomialFunction> mHeightPowerFunction = new Variable<>(	"LightSheetHeightPowerFunction",
																																										new PolynomialFunction(new double[]
																																										{ 1,
																																											0 }));

	private final BoundedVariable<Double> mEffectiveExposureInMicrosecondsVariable = new BoundedVariable<Double>(	"EffectiveExposureInMicroseconds",
																																																								5000.0);
	private final BoundedVariable<Long> mImageHeightVariable = new BoundedVariable<Long>(	"ImageHeight",
																																												2 * 1024L);
	private final BoundedVariable<Double> mReadoutTimeInMicrosecondsPerLineVariable = new BoundedVariable<Double>("ReadoutTimeInMicrosecondsPerLine",
																																																								9.74);
	private final BoundedVariable<Double> mOverScanVariable = new BoundedVariable<Double>("OverScan",
																																												1.3);

	private final BoundedVariable<Double> mXVariable = new BoundedVariable<Double>(	"LightSheetX",
																																									0.0);
	private final BoundedVariable<Double> mYVariable = new BoundedVariable<Double>(	"LightSheetY",
																																									0.0);
	private final BoundedVariable<Number> mZVariable = new BoundedVariable<Number>(	"LightSheetZ",
																																									0.0);

	private final BoundedVariable<Double> mAlphaInDegreesVariable = new BoundedVariable<Double>("LightSheetAlphaInDegrees",
																																															0.0);
	private final BoundedVariable<Double> mBetaInDegreesVariable = new BoundedVariable<Double>(	"LightSheetBetaInDegrees",
																																															0.0);
	private final BoundedVariable<Double> mWidthVariable = new BoundedVariable<Double>(	"LightSheetRange",
																																											0.0);
	private final BoundedVariable<Double> mHeightVariable = new BoundedVariable<Double>("LightSheetLength",
																																											0.0);
	private final BoundedVariable<Double> mPowerVariable = new BoundedVariable<Double>(	"LightSheetLengthPower",
																																											1.0);
	private final Variable<Boolean> mAdaptPowerToWidthHeightVariable = new Variable<Boolean>(	"AdaptLightSheetPowerToWidthHeight",
																																														false);

	private final BoundedVariable<Double> mLineExposureInMicrosecondsVariable = new BoundedVariable<Double>("LineExposureInMicroseconds",
																																																					10.0);

	private final Variable<Boolean>[] mLaserOnOffVariableArray;

	private final Variable<Boolean>[] mSIPatternOnOffVariableArray;

	private final Variable<StructuredIlluminationPatternInterface>[] mStructuredIlluminationPatternVariableArray;

	private Movement mBeforeExposureMovement, mExposureMovement;

	private RampSteppingStave mBeforeExposureZStave,
			mBeforeExposureYStave, mExposureYStave, mExposureZStave;

	private ConstantStave mBeforeExposureXStave, mExposureXStave,
			mBeforeExposureBStave, mExposureBStave, mBeforeExposureWStave,
			mExposureWStave, mBeforeExposureLAStave, mExposureLAStave;
	private IntervalStave mNonSIIluminationLaserTriggerStave;

	private EdgeStave mBeforeExposureTStave, mExposureTStave;

	private final int mNumberOfLaserDigitalControls;

	@SuppressWarnings("unchecked")
	public LightSheet(String pName,
										final double pReadoutTimeInMicrosecondsPerLine,
										final long pNumberOfLines,
										final int pNumberOfLaserDigitalControls)
	{
		super(pName);

		mNumberOfLaserDigitalControls = pNumberOfLaserDigitalControls;

		mLaserOnOffVariableArray = new Variable[mNumberOfLaserDigitalControls];

		mSIPatternOnOffVariableArray = new Variable[mNumberOfLaserDigitalControls];

		mStructuredIlluminationPatternVariableArray = new Variable[mNumberOfLaserDigitalControls];

		mReadoutTimeInMicrosecondsPerLineVariable.set(pReadoutTimeInMicrosecondsPerLine);
		mImageHeightVariable.set(pNumberOfLines);

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

		mNonSIIluminationLaserTriggerStave = new IntervalStave(	"trigger.out",
																														0,
																														1,
																														1,
																														0);

		mOverScanVariable.setMinMax(1.001, 2);

		@SuppressWarnings("rawtypes")
		final VariableSetListener lVariableListener = (o, n) -> {
			System.out.println(getName() + ": new variable value: " + n);
			update();
			notifyListeners(this);
		};

		for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
		{
			final String lLaserName = "Laser" + i + ".exposure.trig";

			mStructuredIlluminationPatternVariableArray[i] = new Variable<StructuredIlluminationPatternInterface>("StructuredIlluminationPattern",
																																																						new BinaryStructuredIlluminationPattern());

			mLaserOnOffVariableArray[i] = new Variable<Boolean>(lLaserName,
																													false);
			mLaserOnOffVariableArray[i].addSetListener(lVariableListener);

			mSIPatternOnOffVariableArray[i] = new Variable<Boolean>(lLaserName + "SIPatternOnOff",
																															false);
			mSIPatternOnOffVariableArray[i].addSetListener(lVariableListener);
		}

		mReadoutTimeInMicrosecondsPerLineVariable.addSetListener(lVariableListener);
		mOverScanVariable.addSetListener(lVariableListener);
		mEffectiveExposureInMicrosecondsVariable.addSetListener(lVariableListener);
		mImageHeightVariable.addSetListener(lVariableListener);

		mXVariable.addSetListener(lVariableListener);
		mYVariable.addSetListener(lVariableListener);
		mZVariable.addSetListener(lVariableListener);
		mBetaInDegreesVariable.addSetListener(lVariableListener);
		mAlphaInDegreesVariable.addSetListener(lVariableListener);
		mHeightVariable.addSetListener(lVariableListener);
		mWidthVariable.addSetListener(lVariableListener);
		mPowerVariable.addSetListener(lVariableListener);
		mOverScanVariable.addSetListener(lVariableListener);
		mAdaptPowerToWidthHeightVariable.addSetListener(lVariableListener);

		for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
		{
			mStructuredIlluminationPatternVariableArray[i].addSetListener((	u,
																																			v) -> {
				update();
				notifyListeners(this);
			});
		}

		final VariableSetListener<?> lFunctionVariableListener = (o, n) -> {
			System.out.println(getName() + ": new function: " + n);
			resetBounds();
			update();
			notifyListeners(this);
		};

		resetFunctions();
		resetBounds();

		mXFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
		mYFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
		mZFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);

		mAlphaFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
		mBetaFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
		mWidthFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
		mHeightFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);
		mPowerFunction.addSetListener((VariableSetListener<UnivariateAffineFunction>) lFunctionVariableListener);

		mWidthPowerFunction.addSetListener((VariableSetListener<PolynomialFunction>) lFunctionVariableListener);
		mHeightPowerFunction.addSetListener((VariableSetListener<PolynomialFunction>) lFunctionVariableListener);

		update();
		notifyListeners(this);
	}

	@Override
	public void resetFunctions()
	{
		mXFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																				.getUnivariateAffineFunction("device.lsm.lighsheet." + getName()
																																			+ ".x.f"));

		mYFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																				.getUnivariateAffineFunction("device.lsm.lighsheet." + getName()
																																			+ ".y.f"));

		mZFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																				.getUnivariateAffineFunction("device.lsm.lighsheet." + getName()
																																			+ ".z.f"));

		mWidthFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.lighsheet." + getName()
																																					+ ".w.f"));

		mHeightFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.lighsheet." + getName()
																																					+ ".h.f"));

		mAlphaFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.lighsheet." + getName()
																																					+ ".a.f"));

		mBetaFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																					.getUnivariateAffineFunction("device.lsm.lighsheet." + getName()
																																				+ ".b.f"));

		mPowerFunction.set(MachineConfiguration.getCurrentMachineConfiguration()
																						.getUnivariateAffineFunction("device.lsm.lighsheet." + getName()
																																					+ ".p.f"));

		// TODO: load a polynomial:
		mWidthPowerFunction.set(new PolynomialFunction(new double[]
		{ 1 }));

		mHeightPowerFunction.set(new PolynomialFunction(new double[]
		{ 1 }));/**/
	}

	@Override
	public void resetBounds()
	{

		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.lighsheet." + getName()
																									+ ".x.bounds",
																							mXVariable,
																							mXFunction.get());
		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.lighsheet." + getName()
																									+ ".y.bounds",
																							mYVariable,
																							mYFunction.get());
		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.lighsheet." + getName()
																									+ ".z.bounds",
																							mZVariable,
																							mZFunction.get());

		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.lighsheet." + getName()
																									+ ".w.bounds",
																							mWidthVariable,
																							mWidthFunction.get());
		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.lighsheet." + getName()
																									+ ".h.bounds",
																							mHeightVariable,
																							mHeightFunction.get());

		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.lighsheet." + getName()
																									+ ".a.bounds",
																							mAlphaInDegreesVariable,
																							mAlphaFunction.get());
		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.lighsheet." + getName()
																									+ ".b.bounds",
																							mBetaInDegreesVariable,
																							mBetaFunction.get());

		MachineConfiguration.getCurrentMachineConfiguration()
												.getBoundsForVariable("device.lsm.lighsheet." + getName()
																									+ ".p.bounds",
																							mPowerVariable,
																							mPowerFunction.get());

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
																																																												+ ".x.index",
																																																										2),
																																		mBeforeExposureXStave);

		mBeforeExposureYStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".y.index",
																																																										3),
																																		mBeforeExposureYStave);

		mBeforeExposureZStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".z.index",
																																																										4),
																																		mBeforeExposureZStave);

		mBeforeExposureBStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".b.index",
																																																										5),
																																		mBeforeExposureBStave);

		mBeforeExposureWStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".w.index",
																																																										6),
																																		mBeforeExposureWStave);

		mBeforeExposureLAStave = pBeforeExposureMovement.ensureSetStave(lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".la.index",
																																																										7),
																																		mBeforeExposureLAStave);

		mBeforeExposureTStave = pBeforeExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																												+ ".t.index",
																																																										8 + 7),
																																		mBeforeExposureTStave);

	}

	private void ensureStavesAddedToExposureMovement(Movement pExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();

		// Analog outputs at exposure:

		mExposureXStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".x.index",
																																																				2),
																												mExposureXStave);

		mExposureYStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".y.index",
																																																				3),
																												mExposureYStave);

		mExposureZStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".z.index",
																																																				4),
																												mExposureZStave);

		mExposureBStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".b.index",
																																																				5),
																												mExposureBStave);

		mExposureWStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".w.index",
																																																				6),
																												mExposureWStave);

		mExposureLAStave = pExposureMovement.ensureSetStave(lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".la.index",
																																																				7),
																												mExposureLAStave);

		mExposureTStave = pExposureMovement.ensureSetStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getName().toLowerCase()
																																																						+ ".t.index",
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
																																										+ ".ld.index"
																																										+ i,
																																								8 + i);
		return mExposureMovement.ensureSetStave(lLaserDigitalLineIndex,
																						pStave);
	}

	@Override
	public void update()
	{
		synchronized (this)
		{
			if (mBeforeExposureMovement == null || mExposureMovement == null)
				return;

			System.out.println("Updating: " + getName());
			final double lReadoutTimeInMicroseconds = getBeforeExposureMovementDuration(TimeUnit.MICROSECONDS);
			final double lExposureMovementTimeInMicroseconds = getExposureMovementDuration(TimeUnit.MICROSECONDS);

			mBeforeExposureMovement.setDuration(round(lReadoutTimeInMicroseconds),
																					TimeUnit.MICROSECONDS);

			mExposureMovement.setDuration(round(lExposureMovementTimeInMicroseconds),
																		TimeUnit.MICROSECONDS);

			final double lLineExposureTimeInMicroseconds = lReadoutTimeInMicroseconds + lExposureMovementTimeInMicroseconds;
			mLineExposureInMicrosecondsVariable.set(lLineExposureTimeInMicroseconds);

			final double lGalvoYOffsetBeforeRotation = mYVariable.get();
			final double lGalvoZOffsetBeforeRotation = mZVariable.get()
																														.doubleValue();

			final double lGalvoYOffset = galvoRotateY(lGalvoYOffsetBeforeRotation,
																								lGalvoZOffsetBeforeRotation);
			final double lGalvoZOffset = galvoRotateZ(lGalvoYOffsetBeforeRotation,
																								lGalvoZOffsetBeforeRotation);

			final double lLightSheetHeight = mHeightFunction.get()
																											.value(mHeightVariable.get()) * mOverScanVariable.get();
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
			mBeforeExposureYStave.setExponent(0.2f);

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
																														.value(mXVariable.get()));
			mExposureXStave.setValue((float) getXFunction().get()
																											.value(mXVariable.get()));

			mBeforeExposureBStave.setValue((float) getBetaFunction().get()
																															.value(mBetaInDegreesVariable.get()));
			mExposureBStave.setValue((float) getBetaFunction().get()
																												.value(mBetaInDegreesVariable.get()));

			/*final double lFocalLength = mFocalLengthInMicronsVariable.get();
			final double lLambdaInMicrons = mLambdaInMicronsVariable.get();
			final double lLightSheetRangeInMicrons = mWidthVariable.getValue();

			final double lIrisDiameterInMm = GaussianBeamGeometry.getBeamIrisDiameter(lFocalLength,
																																								lLambdaInMicrons,
																																								lLightSheetRangeInMicrons);/**/
			double lWidthValue = getWidthFunction().get()
																							.value(mWidthVariable.get());

			mBeforeExposureWStave.setValue((float) lWidthValue);
			mExposureWStave.setValue((float) lWidthValue);

			final double lOverscan = mOverScanVariable.get();
			double lMarginTimeInMicroseconds = (lOverscan - 1) / (2 * lOverscan)
																					* lExposureMovementTimeInMicroseconds;
			final double lMarginTimeRelativeUnits = microsecondsToRelative(	lExposureMovementTimeInMicroseconds,
																																			lMarginTimeInMicroseconds);

			boolean lIsStepping = true;
			for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
				lIsStepping &= mSIPatternOnOffVariableArray[i].get();

			mBeforeExposureYStave.setStepping(lIsStepping);
			mExposureYStave.setStepping(lIsStepping);
			mBeforeExposureZStave.setStepping(lIsStepping);
			mExposureZStave.setStepping(lIsStepping);

			for (int i = 0; i < mLaserOnOffVariableArray.length; i++)
			{
				final Variable<Boolean> lLaserBooleanVariable = mLaserOnOffVariableArray[i];

				if (mSIPatternOnOffVariableArray[i].get())
				{

					final StructuredIlluminationPatternInterface lStructuredIlluminatioPatternInterface = mStructuredIlluminationPatternVariableArray[i].get();
					final StaveInterface lSIIlluminationLaserTriggerStave = lStructuredIlluminatioPatternInterface.getStave(lMarginTimeRelativeUnits);
					lSIIlluminationLaserTriggerStave.setEnabled(lLaserBooleanVariable.get());

					setLaserDigitalTriggerStave(mExposureMovement,
																			i,
																			lSIIlluminationLaserTriggerStave);
				}
				else
				{
					mNonSIIluminationLaserTriggerStave.setEnabled(lLaserBooleanVariable.get());
					mNonSIIluminationLaserTriggerStave.setStart((float) lMarginTimeRelativeUnits);
					mNonSIIluminationLaserTriggerStave.setStop((float) (1 - lMarginTimeRelativeUnits));
					setLaserDigitalTriggerStave(mExposureMovement,
																			i,
																			mNonSIIluminationLaserTriggerStave);
				}

			}

			double lPowerValue = mPowerFunction.get()
																					.value(mPowerVariable.get());

			if (mAdaptPowerToWidthHeightVariable.get())
			{
				double lWidthPowerFactor = mWidthPowerFunction.get()
																											.value(lWidthValue);

				double lHeightPowerFactor = mHeightPowerFunction.get()
																												.value(lLightSheetHeight / lOverscan);/**/

				lPowerValue *= lWidthPowerFactor * lHeightPowerFactor;
			}

			mBeforeExposureLAStave.setValue(0f);
			mExposureLAStave.setValue((float) lPowerValue);

		}

	}

	@Override
	public int getNumberOfLaserDigitalControls()
	{
		return mNumberOfLaserDigitalControls;
	}

	public long getExposureMovementDuration(TimeUnit pTimeUnit)
	{
		return pTimeUnit.convert(	mEffectiveExposureInMicrosecondsVariable.get()
																																			.longValue(),
															TimeUnit.MICROSECONDS);
	}

	public long getBeforeExposureMovementDuration(TimeUnit pTimeUnit)
	{
		return pTimeUnit.convert(	(long) (mReadoutTimeInMicrosecondsPerLineVariable.get() * mImageHeightVariable.get() / 2),
															TimeUnit.MICROSECONDS);
	}

	private double galvoRotateY(double pY, double pZ)
	{
		final double lAlpha = Math.toRadians(mAlphaFunction.get()
																												.value(mAlphaInDegreesVariable.get()));
		return pY * cos(lAlpha) - pZ * sin(lAlpha);
	}

	private double galvoRotateZ(double pY, double pZ)
	{
		final double lAlpha = Math.toRadians(mAlphaFunction.get()
																												.value(mAlphaInDegreesVariable.get()));
		return pY * sin(lAlpha) + pZ * cos(lAlpha);
	}

	@Override
	public BoundedVariable<Long> getImageHeightVariable()
	{
		return mImageHeightVariable;
	}

	public void setEffectiveExposureInMicroseconds(final int pEffectiveExposureInMicroseconds)
	{
		mEffectiveExposureInMicrosecondsVariable.set((double) pEffectiveExposureInMicroseconds);
	}

	@Override
	public BoundedVariable<Double> getEffectiveExposureInMicrosecondsVariable()
	{
		return mEffectiveExposureInMicrosecondsVariable;
	}

	@Override
	public BoundedVariable<Double> getLineExposureInMicrosecondsVariable()
	{
		return mLineExposureInMicrosecondsVariable;
	}

	@Override
	public BoundedVariable<Double> getOverScanVariable()
	{
		return mOverScanVariable;
	}

	@Override
	public BoundedVariable<Double> getReadoutTimeInMicrosecondsPerLineVariable()
	{
		return mReadoutTimeInMicrosecondsPerLineVariable;
	}

	@Override
	public BoundedVariable<Double> getXVariable()
	{
		return mXVariable;
	}

	@Override
	public BoundedVariable<Double> getYVariable()
	{
		return mYVariable;
	}

	@Override
	public BoundedVariable<Number> getZVariable()
	{
		return mZVariable;
	}

	@Override
	public BoundedVariable<Double> getAlphaInDegreesVariable()
	{
		return mAlphaInDegreesVariable;
	}

	@Override
	public BoundedVariable<Double> getBetaInDegreesVariable()
	{
		return mBetaInDegreesVariable;
	}

	@Override
	public BoundedVariable<Double> getWidthVariable()
	{
		return mWidthVariable;
	}

	@Override
	public BoundedVariable<Double> getHeightVariable()
	{
		return mHeightVariable;
	}

	@Override
	public BoundedVariable<Double> getPowerVariable()
	{
		return mPowerVariable;
	}

	@Override
	public Variable<Boolean> getAdaptPowerToWidthHeightVariable()
	{
		return mAdaptPowerToWidthHeightVariable;
	}

	@Override
	public Variable<StructuredIlluminationPatternInterface> getSIPatternVariable(int pLaserIndex)
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
	public Variable<Boolean> getSIPatternOnOffVariable(int pLaserIndex)
	{
		return mSIPatternOnOffVariableArray[pLaserIndex];
	}

	@Override
	public Variable<Boolean> getLaserOnOffArrayVariable(int pLaserIndex)
	{
		return mLaserOnOffVariableArray[pLaserIndex];
	}

	@Override
	public Variable<UnivariateAffineFunction> getXFunction()
	{
		return mXFunction;
	}

	@Override
	public Variable<UnivariateAffineFunction> getYFunction()
	{
		return mYFunction;
	}

	@Override
	public Variable<UnivariateAffineFunction> getZFunction()
	{
		return mZFunction;
	}

	@Override
	public Variable<UnivariateAffineFunction> getWidthFunction()
	{
		return mWidthFunction;
	}

	@Override
	public Variable<UnivariateAffineFunction> getHeightFunction()
	{
		return mHeightFunction;
	}

	@Override
	public Variable<UnivariateAffineFunction> getAlphaFunction()
	{
		return mAlphaFunction;
	}

	@Override
	public Variable<UnivariateAffineFunction> getBetaFunction()
	{
		return mBetaFunction;
	}

	@Override
	public Variable<UnivariateAffineFunction> getPowerFunction()
	{
		return mPowerFunction;
	}

	@Override
	public Variable<PolynomialFunction> getWidthPowerFunction()
	{
		return mWidthPowerFunction;
	}

	@Override
	public Variable<PolynomialFunction> getHeightPowerFunction()
	{
		return mHeightPowerFunction;
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
