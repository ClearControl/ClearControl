package rtlib.ip.iqm;

import rtlib.kam.memory.ndarray.NDArrayTyped;

public interface ImageQualityMetricInterface<O>
{

	double[] computeImageQualityMetric(NDArrayTyped<O> pNDArray);

}
