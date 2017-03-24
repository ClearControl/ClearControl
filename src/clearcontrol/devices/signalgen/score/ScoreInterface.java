package clearcontrol.devices.signalgen.score;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.devices.signalgen.movement.MovementInterface;

/**
 * Score interface
 *
 * @author Loic Royer (2015)
 *
 */
public interface ScoreInterface
{

  /**
   * Returns the number of movements in score.
   * 
   * @return number of movements
   */
  public abstract int getNumberOfMovements();

  /**
   * Returns list of movements in score
   * 
   * @return list of movements
   */
  public abstract ArrayList<MovementInterface> getMovements();

  /**
   * Clears the score from any movement.
   */
  public abstract void clear();

  /**
   * Removes movement at given index.
   * 
   * @param pIndex
   *          movement index.
   */
  public abstract void removeMovementAt(final int pIndex);

  /**
   * Inserts movement at given index.
   * 
   * @param pIndex
   *          given index
   * @param pMovement
   *          given movement
   */
  public abstract void insertMovementAt(final int pIndex,
                                        final MovementInterface pMovement);

  /**
   * Adds movement to score.
   * 
   * @param pMovement
   *          movement to add
   */
  public abstract void addMovement(final MovementInterface pMovement);

  /**
   * Adds movement multiple times.
   * 
   * @param pMovement
   *          movement to add
   * @param pNumberOfTimes
   *          number of times
   */
  public abstract void addMovementMultipleTimes(final MovementInterface pMovement,
                                                final int pNumberOfTimes);

  /**
   * Adds all movements in given score to this score.
   * 
   * @param pScore
   *          score from which movements are added
   */
  public abstract void addScore(ScoreInterface pScore);

  /**
   * Adds _copies_ of all movements in given score to this score
   * 
   * @param pScore
   *          score to copy into this score.
   */
  public abstract void addScoreCopy(ScoreInterface pScore);

  /**
   * Returns the movement at the given movement index position.
   * 
   * @param pMovementIndex
   *          movement index.
   * @return movement
   */
  public abstract MovementInterface getMovement(int pMovementIndex);

  /**
   * Return maximum number of time points.
   * 
   * @return number of time points
   */
  public abstract int getMaxNumberOfStaves();

  /**
   * Returns the duration of this score in the requested time unit
   * 
   * @param pTimeUnit
   *          time unit
   * @return duration
   */
  public abstract long getDuration(TimeUnit pTimeUnit);

}
