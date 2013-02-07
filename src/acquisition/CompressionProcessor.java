package acquisition;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import recycling.Recycler;
import turbojpeg.utils.StopWatch;
import turbojpegj.TurboJpegJCompressor;
import asyncprocs.ProcessorInterface;

import compression.AdaptiveQuantizer;

import frames.CompressedFrame;
import frames.Frame;

public class CompressionProcessor	implements
																	ProcessorInterface<Frame, CompressedFrame>
{

	ThreadLocal<AdaptiveQuantizer> mAdaptiveQuantizerThreadLocal = new ThreadLocal<AdaptiveQuantizer>();
	ThreadLocal<TurboJpegJCompressor> mTurboJpegJCompressorThreadLocal = new ThreadLocal<TurboJpegJCompressor>();

	ThreadLocal<Recycler<CompressedFrame>> mFrameManagerThreadLocal = new ThreadLocal<Recycler<CompressedFrame>>();

	public CompressionProcessor()
	{
		super();

	}

	@Override
	public CompressedFrame process(Frame pFrame16Bit)
	{

		AdaptiveQuantizer lAdaptiveQuantizer = mAdaptiveQuantizerThreadLocal.get();

		if (lAdaptiveQuantizer == null)
		{
			lAdaptiveQuantizer = new AdaptiveQuantizer();
			mAdaptiveQuantizerThreadLocal.set(lAdaptiveQuantizer);
		}

		TurboJpegJCompressor lTurboJpegJCompressor = mTurboJpegJCompressorThreadLocal.get();
		if (lTurboJpegJCompressor == null)
		{
			lTurboJpegJCompressor = new TurboJpegJCompressor();
			mTurboJpegJCompressorThreadLocal.set(lTurboJpegJCompressor);
		}

		Recycler<CompressedFrame> lFrameRecycler = mFrameManagerThreadLocal.get();
		if (lFrameRecycler == null)
		{
			lFrameRecycler = new Recycler<CompressedFrame>();
			mFrameManagerThreadLocal.set(lFrameRecycler);
		}

		StopWatch lAdaptiveQuantizerStopWatch = StopWatch.start();
		Frame lFrame8Bit = lAdaptiveQuantizer.apply(pFrame16Bit);
		long lElapsedTime = lAdaptiveQuantizerStopWatch.time(TimeUnit.MILLISECONDS);
		// System.out.format("AdaptiveQuantizer: %d ms \n", lElapsedTime);

		final boolean lCompressedSuccessfully = lTurboJpegJCompressor.compressMonochrome(	lFrame8Bit.width,
																																											lFrame8Bit.height,
																																											lFrame8Bit.buffer);/**/
		// System.out.format("TurboJpegJCompressor.compressMonochrome: %d ms \n",
		// lTurboJpegJCompressor.getLastImageCompressionElapsedTimeInMs());
		if (!lCompressedSuccessfully)
			return null;

		final ByteBuffer lCompressedByteBuffer = lTurboJpegJCompressor.getCompressedBuffer();

		final CompressedFrame lCompressedFrame = lFrameRecycler.requestFrame(CompressedFrame.class);

		if (lFrameRecycler.getCounter() > 1000)
			System.out.println("Too many CompressedFrame requested !");

		lCompressedFrame.copyFrom(lCompressedByteBuffer,
															lFrame8Bit.index,
															lFrame8Bit.width,
															lFrame8Bit.height,
															1);/**/

		lCompressedFrame.set8BitTo16BitLookupTable(lAdaptiveQuantizer.get8BitTo16BitLookupTable());

		return lCompressedFrame;
	}

	@Override
	public void close() throws IOException
	{
		mAdaptiveQuantizerThreadLocal = null;
		mTurboJpegJCompressorThreadLocal = null;
		mFrameManagerThreadLocal = null;
	}

}
