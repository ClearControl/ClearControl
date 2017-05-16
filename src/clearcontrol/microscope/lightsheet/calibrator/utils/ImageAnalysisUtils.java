package clearcontrol.microscope.lightsheet.calibrator.utils;

import static java.lang.Math.max;
import static java.lang.Math.pow;

import java.util.stream.IntStream;

import clearcontrol.stack.OffHeapPlanarStack;
import coremem.ContiguousMemoryInterface;
import coremem.buffers.ContiguousBuffer;
import coremem.fragmented.FragmentedMemoryInterface;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Image analysis utils
 *
 * @author royer
 */
public class ImageAnalysisUtils
{

  /**
   * Computes a given percentile intensity for each plane of a given stack.
   * 
   * @param pStack
   *          stack
   * @param pPercentile
   *          percentile
   * @return percentile for each plane
   */
  public static double[] computePercentileIntensityPerPlane(OffHeapPlanarStack pStack,
                                                            int pPercentile)
  {
    int lNumberOfPlanes = (int) pStack.getDepth();
    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();
    DescriptiveStatistics lDescriptiveStatistics =
                                                 new DescriptiveStatistics();
    double[] lPercentileArray = new double[lNumberOfPlanes];
    for (int p = 0; p < lNumberOfPlanes; p++)
    {
      ContiguousMemoryInterface lContiguousMemoryInterface =
                                                           lFragmentedMemory.get(p);
      ContiguousBuffer lBuffer =
                               ContiguousBuffer.wrap(lContiguousMemoryInterface);

      lDescriptiveStatistics.clear();
      while (lBuffer.hasRemainingByte())
      {
        double lValue = lBuffer.readChar();
        lDescriptiveStatistics.addValue(lValue);
      }
      lPercentileArray[p] =
                          lDescriptiveStatistics.getPercentile(pPercentile);
    }

    return lPercentileArray;
  }

  /**
   * Computes the average intensity of each plane of a given stack
   * 
   * @param pStack
   *          stack
   * @return average intensity per plane
   */
  public static double[] computeImageAverageIntensityPerPlane(OffHeapPlanarStack pStack)
  {
    int lNumberOfPlanes = (int) pStack.getDepth();
    long lNumberOfPixelsPerPlane = pStack.getWidth()
                                   * pStack.getHeight();
    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();
    double[] lIntensityArray = new double[lNumberOfPlanes];
    for (int p = 0; p < lNumberOfPlanes; p++)
    {
      ContiguousMemoryInterface lContiguousMemoryInterface =
                                                           lFragmentedMemory.get(p);
      ContiguousBuffer lBuffer =
                               ContiguousBuffer.wrap(lContiguousMemoryInterface);

      float lSum = 0;
      while (lBuffer.hasRemainingByte())
      {
        lSum += lBuffer.readChar();
      }
      lIntensityArray[p] = lSum / lNumberOfPixelsPerPlane;
    }

    return lIntensityArray;
  }

  /**
   * Computes the average intensity for a given stack
   * 
   * @param pStack
   *          stack
   * @return average intensity for whole stack
   */
  public static double computeImageAverageIntensity(OffHeapPlanarStack pStack)
  {
    double lNumberOfVoxels = pStack.getVolume();

    ContiguousBuffer lBuffer =
                             ContiguousBuffer.wrap(pStack.getContiguousMemory());

    float lSum = 0;
    while (lBuffer.hasRemainingByte())
    {
      lSum += lBuffer.readChar();
    }
    double lIntensity = lSum / lNumberOfVoxels;

    return lIntensity;
  }

  /**
   * Computes the average intensity elevated to a given power per plane of a
   * given stack
   * 
   * @param pStack
   *          stack
   * @param pPower
   *          power
   * @return array of metrics
   */
  public static double[] computeAveragePowerIntensityPerPlane(OffHeapPlanarStack pStack,
                                                              int pPower)
  {
    int lNumberOfPlanes = (int) pStack.getDepth();
    int lNumberOfPixelsPerPlane = (int) (pStack.getWidth()
                                         * pStack.getHeight());

    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();
    double[] lIntensityArray = new double[lNumberOfPlanes];
    for (int p = 0; p < lNumberOfPlanes; p++)
    {
      ContiguousMemoryInterface lContiguousMemoryInterface =
                                                           lFragmentedMemory.get(p);
      ContiguousBuffer lBuffer =
                               ContiguousBuffer.wrap(lContiguousMemoryInterface);

      float lSumOfPowers = 0;

      while (lBuffer.hasRemainingByte())
      {
        float lValue = lBuffer.readChar();
        lSumOfPowers += pow(lValue, pPower);
      }
      lIntensityArray[p] = lSumOfPowers / lNumberOfPixelsPerPlane;
    }

    return lIntensityArray;
  }

