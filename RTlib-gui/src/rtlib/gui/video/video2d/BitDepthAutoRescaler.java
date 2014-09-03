package rtlib.gui.video.video2d;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import rtlib.core.memory.TypeId;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;
import rtlib.kam.memory.ndarray.NDArray;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import rtlib.kam.memory.ram.RAM;

public class BitDepthAutoRescaler
{
	private static final float cMinMaxDampeningAlpha = 0.05f;

	private boolean mIsFloat = false;
	private volatile boolean mAutoRescale;

	private volatile double mGamma = 1;
	private volatile boolean mGammaOn = false;

	private volatile float mMinimum = 0,
			mMaximum = Character.MAX_VALUE;

	private float mManualMinimum = 0, mManualMaximum = 1;

	private NDArrayTyped<Byte> mConvertedSourceBuffer;

	public BitDepthAutoRescaler(boolean pIsFloat)
	{
		super();
		mIsFloat = pIsFloat;
	}

	@SuppressWarnings("unchecked")
	public NDArrayTyped<Byte> convertBuffer(final NDArrayTyped<?> pNewContentBuffer)
	{
		if (TypeId.isByte(pNewContentBuffer.getType()))
		{
			return (NDArrayTyped<Byte>) pNewContentBuffer;
		}

		final long lNDArrayLength = pNewContentBuffer.getVolume();
		if (mConvertedSourceBuffer == null || mConvertedSourceBuffer.getVolume() != lNDArrayLength)
		{
			mConvertedSourceBuffer = NDArrayTypedDirect.allocateTXY(Byte.class,
																															pNewContentBuffer.getWidth(),
																															pNewContentBuffer.getHeight());
		}

		if (TypeId.is16bitInt(pNewContentBuffer.getType()))
		{

			convertFrom16bitIntegerAndRescaledAuto(	pNewContentBuffer,
																							mConvertedSourceBuffer,
																							mAutoRescale);
		}
		else if (TypeId.isFloat(pNewContentBuffer.getType()))
		{

			convertFrom32bitFloatAndRescaledAuto(	pNewContentBuffer,
																						mConvertedSourceBuffer,
																						mAutoRescale);

		}
		else if (TypeId.isDouble(pNewContentBuffer.getType()))
		{

			convertFrom64bitFloatAndRescaledAuto(	pNewContentBuffer,
																						mConvertedSourceBuffer,
																						mAutoRescale);

		}

		return mConvertedSourceBuffer;
	}

	private void convertFrom64bitFloatAndRescaledAuto(NDArray pNDArraySource,
																										NDArray pNDArrayDestination,
																										boolean pAutoRescale)
	{

		final double lMinimum = pAutoRescale ? mMinimum
																				: mManualMinimum;
		final double lMaximum = pAutoRescale ? mMaximum
																				: mManualMaximum;
		final double lCurrentWidth = lMaximum - lMinimum;

		double lNewMin = Float.POSITIVE_INFINITY;
		double lNewMax = Float.NEGATIVE_INFINITY;

		final boolean lGammaOn = mGammaOn;
		final double lGamma = getGamma();

		final RAM lSourceRam = pNDArraySource.getRAM();
		final RAM lDestinationRam = pNDArrayDestination.getRAM();
		long length = pNDArraySource.getVolume();
		for (int i = 0; i < length; i++)
		{
			final double lDoubleValue = lSourceRam.getDoubleAligned(i);

			if (pAutoRescale)
			{
				lNewMin = min(lNewMin, lDoubleValue);
				lNewMax = max(lNewMax, lDoubleValue);
			}
			int lIntegerMappedValue = 0;
			if (lCurrentWidth > 0)
			{
				double lNormalizedValue = (((lDoubleValue - lMinimum)) / lCurrentWidth);
				if (lGammaOn)
					lNormalizedValue = pow(lNormalizedValue, lGamma);
				lIntegerMappedValue = (int) (255 * lNormalizedValue);
			}

			/*
			System.out.println("lDoubleValue=" + lDoubleValue);
			System.out.println("lMaximum=" + lMaximum);
			System.out.println("lCurrentWidth=" + lCurrentWidth);
			System.out.println("lIntegerMappedValue=" + lIntegerMappedValue);/**/

			lDestinationRam.setByte(i, clampToByte(lIntegerMappedValue));
		}

		if (pAutoRescale)
		{
			mMinimum = (float) ((1 - cMinMaxDampeningAlpha) * mMinimum + cMinMaxDampeningAlpha * lNewMin);
			mMaximum = (float) ((1 - cMinMaxDampeningAlpha) * mMaximum + cMinMaxDampeningAlpha * lNewMax);
		}
	}

