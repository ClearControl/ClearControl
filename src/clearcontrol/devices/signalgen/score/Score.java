package clearcontrol.devices.signalgen.score;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.name.NameableBase;
import clearcontrol.devices.signalgen.movement.MovementInterface;

/**
 * Score
 *
 * @author royer
 */
public class Score extends NameableBase implements ScoreInterface
{

  private final ArrayList<MovementInterface> mMovementList =
                                                           new ArrayList<MovementInterface>();

  /**
   * Instantiates a score of given name
   * 
   * @param pName
   *          score name
   */
  public Score(final String pName)
  {
    super(pName);
  }

  /**
   * Copy constructor
   * 
   * @param pScore
   *          scopre to copy
   */
  public Score(final Score pScore)
  {
    super(pScore.getName());

    for (MovementInterface lMovement : pScore.getMovements())
      mMovementList.add(lMovement.duplicate());

  }

  @Override
  public Score duplicate()
  {
    return new Score(this);
  }

  @Override
  public void addMovement(final MovementInterface pMovement)
  {
    mMovementList.add(pMovement);
  }

  @Override
  public void addMovementMultipleTimes(final MovementInterface pMovement,
                                       final int pNumberOfTimes)
  {
    for (int i = 0; i < pNumberOfTimes; i++)
    {
      addMovement(pMovement);
    }
  }

  @Override
  public void addScore(ScoreInterface pScore)
  {
    for (final MovementInterface lMovementInterface : pScore.getMovements())
    {
      addMovement(lMovementInterface);
    }
  }

  @Override
  public void addScoreCopy(ScoreInterface pScore)
  {
    for (final MovementInterface lMovementInterface : pScore.getMovements())
    {
      addMovement(lMovementInterface.duplicate());
    }
  }

  @Override
  public void insertMovementAt(final int pIndex,
                               final MovementInterface pMovement)
  {
    mMovementList.add(pIndex, pMovement);
  }

  @Override
  public void removeMovementAt(final int pIndex)
  {
    mMovementList.remove(pIndex);
  }

  @Override
  public MovementInterface getMovement(int pMovementIndex)
  {
    return mMovementList.get(pMovementIndex);
  }

  @Override
  public MovementInterface getLastMovement()
  {
    return mMovementList.get(mMovementList.size() - 1);
  }

  @Override
  public void clear()
  {
    mMovementList.clear();
  }

  @Override
  public ArrayList<MovementInterface> getMovements()
  {
    return mMovementList;
  }

  @Override
  public int getNumberOfMovements()
  {
    return mMovementList.size();
  }

  @Override
  public int getMaxNumberOfStaves()
  {
    int lMaxNumberOfStaves = 0;

    for (final MovementInterface lMovement : mMovementList)
      lMaxNumberOfStaves = max(lMaxNumberOfStaves,
                               lMovement.getNumberOfStaves());

    return lMaxNumberOfStaves;
  }

  @Override
  public long getDuration(TimeUnit pTimeUnit)
  {
    long lDurationInNs = 0;
    for (final MovementInterface lMovement : mMovementList)
    {
      lDurationInNs += lMovement.getDuration(TimeUnit.NANOSECONDS);
    }
    return pTimeUnit.convert(lDurationInNs, TimeUnit.NANOSECONDS);
  }


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + ((mMovementList == null) ? 0
                                        : mMovementList.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Score other = (Score) obj;
    if (mMovementList == null)
    {
      if (other.mMovementList != null)
        return false;
    }
    else if (!mMovementList.equals(other.mMovementList))
      return false;
    return true;
  }
  /**/

  @Override
  public String toString()
  {
    return String.format("Score[name=%s, duration=%g sec, #movements=%d, #staves=%d]",
                         getName(),
                         getDuration(TimeUnit.MICROSECONDS) * 1e-6,
                         getNumberOfMovements(),
                         getMaxNumberOfStaves());
  }

}
