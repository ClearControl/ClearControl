package clearcontrol.devices.signalgen.score;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.devices.signalgen.measure.MeasureInterface;

/**
 * Score interface
 *
 * @author Loic Royer (2015)
 *
 */
public interface ScoreInterface
{

  /**
   * Duplicates this score by performing a deep copy
   * 
   * @return clone
   */
  public abstract ScoreInterface duplicate();

  /**
   * Returns the number of measures in score.
   * 
   * @return number of measures
   */
  public abstract int getNumberOfMeasures();

  /**
   * Returns list of measures in score
   * 
   * @return list of measures
   */
  public abstract ArrayList<MeasureInterface> getMeasures();

  /**
   * Clears the score from any measure.
   */
  public abstract void clear();

  /**
   * Removes measure at given index.
   * 
   * @param pIndex
   *          measure index.
   */
  public abstract void removeMeasureAt(final int pIndex);

  /**
   * Inserts measure at given index.
   * 
   * @param pIndex
   *          given index
   * @param pMeasure
   *          given measure
   */
  public abstract void insertMeasureAt(final int pIndex,
                                        final MeasureInterface pMeasure);

  /**
   * Adds measure to score.
   * 
   * @param pMeasure
   *          measure to add
   */
  public abstract void addMeasure(final MeasureInterface pMeasure);

  /**
   * Adds measure multiple times.
   * 
   * @param pMeasure
   *          measure to add
   * @param pNumberOfTimes
   *          number of times
   */
  public abstract void addMeasureMultipleTimes(final MeasureInterface pMeasure,
                                                final int pNumberOfTimes);

  /**
   * Adds all measures in given score to this score.
   * 
   * @param pScore
   *          score from which measures are added
   */
  public abstract void addScore(ScoreInterface pScore);

  /**
   * Adds _copies_ of all measures in given score to this score
   * 
   * @param pScore
   *          score to copy into this score.
   */
  public abstract void addScoreCopy(ScoreInterface pScore);

  /**
   * Returns the measure at the given measure index position.
   * 
   * @param pMeasureIndex
   *          measure index.
   * @return measure
   */
  public abstract MeasureInterface getMeasure(int pMeasureIndex);

  /**
   * Returns the last measure of this score
   * 
   * @return last measure
   */
  public abstract MeasureInterface getLastMeasure();

  /**
   * Return maximum number of staves
   * 
   * @return maximum number of staves
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
