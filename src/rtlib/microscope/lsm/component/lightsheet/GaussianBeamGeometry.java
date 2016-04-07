package rtlib.microscope.lsm.component.lightsheet;

import static java.lang.Math.PI;
import static java.lang.Math.tan;

public class GaussianBeamGeometry
{
	public static double getBeamIrisDiameter(	double pFocalLengthInMicrons,
																						double pLambdaInMicrons,
																						double pBeamLength)
	{
		return pFocalLengthInMicrons * tan((2 * pLambdaInMicrons) / (PI * pBeamLength));
	}
}
