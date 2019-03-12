package clearcontrol.devices.signalgen.score;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.device.name.NameableBase;
import clearcontrol.devices.signalgen.measure.MeasureInterface;

/**
 * Score
 *
 * @author royer
 */
public class Score extends NameableBase implements ScoreInterface
{

  private final ArrayList<MeasureInterface> mMeasureList =
                                                           new ArrayList<MeasureInterface>();

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

    for (MeasureInterface lMeasure : pScore.getMeasures())
      mMeasureList.add(lMeasure.duplicate());

  }

  @Override
  public Score duplicate()
  {
    return new Score(this);
  }

  @Override
  public void addMeasure(final MeasureInterface pMeasure)
  {
    mMeasureList.add(pMeasure);
  }

  @Override
  public void addMeasureMultipleTimes(final MeasureInterface pMeasure,
                                       final int pNumberOfTimes)
  {
    for (int i = 0; i < pNumberOfTimes; i++)
    {
      addMeasure(pMeasure);
    }
  }

  @Override
  public void addScore(ScoreInterface pScore)
  {
    for (final MeasureInterface lMeasureInterface : pScore.getMeasures())
    {
      addMeasure(lMeasureInterface);
    }
  }

  @Override
  public void addScoreCopy(ScoreInterface pScore)
  {
    for (final MeasureInterface lMeasureInterface : pScore.getMeasures())
    {
      addMeasure(lMeasureInterface.duplicate());
    }
  }

  @Override
  public void insertMeasureAt(final int pIndex,
                               final MeasureInterface pMeasure)
  {
    mMeasureList.add(pIndex, pMeasure);
  }

  @Override
  public void removeMeasureAt(final int pIndex)
  {
    mMeasureList.remove(pIndex);
  }

  @Override
  public MeasureInterface getMeasure(int pMeasureIndex)
  {
    return mMeasureList.get(pMeasureIndex);
  }

  @Override
  public MeasureInterface getLastMeasure()
  {
    return mMeasureList.get(mMeasureList.size() - 1);
  }

  @Override
  public void clear()
  {
    mMeasureList.clear();
  }

  @Override
  public ArrayList<MeasureInterface> getMeasures()
  {
    return mMeasureList;
  }

  @Override
  public int getNumberOfMeasures()
  {
    return mMeasureList.size();
  }

  @Override
  public int getMaxNumberOfStaves()
  {
    int lMaxNumberOfStaves = 0;

    for (final MeasureInterface lMeasure : mMeasureList)
      lMaxNumberOfStaves = max(lMaxNumberOfStaves,
                               lMeasure.getNumberOfStaves());

    return lMaxNumberOfStaves;
  }

  @Override
  public long getDuration(TimeUnit pTimeUnit)
  {
    long lDurationInNs = 0;
    for (final MeasureInterface lMeasure : mMeasureList)
    {
      lDurationInNs += lMeasure.getDuration(TimeUnit.NANOSECONDS);
    }
    return pTimeUnit.convert(lDurationInNs, TimeUnit.NANOSECONDS);
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + ((mMeasureList == null) ? 0
                                        : mMeasureList.hashCode());
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
    if (mMeasureList == null)
    {
      if (other.mMeasureList != null)
        return false;
    }
    else if (!mMeasureList.equals(other.mMeasureList))
      return false;
    return true;
  }
  /**/

  @Override
  public String toString()
  {
    return String.format("Score[name=%s, duration=%g sec, #measures=%d, #staves=%d]",
                         getName(),
                         getDuration(TimeUnit.MICROSECONDS) * 1e-6,
                         getNumberOfMeasures(),
                         getMaxNumberOfStaves());
  }

}
