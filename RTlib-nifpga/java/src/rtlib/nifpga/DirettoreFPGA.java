package rtlib.nifpga;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.bridj.Pointer;

import rtlib.nifpga.bridj.DirettoreLibrary;
import rtlib.nifpga.bridj.LStrHandleStruct;
import rtlib.nifpga.bridj.TD1;

public class DirettoreFPGA implements Closeable
{

	public static final int cNanosecondsPerTicks = 25;
	private static final int cMinimumDeltaTimeInNanoseconds = (3000);
	public static final int cNumberOfChannels = 16;
	public static final int cFIFODepth = 3;

	private Pointer<Pointer<Integer>> mFPGAReference;
	private Pointer<TD1> mErrorOut;
	private boolean mError = false;

	private int mTriggerFIFODepth = cFIFODepth;
	private int mMatrixFIFODepth = cFIFODepth;
	Pointer<Integer> mPointerToSpaceLeftInQueue;

	private final Object mLockObject = new Object();

	public boolean open()
	{
		synchronized (mLockObject)
		{
			mFPGAReference = Pointer.allocatePointer(Integer.class);
			mErrorOut = Pointer.allocate(TD1.class);
			mPointerToSpaceLeftInQueue = Pointer.allocate(Integer.class);

			DirettoreLibrary.direttoreOpen(	mTriggerFIFODepth,
																			mMatrixFIFODepth,
																			mFPGAReference,
																			mErrorOut);
			return !(mError = reportError(mErrorOut));
		}
	}

	@Override
	public void close()
	{
		synchronized (mLockObject)
		{
			DirettoreLibrary.direttoreClose(mFPGAReference, mErrorOut);
			mErrorOut.release();
			mFPGAReference.release();
			mPointerToSpaceLeftInQueue.release();
			mError = reportError(mErrorOut);
		}
	}

	public boolean start()
	{
		synchronized (mLockObject)
		{
			if (mError)
				return false;
			DirettoreLibrary.direttoreStart(mFPGAReference, mErrorOut);
			return !(mError = reportError(mErrorOut));
		}
	}

	public boolean stop()
	{
		synchronized (mLockObject)
		{
			DirettoreLibrary.direttoreStop(mFPGAReference, mErrorOut);
			return !(mError = reportError(mErrorOut));
		}
	}

	public long play(	double pDeltaTimeInMicroSeconds,
										int pNumberOfTimePointsToPlay,
										int pSyncChannel,
										int pSyncMode,
										final int pNumberOfMatrices,
										ShortBuffer pMatricesBuffer)
	{
		synchronized (mLockObject)
		{
			final IntBuffer lDeltaTimeBuffer = ByteBuffer.allocateDirect(pNumberOfMatrices * 4)
																										.order(ByteOrder.nativeOrder())
																										.asIntBuffer();

			final IntBuffer lNbTimePointsBuffer = ByteBuffer.allocateDirect(pNumberOfMatrices * 4)
																											.order(ByteOrder.nativeOrder())
																											.asIntBuffer();

			final ByteBuffer lSyncControlByteBuffer = ByteBuffer.allocateDirect(pNumberOfMatrices * 4)
																													.order(ByteOrder.nativeOrder());
			final IntBuffer lSyncControlShortBuffer = lSyncControlByteBuffer.asIntBuffer();

			final short lDeltaTimeInTicks = convertMicroSecondsToTicks(pDeltaTimeInMicroSeconds);

			for (int m = 0; m < pNumberOfMatrices; m++)
			{
				lDeltaTimeBuffer.put(lDeltaTimeInTicks);
				lNbTimePointsBuffer.put(pNumberOfTimePointsToPlay);
				lSyncControlByteBuffer.put((byte) pSyncMode);
				lSyncControlByteBuffer.put((byte) pSyncChannel);
				lSyncControlByteBuffer.put((byte) 0);
				lSyncControlByteBuffer.put((byte) 0);
			}

			return play(lDeltaTimeBuffer,
									lNbTimePointsBuffer,
									lSyncControlShortBuffer,
									pNumberOfMatrices,
									pMatricesBuffer);
		}
	}

