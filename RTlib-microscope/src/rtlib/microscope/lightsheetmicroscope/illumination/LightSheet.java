package rtlib.microscope.lightsheetmicroscope.illumination;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import org.apache.commons.math3.analysis.UnivariateFunction;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.configuration.MachineConfiguration;
import rtlib.core.device.UpdatableDevice;
import rtlib.core.math.regression.linear.UnivariateAffineFunction;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.staves.ConstantStave;
import rtlib.symphony.staves.GalvoScannerStave;
import rtlib.symphony.staves.LaserTriggerBinaryPattern2Stave;

public class LightSheet extends UpdatableDevice	implements
																									LightSheetInterface,
																									AsynchronousExecutorServiceAccess
{

	private static final double cMicronsToNormGalvoUnits = -0.003026;

	private final UnivariateFunction mLightSheetXConversion = new UnivariateAffineFunction();
	private final UnivariateFunction mLightSheetYConversion = new UnivariateAffineFunction(	cMicronsToNormGalvoUnits,
																																													0);
	private final UnivariateFunction mLightSheetZConversion = new UnivariateAffineFunction(	cMicronsToNormGalvoUnits,
																																													0);
	private final UnivariateFunction mLightSheetBetaConversion = new UnivariateAffineFunction(1,
																																														0);
	private final UnivariateFunction mLightSheetIrisDiameterConversion = new UnivariateAffineFunction(	1,
																																															0);

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

	private final GalvoScannerStave mLightSheetStaveBeforeExposureZ,
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
	private final LaserTriggerBinaryPattern2Stave[] mLightSheetStaveLaserLD;

	private final int mNumberOfLaserDigitalControls;






	public LightSheet(String pName,
										final double pReadoutTimeInMicrosecondsPerLine,
										final int pNumberOfLines,
										final int pNumberOfLaserDigitalControls)
	{
		super(pName);

		mNumberOfLaserDigitalControls = pNumberOfLaserDigitalControls;

		final DoubleVariable lDoubleUpdateListener = new DoubleVariable("UpdateListener",
																																		0)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				setUpToDate(false);
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mLaserOnOffArray = new BooleanVariable[mNumberOfLaserDigitalControls];

		mLightSheetStaveLaserLD = new LaserTriggerBinaryPattern2Stave[mNumberOfLaserDigitalControls];

		mReadoutTimeInMicrosecondsPerLine.setValue(pReadoutTimeInMicrosecondsPerLine);
		mImageHeight.setValue(pNumberOfLines);

		mLightSheetStaveBeforeExposureLA = new ConstantStave("laser.beforeexp.am",
																												0);
		mLightSheetStaveExposureLA = new ConstantStave("laser.exposure.am",
																									0);

		mLightSheetStaveBeforeExposureZ = new GalvoScannerStave("lightsheet.z.be");
		mLightSheetStaveBeforeExposureY = new GalvoScannerStave("lightsheet.y.be");
		mLightSheetStaveBeforeExposureX = new ConstantStave("lightsheet.x.be",
																												0);
		mLightSheetStaveBeforeExposureB = new ConstantStave("lightsheet.b.be",
																												0);
		mLightSheetStaveBeforeExposureR = new ConstantStave("lightsheet.r.be",
																												0);
		mLightSheetStaveBeforeExposureT = new ConstantStave("trigger.out.be",
																												1);

		mLightSheetStaveExposureZ = new GalvoScannerStave("lightsheet.z.e");
		mLightSheetStaveExposureY = new GalvoScannerStave("lightsheet.y.e");
		mLightSheetStaveExposureX = new ConstantStave("lightsheet.x.e", 0);
		mLightSheetStaveExposureB = new ConstantStave("lightsheet.b.e", 0);
		mLightSheetStaveExposureR = new ConstantStave("lightsheet.r.e", 0);
		mLightSheetStaveExposureT = new ConstantStave("trigger.out.e", 0);

		for (int i = 0; i < mLaserOnOffArray.length; i++)
		{
			final String lLaserName = "Laser" + i + ".exposure.trig";
			mLightSheetStaveLaserLD[i] = new LaserTriggerBinaryPattern2Stave(lLaserName);

			mLaserOnOffArray[i] = new BooleanVariable(lLaserName, false);
			mLaserOnOffArray[i].sendUpdatesTo(lDoubleUpdateListener);
		}

		mReadoutTimeInMicrosecondsPerLine.sendUpdatesTo(lDoubleUpdateListener);
		mMarginTimeInMicroseconds.sendUpdatesTo(lDoubleUpdateListener);
		mEffectiveExposureInMicroseconds.sendUpdatesTo(lDoubleUpdateListener);
		mImageHeight.sendUpdatesTo(lDoubleUpdateListener);

		mLightSheetXInMicrons.sendUpdatesTo(lDoubleUpdateListener);
		mLightSheetYInMicrons.sendUpdatesTo(lDoubleUpdateListener);
		mLightSheetZInMicrons.sendUpdatesTo(lDoubleUpdateListener);
		mLightSheetBetaInDegrees.sendUpdatesTo(lDoubleUpdateListener);
		mLightSheetAlphaInDegrees.sendUpdatesTo(lDoubleUpdateListener);
		mLightSheetLengthInMicrons.sendUpdatesTo(lDoubleUpdateListener);

		mPatternOnOff.sendUpdatesTo(lDoubleUpdateListener);
		mPatternPeriod.sendUpdatesTo(lDoubleUpdateListener);
		mPatternPhaseIndex.sendUpdatesTo(lDoubleUpdateListener);
		mPatternOnLength.sendUpdatesTo(lDoubleUpdateListener);


	}

	public void addStavesToBeforeExposureMovement(Movement pBeforeExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();


		// Analog outputs before exposure:
		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																													+ ".index.x",
																																											1),
																			mLightSheetStaveBeforeExposureX);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																													+ ".index.y",
																																											2),
																			mLightSheetStaveBeforeExposureY);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																													+ ".index.y",
																																											3),
																			mLightSheetStaveBeforeExposureZ);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																													+ ".index.b",
																																											4),
																			mLightSheetStaveBeforeExposureB);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																													+ ".index.r",
																																											5),
																			mLightSheetStaveBeforeExposureR);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																													+ ".index.la",
																																											6),
																			mLightSheetStaveBeforeExposureLA);

		pBeforeExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																													+ ".index.t",
																																											8 + 7),
																			mLightSheetStaveBeforeExposureT);

	}

	public void addStavesToExposureMovement(Movement pExposureMovement)
	{
		final MachineConfiguration lCurrentMachineConfiguration = MachineConfiguration.getCurrentMachineConfiguration();


		// Analog outputs at exposure:

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																										+ ".index.x",
																																								1),
																mLightSheetStaveBeforeExposureX);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																										+ ".index.y",
																																								2),
																mLightSheetStaveBeforeExposureY);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																										+ ".index.y",
																																								3),
																mLightSheetStaveBeforeExposureZ);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																										+ ".index.b",
																																								4),
																mLightSheetStaveBeforeExposureB);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																										+ ".index.r",
																																								5),
																mLightSheetStaveBeforeExposureR);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																										+ ".index.la",
																																								6),
																mLightSheetStaveBeforeExposureLA);

		pExposureMovement.setStave(	lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																										+ ".index.t",
																																								8 + 7),
																mLightSheetStaveBeforeExposureT);

		for (int i = 0; i < mLaserOnOffArray.length; i++)
		{
			final int lLaserDigitalLineIndex = lCurrentMachineConfiguration.getIntegerProperty(	"device.lsm.lightsheet." + getDeviceName().toLowerCase()
																																															+ ".index.ld"
																																															+ i,
																																													8 + i);
			pExposureMovement.setStave(	lLaserDigitalLineIndex,
																	mLightSheetStaveLaserLD[i]);
		}

	}


	@Override
	public void ensureIsUpToDate()
	{
		if (!isUpToDate())
		{
			synchronized (this)
			{

				mNumberOfPhasesPerPlane.setValue(getNumberOfPhases());

				final int lEffectiveExposureInMicroseconds = (int) mEffectiveExposureInMicroseconds.getValue();

				final double lReadoutTimeInMicroseconds = mReadoutTimeInMicrosecondsPerLine.getValue() * mImageHeight.getValue()
																									/ 2;

				final double lExposureMovementTimeInMicroseconds = lEffectiveExposureInMicroseconds;

				final double lLineExposureTimeInMicroseconds = lReadoutTimeInMicroseconds + lEffectiveExposureInMicroseconds;
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

				final double lGalvoYLowValue = mLightSheetYConversion.value(lGalvoYOffset - lGalvoAmplitudeY);
				final double lGalvoYHighValue = mLightSheetYConversion.value(lGalvoYOffset + lGalvoAmplitudeY);

				final double lGalvoZLowValue = mLightSheetZConversion.value(lGalvoZOffset - lGalvoAmplitudeZ);
				final double lGalvoZHighValue = mLightSheetZConversion.value(lGalvoZOffset + lGalvoAmplitudeZ);

				mLightSheetStaveBeforeExposureY.mSyncStart = 0;
				mLightSheetStaveBeforeExposureY.mSyncStop = 1;
				mLightSheetStaveBeforeExposureY.mStartValue = lGalvoYHighValue;
				mLightSheetStaveBeforeExposureY.mStopValue = lGalvoYLowValue;

				mLightSheetStaveBeforeExposureZ.mSyncStart = 0;
				mLightSheetStaveBeforeExposureZ.mSyncStop = 1;
				mLightSheetStaveBeforeExposureZ.mStartValue = lGalvoZHighValue;
				mLightSheetStaveBeforeExposureZ.mStopValue = lGalvoZLowValue;

				mLightSheetStaveExposureY.mSyncStart = 0;
				mLightSheetStaveExposureY.mSyncStop = 1;
				mLightSheetStaveExposureY.mStartValue = lGalvoYLowValue;
				mLightSheetStaveExposureY.mStopValue = lGalvoYHighValue;
				mLightSheetStaveExposureY.mOutsideValue = lGalvoYHighValue;
				mLightSheetStaveExposureY.mNoJump = true;

				mLightSheetStaveExposureZ.mSyncStart = 0;
				mLightSheetStaveExposureZ.mSyncStop = 1;
				mLightSheetStaveExposureZ.mStartValue = lGalvoZLowValue;
				mLightSheetStaveExposureZ.mStopValue = lGalvoZHighValue;
				mLightSheetStaveExposureZ.mOutsideValue = lGalvoZHighValue;
				mLightSheetStaveExposureZ.mNoJump = true;

				mLightSheetStaveBeforeExposureX.mValue = mLightSheetXConversion.value(mLightSheetXInMicrons.getValue());
				mLightSheetStaveExposureX.mValue = mLightSheetXConversion.value(mLightSheetXInMicrons.getValue());

				mLightSheetStaveBeforeExposureB.mValue = mLightSheetBetaConversion.value(mLightSheetBetaInDegrees.getValue());
				mLightSheetStaveExposureB.mValue = mLightSheetBetaConversion.value(mLightSheetBetaInDegrees.getValue());

				
				final double lFocalLength = mFocalLengthInMicronsVariable.get();
				final double lLambdaInMicrons = mLambdaInMicronsVariable.get();
				final double lLightSheetRangeInMicrons = mLightSheetRangeInMicrons.getValue();
				
				final double lIrisDiameterInMm = GaussianBeamGeometry.getBeamIrisDiameter(lFocalLength,
																																							lLambdaInMicrons,
																																							lLightSheetRangeInMicrons);
				
				mLightSheetStaveBeforeExposureR.mValue = mLightSheetIrisDiameterConversion.value(lIrisDiameterInMm);
				mLightSheetStaveExposureR.mValue = mLightSheetStaveBeforeExposureR.mValue;

				final double lMarginTimeRelativeUnits = microsecondsToRelative(	lExposureMovementTimeInMicroseconds,
																																				lMarginTimeInMicroseconds);

				for (int i = 0; i < mLaserOnOffArray.length; i++)
				{
					final LaserTriggerBinaryPattern2Stave lLaserTriggerStave = mLightSheetStaveLaserLD[i];
					final BooleanVariable lLaserBooleanVariable = mLaserOnOffArray[i];

					lLaserTriggerStave.mEnabled = lLaserBooleanVariable.getBooleanValue();
					lLaserTriggerStave.mSyncStart = clamp01(lMarginTimeRelativeUnits);
					lLaserTriggerStave.mSyncStop = clamp01(1 - lMarginTimeRelativeUnits);
					lLaserTriggerStave.mEnablePattern = mPatternOnOff.getBooleanValue();
					lLaserTriggerStave.mPatternLineLength = mLightSheetLengthInMicrons.getValue();
					lLaserTriggerStave.mPatternPeriod = mPatternPeriod.getValue();
					lLaserTriggerStave.mPatternPhaseIndex = mPatternPhaseIndex.getValue();
					lLaserTriggerStave.mPatternOnLength = mPatternOnLength.getValue();
					lLaserTriggerStave.mPatternPhaseIncrement = mPatternPhaseIncrement.getValue();
				}

				mLightSheetStaveExposureLA.mValue = 1;
				mLightSheetStaveBeforeExposureLA.mValue = 1;

				// System.out.println("mMovement.requestUpdateAllStaves();");

				requestUpdateAllStaves();

				setUpToDate(true);
			}
		}
	}

	private void requestUpdateAllStaves()
	{
		mLightSheetStaveBeforeExposureX.requestUpdate();
		mLightSheetStaveExposureX.requestUpdate();

		mLightSheetStaveBeforeExposureY.requestUpdate();
		mLightSheetStaveExposureY.requestUpdate();

		mLightSheetStaveBeforeExposureZ.requestUpdate();
		mLightSheetStaveExposureZ.requestUpdate();

		mLightSheetStaveBeforeExposureB.requestUpdate();
		mLightSheetStaveExposureB.requestUpdate();

		mLightSheetStaveBeforeExposureT.requestUpdate();
		mLightSheetStaveExposureT.requestUpdate();

		mLightSheetStaveBeforeExposureLA.requestUpdate();
		mLightSheetStaveExposureLA.requestUpdate();
		
		for (int i = 0; i < mLaserOnOffArray.length; i++)
		{
			final LaserTriggerBinaryPattern2Stave lLaserTriggerStave = mLightSheetStaveLaserLD[i];
			lLaserTriggerStave.requestUpdate();
		}
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

	public int getEffectiveExposureInMicroseconds()
	{
		return (int) mEffectiveExposureInMicroseconds.getValue();
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

	public GalvoScannerStave getGalvoScannerStaveBeforeExposureZ()
	{
		return mLightSheetStaveBeforeExposureZ;
	}

	public GalvoScannerStave getGalvoScannerStaveBeforeExposureY()
	{
		return mLightSheetStaveBeforeExposureY;
	}

	public ConstantStave getIllumPifocStaveBeforeExposureX()
	{
		return mLightSheetStaveBeforeExposureX;
	}

	public GalvoScannerStave getGalvoScannerStaveExposureZ()
	{
		return mLightSheetStaveExposureZ;
	}

	public GalvoScannerStave getGalvoScannerStaveExposureY()
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
