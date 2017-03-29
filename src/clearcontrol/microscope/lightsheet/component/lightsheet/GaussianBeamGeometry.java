package clearcontrol.microscope.lightsheet.component.lightsheet;

import static java.lang.Math.PI;
import static java.lang.Math.tan;

/**
 * Gaussian beam geomtry utils
 *
 * @author royer
 */
public class GaussianBeamGeometry
{
  /**
   * Returns the beam irirs diameter given a focal length, wavelength in
   * microns, and beam length
   * 
   * @param pFocalLengthInMicrons
   *          focal length in microns
   * @param pLambdaInMicrons
   *          wavelength in microns
   * @param pBeamLength
   *          beam length
   * @return beam iris diameter
   */
  public static double getBeamIrisDiameter(double pFocalLengthInMicrons,
                                           double pLambdaInMicrons,
                                           double pBeamLength)
  {
    return pFocalLengthInMicrons
           * tan((2 * pLambdaInMicrons) / (PI * pBeamLength));
  }
}
