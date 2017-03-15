package clearcontrol.ip.iqm;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.type.NativeType;

public interface ImageQualityMetricInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>>
{
  double[] computeImageQualityMetric(OffHeapPlanarImg<T, A> pOffHeapPlanarImg);
}
