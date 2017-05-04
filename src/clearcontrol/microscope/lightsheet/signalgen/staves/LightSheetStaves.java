package clearcontrol.microscope.lightsheet.signalgen.staves;

import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.configuration.MachineConfiguration;
import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.devices.signalgen.movement.Movement;
import clearcontrol.devices.signalgen.staves.BezierStave;
import clearcontrol.devices.signalgen.staves.ConstantStave;
import clearcontrol.devices.signalgen.staves.EdgeStave;
import clearcontrol.devices.signalgen.staves.IntervalStave;
import clearcontrol.devices.signalgen.staves.RampContinuousStave;
import clearcontrol.devices.signalgen.staves.RampSteppingStave;
import clearcontrol.devices.signalgen.staves.StaveInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetQueue;
import clearcontrol.microscope.lightsheet.component.lightsheet.si.StructuredIlluminationPatternInterface;

/**
 *
 *
 * @author royer
 */
@SuppressWarnings("javadoc")
public class LightSheetStaves implements LoggingInterface
{

  private LightSheetQueue mLightSheetQueue;

  private final BoundedVariable<Double> mLineExposureInMicrosecondsVariable =
                                                                            new BoundedVariable<Double>("LineExposureInMicroseconds",
                                                                                                        10.0);

  private BezierStave mBeforeExposureYStave, mBeforeExposureZStave;

  private RampSteppingStave mExposureYStave, mExposureZStave;

  private RampContinuousStave mFinalYStave;

  private ConstantStave mBeforeExposureXStave, mExposureXStave,
      mBeforeExposureBStave, mExposureBStave, mBeforeExposureWStave,
      mExposureWStave, mBeforeExposureLAStave, mExposureLAStave;
  private IntervalStave mNonSIIluminationLaserTriggerStave;

  private EdgeStave mBeforeExposureTStave, mExposureTStave,
      mFinalTStave;

  private int mStaveXIndex, mStaveYIndex, mStaveZIndex, mStaveBIndex,
      mStaveWIndex, mStaveLAIndex, mStaveTIndex;

  public LightSheetStaves(LightSheetQueue pLightSheetQueue)
  {
    super();
    mLightSheetQueue = pLightSheetQueue;

    mBeforeExposureLAStave =
                           new ConstantStave("laser.beforeexp.am", 0);
    mExposureLAStave = new ConstantStave("laser.exposure.am", 0);

    mBeforeExposureXStave = new ConstantStave("lightsheet.x.be", 0);
    mBeforeExposureYStave = new BezierStave("lightsheet.y.be", 0);
    mBeforeExposureZStave = new BezierStave("lightsheet.z.be", 0);
    mBeforeExposureBStave = new ConstantStave("lightsheet.b.be", 0);
    mBeforeExposureWStave = new ConstantStave("lightsheet.r.be", 0);
    mBeforeExposureTStave = new EdgeStave("trigger.out.be", 1, 1, 0);

    mExposureXStave = new ConstantStave("lightsheet.x.e", 0);
    mExposureYStave = new RampSteppingStave("lightsheet.y.e");
    mExposureZStave = new RampSteppingStave("lightsheet.z.e");
    mExposureBStave = new ConstantStave("lightsheet.b.e", 0);
    mExposureWStave = new ConstantStave("lightsheet.r.e", 0);
    mExposureTStave = new EdgeStave("trigger.out.e", 1, 0, 0);

    mFinalTStave = new EdgeStave("trigger.out.f", 0.5f, 1, 0);
    mFinalYStave = new RampContinuousStave("lightsheet.y.f");

    mNonSIIluminationLaserTriggerStave =
                                       new IntervalStave("trigger.out",
                                                         0,
                                                         1,
                                                         1,
                                                         0);

    final MachineConfiguration lCurrentMachineConfiguration =
                                                            MachineConfiguration.getCurrentMachineConfiguration();

    mStaveXIndex =
                 lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet."
                                                                 + getLightSheet().getName()
                                                                 + ".x.index",
                                                                 2);

