package networkvideo;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


import utils.concurency.thread.EnhancedThread;
import utils.network.jpgvideo.JpgVideoServer;

import timming.StopWatch;

import frames.CompressedFrame;
import frames.Frame;
import frames.quantizers.AdaptiveQuantizer;
import asyncprocs.AsynchronousProcessorBase;
import asyncprocs.AsynchronousProcessorInterface;

public class AsynchronousVideoServer<F extends Frame> extends
																											AsynchronousProcessorBase<F, Void> implements
																																												Closeable
{
	private long mLastElapsedTimeToSendFrame;
	private JpgVideoServer mJpgVideoServer;

	private AdaptiveQuantizer mAdaptiveQuantizer;

	private int mFrameCounter = 0;
	private int mEveryNthFrame = 100;
	private int mMaxQueueSize;
	private boolean mDoCompression;

	public AsynchronousVideoServer(	String pName,
																	int pMaxQueueSize,
																	final int pPort,
																	final boolean pDoCompression,
																	final double pCompressionTarget)
	{
		super(pName, pMaxQueueSize);
		mMaxQueueSize = pMaxQueueSize;
		mDoCompression = pDoCompression;

		mJpgVideoServer = new JpgVideoServer(	pPort,
																					pCompressionTarget,
																					512,
																					512);

		mAdaptiveQuantizer = new AdaptiveQuantizer();
	}

	@Override
	public boolean start()
	{
		try
		{
			mJpgVideoServer.startListening();
			return super.start();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public Void process(F pFrame)
	{

		try
		{
			// System.out.println("-------------> "+mFrameCounter);
			if (super.getInputQueue().size() < mMaxQueueSize) // mFrameCounter %
			// mEveryNthFrame == 0 ||
			{
				if (mDoCompression)
				{
					Frame l8BitFrame = mAdaptiveQuantizer.apply(pFrame);
					mJpgVideoServer.compressAndSendImage(	l8BitFrame.buffer,
																								pFrame.width,
																								pFrame.height);
				}
				else
				{
					mJpgVideoServer.sendImage(pFrame.buffer,
																		pFrame.width,
																		pFrame.height);
				}
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		mFrameCounter++;
		return null;
	}

	@Override
	public void close()
	{
		super.close();
		mJpgVideoServer.stopSending();
		mJpgVideoServer.stopListening();
	}

	public long getLastElapsedTimeToSendFrame()
	{
		return mLastElapsedTimeToSendFrame;
	}

	public boolean isSending()
	{
		return mJpgVideoServer.isSending();
	}

}
