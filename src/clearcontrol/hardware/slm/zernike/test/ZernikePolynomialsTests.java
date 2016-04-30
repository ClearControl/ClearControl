package clearcontrol.hardware.slm.zernike.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import clearcontrol.hardware.slm.zernike.ZernikePolynomials;

public class ZernikePolynomialsTests
{

	private static final double cEpsilon = 1e-10;

	/**
	 * Zernicke radial polynomial R^m_n(x,y) tested against using values returned
	 * Wolfram Alpha's function ZernikeR[0,0,0] (http://www.wolframalpha.com/)
	 * 
	 */
	@Test
	public void testZmnxy()
	{
		assertEquals(	1,
									ZernikePolynomials.computeZnmrt(0, 0, 0, 0),
									cEpsilon);

		assertEquals(	-0.625,
									ZernikePolynomials.computeZnmrt(3, 1, 0.5, 0),
									cEpsilon);

		assertEquals(	-0.5,
									ZernikePolynomials.computeZnmrt(2,
																									0,
																									0.5,
																									Math.PI / 2),
									cEpsilon);

	}

	/**
	 * Zernicke radial polynomial R^m_n(r) tested against values returned Wolfram
	 * Alpha's function ZernikeR[0,0,0] (http://www.wolframalpha.com/)
	 * 
	 */
	@Test
	public void testRmnr()
	{
		assertEquals(1, ZernikePolynomials.computeRnmr(0, 0, 0), cEpsilon);

		assertEquals(	-0.5,
									ZernikePolynomials.computeRnmr(2, 0, 0.5),
									cEpsilon);

		assertEquals(	1.0,
									ZernikePolynomials.computeRnmr(0, 0, 0),
									cEpsilon);

		assertEquals(	0,
									ZernikePolynomials.computeRnmr(0, 2, 0.5),
									cEpsilon);

		assertEquals(	0.0,
									ZernikePolynomials.computeRnmr(1, 1, 0),
									cEpsilon);

		assertEquals(	0.5,
									ZernikePolynomials.computeRnmr(1, 1, 0.5),
									cEpsilon);

		assertEquals(	1.0,
									ZernikePolynomials.computeRnmr(1, 1, 1),
									cEpsilon);

		assertEquals(	0.0,
									ZernikePolynomials.computeRnmr(2, 1, 0.33),
									cEpsilon);

		assertEquals(	0.0,
									ZernikePolynomials.computeRnmr(1, 2, 0.33),
									cEpsilon);

		assertEquals(	0.1089,
									ZernikePolynomials.computeRnmr(2, 2, 0.33),
									cEpsilon);

		assertEquals(	9.00313068e-19,
									ZernikePolynomials.computeRnmr(100, 100, 0.66),
									cEpsilon);

	}

}
