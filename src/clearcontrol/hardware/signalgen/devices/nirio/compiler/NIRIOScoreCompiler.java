package clearcontrol.hardware.signalgen.devices.nirio.compiler;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import nirioj.direttore.Direttore;
import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.hardware.signalgen.movement.Movement;
import clearcontrol.hardware.signalgen.movement.MovementInterface;
import clearcontrol.hardware.signalgen.score.ScoreInterface;
import clearcontrol.hardware.signalgen.staves.ConstantStave;
import clearcontrol.hardware.signalgen.staves.IntervalStave;
import clearcontrol.hardware.signalgen.staves.StaveInterface;
import clearcontrol.hardware.signalgen.staves.ZeroStave;
import coremem.buffers.ContiguousBuffer;

public class NIRIOScoreCompiler	implements
																AsynchronousExecutorServiceAccess
{

	public static void compile(	NIRIOCompiledScore pNIRIOCompiledScore,
															ScoreInterface pScore)
	{

		ensureBuffersAreLargeEnough(pNIRIOCompiledScore, pScore);

		final ArrayList<MovementInterface> lMovements = pScore.getMovements();

		for (final MovementInterface lMovement : lMovements)
		{
			compileMovement(pNIRIOCompiledScore, lMovement);
		}

	}

	private static void ensureBuffersAreLargeEnough(NIRIOCompiledScore pNIRIOCompiledScore,
																									ScoreInterface pScore)
	{
		final int lNumberOfMovements = pScore.getMovements().size();

		pNIRIOCompiledScore.setNumberOfMovements(0);

		final int lDeltaTimeBufferLengthInBytes = 4 * lNumberOfMovements;

		if (pNIRIOCompiledScore.getDeltaTimeBuffer() == null || pNIRIOCompiledScore.getDeltaTimeBuffer()
																																								.getSizeInBytes() < lDeltaTimeBufferLengthInBytes)
		{
			pNIRIOCompiledScore.setDeltaTimeBuffer(ContiguousBuffer.allocate(lDeltaTimeBufferLengthInBytes));
		}
		pNIRIOCompiledScore.getDeltaTimeBuffer().rewind();

		final int lSyncBufferLengthInBytes = 4 * lNumberOfMovements;

		if (pNIRIOCompiledScore.getSyncBuffer() == null || pNIRIOCompiledScore.getSyncBuffer()
																																					.getSizeInBytes() < lSyncBufferLengthInBytes)
		{
			pNIRIOCompiledScore.setSyncBuffer(ContiguousBuffer.allocate(lSyncBufferLengthInBytes));
		}
		pNIRIOCompiledScore.getSyncBuffer().rewind();

		final int lNumberOfTimePointsBufferLengthInBytes = 4 * lNumberOfMovements;

		if (pNIRIOCompiledScore.getNumberOfTimePointsBuffer() == null || pNIRIOCompiledScore.getNumberOfTimePointsBuffer()
																																												.getSizeInBytes() < lNumberOfTimePointsBufferLengthInBytes)
		{
			pNIRIOCompiledScore.setNumberOfTimePointsBuffer(ContiguousBuffer.allocate(lNumberOfTimePointsBufferLengthInBytes));
		}
		pNIRIOCompiledScore.getNumberOfTimePointsBuffer().rewind();

		final long lMatricesBufferLengthInBytes = Movement.cDefaultNumberOfStavesPerMovement * lNumberOfMovements
																							* 2048
																							* 2;

		if (pNIRIOCompiledScore.getScoreBuffer() == null || pNIRIOCompiledScore.getScoreBuffer()
																																						.getSizeInBytes() < lMatricesBufferLengthInBytes)
		{
			pNIRIOCompiledScore.setScoreBuffer(ContiguousBuffer.allocate(lMatricesBufferLengthInBytes));
		}
		pNIRIOCompiledScore.getScoreBuffer().rewind();

	}

	private static void compileMovement(NIRIOCompiledScore pNIRIOCompiledScore,
																			MovementInterface pMovement)
	{
		final int pDeltaTimeInTicks = round(getDeltaTimeInNs(pMovement) / Direttore.cNanosecondsPerTicks);
		pNIRIOCompiledScore.getDeltaTimeBuffer()
												.writeInt(pDeltaTimeInTicks);

		final byte lSyncMode = getSyncMode(pMovement);
		final byte lSyncChannel = (byte) pMovement.getSyncChannel();
		final int lSync = twoBytesToShort(lSyncChannel, lSyncMode);
		pNIRIOCompiledScore.getSyncBuffer().writeInt(lSync);

		final long lNumberOfTimePoints = getNumberOfTimePoints(pMovement);
		pNIRIOCompiledScore.getNumberOfTimePointsBuffer()
												.writeInt(toIntExact(lNumberOfTimePoints));

		addMovementToBuffer(pNIRIOCompiledScore.getScoreBuffer(),
												pMovement);

		pNIRIOCompiledScore.setNumberOfMovements(pNIRIOCompiledScore.getNumberOfMovements() + 1);
	}

	private static void addMovementToBuffer(ContiguousBuffer pScoreBuffer,
																					MovementInterface pMovement)
	{
		final long lNumberOfTimePoints = getNumberOfTimePoints(pMovement);
		final int lNumberOfStaves = pMovement.getNumberOfStaves();

		pScoreBuffer.pushPosition();
		long lNumberOfShortsInMovement = lNumberOfTimePoints * lNumberOfStaves;
		pScoreBuffer.writeBytes(2 * lNumberOfShortsInMovement, (byte) 0);
		pScoreBuffer.popPosition();

		pScoreBuffer.pushPosition();
		for (int s = 0; s < lNumberOfStaves; s++)
		{

			pScoreBuffer.pushPosition();
			final StaveInterface lStave = pMovement.getStave(s);

			if (lStave instanceof ZeroStave)
			{
				addZeroStaveToBuffer(	pScoreBuffer,
															lNumberOfTimePoints,
															lNumberOfStaves);
			}
			else if (lStave instanceof ConstantStave)
			{
				final ConstantStave lConstantStave = (ConstantStave) lStave;
				addConstantStaveToBuffer(	pScoreBuffer,
																	lNumberOfTimePoints,
																	lNumberOfStaves,
																	lConstantStave.getConstantValue());
			}
			else if (lStave instanceof IntervalStave)
			{
				final IntervalStave lIntervalStave = (IntervalStave) lStave;
				addIntervalStaveToBuffer(	pScoreBuffer,
																	lNumberOfTimePoints,
																	lNumberOfStaves,
																	lIntervalStave);
			}
			else
			{
				addStaveToBuffer(	pScoreBuffer,
													lNumberOfTimePoints,
													lNumberOfStaves,
													lStave);
			}

			pScoreBuffer.popPosition();
			pScoreBuffer.skipShorts(1);
		}
		pScoreBuffer.popPosition();

		pScoreBuffer.skipShorts(lNumberOfShortsInMovement);

	}

	private static void addIntervalStaveToBuffer(	ContiguousBuffer pScoreBuffer,
																								long pNumberOfTimePoints,
																								int pNumberOfStaves,
																								IntervalStave pIntervalStave)
	{
		final float lSyncStart = pIntervalStave.getStart();
		final float lSyncStop = pIntervalStave.getStop();
		final short lInsideValue = getShortForFloat(pIntervalStave.getInsideValue());
		final short lOutsideValue = getShortForFloat(pIntervalStave.getOutsideValue());
		final boolean lEnabled = pIntervalStave.isEnabled();

		final float lInvNumberOfTimepoints = 1f / pNumberOfTimePoints;
		for (int t = 0; t < pNumberOfTimePoints; t++)
		{
			final float lNormalizedTime = t * lInvNumberOfTimepoints;

			if (!lEnabled)
			{
				pScoreBuffer.writeShort(lOutsideValue);
			}
			else if (t == pNumberOfTimePoints - 1 && lSyncStart == 0)
			{
				pScoreBuffer.writeShort(lOutsideValue);
			}
			else
			{
				if (lNormalizedTime < lSyncStart || lNormalizedTime > lSyncStop)
					pScoreBuffer.writeShort(lOutsideValue);
				else
					pScoreBuffer.writeShort(lInsideValue);
			}

			pScoreBuffer.skipShorts(pNumberOfStaves - 1);
		}

	}

	private static void addConstantStaveToBuffer(	ContiguousBuffer pScoreBuffer,
																								final long pNumberOfTimePoints,
																								final int pNumberOfStaves,
																								final float pFloatConstant)
	{
		final short lShortValue = getShortForFloat(pFloatConstant);
		for (int t = 0; t < pNumberOfTimePoints; t++)
		{
			pScoreBuffer.writeShort(lShortValue);
			pScoreBuffer.skipShorts(pNumberOfStaves - 1);
		}
	}

	private static void addZeroStaveToBuffer(	ContiguousBuffer pScoreBuffer,
																						final long pNumberOfTimePoints,
																						final int pNumberOfStaves)
	{
		// do nothing - already 0
	}

	private static void addStaveToBuffer(	ContiguousBuffer pScoreBuffer,
																				final long pNumberOfTimePoints,
																				final int pNumberOfStaves,
																				final StaveInterface pStave)
	{
		final float lInvNumberOfTimepoints = 1f / pNumberOfTimePoints;
		for (int t = 0; t < pNumberOfTimePoints; t++)
		{
			final float lNormalizedTime = t * lInvNumberOfTimepoints;
			final float lFloatValue = pStave.getValue(lNormalizedTime);
			final short lShortValue = getShortForFloat(lFloatValue);
			pScoreBuffer.writeShort(lShortValue);
			pScoreBuffer.skipShorts(pNumberOfStaves - 1);
		}
	}

	private static short getShortForFloat(final float lFloatValue)
	{
		return (short) round(clamp(lFloatValue) * Short.MAX_VALUE);
	}

	private static float clamp(float pFloatValue)
	{
		return min(max(pFloatValue, -1), 1);
	}

	private static short twoBytesToShort(	final byte pHigh,
																				final byte pLow)
	{
		final short lShort = (short) (pHigh << 8 | pLow & 0xFF);
		return lShort;
	}

	private static byte getSyncMode(MovementInterface pMovement)
	{
		return (byte) (pMovement.isSync()	? 0
																			: pMovement.isSyncOnRisingEdge() ? 1
																																			: 2);
	}

	private static double getNumberOfTimePointsDouble(MovementInterface pMovementInterface)
	{
		final long lMinDeltaTime = Direttore.cMinimumDeltaTimeInNanoseconds;
		final long lMaxNumberOfTimePointsPerMovement = Direttore.cMaxNumberOfTimePointsPerMovement;
		final long lDuration = pMovementInterface.getDuration(TimeUnit.NANOSECONDS);

		final double lNumberOfTimePoints = min(	lMaxNumberOfTimePointsPerMovement,
																						((double) lDuration) / lMinDeltaTime);

		return lNumberOfTimePoints;
	}

	public static long getNumberOfTimePoints(MovementInterface pMovementInterface)
	{
		final long lNumberOfTimePoints = round(getNumberOfTimePointsDouble(pMovementInterface));

		return lNumberOfTimePoints;
	}

	public static long getDeltaTimeInNs(MovementInterface pMovementInterface)
	{

		final long lDuration = pMovementInterface.getDuration(TimeUnit.NANOSECONDS);
		final double lNumberOfTimePoints = getNumberOfTimePointsDouble(pMovementInterface);

		final long lDeltaTime = round(lDuration / lNumberOfTimePoints);

		return lDeltaTime;
	}

}
