package clearcontrol.devices.signalgen.devices.nirio.compiler;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import nirioj.direttore.Direttore;
import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.devices.signalgen.measure.Measure;
import clearcontrol.devices.signalgen.measure.MeasureInterface;
import clearcontrol.devices.signalgen.score.ScoreInterface;
import clearcontrol.devices.signalgen.staves.ConstantStave;
import clearcontrol.devices.signalgen.staves.IntervalStave;
import clearcontrol.devices.signalgen.staves.StaveInterface;
import clearcontrol.devices.signalgen.staves.ZeroStave;
import coremem.buffers.ContiguousBuffer;

public class NIRIOScoreCompiler implements AsynchronousExecutorFeature
{

  public static void compile(NIRIOCompiledScore pNIRIOCompiledScore,
                             ScoreInterface pScore)
  {

    ensureBuffersAreLargeEnough(pNIRIOCompiledScore, pScore);

    final ArrayList<MeasureInterface> lMeasures =
                                                  pScore.getMeasures();

    for (final MeasureInterface lMeasure : lMeasures)
    {
      compileMeasure(pNIRIOCompiledScore, lMeasure);
    }

  }

  private static void ensureBuffersAreLargeEnough(NIRIOCompiledScore pNIRIOCompiledScore,
                                                  ScoreInterface pScore)
  {
    final int lNumberOfMeasures = pScore.getMeasures().size();

    pNIRIOCompiledScore.setNumberOfMeasures(0);

    final int lDeltaTimeBufferLengthInBytes = 4 * lNumberOfMeasures;

    if (pNIRIOCompiledScore.getDeltaTimeBuffer() == null
        || pNIRIOCompiledScore.getDeltaTimeBuffer()
                              .getSizeInBytes() < lDeltaTimeBufferLengthInBytes)
    {
      pNIRIOCompiledScore.setDeltaTimeBuffer(ContiguousBuffer.allocate(lDeltaTimeBufferLengthInBytes));
    }
    pNIRIOCompiledScore.getDeltaTimeBuffer().rewind();

    final int lSyncBufferLengthInBytes = 4 * lNumberOfMeasures;

    if (pNIRIOCompiledScore.getSyncBuffer() == null
        || pNIRIOCompiledScore.getSyncBuffer()
                              .getSizeInBytes() < lSyncBufferLengthInBytes)
    {
      pNIRIOCompiledScore.setSyncBuffer(ContiguousBuffer.allocate(lSyncBufferLengthInBytes));
    }
    pNIRIOCompiledScore.getSyncBuffer().rewind();

    final int lNumberOfTimePointsBufferLengthInBytes =
                                                     4 * lNumberOfMeasures;

    if (pNIRIOCompiledScore.getNumberOfTimePointsBuffer() == null
        || pNIRIOCompiledScore.getNumberOfTimePointsBuffer()
                              .getSizeInBytes() < lNumberOfTimePointsBufferLengthInBytes)
    {
      pNIRIOCompiledScore.setNumberOfTimePointsBuffer(ContiguousBuffer.allocate(lNumberOfTimePointsBufferLengthInBytes));
    }
    pNIRIOCompiledScore.getNumberOfTimePointsBuffer().rewind();

    final long lMatricesBufferLengthInBytes =
                                            Measure.cDefaultNumberOfStavesPerMeasure
                                              * lNumberOfMeasures
                                              * 2048
                                              * 2;

    if (pNIRIOCompiledScore.getScoreBuffer() == null
        || pNIRIOCompiledScore.getScoreBuffer()
                              .getSizeInBytes() < lMatricesBufferLengthInBytes)
    {
      pNIRIOCompiledScore.setScoreBuffer(ContiguousBuffer.allocate(lMatricesBufferLengthInBytes));
    }
    pNIRIOCompiledScore.getScoreBuffer().rewind();

  }

  private static void compileMeasure(NIRIOCompiledScore pNIRIOCompiledScore,
                                      MeasureInterface pMeasure)
  {
    final int pDeltaTimeInTicks =
                                round(getDeltaTimeInNs(pMeasure)
                                      / Direttore.cNanosecondsPerTicks);
    pNIRIOCompiledScore.getDeltaTimeBuffer()
                       .writeInt(pDeltaTimeInTicks);

    final byte lSyncMode = getSyncMode(pMeasure);
    final byte lSyncChannel = (byte) pMeasure.getSyncChannel();
    final int lSync = twoBytesToShort(lSyncChannel, lSyncMode);
    pNIRIOCompiledScore.getSyncBuffer().writeInt(lSync);

    final long lNumberOfTimePoints = getNumberOfTimePoints(pMeasure);
    pNIRIOCompiledScore.getNumberOfTimePointsBuffer()
                       .writeInt(toIntExact(lNumberOfTimePoints));

    addMeasureToBuffer(pNIRIOCompiledScore.getScoreBuffer(),
                        pMeasure);

    pNIRIOCompiledScore.setNumberOfMeasures(pNIRIOCompiledScore.getNumberOfMeasures()
                                             + 1);
  }

