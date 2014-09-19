package rtlib.ip.iqm.fitting;

public class SmartArgMaxFinder implements ArgMaxFinder1D
{

	private ParabolaFitArgMaxFinder mParabolaFitArgMaxFinder;
	private GaussianFitArgMaxFinder mGaussianFitArgMaxFinder;
	private Top5ArgMaxFinder mTop5ParabolaArgMaxFinder;
	private COMArgMaxFinder mCOMArgMaxFinder;
	private ModeArgMaxFinder mModeArgMaxFinder;
	private MedianArgMaxFinder mMedianArgMaxFinder;
	private DenoisingArgMaxFinder mDenoisingArgMaxFinder;

	private EnsembleArgMaxFinder mEnsembleArgMaxFinder;

	public SmartArgMaxFinder()
	{
		super();
		mGaussianFitArgMaxFinder = new GaussianFitArgMaxFinder();
		mParabolaFitArgMaxFinder = new ParabolaFitArgMaxFinder();
		mTop5ParabolaArgMaxFinder = new Top5ArgMaxFinder(new ParabolaFitArgMaxFinder());
		mCOMArgMaxFinder = new COMArgMaxFinder();
		mModeArgMaxFinder = new ModeArgMaxFinder();
		mMedianArgMaxFinder = new MedianArgMaxFinder();
		mDenoisingArgMaxFinder = new DenoisingArgMaxFinder(new ModeArgMaxFinder());

		mEnsembleArgMaxFinder = new EnsembleArgMaxFinder();
		mEnsembleArgMaxFinder.add(mGaussianFitArgMaxFinder);
		mEnsembleArgMaxFinder.add(mParabolaFitArgMaxFinder);
		mEnsembleArgMaxFinder.add(mTop5ParabolaArgMaxFinder);
		mEnsembleArgMaxFinder.add(mCOMArgMaxFinder);
		mEnsembleArgMaxFinder.add(mModeArgMaxFinder);
		mEnsembleArgMaxFinder.add(mMedianArgMaxFinder);
		mEnsembleArgMaxFinder.add(mDenoisingArgMaxFinder);
	}

	@Override
	public double argmax(double[] pX, double[] pY)
	{
		return mDenoisingArgMaxFinder.argmax(pX, pY);
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
