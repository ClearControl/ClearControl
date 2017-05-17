package clearcontrol.devices.signalgen.movement;

import java.util.concurrent.TimeUnit;

import clearcontrol.devices.signalgen.staves.BezierStave;
import clearcontrol.devices.signalgen.staves.StaveInterface;

/**
 *
 *
 * @author royer
 */
public class TransitionMovement
{

  private static final float cEpsilon = 0.01f;

  /**
   * Returns a movement that smoothly transitions from the stave values of a
   * previous movement to the next movement
   * 
   * @param pPreviousMovement
   *          previous movement
   * @param pNextMovement
   *          next movement
   * @param pDuration
   *          duration
   * @param pTimeUnit
   *          time unit
   * @return transition movement
   */
  public static MovementInterface make(MovementInterface pPreviousMovement,
                                       MovementInterface pNextMovement,
                                       long pDuration,
                                       TimeUnit pTimeUnit)
  {


    Movement lTransitionMovement = new Movement("TransitionMovement",
                                                pPreviousMovement.getNumberOfStaves());

    adjustInternal(lTransitionMovement,
                   pPreviousMovement,
                   pNextMovement,
                   pDuration,
                   pTimeUnit);

    return lTransitionMovement;
  }

  public static void adjust(MovementInterface pTransitionMovement,
                            MovementInterface pPreviousMovement,
                            MovementInterface pNextMovement,
                            long pDuration,
                            TimeUnit pTimeUnit)
  {
    adjustInternal(pTransitionMovement,
                   pPreviousMovement,
                   pNextMovement,
                   pDuration,
                   pTimeUnit);
  }

  private static void adjustInternal(MovementInterface lTransitionMovement,
                                     MovementInterface pPreviousMovement,
                                     MovementInterface pNextMovement,
                                     long pDuration,
                                     TimeUnit pTimeUnit)
  {
    int lNumberOfStaves = pPreviousMovement.getNumberOfStaves();
    lTransitionMovement.setDuration(pDuration, pTimeUnit);

    for (int i = 0; i < lNumberOfStaves; i++)
    {
      BezierStave lTransitionStave = new BezierStave("TransitionStave"
                                                     + i, 0);

      StaveInterface lPreviousStave = pPreviousMovement.getStave(i);
      StaveInterface lNextStave = pNextMovement.getStave(i);

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

      lTransitionMovement.setStave(i, lTransitionStave);

    }
  }


}
