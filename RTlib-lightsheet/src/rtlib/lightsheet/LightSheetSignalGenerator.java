package rtlib.lightsheet;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.analysis.UnivariateFunction;

import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.device.SignalStartableDevice;
import rtlib.core.device.queue.QueueProvider;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleInputVariableInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.core.variable.persistence.ObjectVariableAsFile;
import rtlib.symphony.devices.SignalGeneratorInterface;
import rtlib.symphony.interfaces.MovementInterface;
import rtlib.symphony.movement.Movement;
import rtlib.symphony.score.CompiledScore;
import rtlib.symphony.score.Score;
import rtlib.symphony.staves.ConstantStave;
import rtlib.symphony.staves.GalvoScannerStave;
import rtlib.symphony.staves.LaserTriggerBinaryPattern2Stave;

public class LightSheetSignalGenerator<M extends UnivariateFunction>	extends
																																			SignalStartableDevice	implements
																																														LightSheetSignalGeneratorInterface<M>,
																																														AsynchronousExecutorServiceAccess
{

	private static final int cNumberOfLaserDigitalControls = 6;


	private final SignalGeneratorInterface mSignalGenerator;
	private final Score mScore;
	private final CompiledScore mCompiledScore;
	private volatile int mEnqueuedStateCounter = 0;

	public DoubleVariable mEffectiveExposureInMicroseconds = new DoubleVariable("EffectiveExposureInMicroseconds",
																																							5000);
	public DoubleVariable mImageHeight = new DoubleVariable("NumberOfLinesTimesTwo",
																													2 * 1024);
	public DoubleVariable mReadoutTimeInMicrosecondsPerLine = new DoubleVariable(	"ReadoutTimeInMicrosecondsPerLine",
																																								9.74);
	public DoubleVariable mMarginTimeInMicroseconds = new DoubleVariable(	"MarginTimeInMicroseconds",
																																				100);

	public DoubleVariable mLightSheetYInMicrons = new DoubleVariable(	"LightSheetYInMicrons",
																																		0);
	public DoubleVariable mLightSheetZInMicrons = new DoubleVariable(	"LightSheetZInMicrons",
																																		0);
	public DoubleVariable mLightSheetAlphaInDegrees = new DoubleVariable(	"LightSheetAlphaInDegrees",
																																				0);
	public DoubleVariable mLightSheetLengthInMicrons = new DoubleVariable("LightSheetLengthInMicrons",
																																				100);
	public DoubleVariable mMicronsToNormGalvoUnit = new DoubleVariable(	"MicronsToNormGalvoUnit",
																																			-0.003026);

	public DoubleVariable mFocusZ = new DoubleVariable("FocusZ", 50);
	public DoubleVariable mStageY = new DoubleVariable("StageY", 125);

	public DoubleVariable mLineExposureInMicroseconds = new DoubleVariable(	"LineExposureInMicroseconds",
																																					10);

	public final BooleanVariable[] mLaserOnOffArray = new BooleanVariable[cNumberOfLaserDigitalControls];
	public final BooleanVariable mPatternOnOff = new BooleanVariable(	"PatternOnOff",
																																		false);
	public final DoubleVariable mPatternPeriod = new DoubleVariable("PatternPeriod",
																																	2);
	public final DoubleVariable mPatternPhaseIndex = new DoubleVariable("PatternPhaseIndex",
																																			0);
	public final DoubleVariable mPatternOnLength = new DoubleVariable("PatternOnLength",
																																		1);
	public final DoubleVariable mPatternPhaseIncrement = new DoubleVariable("PatternPhaseIncrement",
																																					1);

	public final DoubleVariable mNumberOfPhasesPerPlane = new DoubleVariable(	"NumberOfPhases",
																																						2);

	public final BooleanVariable mLockLightSheetToPifoc = new BooleanVariable("LockLightSheetToPifoc",
																																						false);
	public final ObjectVariable<M> mPifoc2LightSheetModel;

	private Movement mBeforeExposureMovement;
	private Movement mExposureMovement;

	private GalvoScannerStave mGalvoScannerStaveBeforeExposureZ,
			mGalvoScannerStaveBeforeExposureY, mGalvoScannerStaveExposureZ,
			mGalvoScannerStaveExposureY;
	private ConstantStave mCameraTriggerStaveBeforeExposure,
			mCameraTriggerStaveExposure;
	private final LaserTriggerBinaryPattern2Stave[] mLaserTriggerStave = new LaserTriggerBinaryPattern2Stave[cNumberOfLaserDigitalControls];
	private final ConstantStave mLaserAnalogModulationBeforeExposure,
			mLaserAnalogModulationExposure, mFocusStaveBeforeExposureZ,
			mFocusStaveExposureZ;

	private volatile boolean mIsUpToDate = false;

	private volatile QueueProvider<LightSheetSignalGenerator<M>> mQueueProvider;
	private volatile Future<Boolean> mFuture;
	private volatile boolean mIsPlaying = false;

	public LightSheetSignalGenerator(	String pName,
																		SignalGeneratorInterface pSignalGenerator,
																		M pPifoc2LightSheetModel,
																		final double pReadoutTimeInMicrosecondsPerLine,
																		final int pNumberOfLines)
	{
		super(pName);

		mPifoc2LightSheetModel = new ObjectVariableAsFile<M>(pName + "Pifoc2LightSheetModel");

		mSignalGenerator = pSignalGenerator;

		final DoubleVariable lDoubleUpdateListener = new DoubleVariable("UpdateListener",
																																		0)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				mIsUpToDate = false;
				// System.out.println("UPDATING!");
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		@SuppressWarnings("rawtypes")
		final ObjectVariable<M> lReferenceUpdateListener = new ObjectVariable<M>("ReferenceUpdateListener")
		{
			@SuppressWarnings("unchecked")
			@Override
			public M setEventHook(final M pOldReference,
														final M pNewReference)
			{
				mIsUpToDate = false;
				// System.out.println("UPDATING!");
				return super.setEventHook(pOldReference, pNewReference);
			}
		};

		mReadoutTimeInMicrosecondsPerLine.setValue(pReadoutTimeInMicrosecondsPerLine);
		mImageHeight.setValue(pNumberOfLines);

		mScore = new Score(LightSheetSignalGenerator.class.getSimpleName() + ".score");
		mCompiledScore = new CompiledScore(LightSheetSignalGenerator.class.getSimpleName() + ".compiledscore");

		mFocusStaveBeforeExposureZ = new ConstantStave(	"Focus.beforeexp.z",
																										0);
		mFocusStaveExposureZ = new ConstantStave("Focus.exposure.z", 0);
		mLaserAnalogModulationBeforeExposure = new ConstantStave(	"Laser.beforeexp.am",
																															0);
		mLaserAnalogModulationExposure = new ConstantStave(	"Laser.exposure.am",
																												0);

		prepareBeforeExposureMovement(lDoubleUpdateListener);
		prepareExposureMovement(lDoubleUpdateListener);

		mScore.addMovement(mBeforeExposureMovement);
		mScore.addMovement(mExposureMovement);

		mReadoutTimeInMicrosecondsPerLine.sendUpdatesTo(lDoubleUpdateListener);
		mMarginTimeInMicroseconds.sendUpdatesTo(lDoubleUpdateListener);
		mEffectiveExposureInMicroseconds.sendUpdatesTo(lDoubleUpdateListener);
		mImageHeight.sendUpdatesTo(lDoubleUpdateListener);

		mLightSheetYInMicrons.sendUpdatesTo(lDoubleUpdateListener);
		mLightSheetZInMicrons.sendUpdatesTo(lDoubleUpdateListener);
		mLightSheetAlphaInDegrees.sendUpdatesTo(lDoubleUpdateListener);
		mLightSheetLengthInMicrons.sendUpdatesTo(lDoubleUpdateListener);
		mMicronsToNormGalvoUnit.sendUpdatesTo(lDoubleUpdateListener);

		mFocusZ.sendUpdatesTo(lDoubleUpdateListener);
		mStageY.sendUpdatesTo(lDoubleUpdateListener);

		mPifoc2LightSheetModel.set(pPifoc2LightSheetModel);
		mLockLightSheetToPifoc.sendUpdatesTo(lDoubleUpdateListener);
		mPifoc2LightSheetModel.sendUpdatesTo(lReferenceUpdateListener);

		mPatternOnOff.sendUpdatesTo(lDoubleUpdateListener);
		mPatternPeriod.sendUpdatesTo(lDoubleUpdateListener);
		mPatternPhaseIndex.sendUpdatesTo(lDoubleUpdateListener);
		mPatternOnLength.sendUpdatesTo(lDoubleUpdateListener);

	}

	private void prepareBeforeExposureMovement(final DoubleInputVariableInterface lUpdateListener)
	{
		mBeforeExposureMovement = new Movement("BeforeExposure");

		mGalvoScannerStaveBeforeExposureZ = new GalvoScannerStave("Galvo.beforexp.z");
		mGalvoScannerStaveBeforeExposureY = new GalvoScannerStave("Galvo.beforexp.y");
		mCameraTriggerStaveBeforeExposure = new ConstantStave("Camera.beforexp.trig",
																													1);

		mBeforeExposureMovement.setStave(	0,
																			mGalvoScannerStaveBeforeExposureZ);
		mBeforeExposureMovement.setStave(	1,
																			mGalvoScannerStaveBeforeExposureY);
		mBeforeExposureMovement.setStave(2, mFocusStaveBeforeExposureZ);
		mBeforeExposureMovement.setStave(	3,
																			mLaserAnalogModulationBeforeExposure);

		mBeforeExposureMovement.setStave(	6,
																			new ConstantStave("laser561",
																												32628 / 10.0));
		mBeforeExposureMovement.setStave(	7,
																			new ConstantStave("laser594",
																												32628 / 10.0));

		mBeforeExposureMovement.setStave(	8 + 7,
																			mCameraTriggerStaveBeforeExposure);

	}

	private void prepareExposureMovement(final DoubleVariable lUpdateListener)
	{
		mExposureMovement = new Movement("Exposure");

		mGalvoScannerStaveExposureZ = new GalvoScannerStave("Galvo.exposure.z");
		mGalvoScannerStaveExposureY = new GalvoScannerStave("Galvo.exposure.y");
		mCameraTriggerStaveExposure = new ConstantStave("Camera.exposure.trig",
																										0);

		mExposureMovement.setStave(0, mGalvoScannerStaveExposureZ);
		mExposureMovement.setStave(1, mGalvoScannerStaveExposureY);
		mExposureMovement.setStave(2, mFocusStaveExposureZ);
		mExposureMovement.setStave(3, mLaserAnalogModulationExposure);

		mExposureMovement.setStave(6, new ConstantStave("laser561",
																										32628 / 10.0));
		mExposureMovement.setStave(7, new ConstantStave("laser594",
																										32628 / 10.0));

		mExposureMovement.setStave(8 + 7, mCameraTriggerStaveExposure);

		for (int i = 0; i < mLaserOnOffArray.length; i++)
		{
			final String lLaserName = "Laser" + i + ".exposure.trig";
			mLaserTriggerStave[i] = new LaserTriggerBinaryPattern2Stave(lLaserName);
			mExposureMovement.setStave(8 + i, mLaserTriggerStave[i]);
			mLaserOnOffArray[i] = new BooleanVariable(lLaserName, false);
			mLaserOnOffArray[i].sendUpdatesTo(lUpdateListener);
		}
	}

	private void ensureIsUpToDate()
	{
		if (!mIsUpToDate)
		{
			synchronized (this)
			{

				mNumberOfPhasesPerPlane.setValue(getNumberOfPhases());

				final int lEffectiveExposureInMicroseconds = (int) mEffectiveExposureInMicroseconds.getValue();

				final double lReadoutTimeInMicroseconds = mReadoutTimeInMicrosecondsPerLine.getValue() * mImageHeight.getValue()
																									/ 2;
				// System.out.println("lReadoutTimeInMicroseconds=" +
				// lReadoutTimeInMicroseconds);
				final double lTemporalGranularityInMicroseconds = mSignalGenerator.getTemporalGranularityInMicroseconds();

				final double lExposureMovementTimeInMicroseconds = lEffectiveExposureInMicroseconds;
				// System.out.println("lExposureMovementTimeInMicroseconds=" +
				// lExposureMovementTimeInMicroseconds);
				mExposureMovement.setTotalDurationAndGranularityInMicroseconds(	lExposureMovementTimeInMicroseconds,
																																				lTemporalGranularityInMicroseconds,
																																				2048);

				mBeforeExposureMovement.setTotalDurationAndGranularityInMicroseconds(	lReadoutTimeInMicroseconds,
																																							lTemporalGranularityInMicroseconds,
																																							2048);

				final double lLineExposureTimeInMicroseconds = lReadoutTimeInMicroseconds + lEffectiveExposureInMicroseconds;
				mLineExposureInMicroseconds.setValue(lLineExposureTimeInMicroseconds);

				final double lMarginTimeInMicroseconds = mMarginTimeInMicroseconds.getValue();

				final double lGalvoAmplitude = mMicronsToNormGalvoUnit.getValue() * mLightSheetLengthInMicrons.getValue();
				final double lGalvoAngle = Math.toRadians(mLightSheetAlphaInDegrees.getValue());

				final boolean lLockLightSheetToPifoc = mLockLightSheetToPifoc.getBooleanValue();

				final UnivariateFunction lPifoc2LightSheetModel = mPifoc2LightSheetModel.getReference();

				final double lLightSheetZInMicronsLockedToPifoc = lPifoc2LightSheetModel == null ? mLightSheetZInMicrons.getValue()
																																												: lPifoc2LightSheetModel.value(mFocusZ.getValue());

				final boolean lIsLocking = mPifoc2LightSheetModel.isNotNull() && lLockLightSheetToPifoc;

				if (lIsLocking)
				{
					mLightSheetZInMicrons.setValue(lLightSheetZInMicronsLockedToPifoc);
				}
				final double lLightSheetZInMicrons = mLightSheetZInMicrons.getValue();

				final double lGalvoYOffsetInNormalizedUnitsBeforeRotation = mMicronsToNormGalvoUnit.getValue() * mLightSheetYInMicrons.getValue();
				final double lGalvoYOffsetInNormalizedUnitsToY = lGalvoYOffsetInNormalizedUnitsBeforeRotation * Math.cos(lGalvoAngle);
				final double lGalvoYOffsetInNormalizedUnitsToZ = -lGalvoYOffsetInNormalizedUnitsBeforeRotation * Math.sin(lGalvoAngle);

				final double lGalvoZOffsetInNormalizedUnitsBeforeRotation = mMicronsToNormGalvoUnit.getValue() * lLightSheetZInMicrons;
				final double lGalvoZffsetInNormalizedUnitsToY = lGalvoZOffsetInNormalizedUnitsBeforeRotation * Math.sin(lGalvoAngle);
				final double lGalvoZOffsetInNormalizedUnitsToZ = lGalvoZOffsetInNormalizedUnitsBeforeRotation * Math.cos(lGalvoAngle);

				final double lGalvoYOffsetInNormalizedUnits = lGalvoYOffsetInNormalizedUnitsToY + lGalvoZffsetInNormalizedUnitsToY;
				final double lGalvoZOffsetInNormalizedUnits = lGalvoYOffsetInNormalizedUnitsToZ + lGalvoZOffsetInNormalizedUnitsToZ;

				final double lGalvoAmplitudeZInNormalizedUnits = lGalvoAmplitude * Math.sin(lGalvoAngle);
				final double lGalvoAmplitudeYInNormalizedUnits = lGalvoAmplitude * Math.cos(lGalvoAngle);

				final double lGalvoZLowValue = lGalvoZOffsetInNormalizedUnits - lGalvoAmplitudeZInNormalizedUnits;
				final double lGalvoZHighValue = lGalvoZOffsetInNormalizedUnits + lGalvoAmplitudeZInNormalizedUnits;

				final double lGalvoYLowValue = lGalvoYOffsetInNormalizedUnits - lGalvoAmplitudeYInNormalizedUnits;
				final double lGalvoYHighValue = lGalvoYOffsetInNormalizedUnits + lGalvoAmplitudeYInNormalizedUnits;

				mGalvoScannerStaveBeforeExposureZ.mSyncStart = 0;
				mGalvoScannerStaveBeforeExposureZ.mSyncStop = 1;
				mGalvoScannerStaveBeforeExposureZ.mStartValue = lGalvoZHighValue;
				mGalvoScannerStaveBeforeExposureZ.mStopValue = lGalvoZLowValue;

				mGalvoScannerStaveBeforeExposureY.mSyncStart = 0;
				mGalvoScannerStaveBeforeExposureY.mSyncStop = 1;
				mGalvoScannerStaveBeforeExposureY.mStartValue = lGalvoYHighValue;
				mGalvoScannerStaveBeforeExposureY.mStopValue = lGalvoYLowValue;

				mGalvoScannerStaveExposureZ.mSyncStart = 0;
				mGalvoScannerStaveExposureZ.mSyncStop = 1;
				mGalvoScannerStaveExposureZ.mStartValue = lGalvoZLowValue;
				mGalvoScannerStaveExposureZ.mStopValue = lGalvoZHighValue;
				mGalvoScannerStaveExposureZ.mOutsideValue = lGalvoZHighValue;
				mGalvoScannerStaveExposureZ.mNoJump = true;

				mGalvoScannerStaveExposureY.mSyncStart = 0;
				mGalvoScannerStaveExposureY.mSyncStop = 1;
				mGalvoScannerStaveExposureY.mStartValue = lGalvoYLowValue;
				mGalvoScannerStaveExposureY.mStopValue = lGalvoYHighValue;
				mGalvoScannerStaveExposureY.mOutsideValue = lGalvoYHighValue;
				mGalvoScannerStaveExposureY.mNoJump = true;

				final double lMarginTimeRelativeUnits = microsecondsToRelative(	lExposureMovementTimeInMicroseconds,
																																				lMarginTimeInMicroseconds);

				for (int i = 0; i < mLaserOnOffArray.length; i++)
				{
					final LaserTriggerBinaryPattern2Stave lLaserTriggerStave = mLaserTriggerStave[i];
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

				mFocusStaveExposureZ.mValue = clamp01(mFocusZ.getValue() / 100);
				mFocusStaveBeforeExposureZ.mValue = 0.0 + mFocusStaveExposureZ.mValue;

				mLaserAnalogModulationExposure.mValue = 1;
				mLaserAnalogModulationBeforeExposure.mValue = 1;

				// System.out.println("mMovement.requestUpdateAllStaves();");

				mBeforeExposureMovement.requestUpdateAllStaves();
				mExposureMovement.requestUpdateAllStaves();

				mIsUpToDate = true;
			}
		}
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

	@Override
	public void requestUpdate()
	{
		mIsUpToDate = false;
	}

	public Score getScore()
	{
		return mScore;
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

		/*System.out.println("lIsPatterned=" + lIsPatterned);
		System.out.println("lPatternPeriod=" + lPatternPeriod);
		System.out.println("lPatternPhaseIncrement=" + lPatternPhaseIncrement);
		System.out.println("lNumberOfPhases=" + lNumberOfPhases);/**/

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
	public void ensureQueueIsUpToDate()
	{
		if (!mIsUpToDate)
		{
			ensureIsUpToDate();
			if (mQueueProvider != null)
			{
				mQueueProvider.buildQueue(this);
			}
		}
	}

	@Override
	public void addCurrentStateToQueue()
	{
		mEnqueuedStateCounter++;
		addCurrentStateToQueueNotCounting();
	}

	@Override
	public void addCurrentStateToQueueNotCounting()
	{
		ensureIsUpToDate();
		mCompiledScore.addMovement(mBeforeExposureMovement);
		mCompiledScore.addMovement(mExposureMovement);
	}

	@Override
	public void clearQueue()
	{
		mEnqueuedStateCounter = 0;
		mCompiledScore.clear();
	}

	@Override
	public final void finalizeQueueFor3DStackAcquisition()
	{
		// TODO: this is a workaround for a bug when acquiring a sequence of images
		// from the camera: can't get n images back only get
		// n-1. Seems to be the normal behaviour of the camera...
		addCurrentStateToQueue();
		mEnqueuedStateCounter--;
	}

	@Override
	public QueueProvider<LightSheetSignalGenerator<M>> getQueueProviderFor2DContinuousAcquisition()
	{
		final QueueProvider<LightSheetSignalGenerator<M>> lQueueProvider = new QueueProvider<LightSheetSignalGenerator<M>>()
		{
			@Override
			public void buildQueue(final LightSheetSignalGenerator<M> pFPGALightSheetSignalGenerator)
			{
				prepareQueueFor2DContinuousAcquisition();
			}
		};
		return lQueueProvider;
	}

	public void prepareQueueFor2DContinuousAcquisition()
	{
		clearQueue();
		addCurrentStateToQueue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setQueueProvider(final QueueProvider<?> pQueueProvider)
	{
		mQueueProvider = (QueueProvider<LightSheetSignalGenerator<M>>) pQueueProvider;
	}

	@Override
	public int getQueueLength()
	{
		return mEnqueuedStateCounter;
	}

	@Override
	public Future<Boolean> playQueue()
	{
		final Callable<Boolean> lCall = () -> {
			final Thread lCurrentThread = Thread.currentThread();
			final int lCurrentThreadPriority = lCurrentThread.getPriority();
			lCurrentThread.setPriority(Thread.MAX_PRIORITY);
			mIsPlaying = true;
			final boolean lPlayed = mSignalGenerator.play(mCompiledScore);
			mIsPlaying = false;
			lCurrentThread.setPriority(lCurrentThreadPriority);
			return lPlayed;
		};
		mFuture = executeAsynchronously(lCall);
		return mFuture;
	}

	@Override
	public long estimatePlayTimeInMilliseconds()
	{
		long lDurationInMilliseconds = 0;
		for (final MovementInterface lMovement : mScore.getMovements())
		{
			lDurationInMilliseconds += lMovement.getDurationInMilliseconds();
		}

		lDurationInMilliseconds *= mCompiledScore.getNumberOfMovements();
		return TimeUnit.SECONDS.convert(lDurationInMilliseconds,
																		TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean open()
	{
		System.out.println(this.getClass().getSimpleName() + ": open()");
		return mSignalGenerator.open();
	}

	@Override
	public boolean start()
	{
		System.out.println(this.getClass().getSimpleName() + ": start()");
		return mSignalGenerator.start();
	}

	public boolean resume()
	{
		System.out.println(this.getClass().getSimpleName() + ": resume()");
		return true;
	}

	@Override
	public boolean stop()
	{
		System.out.println(this.getClass().getSimpleName() + ": stop()");
		return mSignalGenerator.stop();
	}

	@Override
	public boolean close()
	{
		return mSignalGenerator.close();
	}

	@Override
	public boolean isPlaying()
	{
		return mIsPlaying;
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
	public DoubleVariable getFocusZVariable()
	{
		return mFocusZ;
	}

	@Override
	public DoubleVariable getLightSheetLengthInMicronsVariable()
	{
		return mLightSheetLengthInMicrons;
	}

	@Override
	public DoubleVariable getStageYVariable()
	{
		return mStageY;
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

	@Override
	public BooleanVariable getLockLightSheetToPifocVariable()
	{
		return mLockLightSheetToPifoc;
	}

	@Override
	public DoubleVariable getMicronsToNormGalvoUnitVariable()
	{
		return mMicronsToNormGalvoUnit;
	}

	@Override
	public ObjectVariable<M> getPifoc2LightSheetModelVariable()
	{
		return mPifoc2LightSheetModel;
	}

}
