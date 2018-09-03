package clearcontrol.core.math.outliers;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.List;

import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.StatUtils;

/**
 * Outlier remover.
 * 
 * Static methods that can remove outliers from lists of double values.
 *
 * @author royer
 */
public class OutlierRemover
{

  /**
   * Returns a filtered list of data points after removing low and high
   * outliers.
   * 
   * @param pDataPoints
   *          data points to remove outliers from.
   * @param pSigmas
   *          number of sigmas of the data to keep.
   * @return filtered data points
   */
  static public TDoubleArrayList removeOutliers(double[] pDataPoints,
                                                double pSigmas)
  {
    final int length = pDataPoints.length;

    final double lMedian = StatUtils.percentile(pDataPoints, 50);

    final double[] lAbsoluteDeviations = new double[length];

    for (int i = 0; i < length; i++)
      lAbsoluteDeviations[i] = abs(pDataPoints[i] - lMedian);

    final double lMedianAbsoluteDeviation =
                                          StatUtils.percentile(lAbsoluteDeviations,
                                                               50);

    final double lLowerBound = lMedian
                               - pSigmas * lMedianAbsoluteDeviation;
    final double lUpperBound = lMedian
                               + pSigmas * lMedianAbsoluteDeviation;

    final TDoubleArrayList lResultArray =
                                        new TDoubleArrayList(length);

    for (int i = 0; i < length; i++)
      if (pDataPoints[i] > lLowerBound
          && pDataPoints[i] < lUpperBound)
        lResultArray.add(pDataPoints[i]);

    return lResultArray;
  }

  /**
   * Returns a filtered list of data points after removing low and high
   * outliers.
   * 
   * @param pDataPoints
   *          data points
   * @param pSigmas
   *          number of sigmas of the data to keep.
   * @return filtere data points
   */
  static public <O> ArrayList<Pair<O, Double>> removeOutliers(List<Pair<O, Double>> pDataPoints,
                                                              double pSigmas)
  {
    final int length = pDataPoints.size();

    final TDoubleArrayList lDataPoints = new TDoubleArrayList();

    for (final Pair<O, Double> lValuedObject : pDataPoints)
      lDataPoints.add(lValuedObject.getValue());

    final double lMedian = StatUtils.percentile(lDataPoints.toArray(),
                                                50);

    final double[] lAbsoluteDeviations = new double[length];

    for (int i = 0; i < length; i++)
      lAbsoluteDeviations[i] = abs(lDataPoints.getQuick(i) - lMedian);

    final double lMedianAbsoluteDeviation =
                                          StatUtils.percentile(lAbsoluteDeviations,
                                                               50);

    final double lLowerBound = lMedian
                               - pSigmas * lMedianAbsoluteDeviation;
    final double lUpperBound = lMedian
                               + pSigmas * lMedianAbsoluteDeviation;

    final ArrayList<Pair<O, Double>> lResultList =
                                                 new ArrayList<>(length);

    for (final Pair<O, Double> lValuedObject : pDataPoints)
      if (lValuedObject.getValue() > lLowerBound
          && lValuedObject.getValue() < lUpperBound)
        lResultList.add(lValuedObject);
    /*else
    	System.out.println("removed: " + lValuedObject);/**/

    return lResultList;
  }
}
