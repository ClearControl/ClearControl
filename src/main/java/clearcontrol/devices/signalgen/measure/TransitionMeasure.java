package clearcontrol.devices.signalgen.measure;

import java.util.concurrent.TimeUnit;

import clearcontrol.devices.signalgen.staves.BezierStave;
import clearcontrol.devices.signalgen.staves.StaveInterface;

/**
 *
 *
 * @author royer
 */
public class TransitionMeasure
{

  private static final float cEpsilon = 0.01f;

  /**
   * Returns a measure that smoothly transitions from the stave values of a
   * previous measure to the next measure
   * 
   * @param pPreviousMeasure
   *          previous measure
   * @param pNextMeasure
   *          next measure
   * @param pDuration
   *          duration
   * @param pTimeUnit
   *          time unit
   * @return transition measure
   */
  public static MeasureInterface make(MeasureInterface pPreviousMeasure,
                                      MeasureInterface pNextMeasure,
                                      long pDuration,
                                      TimeUnit pTimeUnit)
  {

    Measure lTransitionMeasure =
                                 new Measure("TransitionMeasure",
                                              pPreviousMeasure.getNumberOfStaves());

    adjustInternal(lTransitionMeasure,
                   pPreviousMeasure,
                   pNextMeasure,
                   pDuration,
                   pTimeUnit);

    return lTransitionMeasure;
  }

  public static void adjust(MeasureInterface pTransitionMeasure,
                            MeasureInterface pPreviousMeasure,
                            MeasureInterface pNextMeasure,
                            long pDuration,
                            TimeUnit pTimeUnit)
  {
    adjustInternal(pTransitionMeasure,
                   pPreviousMeasure,
                   pNextMeasure,
                   pDuration,
                   pTimeUnit);
  }

  private static void adjustInternal(MeasureInterface lTransitionMeasure,
                                     MeasureInterface pPreviousMeasure,
                                     MeasureInterface pNextMeasure,
                                     long pDuration,
                                     TimeUnit pTimeUnit)
  {
    int lNumberOfStaves = pPreviousMeasure.getNumberOfStaves();
    lTransitionMeasure.setDuration(pDuration, pTimeUnit);

    for (int i = 0; i < lNumberOfStaves; i++)
    {
      BezierStave lTransitionStave = new BezierStave("TransitionStave"
                                                     + i, 0);

      StaveInterface lPreviousStave = pPreviousMeasure.getStave(i);
      StaveInterface lNextStave = pNextMeasure.getStave(i);

      float lPreviousValue = lPreviousStave.getValue(1);
      float lNextValue = lNextStave.getValue(0);

      float lPreviousSlope = (lPreviousStave.getValue(1)
                              - lPreviousStave.getValue(1 - cEpsilon))
                             / cEpsilon;
      float lNextSlope = (lNextStave.getValue(1)
                          - lNextStave.getValue(1 - cEpsilon))
                         / cEpsilon;

      lTransitionStave.setStartValue(lPreviousValue);
      lTransitionStave.setStopValue(lNextValue);

      lTransitionStave.setStartSlope(lPreviousSlope);
      lTransitionStave.setStopSlope(lNextSlope);

      lTransitionStave.setMargin(0.05f);
      lTransitionStave.setSmoothness(0.33f);

      lTransitionMeasure.setStave(i, lTransitionStave);

    }
  }

}
