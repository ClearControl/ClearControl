package clearcontrol.microscope.lightsheet.autopilot.utils;

import java.util.Iterator;

import org.python.bouncycastle.util.Arrays;

/**
 * n-dimensional iterator
 *
 * @author royer
 */
public class NDIterator implements Iterator<int[]>
{

  private int[] mDimensions;
  private int[] mCursor;

  private boolean mReachedEnd = false;

  /**
   * Instanciates a n-dimensional iterator
   * 
   * @param pDimensions
   *          n-dimensional grid dimensions
   */
  public NDIterator(int... pDimensions)
  {
    mDimensions = pDimensions;
    mCursor = new int[pDimensions.length];
  }

  /**
   * Resets iterator
   */
  public void reset()
  {
    Arrays.fill(mCursor, 0);
    mReachedEnd = false;
  }

  @Override
  public boolean hasNext()
  {
    return !mReachedEnd;
  }

  @Override
  public int[] next()
  {
    int[] lCurrentCursor = Arrays.copyOf(mCursor, mCursor.length);
    increment(1);
    return lCurrentCursor;
  }

  private void increment(int pIncrement)
  {
    int lCarry = pIncrement;
    for (int i = 0; i < mDimensions.length && lCarry > 0; i++)
    {
      mCursor[i] += lCarry;
      if (mCursor[i] >= mDimensions[i])
      {
        lCarry = mCursor[i] / mDimensions[i];
        mCursor[i] = mCursor[i] % mDimensions[i];

        if (i == mDimensions.length - 1 && lCarry > 0)
        {
          mReachedEnd = true;
        }
      }
      else
      {
        lCarry = 0;
      }
    }
  }

  /**
   * Returns current coordinate value for given dimension
   * 
   * @param pIndex
   *          dimension index
   * @return coordinate values
   */
  public int get(int pIndex)
  {
    return mCursor[pIndex];
  }

  /**
   * Returns the iterator grid volume which corresponds to the number of
   * iterations
   * 
   * @return number of iterationss
   */
  public int getNumberOfIterations()
  {
    int lSize = 1;

    for (int i = 0; i < mDimensions.length; i++)
      lSize *= mDimensions[i];
    return lSize;
  }

}
