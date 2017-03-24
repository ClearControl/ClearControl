package clearcontrol.core.math.interpolation;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.gui.plots.MultiPlot;
import clearcontrol.gui.plots.PlotTab;
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
public class SplineInterpolationTable implements Cloneable
{
  private final TreeSet<Row> mTable;
  private final ArrayList<UnivariateFunction> mInterpolatingFunctionsList;
  private final int mNumberOfColumns;
  private volatile boolean mIsUpToDate = false;

  /**
   * Creates a SplineInterpolationTable witha given number of columns.
   * 
   * @param pNumberOfColumns
   *          number of columns
   */
  public SplineInterpolationTable(int pNumberOfColumns)
  {
    super();
    mTable = new TreeSet<>();
    mInterpolatingFunctionsList = new ArrayList<>();
    mNumberOfColumns = pNumberOfColumns;
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
   * Returns the number of rows in the table
   * 
   * @return number of rows
   */
  public int getNumberOfRows()
  {
    return mTable.size();
  }

  /**
   * Number of Columns
   * 
   * @return number of columns
   */
  public int getNumberOfColumns()
  {
    return mTable.first().getNumberOfColumns();
  }

  /**
   * Returns the Row at a given index
   * 
   * @param pRowIndex
   *          Row index
   * @return Row at index
   */
  public Row getRow(int pRowIndex)
  {
    Iterator<Row> lIterator = mTable.iterator();

    for (int i = 0; i < pRowIndex && lIterator.hasNext(); i++)
      lIterator.next();

    return lIterator.next();
  }

  /**
   * Returns the nearest Row for a given X
   * 
   * @param pX
   *          X
   * @return nearest Row
   */
  public Row getNearestRow(double pX)
  {
    final Row lCeiling = mTable.ceiling(new Row(0, pX));
    final Row lFloor = mTable.floor(new Row(0, pX));

    if (abs(lCeiling.getX() - pX) < abs(lFloor.getX() - pX))
      return lCeiling;
    else
      return lFloor;
  }

  /**
   * Returns the ceiling Row for a given X
   * 
   * @param pX
   *          X
   * @return ceiling Row
   */
  public Row getCeilRow(double pX)
  {
    final Row lCeiling = mTable.ceiling(new Row(0, pX));
    return lCeiling;
  }

  /**
   * Returns the floor Row for a given X
   * 
   * @param pX
   *          X
   * @return floor row
   */
  public Row getFloorRow(double pX)
  {
    final Row lFloor = mTable.floor(new Row(0, pX));
    return lFloor;
  }

  /**
   * Adds a Row for a given X value.
   * 
   * @param pX
   *          X
   * @return Row
   */
  public Row addRow(double pX)
  {
    final Row lRow = new Row(mNumberOfColumns, pX);
    mTable.add(lRow);
    mIsUpToDate = false;
    return lRow;
  }

  /**
   * Returns the max X
   * 
   * @return max X
   */
  public double getMaxX()
  {
    return mTable.last().x;
  }

  /**
   * Returns min X
   * 
   * @return min X
   */
  public double getMinX()
  {
    return mTable.first().x;
  }

  /**
   * Returns the nearest value Y=f(X) for a given column index and value X.
   * 
   * @param pColumnIndex
   *          column index
   * @param pX
   *          X value
   * @return Y=f(X) nearest value
   */
  public double getNearestValue(int pColumnIndex, double pX)
  {
    return getNearestRow(pX).getY(pColumnIndex);
  }

  /**
   * Returns the ceiling value Y=f(X) for a given column index and value X.
   * 
   * @param pColumnIndex
   *          column index
   * @param pX
   *          X value
   * @return Y=f(X) ceiling value
   */
  public double getCeil(int pColumnIndex, double pX)
  {
    return getNearestRow(pX).getY(pColumnIndex);
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
  public double getInterpolatedValue(int pColumnIndex, double pX)
  {
    ensureIsUpToDate();

    final UnivariateFunction lUnivariateFunction =
                                                 mInterpolatingFunctionsList.get(pColumnIndex);
    final double lValue = lUnivariateFunction.value(pX);
    return lValue;
  }

  /**
   * Sets the Y value for a given column and row index.
   * 
   * @param pRowIndex
   *          row index
   * @param pColumnIndex
   *          column index
   * @param pValue
   *          value
   */
  public void setY(int pRowIndex, int pColumnIndex, double pValue)
  {
    getRow(pRowIndex).setY(pColumnIndex, pValue);
  }

  /**
   * Sets the Y value for a given column and row index.
   * 
   * @param pRowIndex
   *          row index
   * @param pColumnIndex
   *          column index
   * @param pDeltaValue
   *          delta value
   */
  public void addY(int pRowIndex,
                   int pColumnIndex,
                   double pDeltaValue)
  {
    getRow(pRowIndex).addY(pColumnIndex, pDeltaValue);
  }

  /**
   * Returns the Y value for a given column and row index.
   * 
   * @param pRowIndex
   *          row index
   * @param pColumnIndex
   *          column index
   * @return y value
   */
  public double getY(int pRowIndex, int pColumnIndex)
  {
    return getRow(pRowIndex).getY(pColumnIndex);
  }

  /**
   * Sets the Y value at a given row index for all columns.
   * 
   * @param pRowIndex
   *          row index
   * @param pValue
   *          value
   */
  public void setY(int pRowIndex, double pValue)
  {
    int lNumberOfColumns = getNumberOfColumns();
    for (int c = 0; c < lNumberOfColumns; c++)
      getRow(pRowIndex).setY(c, pValue);
  }

  /**
   * Sets the Y value for all entries in the table.
   * 
   * @param pValue
   *          value
   */
  public void setY(double pValue)
  {
    int lNumberOfColumns = getNumberOfColumns();
    int lNumberOfRows = getNumberOfRows();

    for (int c = 0; c < lNumberOfColumns; c++)
      for (int r = 0; r < lNumberOfRows; r++)
        getRow(r).setY(c, pValue);
  }

  /**
   * Returns true if this table interpolation is up to date.
   * 
   * @return
   */
  private boolean isIsUpToDate()
  {
    boolean lIsUpToDate = mIsUpToDate;
    for (final Row lRow : mTable)
    {
      lIsUpToDate &= lRow.isUpToDate();
    }
    return lIsUpToDate;
  }

  /**
   * Sets the up-to-date flag to true.
   */
  private void setUpToDate()
  {
    mIsUpToDate = true;
    for (final Row lRow : mTable)
    {
      lRow.setUpToDate(true);
    }
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
        final double lRangeWidth = abs(lMaxX - lMinX);

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

  /**
   * Displays a MultiPlot for debug purposes.
   * 
   * @param pMultiPlotName
   *          multiplot name
   * @return multiplot
   */
  public MultiPlot displayTable(String pMultiPlotName)
  {
    final MultiPlot lMultiPlot =
                               MultiPlot.getMultiPlot(pMultiPlotName);

    for (int i = 0; i < mNumberOfColumns; i++)
    {
      final PlotTab lPlot = lMultiPlot.getPlot("Column" + i);
      lPlot.setLinePlot("interpolated");
      lPlot.setScatterPlot("samples");

      for (final Row lRow : mTable)
      {
        final double x = lRow.x;
        final double y = lRow.getY(i);

        lPlot.addPoint("samples", x, y);
      }

      final double lMinX = getMinX();
      final double lMaxX = getMaxX();
      final double lRangeWidth = lMaxX - lMinX;
      final double lStep = (lMaxX - lMinX) / 1024;

      for (double x = lMinX
                      - 0.1
                        * lRangeWidth; x <= lMaxX
                                            + 0.1
                                              * lRangeWidth; x +=
                                                               lStep)
      {
        final double y = getInterpolatedValue(i, x);
        lPlot.addPoint("interpolated", x, y);
      }

      lPlot.ensureUpToDate();

    }

    return lMultiPlot;
  }

}
