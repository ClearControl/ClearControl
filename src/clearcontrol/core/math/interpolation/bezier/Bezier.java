package clearcontrol.core.math.interpolation.bezier;

import static java.lang.Math.pow;

public class Bezier
{

  private int x1, x2, x3, x4;

  public Bezier(int pX1, int pX2, int pX3, int pX4)
  {
    super();
    x1 = pX1;
    x2 = pX2;
    x3 = pX3;
    x4 = pX4;
  }

  public double getX(double t)
  {

    double X = x1 + 3 * t * (x2 - x1)
               + 3 * (pow(t, 2)) * (x1 + x3 - (2 * x2))
               + (pow(t, 3)) * (x4 - x1 + 3 * x2 - 3 * x3);

    return X;

  }

}
