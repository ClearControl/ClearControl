package frames.quantizers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import recycling.Recycler;
import turbojpeg.utils.StopWatch;
import turbojpegj.TurboJpegJCompressor;
import asyncprocs.ProcessorInterface;


import frames.CompressedFrame;
import frames.Frame;
import frames.quantizers.AdaptiveQuantizer;

public class AdaptiveQuantizerProcessor	implements
																	ProcessorInterface<Frame, Frame>
{

	ThreadLocal<AdaptiveQuantizer> mAdaptiveQuantizerThreadLocal = new ThreadLocal<AdaptiveQuantizer>();
	ThreadLocal<Recycler<Frame>> mFrameRecyclerThreadLocal = new ThreadLocal<Recycler<Frame>>();
	private long mElapsedTime;

	public AdaptiveQuantizerProcessor()
	{
		super();

	}

	@Override
	public Frame process(Frame pFrame16Bit)
	{

		AdaptiveQuantizer lAdaptiveQuantizer = mAdaptiveQuantizerThreadLocal.get();

		if (lAdaptiveQuantizer == null)
		{
			lAdaptiveQuantizer = new AdaptiveQuantizer();
			mAdaptiveQuantizerThreadLocal.set(lAdaptiveQuantizer);
		}

		Recycler<Frame> lFrameRecycler = mFrameRecyclerThreadLocal.get();
		if (lFrameRecycler == null)
		{
			lFrameRecycler = new Recycler<Frame>(Frame.class);
			mFrameRecyclerThreadLocal.set(lFrameRecycler);
		}

		final Frame l8BitFrame = lFrameRecycler.requestFrame();
		
		l8BitFrame.width = pFrame16Bit.width;
		l8BitFrame.height = pFrame16Bit.height;
		l8BitFrame.bpp=1;
		final int l8BitBufferLength = l8BitFrame.width*l8BitFrame.height*l8BitFrame.bpp;
		if(l8BitFrame.buffer==null || l8BitFrame.buffer.capacity()<l8BitBufferLength)
		{
			l8BitFrame.buffer = ByteBuffer.allocateDirect(l8BitBufferLength).order(ByteOrder.nativeOrder());
		}
				
		StopWatch lAdaptiveQuantizerStopWatch = StopWatch.start();
		Frame lFrame8Bit = lAdaptiveQuantizer.apply(pFrame16Bit,l8BitFrame);
		mElapsedTime = lAdaptiveQuantizerStopWatch.time(TimeUnit.MILLISECONDS);
	
		if (lFrameRecycler.getCounter() > 1000)
			System.out.println("Too many CompressedFrame requested !");

		pFrame16Bit.releaseFrame();
		
		return lFrame8Bit;
	}

	@Override
	public void close() throws IOException
	{
		mAdaptiveQuantizerThreadLocal = null;
		mFrameRecyclerThreadLocal = null;
	}

	public long getElapsedTime()
	{
		return mElapsedTime;
	}

}