  /**
   * Computes the average squared intensity variation per plane
   * 
   * @param pStack
   *          stack
   * @return array of metrics
   */
  public static double[] computeAverageSquareVariationPerPlane(OffHeapPlanarStack pStack)
  {
    int lNumberOfPlanes = (int) pStack.getDepth();
    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();
    double[] lIntensityArray = new double[lNumberOfPlanes];

    IntStream.range(0, lNumberOfPlanes).parallel().forEach((p) -> {

      ContiguousMemoryInterface lContiguousMemoryInterface =
                                                           lFragmentedMemory.get(p);
      ContiguousBuffer lBuffer =
                               ContiguousBuffer.wrap(lContiguousMemoryInterface);

      int lNumberOfPixelsPerPlane = (int) (pStack.getWidth()
                                           * pStack.getHeight());
      float lInverseNumberOfPixelsPerPlane = 1.0f
                                             / lNumberOfPixelsPerPlane;

      float lPreviousValue =
                           lContiguousMemoryInterface.getCharAligned(0);

      float lSumOfPowers = 0;

      while (lBuffer.hasRemainingByte())
      {
        float lValue = lBuffer.readChar();
        float lVariation = lValue - lPreviousValue;
        lSumOfPowers += lInverseNumberOfPixelsPerPlane * lVariation
                        * lVariation;

        lPreviousValue = 0.9f * lPreviousValue + 0.1f * lValue;
      }
      lIntensityArray[p] = lSumOfPowers;

    });

    return lIntensityArray;
  }

  /**
   * Returns the sum of all stack voxel intensities.
   * 
   * @param pStack
   *          stack
   * @return intensity integral
   */
  public static double computeImageSumIntensity(OffHeapPlanarStack pStack)
  {
    int lNumberOfPlanes = (int) pStack.getDepth();
    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();
    float lSumIntensity = 0;
    for (int p = 0; p < lNumberOfPlanes; p++)
    {
      ContiguousMemoryInterface lContiguousMemoryInterface =
                                                           lFragmentedMemory.get(p);
      ContiguousBuffer lBuffer =
                               ContiguousBuffer.wrap(lContiguousMemoryInterface);

      while (lBuffer.hasRemainingByte())
      {
        lSumIntensity += lBuffer.readChar();
      }
    }

    return lSumIntensity;
  }

  /**
   * Removes noise from the stack
   * 
   * @param pStack
   *          stack
   */
  public static void cleanWithMin(OffHeapPlanarStack pStack)
  {
    int lNumberOfPlanes = (int) pStack.getDepth();
    int lWidth = (int) pStack.getWidth();
    int lHeight = (int) pStack.getHeight();
    int lLength = lWidth * lHeight;

    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();
    for (int p = 0; p < lNumberOfPlanes; p++)
    {
      ContiguousMemoryInterface lBuffer = lFragmentedMemory.get(p);

      for (int o = 0; o < 2; o++)
        for (int i = lWidth; i < (lLength - lWidth - 1); i += 2)
        {
          char lN = lBuffer.getCharAligned(o + i - lWidth);
          char lW = lBuffer.getCharAligned(o + i - 1);
          char lC = lBuffer.getCharAligned(o + i);
          char lE = lBuffer.getCharAligned(o + i + 1);
          char lS = lBuffer.getCharAligned(o + i + lWidth);

          char lMin = min(min(min(lN, lW), min(lC, lE)), lS);

          lBuffer.setCharAligned(o + i, lMin);
        }

    }

  }

  private static final char min(char pA, char pB)
  {
    return (pA > pB) ? pB : pA;
  }

  /**
   * Returns the center-of-mass of the brightest voxels per image plane for a
   * given stack.
   * 
   * @param pStack
   *          stack
   * @return array of 2D points, one for each plane.
   */
  public static Vector2D[] findCOMOfBrightestPointsForEachPlane(OffHeapPlanarStack pStack)
  {
    int lNumberOfPlanes = (int) pStack.getDepth();

    Vector2D[] lPoints = new Vector2D[lNumberOfPlanes];

    FragmentedMemoryInterface lFragmentedMemory =
                                                pStack.getFragmentedMemory();

    int lWidth = (int) pStack.getWidth();
    int lHeight = (int) pStack.getHeight();

    TDoubleArrayList lXList = new TDoubleArrayList();
    TDoubleArrayList lYList = new TDoubleArrayList();

    for (int p = 0; p < lNumberOfPlanes; p++)
    {
      ContiguousMemoryInterface lContiguousMemory =
                                                  lFragmentedMemory.get(p);
      ContiguousBuffer lBuffer =
                               ContiguousBuffer.wrap(lContiguousMemory);

      int lMaxValue = 0;
      while (lBuffer.hasRemainingByte())
      {
        lMaxValue = max(lMaxValue, lBuffer.readChar());
      }

      lXList.clear();
      lYList.clear();

      for (int y = 0; y < lHeight; y++)
      {
        long lIndexY = lWidth * y;
        for (int x = 0; x < lWidth; x++)
        {
          long lIndex = lIndexY + x;
          int lValue = lContiguousMemory.getCharAligned(lIndex);
          if (lValue == lMaxValue)
          {
            lXList.add(x);
            lYList.add(y);
            System.out.format("(%d,%d)->%d\n", x, y, lValue);
          }
        }
      }

      double lCOMX = StatUtils.percentile(lXList.toArray(), 50);
      double lCOMY = StatUtils.percentile(lYList.toArray(), 50);

      Vector2D lVector2D = new Vector2D(lCOMX, lCOMY);
      lPoints[p] = lVector2D;
    }

    return lPoints;
  }
}
