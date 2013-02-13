package frames.quantizers;

import java.util.Arrays;

import frames.Frame;

public class AdaptiveQuantizer
{

	private long sum = 0;
	private int[] mHistogram = new int[1 << 16];
	private float[] mWeights = new float[1 << 16];
	private float[] mImportance = new float[1 << 16];
	private short[] m16BitTo8BitLookUpTable = new short[1 << 16];
	private int[] m8BitTo16BitLookupTable = new int[1 << 8];

	private short[] m16BitBuffer;
	private byte[] m8BitBuffer;
	private Frame mFrame8Bit;

	public AdaptiveQuantizer()
	{
		super();

		final int length = mWeights.length;
		for (int i = 0; i < length; i++)
		{
			final float ni = ((float) i) / length;
			mWeights[i] = (float) Math.pow(ni, 4);
		}

	}

	private void computeHistogram(short[] pImage)
	{
		int lsum = 0;
		final int[] lHistogram = mHistogram;
		Arrays.fill(lHistogram, 0);
		final int length = pImage.length;
		for (int i = 0; i < length; i++)
		{
			final int lValue = pImage[i] & 0xffff;
			lsum += lValue;
			lHistogram[lValue]++;
		}
		sum = lsum;
	}

	private void computeImportance()
	{
		final int[] lHistogram = mHistogram;
		final float[] lWeights = mWeights;
		final float[] lImportance = mImportance;
		final int length = lHistogram.length;
		for (int i = 0; i < length; i++)
		{
			lImportance[i] = lHistogram[i] * lWeights[i];
		}
	}

	private void computeLookUpTable()
	{
		final float[] lImportance = mImportance;
		final int length = lImportance.length;
		final int[] l8BitTo16BitLookupTable = m8BitTo16BitLookupTable;
		final short[] l16BitTo8BitLookUpTable = m16BitTo8BitLookUpTable;

		float total = 0;
		for (int i = 0; i < length; i++)
		{
			total += lImportance[i];
		}

		final float bucketsize = total / 256;

		int lLast16BitPosition = 0;
		addIndex(l8BitTo16BitLookupTable, 0, 0);
		short lCurrent8BitIndex = 1;

		float sum = 0;
		for (int i = 0; i < length; i++)
		{
			sum += lImportance[i];

			if (sum > bucketsize)
			{
				sum = sum - bucketsize;
				addIndex(	l8BitTo16BitLookupTable,
									lCurrent8BitIndex++,
									(i + lLast16BitPosition) / 2);
				lLast16BitPosition = i;

				if (lCurrent8BitIndex == 256)
					break;
			}

			l16BitTo8BitLookUpTable[i] = lCurrent8BitIndex;
		}

		// System.out.format("lLast16BitPosition=%d \n", lLast16BitPosition);
		// System.out.format("lCurrent8BitIndex=%d \n", lCurrent8BitIndex);
		// System.out.format("sum=%g \n", sum);

	}

	private final void addIndex(final int[] p8BitTo16BitLookUpTable,
															final int p8BitIndex,
															final int p16BitValue)
	{
		p8BitTo16BitLookUpTable[p8BitIndex] = (short) p16BitValue;
	}

	private void translate(Frame pFrame16Bit, Frame pFrame8Bit)
	{
		final short[] l16BitBuffer = m16BitBuffer;
		final int length = l16BitBuffer.length;
		final byte[] l8BitBuffer = m8BitBuffer;

		for (int i = 0; i < length; i++)
		{
			final short lValue = l16BitBuffer[i];
			final int lIndex = lValue & 0xffff;
			l8BitBuffer[i] = (byte) m16BitTo8BitLookUpTable[lIndex];
		}

		pFrame8Bit.buffer.rewind();
		pFrame8Bit.buffer.put(l8BitBuffer);
	}

	public Frame apply(Frame pFrame16Bit)
	{
		return apply(pFrame16Bit, null);
	}

	public Frame apply(Frame pFrame16Bit, Frame pFrame8Bit)
	{
		final int length = pFrame16Bit.width * pFrame16Bit.height;

		mFrame8Bit = pFrame8Bit;

		if (m16BitBuffer == null || m16BitBuffer.length < length)
		{
			m16BitBuffer = new short[length];

		}
		
		if (m8BitBuffer == null || m8BitBuffer.length < length)
		{
			m8BitBuffer = new byte[length];
		}
		
		if (mFrame8Bit == null || mFrame8Bit.width != pFrame16Bit.width
				|| mFrame8Bit.height != pFrame16Bit.height)
		{
			mFrame8Bit = new Frame(	pFrame16Bit.index,
															pFrame16Bit.width,
															pFrame16Bit.height,
															1);
		}

		mFrame8Bit.index = pFrame16Bit.index;

		pFrame16Bit.buffer.rewind();
		pFrame16Bit.buffer.asShortBuffer().get(m16BitBuffer);

		computeHistogram(m16BitBuffer);
		computeImportance();
		computeLookUpTable();

		translate(pFrame16Bit, mFrame8Bit);

		return mFrame8Bit;
	}

	public int[] getHistogram()
	{
		return mHistogram;
	}

	public float[] getImportance()
	{
		return mImportance;
	}

	public final int[] get8BitTo16BitLookupTable()
	{
		return m8BitTo16BitLookupTable;
	}

	public final short[] get16BitTo8BitLookUpTable()
	{
		return m16BitTo8BitLookUpTable;
	}

}
