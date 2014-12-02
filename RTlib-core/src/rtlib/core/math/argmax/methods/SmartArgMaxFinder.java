package rtlib.core.math.argmax.methods;

import rtlib.core.math.argmax.ArgMaxFinder1D;

public class SmartArgMaxFinder implements ArgMaxFinder1D
{

	private ParabolaFitArgMaxFinder mParabolaFitArgMaxFinder;
	private GaussianFitArgMaxFinder mGaussianFitArgMaxFinder;
	private QuarticFitArgMaxFinder mQuarticFitArgMaxFinder;
	private SplineFitArgMaxFinder mSplineFitArgMaxFinder;
	private RandomSplineFitArgMaxFinder mRandomSplineFitArgMaxFinder;
	private LoessFitArgMaxFinder mLoessFitArgMaxFinder;
	private Top5ArgMaxFinder mTop5ParabolaArgMaxFinder;
	private COMArgMaxFinder mCOMArgMaxFinder;
	private ModeArgMaxFinder mModeArgMaxFinder;
	private MedianArgMaxFinder mMedianArgMaxFinder;
	private DenoisingArgMaxFinder mDenoisingArgMaxFinder;

	public SmartArgMaxFinder()
	{
		super();
		mGaussianFitArgMaxFinder = new GaussianFitArgMaxFinder(16 * 1024);
		mParabolaFitArgMaxFinder = new ParabolaFitArgMaxFinder();
		mQuarticFitArgMaxFinder = new QuarticFitArgMaxFinder();
		mSplineFitArgMaxFinder = new SplineFitArgMaxFinder();
		mRandomSplineFitArgMaxFinder = new RandomSplineFitArgMaxFinder();
		mLoessFitArgMaxFinder = new LoessFitArgMaxFinder();
		mTop5ParabolaArgMaxFinder = new Top5ArgMaxFinder(new ParabolaFitArgMaxFinder());
		mCOMArgMaxFinder = new COMArgMaxFinder();
		mModeArgMaxFinder = new ModeArgMaxFinder();
		mMedianArgMaxFinder = new MedianArgMaxFinder();
		mDenoisingArgMaxFinder = new DenoisingArgMaxFinder(new ModeArgMaxFinder());

	}

	@Override
	public Double argmax(double[] pX, double[] pY)
	{
		int lLocalMaxima = countLocalMaxima(pY);
		boolean lDenoiseBefore = lLocalMaxima > 1;

		EnsembleArgMaxFinder lEnsembleArgMaxFinder = new EnsembleArgMaxFinder();
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mGaussianFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mParabolaFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mQuarticFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mSplineFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mRandomSplineFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mLoessFitArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mTop5ParabolaArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mCOMArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mModeArgMaxFinder));
		lEnsembleArgMaxFinder.add(denoiseBefore(lDenoiseBefore,
																						mMedianArgMaxFinder));
		lEnsembleArgMaxFinder.add(mDenoisingArgMaxFinder);

		return lEnsembleArgMaxFinder.argmax(pX, pY);
	}

	private ArgMaxFinder1D denoiseBefore(	boolean pDenoiseBefore,
																				ArgMaxFinder1D pArgMaxFinder1D)
	{
		if (pDenoiseBefore)
			return new DenoisingArgMaxFinder(pArgMaxFinder1D);
		else
			return pArgMaxFinder1D;
	}

	private int countLocalMaxima(double[] pY)
	{
		int lCount = 0;
		final int lLength = pY.length;
		for (int i = 1; i < lLength - 1; i++)
		{
			final double lY = pY[i];
			final double lYbefore = pY[i - 1];
			final double lYafter = pY[i + 1];
			if (lY > lYbefore && lY > lYafter)
				lCount++;
		}
		return lCount;
	}

	/*
	 * 	double[] lGaussianFittedY = mGaussianFitArgMaxFinder.fit(pX, pY);
		double[] lParabolaFittedY = mParabolaFitArgMaxFinder.fit(pX, pY);

		double lGaussianFitAvgError = ComputeFitError.avgerror(	pY,
																														lGaussianFittedY);
		double lParabolaFitAvgError = ComputeFitError.avgerror(	pY,
																														lParabolaFittedY);

		
		if (lGaussianFitAvgError < lParabolaFitAvgError)
		{
			return mGaussianFitArgMaxFinder.argmax(pX, pY);
		}
		else
		{
			return mParabolaFitArgMaxFinder.argmax(pX, pY);
		}

		int lNumberOfLocalMaxima = countLocalMaxima(pY);

		if (lNumberOfLocalMaxima == 1)
		{
			double lTop5ParabolaFitArgMax = mTop5ArgMaxFinder.argmax(pX, pY);
			return lTop5ParabolaFitArgMax;
		}
	 */
}
