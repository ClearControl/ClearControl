package rtlib.hardware.slm.zernike.test;

import static org.junit.Assert.assertEquals;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.junit.Test;

import rtlib.hardware.slm.zernike.TransformMatrices;

public class TransformMatricesTests
{

	private static final int cDimension = 8;

	@Test
	public void testZernickeTransformMatrix()
	{
		final DenseMatrix64F lComputeZernickeTransformMatrix = TransformMatrices.computeZernickeTransformMatrix(cDimension);
		System.out.println(lComputeZernickeTransformMatrix);

		final double lDeterminant = CommonOps.det(lComputeZernickeTransformMatrix);
		System.out.println(lDeterminant);

		// assertEquals(1, lDeterminant, 1e-10);
	}

	@Test
	public void testCosineTransformMatrix()
	{
		final DenseMatrix64F lComputeCosineTransformMatrix = TransformMatrices.computeCosineTransformMatrix(cDimension);
		System.out.println(lComputeCosineTransformMatrix);

		final double lDeterminant = CommonOps.det(lComputeCosineTransformMatrix);
		System.out.println(lDeterminant);

		assertEquals(1, lDeterminant, 1e-10);
	}

}
