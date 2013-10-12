package stackserver.viewer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

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

	public StackServer3DViewer(final StackSourceInterface pStackSourceInterface)
	{
		mStackSourceInterface = pStackSourceInterface;
		mStackSourceInterface.update();

		mJCudaClearVolumeRenderer = new JCudaClearVolumeRenderer(	this.getClass()
																																	.getSimpleName(),
																															512,
																															512,
																															2);
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

	public void renderToFile(final File p2DImageFile)
	{
		final Stack lStack = mStackSourceInterface.getStack(mStackIndex);

		renderStackToFile(lStack, p2DImageFile);

	}

	private void renderStackToFile(	final Stack pStack,
																	final File pP2dImageFile)
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
	}

	public boolean isShowing()
	{
		return mJCudaClearVolumeRenderer.isShowing();
	}

}
