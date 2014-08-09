package rtlib.ao.zernike;

import static java.lang.Math.cos;
import static java.lang.Math.sqrt;

import org.ejml.data.DenseMatrix64F;

public class TransformMatrices
{
	public static DenseMatrix64F computeZernickeTransformMatrix(int pSquareImageWidthHeight)
	{
		int lZernikeVector = pSquareImageWidthHeight * pSquareImageWidthHeight;
		DenseMatrix64F lDenseMatrix64F = new DenseMatrix64F(lZernikeVector,
																												lZernikeVector);

		for (int j = 0; j < pSquareImageWidthHeight; j++)
			for (int i = 0; i < pSquareImageWidthHeight; i++)
				for (int v = 0; v < pSquareImageWidthHeight; v++)
					for (int u = 0; u < pSquareImageWidthHeight; u++)
					{
						final double x = (2.0 / pSquareImageWidthHeight) * (u - (pSquareImageWidthHeight - 1.0) / 2);
						final double y = (2.0 / pSquareImageWidthHeight) * (v - (pSquareImageWidthHeight - 1.0) / 2);

						double lZernikeValue = ZernikePolynomials.computeZijxy(	i,
																																		j,
																																		x,
																																		y);

						// normalization:
						/*lZernikeValue /= sqrt(((j - i) == 0 ? 2 : 1) * PI
																						/ (2 * (i + j) + 2));/**/

						// if (i == 0 && j == 0)
						// lZernikeValue = 1.0 / pSquareImageWidthHeight;

						setMatrixToMatrixLinearMap(	lDenseMatrix64F,
																				pSquareImageWidthHeight,
																				i,
																				j,
																				u,
																				v,
																				lZernikeValue);
					}

		return lDenseMatrix64F;
	}

	public static DenseMatrix64F computeCosineTransformMatrix(int pSquareImageWidthHeight)
	{
		int lZernikeVector = pSquareImageWidthHeight * pSquareImageWidthHeight;
		DenseMatrix64F lDenseMatrix64F = new DenseMatrix64F(lZernikeVector,
																												lZernikeVector);

		double lPiFactor = Math.PI / pSquareImageWidthHeight;

		for (int j = 0; j < pSquareImageWidthHeight; j++)
			for (int i = 0; i < pSquareImageWidthHeight; i++)
				for (int v = 0; v < pSquareImageWidthHeight; v++)
					for (int u = 0; u < pSquareImageWidthHeight; u++)
					{
						double lCosineValue = cos(lPiFactor * (u + 0.5) * i) * cos(lPiFactor * (v + 0.5)
																																									* j);
						if (i == 0)
							lCosineValue *= 1 / sqrt(2.0);

						if (j == 0)
							lCosineValue *= 1 / sqrt(2.0);

						lCosineValue *= 2.0 / pSquareImageWidthHeight;

						setMatrixToMatrixLinearMap(	lDenseMatrix64F,
																				pSquareImageWidthHeight,
																				i,
																				j,
																				u,
																				v,
																				lCosineValue);
					}

		return lDenseMatrix64F;
	}

	private static void setMatrixToMatrixLinearMap(	DenseMatrix64F pDenseMatrix64F,
																									int pSquareImageWidthHeight,
																									int i,
																									int j,
																									int u,
																									int v,
																									double pZernikeValue)
	{
		final int lColumnOffset = j * pSquareImageWidthHeight + i;
		final int lRowOffset = v * pSquareImageWidthHeight + u;
		pDenseMatrix64F.set(lRowOffset, lColumnOffset, pZernikeValue);
	}
}
