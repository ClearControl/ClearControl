package rtlib.ao.utils;

import static java.lang.Math.min;

import org.ejml.data.DenseMatrix64F;

import rtlib.kam.memory.ndarray.NDArray;
import rtlib.kam.memory.ram.RAM;

public class MatrixConversions
{

	public static final void convertMatrixToNDArray(DenseMatrix64F pDenseMatrix64F,
																									NDArray pNDArray)
	{
		RAM lRam = pNDArray.getRAM();
		double[] lArray = pDenseMatrix64F.data;
		int length = (int) min(lArray.length, pNDArray.getVolume());
		for (int i = 0; i < length; i++)
			lRam.setDoubleAligned(i, lArray[i]);
	}

	public static final void convertNDArrayToMatrix(NDArray pNDArray,
																									DenseMatrix64F pDenseMatrix64F)
	{
		RAM lRam = pNDArray.getRAM();
		double[] lArray = pDenseMatrix64F.data;
		int length = (int) min(lArray.length, pNDArray.getVolume());
		for (int i = 0; i < length; i++)
			lArray[i] = lRam.getDoubleAligned(i);
	}

}
