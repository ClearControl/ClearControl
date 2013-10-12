package stackserver.viewer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import javax.media.opengl.GL2;

import ndarray.implementations.heapbuffer.directbuffer.NDArrayDirectBufferByte;
import stack.Stack;
import stackserver.StackSourceInterface;
import clearvolume.controller.QuaternionRotationController;
import clearvolume.jcuda.JCudaClearVolumeRenderer;
import clearvolume.transfertf.TransfertFunctions;

import com.jogamp.graph.math.Quaternion;

public class StackServer3DViewer implements Closeable
{

	private final StackSourceInterface mStackSourceInterface;
	private final JCudaClearVolumeRenderer mJCudaClearVolumeRenderer;

	private int mStackIndex;
	private final QuaternionRotationController mRenderingRotationController;

	private double mScaleZ = 1;
	private volatile FileChannel mMovieFileChannel;

	public StackServer3DViewer(final StackSourceInterface pStackSourceInterface)
	{
		mStackSourceInterface = pStackSourceInterface;
		mStackSourceInterface.update();

		final ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(512 * 512 * 4);

		mJCudaClearVolumeRenderer = new JCudaClearVolumeRenderer(	this.getClass()
																																	.getSimpleName(),
																															512,
																															512,
																															2)
		{
			@Override
			public void renderedImageHook(final GL2 pGl,
																		final int pPixelBufferObjectId)
			{

				lByteBuffer.rewind();
				pGl.glReadPixels(	0,
													0,
													512,
													512,
													GL2.GL_RED,
													GL2.GL_UNSIGNED_INT,
													lByteBuffer);

				if (mMovieFileChannel != null)
					try
					{
						mMovieFileChannel.write(lByteBuffer);
					}
					catch (final IOException e)
					{
						e.printStackTrace();
					}

				/*
				pGl.glBindBuffer(	GL2.GL_PIXEL_UNPACK_BUFFER,
													pPixelBufferObjectId);

				final ByteBuffer llMappedBuffer = pGl.glMapBuffer(pPixelBufferObjectId,
																													GL2.GL_READ_ONLY);

				if (mMovieFileChannel != null)
					try
					{
						mMovieFileChannel.write(llMappedBuffer);
					}
					catch (final IOException e)
					{
						e.printStackTrace();
					}

				pGl.glUnmapBuffer(pPixelBufferObjectId);
				
				/**/
				super.renderedImageHook(pGl, pPixelBufferObjectId);
			}
		};

		mJCudaClearVolumeRenderer.setTransfertFunction(TransfertFunctions.getGrayLevel());
		mJCudaClearVolumeRenderer.setVisible(true);
		mJCudaClearVolumeRenderer.setGamma(0.1);

		mRenderingRotationController = new QuaternionRotationController();
		mJCudaClearVolumeRenderer.setQuaternionController(mRenderingRotationController);

	}

	public long getNumberOfStacks()
	{
		return mStackSourceInterface.getNumberOfStacks();
	}

	public void setQuaternion(final Quaternion pQuaternion)
	{
		mRenderingRotationController.setQuaternion(pQuaternion);
	}

	public void setStackIndex(final int pStackIndex)
	{
		mStackIndex = pStackIndex;
	}

	public boolean ensureFileOpen(final File pMovieFile)
	{
		if (mMovieFileChannel == null)
			try
			{
				mMovieFileChannel = FileChannel.open(	pMovieFile.toPath(),
																							StandardOpenOption.APPEND,
																							StandardOpenOption.WRITE,
																							StandardOpenOption.CREATE);

				return true;
			}
			catch (final IOException e)
			{
				e.printStackTrace();
				return false;
			}
		return false;
	}

	public void renderToFile(final File pMovieFile)
	{
		ensureFileOpen(pMovieFile);

		final Stack lStack = mStackSourceInterface.getStack(mStackIndex);

		renderStackToFileChannel(lStack, mMovieFileChannel);

	}

	private void renderStackToFileChannel(final Stack pStack,
																				final FileChannel pMovieFileChannel)
	{
		final NDArrayDirectBufferByte lNDimensionalArray = pStack.mNDimensionalArray;

		final ByteBuffer lUnderlyingByteBuffer = lNDimensionalArray.getUnderlyingByteBuffer();

		final int lResolutionX = pStack.getWidth();
		final int lResolutionY = pStack.getHeight();
		final int lResolutionZ = pStack.getDepth();

		/*final ByteBuffer lTestBuffer = getTestBuffer(	lResolutionX,
																									lResolutionY,
																									lResolutionZ);/**/

		mJCudaClearVolumeRenderer.setVolumeDataBuffer(lUnderlyingByteBuffer,
																									lResolutionX,
																									lResolutionY,
																									lResolutionZ);

		mJCudaClearVolumeRenderer.setScaleZ(mScaleZ);

		mJCudaClearVolumeRenderer.requestDisplay();

	}

	private ByteBuffer getTestBuffer(	final int pResolutionX,
																		final int pResolutionY,
																		final int pResolutionZ)
	{
		final byte[] lVolumeDataArray = new byte[pResolutionX * pResolutionY
																							* pResolutionZ
																							* 2];

		for (int z = 0; z < pResolutionZ; z++)
			for (int y = 0; y < pResolutionY; y++)
				for (int x = 0; x < pResolutionX; x++)
				{
					final int lIndex = 2 * (x + pResolutionX * y + pResolutionX * pResolutionY
																													* z);
					lVolumeDataArray[lIndex + 1] = (byte) ((x ^ y ^ z));
				}

		return ByteBuffer.wrap(lVolumeDataArray);
	}

	@Override
	public void close() throws IOException
	{
		mJCudaClearVolumeRenderer.close();
		if (mMovieFileChannel != null)
			mMovieFileChannel.close();
	}

	public boolean isShowing()
	{
		return mJCudaClearVolumeRenderer.isShowing();
	}

	public void setScaleZ(final double pScaleZ)
	{
		mScaleZ = pScaleZ;
	}

	public void setGamma(final double pGamma)
	{
		mJCudaClearVolumeRenderer.setGamma(pGamma);
	}

}
