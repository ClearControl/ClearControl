package clearcontrol.core.math.interpolation;

import clearcontrol.core.log.LoggingFeature;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG
 * (http://mpi-cbg.de) March 2018
 */
public class LinearInterpolationTable extends
                                      AbstractInterpolationTable
                                      implements LoggingFeature
{
  /**
   * Creates a LinearInterpolationTable witha given number of columns.
   *
   * @param pNumberOfColumns
   *          number of columns
   */
  public LinearInterpolationTable(int pNumberOfColumns)
  {
    super(pNumberOfColumns);
  }

  @Override
  public double getInterpolatedValue(int pColumnIndex, double pX)
  {
    Row ceilRow = getCeilRow(pX);
    Row floorRow = getFloorRow(pX);

    // extrapolation in case we are at the border...
    if (ceilRow == null && floorRow != null)
    {
      return floorRow.getY(pColumnIndex);
    }
    if (ceilRow != null && floorRow == null)
    {
      return ceilRow.getY(pColumnIndex);
    }

    // error handling; this block should never be entered:
    if (ceilRow == null && floorRow == null)
    {
      warning("interpolation of position " + pX + " failed.");
      return pX;
    }

    double yA = ceilRow.getY(pColumnIndex);
    double yB = floorRow.getY(pColumnIndex);

    double dB = Math.abs(getCeilRow(pX).getX() - pX);
    double dA = Math.abs(getFloorRow(pX).getX() - pX);

    double distance = dA + dB;
    if (Math.abs(distance) < 0.00001)
    {
      return yA;
    }
    double result = (yA * dA + yB * dB) / distance;
    if (Double.isNaN(result))
    {
      return 0;
    }
    return result;
  }

  /**
   * Creates a copy of a SplineInterpolationTable.
   *
   * @param pInterpolationTable
   *          table to copy
   */
  public LinearInterpolationTable(LinearInterpolationTable pInterpolationTable)
  {
    this(pInterpolationTable.mNumberOfColumns);
    for (Row lRow : pInterpolationTable.mTable)
      mTable.add(new Row(lRow));
    mIsUpToDate = false;
  }

  @Override
  public LinearInterpolationTable clone()
  {
    return new LinearInterpolationTable(this);
  }

}