	public long play(	IntBuffer pDeltaTimeInTicks,
										IntBuffer pNumberOfTimePointsToPlay,
										IntBuffer pSyncControl,
										final int pNumberOfMatrices,
										ShortBuffer pMatricesBuffer)
	{
		synchronized (mLockObject)
		{
			if (mError)
				return -1;

			pDeltaTimeInTicks.rewind();
			pNumberOfTimePointsToPlay.rewind();
			pSyncControl.rewind();
			pMatricesBuffer.rewind();

			Pointer<Integer> lPointerToDeltaTimeInTicksBuffer = Pointer.pointerToInts(pDeltaTimeInTicks);
			Pointer<Integer> lPointerToNumberOfTimePointsToPlay = Pointer.pointerToInts(pNumberOfTimePointsToPlay);
			Pointer<Integer> lPointerToSyncControlBuffer = Pointer.pointerToInts(pSyncControl);
			Pointer<Short> lPointerToMatricesBuffer = Pointer.pointerToShorts(pMatricesBuffer);

			/*
			Pointer<Pointer<Integer > > FPGAReference, 
			Pointer<Short > DeltaTimeArray, 
			int DeltaTimeArrayLength, 
			short NumberOfTimePointsToPlay, 
			Pointer<Short > SyncArray, 
			int SyncArrayLength, 
			short NumberOfMatrices, 
			Pointer<Short > MatricesArray, 
			int MatricesArrayLength, 
			Pointer<Integer > SpaceLeftInQueue, 
			Pointer<TD1 > ErrorOut
			*/

			DirettoreLibrary.direttorePlay(	mFPGAReference,
																			lPointerToDeltaTimeInTicksBuffer,
																			pDeltaTimeInTicks.limit(),
																			lPointerToNumberOfTimePointsToPlay,
																			pNumberOfTimePointsToPlay.limit(),
																			lPointerToSyncControlBuffer,
																			pSyncControl.limit(),
																			pNumberOfMatrices,
																			lPointerToMatricesBuffer,
																			pMatricesBuffer.limit(),
																			mPointerToSpaceLeftInQueue,
																			mErrorOut);

			long lEstimatedPlayBackTimeInNanoseconds = 0;
			pDeltaTimeInTicks.rewind();
			pNumberOfTimePointsToPlay.rewind();
			for (int i = 0; i < pNumberOfMatrices; i++)
			{
				final int lDeltaTimeInTicks = pDeltaTimeInTicks.get();
				final int lNumberOfTimePointsToPlay = pNumberOfTimePointsToPlay.get();
				final int lDeltaTimeInNanoseconds = Math.max(	cMinimumDeltaTimeInNanoseconds,
																											lDeltaTimeInTicks * cNanosecondsPerTicks);
				final long lEstimatedPlayBackTimeInNanosecondsForMatrix = lNumberOfTimePointsToPlay * lDeltaTimeInNanoseconds;
				lEstimatedPlayBackTimeInNanoseconds += lEstimatedPlayBackTimeInNanosecondsForMatrix;
			}

			if (lPointerToDeltaTimeInTicksBuffer != null)
				lPointerToDeltaTimeInTicksBuffer.release();
			if (lPointerToNumberOfTimePointsToPlay != null)
				lPointerToNumberOfTimePointsToPlay.release();
			if (lPointerToSyncControlBuffer != null)
				lPointerToSyncControlBuffer.release();
			if (lPointerToMatricesBuffer != null)
				lPointerToMatricesBuffer.release();

			// (mError = reportError(mErrorOut)) ?

			return lEstimatedPlayBackTimeInNanoseconds;/**/
		}
	}

	public int getSpaceLeftInPlayQueue()
	{
		return mPointerToSpaceLeftInQueue.getInt();
	}

	public double getPercentSpaceLeftInPlayQueue()
	{
		final double lQueueSize = cFIFODepth;
		final double lPercent = (getSpaceLeftInPlayQueue()) / lQueueSize;
		return lPercent;
	}

	public double getPlayQueueCurrentFilledLength()
	{
		return cFIFODepth - mPointerToSpaceLeftInQueue.getInt();
	}

	public void clearError()
	{
		mError = false;
	}

	@SuppressWarnings("deprecation")
	private boolean reportError(Pointer<TD1> pErrorOut)
	{
		final TD1 lTd1 = pErrorOut.get();
		final byte lStatus = lTd1.status();
		final int lCode = lTd1.code();
		String lMessage = "";

		final boolean lError = lStatus != 0;

		if (lError)
		{
			try
			{
				final LStrHandleStruct lLStrHandleStruct = lTd1.source()
																												.get()
																												.get();
				final Pointer<Byte> lStr = lLStrHandleStruct.str();
				final int lStringLength = lLStrHandleStruct.cnt();
				lMessage = new String(lStr.getBytes(), lStringLength);
			}
			catch (Throwable e)
			{
				// System.err.println("Error while retreiving error message string");
			}
		}

		/*System.out.format("Error: status:%d, code: %d, message: %s \n",
											lStatus,
											lCode,
											lMessage);/**/

		return lError;
	}

	public double getTemporalGranularityInMicroseconds()
	{
		return 0.001 * cMinimumDeltaTimeInNanoseconds;
	}

	public int getNumberOfChannels()
	{
		return cNumberOfChannels;
	}

	public short convertMicroSecondsToTicks(double pMicroSeconds)
	{
		if (pMicroSeconds == -1)
			return -1;
		return (short) ((pMicroSeconds * 1000.0) / cNanosecondsPerTicks);
	}

}