	private void convertFrom32bitFloatAndRescaledAuto(NDArray pNDArraySource,
																										NDArray pNDArrayDestination,
																										boolean pAutoRescale)
	{

		final float lMinimum = (float) (pAutoRescale ? mMinimum
																								: mManualMinimum);
		final double lMaximum = pAutoRescale ? mMaximum
																					: mManualMaximum;
		final float lCurrentWidth = (float) (lMaximum - lMinimum);

		float lNewMin = Float.MAX_VALUE;
		float lNewMax = Float.MIN_VALUE;

		final boolean lGammaOn = mGammaOn;
		final float lGamma = (float) mGamma;

		final RAM lSourceRam = pNDArraySource.getRAM();
		final RAM lDestinationRam = pNDArrayDestination.getRAM();
		long length = pNDArraySource.getVolume();
		for (int i = 0; i < length; i++)
		{
			final float lFloatValue = lSourceRam.getFloatAligned(i);
			if (pAutoRescale)
			{
				lNewMin = min(lNewMin, lFloatValue);
				lNewMax = max(lNewMax, lFloatValue);
			}
			int lIntegerMappedValue = 0;
			if (lCurrentWidth > 0)
			{
				float lNormalizedValue = (((lFloatValue - lMinimum)) / lCurrentWidth);
				if (lGammaOn)
					lNormalizedValue = (float) pow(lNormalizedValue, lGamma);
				lIntegerMappedValue = (int) (255 * lNormalizedValue);
			}
			lDestinationRam.setByte(i, clampToByte(lIntegerMappedValue));
		}

		if (pAutoRescale)
		{
			mMinimum = ((1 - cMinMaxDampeningAlpha) * mMinimum + cMinMaxDampeningAlpha * lNewMin);
			mMaximum = ((1 - cMinMaxDampeningAlpha) * mMaximum + cMinMaxDampeningAlpha * lNewMax);
		}
	}

	private void convertFrom16bitIntegerAndRescaledAuto(NDArray pNDArraySource,
																											NDArray pNDArrayDestination,
																											boolean pAutoRescale)
	{

		try
		{
			final float lMinimum = pAutoRescale	? mMinimum
																					: mManualMinimum * 65535;
			final float lMaximum = pAutoRescale	? mMaximum
																					: mManualMaximum * 65535;
			final float lCurrentWidth = lMaximum - lMinimum;

			long lNewMin = Long.MAX_VALUE;
			long lNewMax = Long.MIN_VALUE;

			final boolean lGammaOn = mGammaOn;
			final float lGamma = (float) mGamma;

			final RAM lSourceRam = pNDArraySource.getRAM();
			final RAM lDestinationRam = pNDArrayDestination.getRAM();
			long length = pNDArraySource.getVolume();
			for (int i = 0; i < length; i++)
			{
				final int lShortValue = lSourceRam.getCharAligned(i) & 0xFFFF;
				if (pAutoRescale)
				{
					lNewMin = min(lNewMin, lShortValue);
					lNewMax = max(lNewMax, lShortValue);
				}
				int lIntegerMappedValue = 0;
				if (lCurrentWidth > 0)
				{
					float lNormalizedValue = (((lShortValue - lMinimum)) / lCurrentWidth);
					if (lGammaOn)
						lNormalizedValue = (float) pow(lNormalizedValue, lGamma);
					lIntegerMappedValue = (int) (255 * lNormalizedValue);
				}
				lDestinationRam.setByte(i, clampToByte(lIntegerMappedValue));
			}

			if (pAutoRescale)
			{
				mMinimum = ((1 - cMinMaxDampeningAlpha) * mMinimum + cMinMaxDampeningAlpha * lNewMin);
				mMaximum = ((1 - cMinMaxDampeningAlpha) * mMaximum + cMinMaxDampeningAlpha * lNewMax);
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	private static byte clampToByte(final long pIntegerMappedValue)
	{
		byte lByteMappedValue;
		if (pIntegerMappedValue <= 0)
		{
			lByteMappedValue = 0;
		}
		else if (pIntegerMappedValue >= 255)
		{
			lByteMappedValue = (byte) 255;
		}
		else
		{
			lByteMappedValue = (byte) pIntegerMappedValue;
		}
		return lByteMappedValue;
	}

	public boolean isAutoRescale()
	{
		return mAutoRescale;
	}

	public void setAutoRescale(boolean pAutoRescale)
	{
		mAutoRescale = pAutoRescale;
	}

	public double getManualMinimum()
	{
		return mManualMinimum;
	}

	public void setManualMinimum(double pManualMinimum)
	{
		mManualMinimum = (float) pManualMinimum;
	}

	public double getMaximum()
	{
		return mManualMaximum;
	}

	public void setMaximum(double pManualMaximum)
	{
		mManualMaximum = (float) pManualMaximum;
	}

	public boolean isFloat()
	{
		return mIsFloat;
	}

	public double getGamma()
	{
		return mGamma;
	}

	public void setGamma(double pGamma)
	{
		mGamma = pGamma;
		if (mGamma != 1)
			mGammaOn = true;
		else if (mGamma == 1)
			mGammaOn = false;
	}

}
