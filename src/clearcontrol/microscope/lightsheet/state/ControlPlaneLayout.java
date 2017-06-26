package clearcontrol.microscope.lightsheet.state;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

/**
 * Control plane layouts
 *
 * @author royer
 */
public enum ControlPlaneLayout
{
 /**
  * Linear layout: control planes are laid out with equal spacing
  */
 Linear,

 /**
  * Circular layout: control planes are laid with equal spacing on the unit
  * circle and then projected on the x axis.
  */
 Circular;

  /**
   * Returns the position of the control plane of given index for a given number
   * of control planes
   * 
   * @param pNumberOfControlPlanes
   *          number of control planes
   * @param pControlPlaneIndex
   *          control plane index
   * @return normalized position of the control plane within [0,1]
   */
  public double layout(int pNumberOfControlPlanes,
                       int pControlPlaneIndex)
  {
    switch (this)
    {

    case Linear:
      return linear(pNumberOfControlPlanes, pControlPlaneIndex);

    case Circular:
      return circular(pNumberOfControlPlanes, pControlPlaneIndex);

    }

    return linear(pNumberOfControlPlanes, pControlPlaneIndex);
  }

  private double linear(int pNumberOfControlPlanes,
                        int pControlPlaneIndex)
  {
    return ((double) pControlPlaneIndex)
           / (pNumberOfControlPlanes - 1);
  }

  private double circular(int pNumberOfControlPlanes,
                          int pControlPlaneIndex)
  {
    double z = linear(pNumberOfControlPlanes, pControlPlaneIndex);

    double zc = 0.5 * (1 + cos(PI * (1 - z)));

    return zc;
  }

}