  private static void addMeasureToBuffer(ContiguousBuffer pScoreBuffer,
                                          MeasureInterface pMeasure)
  {
    final long lNumberOfTimePoints = getNumberOfTimePoints(pMeasure);
    final int lNumberOfStaves = pMeasure.getNumberOfStaves();

    pScoreBuffer.pushPosition();
    long lNumberOfShortsInMeasure = lNumberOfTimePoints
                                     * lNumberOfStaves;
    pScoreBuffer.writeBytes(2 * lNumberOfShortsInMeasure, (byte) 0);
    pScoreBuffer.popPosition();

    pScoreBuffer.pushPosition();
    for (int s = 0; s < lNumberOfStaves; s++)
    {

      pScoreBuffer.pushPosition();
      final StaveInterface lStave = pMeasure.getStave(s);

      if (lStave instanceof ZeroStave)
      {
        addZeroStaveToBuffer(pScoreBuffer,
                             lNumberOfTimePoints,
                             lNumberOfStaves);
      }
      else if (lStave instanceof ConstantStave)
      {
        final ConstantStave lConstantStave = (ConstantStave) lStave;
        addConstantStaveToBuffer(pScoreBuffer,
                                 lNumberOfTimePoints,
                                 lNumberOfStaves,
                                 lConstantStave.getConstantValue());
      }
      else if (lStave instanceof IntervalStave)
      {
        final IntervalStave lIntervalStave = (IntervalStave) lStave;
        addIntervalStaveToBuffer(pScoreBuffer,
                                 lNumberOfTimePoints,
                                 lNumberOfStaves,
                                 lIntervalStave);
      } /**/
      else
      {
        addStaveToBuffer(pScoreBuffer,
                         lNumberOfTimePoints,
                         lNumberOfStaves,
                         lStave);
      }

      pScoreBuffer.popPosition();
      pScoreBuffer.skipShorts(1);
    }
    pScoreBuffer.popPosition();

    pScoreBuffer.skipShorts(lNumberOfShortsInMeasure);

  }

  private static void addIntervalStaveToBuffer(ContiguousBuffer pScoreBuffer,
                                               long pNumberOfTimePoints,
                                               int pNumberOfStaves,
                                               IntervalStave pIntervalStave)
  {
    final float lSyncStart = pIntervalStave.getStart();
    final float lSyncStop = pIntervalStave.getStop();
    final short lInsideValue =
                             getShortForFloat(pIntervalStave.getInsideValue());
    final short lOutsideValue =
                              getShortForFloat(pIntervalStave.getOutsideValue());
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
        if (lNormalizedTime < lSyncStart
            || lNormalizedTime > lSyncStop)
          pScoreBuffer.writeShort(lOutsideValue);
        else
          pScoreBuffer.writeShort(lInsideValue);
      }

      pScoreBuffer.skipShorts(pNumberOfStaves - 1);
    }

  }

  private static void addConstantStaveToBuffer(ContiguousBuffer pScoreBuffer,
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

  private static void addZeroStaveToBuffer(ContiguousBuffer pScoreBuffer,
                                           final long pNumberOfTimePoints,
                                           final int pNumberOfStaves)
  {
    // do nothing - already 0
  }

  private static void addStaveToBuffer(ContiguousBuffer pScoreBuffer,
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

  private static short twoBytesToShort(final byte pHigh,
                                       final byte pLow)
  {
    final short lShort = (short) (pHigh << 8 | pLow & 0xFF);
    return lShort;
  }

  private static byte getSyncMode(MeasureInterface pMeasure)
  {
    return (byte) (pMeasure.isSync() ? 0
                                      : pMeasure.isSyncOnRisingEdge() ? 1
                                                                       : 2);
  }

  private static double getNumberOfTimePointsDouble(MeasureInterface pMeasureInterface)
  {
    final long lMinDeltaTime =
                             Direttore.cMinimumDeltaTimeInNanoseconds;
    final long lMaxNumberOfTimePointsPerMeasure =
                                                 Direttore.cMaxNumberOfTimePointsPerMeasure;
    final long lDuration =
                         pMeasureInterface.getDuration(TimeUnit.NANOSECONDS);

    final double lNumberOfTimePoints =
                                     min(lMaxNumberOfTimePointsPerMeasure,
                                         ((double) lDuration) / lMinDeltaTime);

    return lNumberOfTimePoints;
  }

  public static long getNumberOfTimePoints(MeasureInterface pMeasureInterface)
  {
    final long lNumberOfTimePoints =
                                   round(getNumberOfTimePointsDouble(pMeasureInterface));

    return lNumberOfTimePoints;
  }

  public static long getDeltaTimeInNs(MeasureInterface pMeasureInterface)
  {

    final long lDuration =
                         pMeasureInterface.getDuration(TimeUnit.NANOSECONDS);
    final double lNumberOfTimePoints =
                                     getNumberOfTimePointsDouble(pMeasureInterface);

    final long lDeltaTime = round(lDuration / lNumberOfTimePoints);

    return lDeltaTime;
  }

}
