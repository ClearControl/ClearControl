package acquisition;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import timming.StopWatch;
import utils.concurency.thread.EnhancedThread;


import frames.Frame;
import asyncprocs.AsynchronousProcessorBase;
import asyncprocs.AsynchronousProcessorInterface;

public class AsynchronousFrameRecorder<F extends Frame> extends
																												AsynchronousProcessorBase<F, Boolean>	implements
																																															Closeable
{

	private static final int cBufferFlushSize = 32 * 1024 * 1024;
	private static final int cMaxBufferSize = 2*cBufferFlushSize; // we need a bit more...

	private FileChannel mChannel;
	private long mLastElapsedTimeToWriteFrame;
	private ByteBuffer mWriteByteBuffer = ByteBuffer.allocateDirect(cMaxBufferSize);

	public AsynchronousFrameRecorder(String pName, int pMaxQueueSize)
	{
		super(pName, pMaxQueueSize);
	}

	public void setFile(File pFile) throws FileNotFoundException
	{
		if (mChannel != null)
			close();

		RandomAccessFile lRandomAccessFile = new RandomAccessFile(pFile,"rw");
		//FileOutputStream lFileOutputStream = new FileOutputStream(pFile);
		mChannel = lRandomAccessFile.getChannel();
	}

	@Override
	public Boolean process(F pFrame)
	{

		// LinkedBlockingQueue<FrameInterface> lInputQueue = super.getInputQueue();
		// System.out.println("lInputQueue; "+lInputQueue);

		StopWatch lStopWatch = StopWatch.start();
		pFrame.writeTo(mWriteByteBuffer);

		if (mWriteByteBuffer.position() > cBufferFlushSize)
		{
			// System.out.println("start writing");
			final double lNumberOfMegaBytesToWrite = ((double)mWriteByteBuffer.position())/(1024*1024);
			
			mWriteByteBuffer.flip();
			final StopWatch lWriteSpeedWatch = StopWatch.start();
			while (mWriteByteBuffer.hasRemaining())
			{
				// System.out.print(".");
				try
				{
					mChannel.write(mWriteByteBuffer);
				}
				catch (IOException e)
				{
					System.err.println(this.getClass().getSimpleName() + ": "
															+ e.getLocalizedMessage());
				}
			}
			final double lTimeInSecondsNeededToWrite = 0.001*lWriteSpeedWatch.time(TimeUnit.MILLISECONDS);
			final double lWriteSpeed = lNumberOfMegaBytesToWrite/lTimeInSecondsNeededToWrite;
			//System.out.println("lTimeInSecondsNeededToWrite: "+lTimeInSecondsNeededToWrite+" s");
			//System.out.println("lNumberOfMegaBytesToWrite: "+lNumberOfMegaBytesToWrite+" MB");
			System.out.println("Write speed: "+lWriteSpeed+" MB/s");
			// System.out.println("\nfinished writing!");
			try
			{
				mChannel.force(false);
			}
			catch (IOException e)
			{
				System.err.println(this.getClass().getSimpleName() + ": "
														+ e.getLocalizedMessage());
			}
			mWriteByteBuffer.clear();
		}

		pFrame.releaseFrame();

		mLastElapsedTimeToWriteFrame = lStopWatch.time(TimeUnit.MILLISECONDS);

		if (mLastElapsedTimeToWriteFrame > 10)
			System.out.println("TOO LONG: " + mLastElapsedTimeToWriteFrame);

		return true;
	}

	@Override
	public void close()
	{
		super.close();
		try
		{
			mChannel.close();
		}
		catch (IOException e)
		{
			System.err.println(e.getLocalizedMessage());
		}
	}

	public long getLastElapsedTimeToWriteFrame()
	{
		return mLastElapsedTimeToWriteFrame;
	}

}