    mStaveYIndex =
                 lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet."
                                                                 + getLightSheet().getName()
                                                                 + ".y.index",
                                                                 3);

    mStaveZIndex =
                 lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet."
                                                                 + getLightSheet().getName()
                                                                 + ".z.index",
                                                                 4);

    mStaveBIndex =
                 lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet."
                                                                 + getLightSheet().getName()
                                                                 + ".b.index",
                                                                 5);

    mStaveWIndex =
                 lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet."
                                                                 + getLightSheet().getName()
                                                                 + ".w.index",
                                                                 6);

    mStaveLAIndex =
                  lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet."
                                                                  + getLightSheet().getName()
                                                                  + ".la.index",
                                                                  7);

    mStaveTIndex =
                 lCurrentMachineConfiguration.getIntegerProperty("device.lsm.lightsheet."
                                                                 + getLightSheet().getName()
                                                                 + ".t.index",
                                                                 8 + 7);

  }

  private LightSheet getLightSheet()
  {
    return mLightSheetQueue.getLightSheet();
  }

  public void addStavesToMovements(Movement pBeforeExposureMovement,
                                   Movement pExposureMovement,
                                   Movement pFinalMovement)
  {
    ensureStavesAddedToBeforeExposureMovement(pBeforeExposureMovement);
    ensureStavesAddedToExposureMovement(pExposureMovement);
    ensureStavesAddedToFinalMovement(pFinalMovement);
  }

  public void ensureStavesAddedToBeforeExposureMovement(Movement pBeforeExposureMovement)
  {

    // Analog outputs before exposure:
    mBeforeExposureXStave =
                          pBeforeExposureMovement.ensureSetStave(mStaveXIndex,
                                                                 mBeforeExposureXStave);

    mBeforeExposureYStave =
                          pBeforeExposureMovement.ensureSetStave(mStaveYIndex,
                                                                 mBeforeExposureYStave);

    mBeforeExposureZStave =
                          pBeforeExposureMovement.ensureSetStave(mStaveZIndex,
                                                                 mBeforeExposureZStave);

    mBeforeExposureBStave =
                          pBeforeExposureMovement.ensureSetStave(mStaveBIndex,
                                                                 mBeforeExposureBStave);

    mBeforeExposureWStave =
                          pBeforeExposureMovement.ensureSetStave(mStaveWIndex,
                                                                 mBeforeExposureWStave);

    mBeforeExposureLAStave =
                           pBeforeExposureMovement.ensureSetStave(mStaveLAIndex,
                                                                  mBeforeExposureLAStave);

    mBeforeExposureTStave =
                          pBeforeExposureMovement.ensureSetStave(mStaveTIndex,
                                                                 mBeforeExposureTStave);

  }

  public void ensureStavesAddedToExposureMovement(Movement pExposureMovement)
  {

    // Analog outputs at exposure:

    mExposureXStave =
                    pExposureMovement.ensureSetStave(mStaveXIndex,
                                                     mExposureXStave);

    mExposureYStave =
                    pExposureMovement.ensureSetStave(mStaveYIndex,
                                                     mExposureYStave);

    mExposureZStave =
                    pExposureMovement.ensureSetStave(mStaveZIndex,
                                                     mExposureZStave);

    mExposureBStave =
                    pExposureMovement.ensureSetStave(mStaveBIndex,
                                                     mExposureBStave);

    mExposureWStave =
                    pExposureMovement.ensureSetStave(mStaveWIndex,
                                                     mExposureWStave);

    mExposureLAStave =
                     pExposureMovement.ensureSetStave(mStaveLAIndex,
                                                      mExposureLAStave);

    mExposureTStave =
                    pExposureMovement.ensureSetStave(mStaveTIndex,
                                                     mExposureTStave);

    for (int i =
               0; i < mLightSheetQueue.getNumberOfLaserDigitalControls(); i++)
      mNonSIIluminationLaserTriggerStave =
                                         setLaserDigitalTriggerStave(pExposureMovement,
                                                                     i,
                                                                     mNonSIIluminationLaserTriggerStave);/**/

  }

  public void ensureStavesAddedToFinalMovement(Movement pFinalMovement)
  {

    pFinalMovement.ensureSetStave(mStaveXIndex,
                                  mBeforeExposureXStave);

    mFinalYStave = pFinalMovement.ensureSetStave(mStaveYIndex,
                                                 mFinalYStave);

    pFinalMovement.ensureSetStave(mStaveZIndex,
                                  mBeforeExposureZStave);

    pFinalMovement.ensureSetStave(mStaveBIndex,
                                  mBeforeExposureBStave);

    pFinalMovement.ensureSetStave(mStaveWIndex,
                                  mBeforeExposureWStave);

    pFinalMovement.ensureSetStave(mStaveLAIndex,
                                  mBeforeExposureLAStave);

    mFinalTStave = pFinalMovement.ensureSetStave(mStaveTIndex,
                                                 mFinalTStave);

  }

  public void update(Movement pBeforeExposureMovement,
                     Movement pExposureMovement)
  {
    synchronized (this)
    {
      // info("Updating: " + getLightSheet().getName());

      final double lReadoutTimeInMicroseconds =
                                              getBeforeExposureMovementDuration(TimeUnit.MICROSECONDS);
      final double lExposureMovementTimeInMicroseconds =
                                                       getExposureMovementDuration(TimeUnit.MICROSECONDS);

      pBeforeExposureMovement.setDuration(round(lReadoutTimeInMicroseconds),
                                          TimeUnit.MICROSECONDS);

      pExposureMovement.setDuration(round(lExposureMovementTimeInMicroseconds),
                                    TimeUnit.MICROSECONDS);

      final double lLineExposureTimeInMicroseconds =
                                                   lReadoutTimeInMicroseconds
                                                     + lExposureMovementTimeInMicroseconds;
      mLineExposureInMicrosecondsVariable.set(lLineExposureTimeInMicroseconds);

      final double lGalvoYOffsetBeforeRotation =
                                               mLightSheetQueue.getYVariable()
                                                               .get()
                                                               .doubleValue();
      final double lGalvoZOffsetBeforeRotation =
                                               mLightSheetQueue.getZVariable()
                                                               .get()
                                                               .doubleValue();

      final double lGalvoYOffset =
                                 galvoRotateY(lGalvoYOffsetBeforeRotation,
                                              lGalvoZOffsetBeforeRotation);
      final double lGalvoZOffset =
                                 galvoRotateZ(lGalvoYOffsetBeforeRotation,
                                              lGalvoZOffsetBeforeRotation);

      final double lLightSheetHeight =
                                     getLightSheet().getHeightFunction()
                                                    .get()
                                                    .value(mLightSheetQueue.getHeightVariable()

                                                                           .get()
                                                                           .doubleValue());
      final double lGalvoAmplitudeY = galvoRotateY(lLightSheetHeight,
                                                   0);
      final double lGalvoAmplitudeZ = galvoRotateZ(lLightSheetHeight,
                                                   0);

      final double lGalvoYLowValue =
                                   getLightSheet().getYFunction()
                                                  .get()
                                                  .value(lGalvoYOffset
                                                         - lGalvoAmplitudeY);
      final double lGalvoYHighValue =
                                    getLightSheet().getYFunction()
                                                   .get()
                                                   .value(lGalvoYOffset
                                                          + lGalvoAmplitudeY);

      final double lGalvoZLowValue =
                                   getLightSheet().getZFunction()
                                                  .get()
                                                  .value(lGalvoZOffset
                                                         - lGalvoAmplitudeZ);
      final double lGalvoZHighValue =
                                    getLightSheet().getZFunction()
                                                   .get()
                                                   .value(lGalvoZOffset
                                                          + lGalvoAmplitudeZ);

      mBeforeExposureYStave.setStartValue((float) lGalvoYHighValue);
      mBeforeExposureYStave.setStopValue((float) lGalvoYLowValue);
      mBeforeExposureYStave.setStartSlope((float) (lGalvoYHighValue
                                                   - lGalvoYLowValue));
      mBeforeExposureYStave.setStopSlope((float) (lGalvoYHighValue
                                                  - lGalvoYLowValue));
      mBeforeExposureYStave.setSmoothness(0.60f);

      mBeforeExposureZStave.setStartValue((float) lGalvoZHighValue);
      mBeforeExposureZStave.setStopValue((float) lGalvoZLowValue);
      mBeforeExposureZStave.setStartSlope((float) (lGalvoZHighValue
                                                   - lGalvoZLowValue));
      mBeforeExposureZStave.setStopSlope((float) (lGalvoZHighValue
                                                  - lGalvoZLowValue));
      mBeforeExposureZStave.setSmoothness(0.60f);

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

      mFinalYStave.setSyncStart(0);
      mFinalYStave.setSyncStop(1);
      mFinalYStave.setStartValue((float) lGalvoYHighValue);
      mFinalYStave.setStopValue((float) lGalvoYHighValue);
      mFinalYStave.setOutsideValue((float) lGalvoYHighValue);
      mFinalYStave.setNoJump(true);

      mBeforeExposureXStave.setValue((float) getLightSheet().getXFunction()
                                                            .get()
                                                            .value(mLightSheetQueue.getXVariable()
                                                                                   .get()
                                                                                   .doubleValue()));
      mExposureXStave.setValue((float) getLightSheet().getXFunction()
                                                      .get()
                                                      .value(mLightSheetQueue.getXVariable()
                                                                             .get()
                                                                             .doubleValue()));

      mBeforeExposureBStave.setValue((float) getLightSheet().getBetaFunction()
                                                            .get()
                                                            .value(mLightSheetQueue.getBetaInDegreesVariable()
                                                                                   .get()
                                                                                   .doubleValue()));
      mExposureBStave.setValue((float) getLightSheet().getBetaFunction()
                                                      .get()
                                                      .value(mLightSheetQueue.getBetaInDegreesVariable()
                                                                             .get()
                                                                             .doubleValue()));

      /*final double lFocalLength = mFocalLengthInMicronsVariable.get();
      final double lLambdaInMicrons = mLambdaInMicronsVariable.get();
      final double lLightSheetRangeInMicrons = mWidthVariable.getValue();
      
      final double lIrisDiameterInMm = GaussianBeamGeometry.getBeamIrisDiameter(lFocalLength,
                                                                                lLambdaInMicrons,
                                                                                lLightSheetRangeInMicrons);/**/
      double lWidthValue =
                         getLightSheet().getWidthFunction()
                                        .get()
                                        .value(mLightSheetQueue.getWidthVariable()
                                                               .get()
                                                               .doubleValue());

      mBeforeExposureWStave.setValue((float) lWidthValue);
      mExposureWStave.setValue((float) lWidthValue);

      final double lOverscan = mLightSheetQueue.getOverScanVariable()
                                               .get()
                                               .doubleValue();
      double lMarginTimeInMicroseconds = (lOverscan - 1)
                                         / (2 * lOverscan)
                                         * lExposureMovementTimeInMicroseconds;
      final double lMarginTimeRelativeUnits =
                                            microsecondsToRelative(lExposureMovementTimeInMicroseconds,
                                                                   lMarginTimeInMicroseconds);

      boolean lIsStepping = true;
      for (int i =
                 0; i < mLightSheetQueue.getNumberOfLaserDigitalControls(); i++)
        lIsStepping &= mLightSheetQueue.getSIPatternOnOffVariable(i)
                                       .get();

      mExposureYStave.setStepping(lIsStepping);
      mExposureZStave.setStepping(lIsStepping);

      for (int i =
                 0; i < mLightSheetQueue.getNumberOfLaserDigitalControls(); i++)
      {
        final Variable<Boolean> lLaserBooleanVariable =
                                                      mLightSheetQueue.getLaserOnOffArrayVariable(i);

        if (mLightSheetQueue.getSIPatternOnOffVariable(i).get())
        {

          final StructuredIlluminationPatternInterface lStructuredIlluminatioPatternInterface =
                                                                                              mLightSheetQueue.getSIPatternVariable(i)
                                                                                                              .get();
          final StaveInterface lSIIlluminationLaserTriggerStave =
                                                                lStructuredIlluminatioPatternInterface.getStave(lMarginTimeRelativeUnits);
          lSIIlluminationLaserTriggerStave.setEnabled(lLaserBooleanVariable.get());

          setLaserDigitalTriggerStave(pExposureMovement,
                                      i,
                                      lSIIlluminationLaserTriggerStave);
        }
        else
        {
          mNonSIIluminationLaserTriggerStave.setEnabled(lLaserBooleanVariable.get());
          mNonSIIluminationLaserTriggerStave.setStart((float) lMarginTimeRelativeUnits);
          mNonSIIluminationLaserTriggerStave.setStop((float) (1.0f
                                                              - lMarginTimeRelativeUnits));
          setLaserDigitalTriggerStave(pExposureMovement,
                                      i,
                                      mNonSIIluminationLaserTriggerStave);
        }

      }

      double lPowerValue =
                         getLightSheet().getPowerFunction()
                                        .get()
                                        .value(mLightSheetQueue.getPowerVariable()
                                                               .get()
                                                               .doubleValue());

      if (mLightSheetQueue.getAdaptPowerToWidthHeightVariable().get())
      {
        double lWidthPowerFactor =
                                 getLightSheet().getWidthPowerFunction()
                                                .get()
                                                .value(lWidthValue);

        double lHeightPowerFactor =
                                  getLightSheet().getHeightPowerFunction()
                                                 .get()
                                                 .value(lLightSheetHeight
                                                        / lOverscan);/**/

        lPowerValue *= lWidthPowerFactor * lHeightPowerFactor;
      }

      mBeforeExposureLAStave.setValue(0f);
      mExposureLAStave.setValue((float) lPowerValue);

    }

  }

  private <O extends StaveInterface> O setLaserDigitalTriggerStave(Movement pExposureMovement,
                                                                   int pLaserLineIndex,
                                                                   O pStave)
  {

    final int lLaserDigitalLineIndex =
                                     MachineConfiguration.getCurrentMachineConfiguration()
                                                         .getIntegerProperty("device.lsm.lightsheet."
                                                                             + getLightSheet().getName()
                                                                             + ".ld"
                                                                             + pLaserLineIndex
                                                                             + ".index",
                                                                             8 + pLaserLineIndex);
    return pExposureMovement.ensureSetStave(lLaserDigitalLineIndex,
                                            pStave);
  }

  public long getExposureMovementDuration(TimeUnit pTimeUnit)
  {
    return pTimeUnit.convert((long) (mLightSheetQueue.getEffectiveExposureInSecondsVariable()
                                                     .get()
                                                     .doubleValue()
                                     * 1e6),
                             TimeUnit.MICROSECONDS);
  }

  public long getBeforeExposureMovementDuration(TimeUnit pTimeUnit)
  {
    return pTimeUnit.convert((long) (mLightSheetQueue.getReadoutTimeInMicrosecondsPerLineVariable()
                                                     .get()
                                                     .doubleValue()
                                     * mLightSheetQueue.getImageHeightVariable()
                                                       .get()
                                                       .doubleValue()
                                     / 2),
                             TimeUnit.MICROSECONDS);
  }

  private double galvoRotateY(double pY, double pZ)
  {
    final double lAlpha =
                        Math.toRadians(getLightSheet().getAlphaFunction()
                                                      .get()
                                                      .value(mLightSheetQueue.getAlphaInDegreesVariable()
                                                                             .get()
                                                                             .doubleValue()));
    return pY * cos(lAlpha) - pZ * sin(lAlpha);
  }

  private double galvoRotateZ(double pY, double pZ)
  {
    final double lAlpha =
                        Math.toRadians(getLightSheet().getAlphaFunction()
                                                      .get()
                                                      .value(mLightSheetQueue.getAlphaInDegreesVariable()
                                                                             .get()
                                                                             .doubleValue()));
    return pY * sin(lAlpha) + pZ * cos(lAlpha);
  }

  private static double microsecondsToRelative(final double pTotalTime,
                                               final double pSubTime)
  {
    return pSubTime / pTotalTime;
  }

  public BezierStave getGalvoScannerStaveBeforeExposureZ()
  {
    return mBeforeExposureZStave;
  }

  public BezierStave getGalvoScannerStaveBeforeExposureY()
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

}
