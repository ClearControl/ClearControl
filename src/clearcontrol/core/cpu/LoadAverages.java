package clearcontrol.core.cpu;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * Load averages
 *
 * @author royer
 */
public class LoadAverages
{
  static private Unsafe cUnsafe;

  static
  {
    Field f;
    try
    {
      f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      cUnsafe = (Unsafe) f.get(null);
    }
    catch (NoSuchFieldException | SecurityException
        | IllegalArgumentException | IllegalAccessException e)
    {
      e.printStackTrace();
    }
  }

  private static ThreadLocal<double[]> cLoadAverageThreadLocal =
                                                               new ThreadLocal<double[]>();

  /**
   * Class carrying the load averages
   *
   * @author royer
   */
  public static class LoadAveragesResult
  {
    @SuppressWarnings("javadoc")
    public double mOneMinute, mFiveMinute, mFifteenMinute;

    @Override
    public String toString()
    {
      return String.format("LoadAveragesResult [mOneMinute=%s, mFiveMinute=%s, mFifteenMinute=%s]",
                           mOneMinute,
                           mFiveMinute,
                           mFifteenMinute);
    }
  }

  /**
   * Returns the load averages (1min, 5min, 15min)
   * 
   * @return load averages results
   */
  public static final LoadAveragesResult getLoadAverages()
  {
    int lNumberOfCores = Runtime.getRuntime().availableProcessors();

    double[] lLoadAveragesArray = cLoadAverageThreadLocal.get();
    if (lLoadAveragesArray == null)
    {
      lLoadAveragesArray = new double[3];
      cLoadAverageThreadLocal.set(lLoadAveragesArray);
    }
    cUnsafe.getLoadAverage(lLoadAveragesArray, 3);

    LoadAveragesResult lLoadAveragesResult = new LoadAveragesResult();
    lLoadAveragesResult.mOneMinute = lLoadAveragesArray[0]
                                     / lNumberOfCores;
    lLoadAveragesResult.mFiveMinute = lLoadAveragesArray[1]
                                      / lNumberOfCores;
    lLoadAveragesResult.mFifteenMinute = lLoadAveragesArray[2]
                                         / lNumberOfCores;

    return lLoadAveragesResult;
  }

}
