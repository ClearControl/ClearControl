package clearcontrol.hardware.signalgen.score;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.device.name.NameableBase;
import clearcontrol.hardware.signalgen.movement.MovementInterface;

public class Score extends NameableBase implements ScoreInterface
{

  private final ArrayList<MovementInterface> mMovementList =
                                                           new ArrayList<MovementInterface>();

  public Score(final String pName)
  {
    super(pName);
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
      addMovement(lMovementInterface.copy());
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
  public String toString()
  {
    return String.format("Score-%s", getName());
  }

}
