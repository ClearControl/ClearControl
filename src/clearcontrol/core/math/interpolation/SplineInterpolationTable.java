package clearcontrol.core.math.interpolation;

import static java.lang.Math.abs;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

/**
 * SplineInterpolationTable provides Spline interpolation for tables. Each
 * column of the table corresponds to a different 'function'f(x) and each row
 * corresponds to a x value. The interpolated value for any x can be queried.
 * 
 * @author royer
 */
public class SplineInterpolationTable extends
                                      AbstractInterpolationTable
                                      implements Cloneable
{

  /**
   * Creates a SplineInterpolationTable witha given number of columns.
   * 
   * @param pNumberOfColumns
   *          number of columns
   */
  public SplineInterpolationTable(int pNumberOfColumns)
  {
    super(pNumberOfColumns);
  }

  /**
   * Creates a copy of a SplineInterpolationTable.
   * 
   * @param pInterpolationTable
   *          table to copy
   */
  public SplineInterpolationTable(SplineInterpolationTable pInterpolationTable)
  {
    this(pInterpolationTable.mNumberOfColumns);
    for (Row lRow : pInterpolationTable.mTable)
      mTable.add(new Row(lRow));
    mIsUpToDate = false;
  }

  @Override
  public SplineInterpolationTable clone()
  {
    return new SplineInterpolationTable(this);
  }

  /**
   * Returns the interpolated value Y=f(X) for a given column index and value X.
   *
   * @param pColumnIndex
   *          column index
   * @param pX
   *          X value
   * @return Y=f(X) interpolated value
   */
  @Override
  public double getInterpolatedValue(int pColumnIndex, double pX)
  {
    ensureIsUpToDate();

    final UnivariateFunction lUnivariateFunction =
                                                 mInterpolatingFunctionsList.get(pColumnIndex);
    final double lValue = lUnivariateFunction.value(pX);
    return lValue;
  }

  /**
   * Ensures that the table is up to date.
   */
  private void ensureIsUpToDate()
  {
    if (!isIsUpToDate())
    {
      final UnivariateInterpolator lUnivariateInterpolator =
                                                           new SplineInterpolator();
      /*new LoessInterpolator(	max(0.25 * mTable.size(),
      																																							3) / mTable.size(),
      																																					0);/**/
      mInterpolatingFunctionsList.clear();

      final TDoubleArrayList x = new TDoubleArrayList();
      final TDoubleArrayList y = new TDoubleArrayList();

      for (int i = 0; i < mNumberOfColumns; i++)
      {
        x.clear();
        y.clear();
        for (final Row lRow : mTable)
        {
          double lValueY = lRow.getY(i);

          if (!Double.isNaN(lValueY))
          {
            y.add(lValueY);
            x.add(lRow.getX());
          }
        }

        final double lMinX = getMinX();
        final double lMaxX = getMaxX();

        // this makes sure that we extrapolate outside of the actual range
        final double lRangeWidth = 10000 * abs(lMaxX - lMinX);

        if (x.size() >= 2)
        {
          x.insert(0, x.get(0) - lRangeWidth);
          x.add(x.get(x.size() - 1) + lRangeWidth);
        }

        if (x.size() >= 2)
        {
          y.insert(0, y.get(0));
          y.add(y.get(y.size() - 1));

          y.set(0,
                y.get(1) - lRangeWidth * ((y.get(1) - y.get(2))
                                          / (x.get(1) - x.get(2))));
          y.set(y.size() - 1,
                y.get(y.size() - 2) + lRangeWidth
                                      * ((y.get(y.size() - 2)
                                          - y.get(y.size() - 3))
                                         / (x.get(y.size() - 2)
                                            - x.get(y.size() - 3))));
        }

        final UnivariateFunction lUnivariateFunction;

        if (x.size() == 0)
        {
          lUnivariateFunction = new UnivariateAffineFunction(0, 0);
        }
        else if (x.size() <= 1)
        {
          lUnivariateFunction =
                              new UnivariateAffineFunction(0,
                                                           x.get(0));
        }
        else
        {

          lUnivariateFunction =
                              lUnivariateInterpolator.interpolate(x.toArray(),
                                                                  y.toArray());
        }
        mInterpolatingFunctionsList.add(lUnivariateFunction);

      }

      setUpToDate();
    }
  }

}
