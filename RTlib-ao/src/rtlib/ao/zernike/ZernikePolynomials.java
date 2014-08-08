package rtlib.ao.zernike;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

public class ZernikePolynomials
{

	/**
	 * Computes the value of the Zernike polynomial Z(i,j) at position (x,y) This
	 * is a re-parametrization of the Zernike basis so that it is easier to
	 * interpret as a transform.
	 * 
	 * @param i
	 *          non-negative integer: i = (n-m)/2
	 * @param j
	 *          non-negative integer: j = (n+m)/2
	 * @param x
	 *          x-coordinate
	 * @param y
	 *          y-coordinate
	 * @return returns value
	 */
	public static double computeZijxy(int i, int j, double x, double y)
	{
		double r2 = (x * x + y * y);
		double t = atan2(y, x);

		int n = j + i;
		int m = j - i;

		return computeZnmr2t(n, m, r2, t);
	}

	/**
	 * Computes the value of the Zernike polynomial Z(n,m) at position (x,y)
	 * 
	 * @param n
	 *          non-negative integer
	 * @param m
	 *          integer
	 * @param x
	 *          x-coordinate
	 * @param y
	 *          y-coordinate
	 * @return returns value
	 */
	public static double computeZnmxy(int n, int m, double x, double y)
	{
		double r2 = (x * x + y * y);
		double t = atan2(y, x);
		return computeZnmr2t(n, m, r2, t);
	}

	/**
	 * Computes the value of the Zernike polynomial Z(n,m) at position (r,t)
	 * (polar coordinates)
	 * 
	 * @param n
	 *          non-negative integer
	 * @param m
	 *          integer
	 * @param r
	 *          x-coordinate
	 * @param t
	 *          y-coordinate
	 * @return returns value
	 */
	public static double computeZnmrt(int n, int m, double r, double t)
	{
		double r2 = r * r;
		return computeZnmr2t(n, m, r2, t);
	}

	/**
	 * Computes the value of the Zernike polynomial Z(n,m) at position (r,t)
	 * (polar coordinates)
	 * 
	 * @param n
	 *          non-negative integer
	 * @param m
	 *          integer
	 * @param r2
	 *          squared modulus (ro)
	 * @param t
	 *          argument (theta)
	 * @return returns value
	 */
	public static double computeZnmr2t(int n, int m, double r2, double t)
	{
		/*if (r2 > 1.0)
		{
			return 0;
		}
		else/**/
		{
			double zr = computeRnmr2(n, m, r2);

			if (m >= 0)
			{
				return zr * cos(m * t);
			}
			else
			{
				return zr * sin(-m * t);
			}
		}
	}

	/**
	 * ComputesZernike radial polynomial R(n,m) value for modulus r
	 * 
	 * @param n
	 * @param m
	 * @param r
	 *          modulus r
	 * @return Zernike radial polynomial R(n,m) value for modulus r
	 */
	public static double computeRnmr(	final int n,
																		final int m,
																		final double r)
	{
		return computeRnmr2(n, m, r * r);
	}

	/**
	 * ComputesZernike radial polynomial R(n,m) value for square modulus r^2
	 * 
	 * @param n
	 * @param m
	 * @param r2
	 *          squared modulus r^2
	 * @return Zernike radial polynomial R(n,m) value for square modulus r^2
	 */
	public static double computeRnmr2(final int n,
																		final int m,
																		final double r2)
	{
		int i;
		int k;

		final int lm = abs(m);
		final int ln = n;

		if ((ln - lm) % 2 != 0)
		{
			return 0;
		}

		double result = 0;
		// if (r2 <= 1.0)
		{
			double sign = 1;
			
			double a=1; // (n-k)!
			double b=1; // k!
			double c=1; // [(n+|m|)/2-k]!
			double d=1; // [(n-|m|)/2-k]!
			
			for (i = 2; i <= ln; i++)
			{
				a *= i;
			}
			for (i = 2; i <= (ln + lm) / 2; i++)
			{
				c *= i;
			}
			for (i = 2; i <= (ln - lm) / 2; i++)
			{
				d *= i;
			}

			double f= 1.0 * a / (b * c * d);

			for (k = 0; k <= (ln - lm) / 2; k++)
			{
				result += sign * f * pow(r2, ln / 2.0 - k);

				if (k < (ln - lm) / 2)
				{
					sign = -sign;

					// For more numerical stability:
					final double da = ln - k;
					final double db =  k + 1;
					final double dc =  (ln + lm) / 2 - k;
					final double dd = (ln - lm) / 2 - k;
					f *= (dc * dd) / (da * db);

					// a = a / (ln - k);
					// b = b * (k + 1);
					// c = c / ((ln + lm) / 2 - k);
					// d = d / ((ln - lm) / 2 - k);
				}
			}
		}
		return result;
	}

}
