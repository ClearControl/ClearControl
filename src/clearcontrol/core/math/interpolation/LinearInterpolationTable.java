package clearcontrol.core.math.interpolation;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import gnu.trove.list.array.TDoubleArrayList;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import static java.lang.Math.abs;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG (http://mpi-cbg.de)
 * March 2018
 */
public class LinearInterpolationTable extends AbstractInterpolationTable
{
  /**
   * Creates a SplineInterpolationTable witha given number of columns.
   *
   * @param pNumberOfColumns
   *          number of columns
   */
  public LinearInterpolationTable(int pNumberOfColumns)
  {
    super(pNumberOfColumns);
  }

  @Override public double getInterpolatedValue(int pColumnIndex,
                                               double pX)
  {
    //ensureIsUpToDate();

    double yA = getCeilRow(pX).getY(pColumnIndex);
    double yB = getFloorRow(pX).getY(pColumnIndex);

    double dB = Math.abs(getCeilRow(pX).getX() - pX);
    double dA = Math.abs(getFloorRow(pX).getX() - pX);

    double distance = dA + dB;
    double result = (yA * dA + yB * dB) / distance;
    if (Double.isNaN(result)) {
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
