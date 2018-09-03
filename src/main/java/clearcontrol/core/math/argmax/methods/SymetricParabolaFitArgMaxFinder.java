package clearcontrol.core.math.argmax.methods;

import clearcontrol.core.math.argmax.ArgMaxFinder1DInterface;
import gnu.trove.list.array.TDoubleArrayList;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * Fits symetric parabolas using triplets of points, and returns the median of
 * all found argmax values.
 *
 * @author royer
 */
public class SymetricParabolaFitArgMaxFinder implements
                                             ArgMaxFinder1DInterface
{

  private PolynomialCurveFitter mPolynomialCurveFitter;

  /**
   * Instantiates a symetric parabola fit argmax finder with default number of
   * iterations.
   */
  public SymetricParabolaFitArgMaxFinder()
  {
    this(1024);
  }

  /**
   * Instantiates a symetric parabola fit argmax finder
   * 
   * @param pMaxIterations
   *          max iterations
   */
  public SymetricParabolaFitArgMaxFinder(int pMaxIterations)
  {
    mPolynomialCurveFitter =
                           PolynomialCurveFitter.create(2)
                                                .withMaxIterations(pMaxIterations);
  }

  @Override
  public Double argmax(double[] pX, double[] pY)
  {
    if (pX.length == 1)
      return pX[0];
    else if (pX.length == 2)
      if (pY[0] > pY[1])
        return pX[0];
      else
        return pX[1];

    TDoubleArrayList lArgMaxList = new TDoubleArrayList();
    for (int i = 0; i < pX.length / 2; i++)
    {
      final double lArgMax = argmaxWithOneParabola(i, pX, pY);
      lArgMaxList.add(lArgMax);
    }
    Median lMedian = new Median();

    double lArgMax = lMedian.evaluate(lArgMaxList.toArray());

    return lArgMax;
  }

  private double argmaxWithOneParabola(int pIndex,
                                       double[] pX,
                                       double[] pY)
  {
    WeightedObservedPoints lObservedPoints =
                                           new WeightedObservedPoints();

    final int lIndexBegin = pIndex;
    final int lIndexMiddle = (pX.length / 2) + pIndex % 2;
    final int lIndexEnd = pX.length - 1 - pIndex;

    lObservedPoints.add(pX[lIndexBegin], pY[lIndexBegin]);
    lObservedPoints.add(pX[lIndexMiddle], pY[lIndexMiddle]);
    lObservedPoints.add(pX[lIndexEnd], pY[lIndexEnd]);

    double[] lFitInfo =
                      mPolynomialCurveFitter.fit(lObservedPoints.toList());

    double a = lFitInfo[2];
    double b = lFitInfo[1];

    double lArgMax = -b / (2 * a);

    return lArgMax;
  }

  @Override
  public String toString()
  {
    return String.format("SymetricParabolaFitArgMaxFinder [mPolynomialCurveFitter=%s]",
                         mPolynomialCurveFitter);
  }

}
