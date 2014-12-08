package rtlib.ao.utils;

import static java.lang.Math.min;

import org.ejml.data.DenseMatrix64F;

import coremem.MemoryRegionInterface;
import rtlib.kam.memory.ndarray.NDArray;

public class MatrixConversions
{

	public static final void convertMatrixToNDArray(DenseMatrix64F pDenseMatrix64F,
																									NDArray pNDArray)
	{
		MemoryRegionInterface lMemoryRegionInterface = pNDArray.getMemoryRegionInterface();
		double[] lArray = pDenseMatrix64F.data;
		int length = (int) min(lArray.length, pNDArray.getVolume());
		for (int i = 0; i < length; i++)
			lMemoryRegionInterface.setDoubleAligned(i, lArray[i]);
	}

	public static final void convertNDArrayToMatrix(NDArray pNDArray,
																									DenseMatrix64F pDenseMatrix64F)
	{
		MemoryRegionInterface lMemoryRegionInterface = pNDArray.getMemoryRegionInterface();
		double[] lArray = pDenseMatrix64F.data;
		int length = (int) min(lArray.length, pNDArray.getVolume());
		for (int i = 0; i < length; i++)
			lArray[i] = lMemoryRegionInterface.getDoubleAligned(i);
	}

}
