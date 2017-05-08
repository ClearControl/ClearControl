package clearcontrol.core.math.interpolation;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * Single Row in a in interpolation table
 * 
 * @author royer
 */
public class Row implements Comparable<Row>
{
  final double x;
  private final TDoubleArrayList y;
  private volatile boolean mIsUpToDate = false;

  /**
   * Constructs a copy of a row.
   * 
   * @param pRow
   *          row to copy
   */
  public Row(Row pRow)
  {
    x = pRow.x;
    y = new TDoubleArrayList(pRow.y);
    mIsUpToDate = false;
  }

  /**
   * Constructs a copy of a row, but for a different x
   * 
   * @param pRow
   *          row to copy
   * @param pX
   *          new x value
   */
  public Row(Row pRow, double pX)
  {
    x = pX;
    y = new TDoubleArrayList(pRow.y);
    mIsUpToDate = false;
  }

  /**
   * Constructs a Row with a given number of columns at a given X value.
   * 
   * @param pNumberOfColumns
   *          number of columns
   * @param pX
   *          X value
   */
  public Row(int pNumberOfColumns, double pX)
  {
    x = pX;

    if (pNumberOfColumns > 0)
    {
      y = new TDoubleArrayList();
      for (int i = 0; i < pNumberOfColumns; i++)
        y.add(0);
    }
    else
      y = null;

  }

  /**
   * Returns the number of columns
   * 
   * @return number of columns
   */
  public int getNumberOfColumns()
  {
    return y.size();
  }

  /**
   * Returns X value.
   * 
   * @return X value
   */
  public double getX()
  {
    return x;
  }

  /**
   * Returns the Y value at a given column index.
   * 
   * @param pColumnIndex
   *          column index
   * @return Y value
   */
  public double getY(int pColumnIndex)
  {
    return y.get(pColumnIndex);
  }

  /**
   * Sets the Y value for a given column.
   * 
   * @param pColumnIndex
   *          column index
   * @param pValue
   *          Y value
   */
  public void setY(int pColumnIndex, double pValue)
  {
    y.set(pColumnIndex, pValue);
    mIsUpToDate = false;
  }

  /**
   * Adds to the Y value of a given column by a certain amount.
   * 
   * @param pColumnIndex
   *          column index
   * @param pDelta
   *          Y value delta
   */
  public void addY(int pColumnIndex, double pDelta)
  {
    y.set(pColumnIndex, y.get(pColumnIndex) + pDelta);
    mIsUpToDate = false;
  }

  /**
   * Returns true if this Row is up-to-date.
   * 
   * @return true if up-to-date , false otherwise.
   */
  public boolean isUpToDate()
  {
    return mIsUpToDate;
  }

  /**
   * Sets the up-to-date flag of this Row.
   * 
   * @param pIsUpToDate
   *          true if up-to-date, false otherwise
   */
  public void setUpToDate(boolean pIsUpToDate)
  {
    mIsUpToDate = pIsUpToDate;
  }

  @Override
  public int compareTo(Row pRow)
  {
    if (getX() > pRow.getX())
      return 1;
    else if (getX() < pRow.getX())
      return -1;
    return 0;
  }

  @Override
  public String toString()
  {
    return "Row [x=" + x
           + ", y="
           + y
           + ", mIsUpToDate="
           + mIsUpToDate
           + "]";
  }

}