package clearcontrol.core.math.interpolation;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import clearcontrol.gui.plots.MultiPlot;
import clearcontrol.gui.plots.PlotTab;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Author: Robert Haase (http://haesleinhuepf.net) at MPI CBG
 * (http://mpi-cbg.de) March 2018
 */
public abstract class AbstractInterpolationTable
{
  private static final double cEpsilon = 1e-9;

  protected final TreeSet<Row> mTable;
  protected final int mNumberOfColumns;
  protected volatile boolean mIsUpToDate = false;
  protected final ArrayList<UnivariateFunction> mInterpolatingFunctionsList;

  public AbstractInterpolationTable(int pNumberOfColumns)
  {
    super();
    mTable = new TreeSet<>();
    mNumberOfColumns = pNumberOfColumns;
    mInterpolatingFunctionsList = new ArrayList<>();
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
   * Removes all rows.
   */
  public void clear()
  {
    mTable.clear();
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
   * Adds a row after a given X value
   *
   * @param pX
   *          X
   */
  public void addRowAfter(double pX)
  {
    Row lRowBefore = getFloorRow(pX);
    Row lRowAfter = getCeilRow(pX + cEpsilon);

    if (lRowAfter == null)
      lRowAfter = lRowBefore;

    if (lRowBefore == null)
      lRowBefore = lRowAfter;

    if (lRowAfter == null)
    {
      addRow(pX);
      return;
    }

    double lX = (lRowBefore.x + lRowAfter.x) / 2;

    if (pX == lX)
      lX++;

    Row lRow = addRow(lX);

    for (int c = 0; c < lRow.getNumberOfColumns(); c++)
    {
      double lValue = 0.5 * (lRowBefore.getY(c) + lRowAfter.getY(c));
      lRow.setY(c, lValue);
    }
  }

  /**
   * Removes the nearest row for a given X value
   *
   * @param pX
   *          X
   * @return remove row
   */
  public Row removeRow(double pX)
  {
    Row lNearestRow = getNearestRow(pX);
    mTable.remove(lNearestRow);
    mIsUpToDate = false;
    return lNearestRow;
  }

  /**
   * Moves a row of given index to a new value
   *
   * @param pRowIndex
   *          row index
   * @param pNewX
   *          new value
   * @return new row
   */
  public Row moveRow(int pRowIndex, double pNewX)
  {
    Row lOldRow = getRow(pRowIndex);
    mTable.remove(lOldRow);
    Row lNewRow = new Row(lOldRow, pNewX);
    mTable.add(lNewRow);
    mIsUpToDate = false;
    return lNewRow;
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
  public abstract double getInterpolatedValue(int pColumnIndex,
                                              double pX);

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
  protected boolean isIsUpToDate()
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
  protected void setUpToDate()
  {
    mIsUpToDate = true;
    for (final Row lRow : mTable)
    {
      lRow.setUpToDate(true);
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
                      - 0.5
                        * lRangeWidth; x <= lMaxX
                                            + 0.5
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
