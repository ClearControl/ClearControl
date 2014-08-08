package rtlib.gui.video.video2d;

import static java.lang.Math.max;
import static java.lang.Math.min;
import rtlib.kam.memory.impl.direct.NDArrayDirect;
import rtlib.kam.memory.ndarray.NDArray;
import rtlib.kam.memory.ram.RAM;

public class BitDepthAutoRescaler
{
	private static final double cMinMaxDampeningAlpha = 0.05;

	private boolean mIsFloat = false;
	private volatile boolean mAutoRescale;

	private volatile long mIntMinimum = Short.MAX_VALUE,
			mIntMaximum = Short.MIN_VALUE;

	private volatile double mFloatMinimum = Short.MAX_VALUE,
			mFloatMaximum = Short.MIN_VALUE;

	private NDArray mConvertedSourceBuffer;

	public BitDepthAutoRescaler()
	{
		super();
	}

	public NDArray convertBuffer(final NDArray pNewContentBuffer)
	{
		if (pNewContentBuffer.getSizeAlongDimension(0) == 1)
		{
			return pNewContentBuffer;
		}
		else if (pNewContentBuffer.getSizeAlongDimension(0) == 2)
		{
			final long lByteBufferLength = pNewContentBuffer.getRAM()
																											.getSizeInBytes();
			final long lConvertedBuferLength = lByteBufferLength / 2;
			if (mConvertedSourceBuffer == null || mConvertedSourceBuffer.getRAM()
																																	.getSizeInBytes() != lConvertedBuferLength)
			{
				mConvertedSourceBuffer = NDArrayDirect.allocateTXY(	Byte.class,
																														pNewContentBuffer.getWidth(),
																														pNewContentBuffer.getHeight());
			}

			if (mIsFloat)
			{
				// TODO: Implement float,double to byte rescaling too
			}
			else
			{
				convert16to8bitIntegerRescaledAuto(	pNewContentBuffer,
																						mConvertedSourceBuffer,
																						mAutoRescale);
			}

			return mConvertedSourceBuffer;

		}

		return null;
	}

	private void convert16to8bitIntegerRescaledAuto(NDArray pNDArraySource,
																									NDArray pNDArrayDestination,
																									boolean pAutoRescale)
	{

		final long lMinimum = mIntMinimum;
		final long lMaximum = mIntMaximum;
		final long lCurrentWidth = lMaximum - lMinimum;

		long lNewMin = Long.MAX_VALUE;
		long lNewMax = Long.MIN_VALUE;

		final RAM lSourceRam = pNDArraySource.getRAM();
		final RAM lDestinationRam = pNDArrayDestination.getRAM();
		long length = pNDArraySource.getVolume();
		for (int i = 0; i < length; i++)
		{
			final int lShortValue = lSourceRam.getShortAligned(i) & 0xFFFF;
			if (pAutoRescale)
			{
				lNewMin = min(lNewMin, lShortValue);
				lNewMax = max(lNewMax, lShortValue);
			}
			int lIntegerMappedValue = 0;
			if (lCurrentWidth > 0)
			{
				lIntegerMappedValue = (int) ((255 * (lShortValue - lMinimum)) / lCurrentWidth);
			}
			lDestinationRam.setByte(i, clampToByte(lIntegerMappedValue));
		}

		if (pAutoRescale)
		{
			mIntMinimum = (long) ((1 - cMinMaxDampeningAlpha) * mIntMinimum + cMinMaxDampeningAlpha * lNewMin);
			mIntMaximum = (long) ((1 - cMinMaxDampeningAlpha) * mIntMaximum + cMinMaxDampeningAlpha * lNewMax);
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

	public double getMinimum()
	{
		return mIsFloat ? mFloatMinimum : mIntMinimum;
	}

	public void setMinimum(double pMinimum)
	{
		if (mIsFloat)
			mFloatMinimum = pMinimum;
		else
			mIntMinimum = (long) pMinimum;
	}

	public double getMaximum()
	{
		return mIsFloat ? mFloatMaximum : mIntMaximum;
	}

	public void setMaximum(double pMaximum)
	{
		if (mIsFloat)
			mFloatMaximum = pMaximum;
		else
			mIntMaximum = (long) pMaximum;
	}

}
