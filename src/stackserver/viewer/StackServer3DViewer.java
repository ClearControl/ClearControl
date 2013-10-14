package stackserver.viewer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

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

	private int mWidth;
	private int mHeight;
	private double mScaleZ = 1;
	private volatile FileChannel mMovieFileChannel;

	public StackServer3DViewer(final StackSourceInterface pStackSourceInterface, final int pWidth, final int pHeight)
	{
		mWidth = pWidth;
		mHeight = pHeight;
		mStackSourceInterface = pStackSourceInterface;
		mStackSourceInterface.update();

		final ByteBuffer lByteBuffer = ByteBuffer.allocateDirect(mWidth * mHeight * 4);

		mJCudaClearVolumeRenderer = new JCudaClearVolumeRenderer(	this.getClass()
																																	.getSimpleName(),
																																	mWidth ,
																																	mHeight,
																															2)
		{
			@Override
			public void renderedImageHook(final GL2 pGl,
																		final int pPixelBufferObjectId)
			{

				lByteBuffer.rewind();
				
				if(mRenderingRotationController==null) return;
				
				

				/*drawAxes(pGl,
				         mRenderingRotationController.getQuaternion(),
				         1.f, 1.f,
				         .12f,
				         4);/**/
				
					
				pGl.glReadPixels(	0,
													0,
													mWidth,
													mHeight,
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
			
			private void drawAxes(final GL2 pGl, final Quaternion lQuaternion,
			                      float x0, float y0,
			                      float pAxesScale, 
			                      final int nWires)
			{
				
				pGl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);
				pGl.glLineWidth(1.0f);
				pGl.glEnable(GL2.GL_LINE_SMOOTH);
				pGl.glEnable(GL2.GL_BLEND);
				pGl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
				
				pGl.glColor4f(1, 1, 1, .4f);
				
				pGl.glMatrixMode(pGl.GL_PROJECTION);
				pGl.glPushMatrix();
				
				pGl.glLoadIdentity();
				gluPerspective(pGl, 45, 1.f, .1f, 20);

				
				//TODO: The normal rotation seems to be inverted,
				//check whether we should negate the angle...
				//float[] lViewMatrix = lQuaternion.toMatrix();
				
				Quaternion lQuatRot = lQuaternion;
				lQuatRot.setW(-lQuatRot.getW());
				float[] lViewMatrix = lQuatRot.toMatrix();
				
				//EndTODO
				
				pGl.glMatrixMode(GL2.GL_MODELVIEW);
				pGl.glPushMatrix();
				pGl.glLoadIdentity();
				
				pGl.glTranslatef(x0, y0 , -3);
				pGl.glMultMatrixf(lViewMatrix,0);
				
				pGl.glScalef(pAxesScale, pAxesScale, pAxesScale);
				for (int i = 0; i < 6; i++){
					
					int xRot = (i%2==0)?1:0;
					int yRot = (i%2==0)?0:1;
					
				  pGl.glRotated(90, xRot, yRot, 0);
				  
				  for (float t = -1f; t<=1.f; t+=2.f/nWires)
					{
				  	
				  	pGl.glBegin(GL2.GL_LINES);
					  
				  	pGl.glVertex3d(-1.f, t, 1.f);
				  	pGl.glVertex3d(1.f, t, 1.f);
				  	pGl.glVertex3d(t,-1.f, 1.f);
				  	pGl.glVertex3d(t,1.f, 1.f);
				  	
				  	pGl.glEnd();
					}
				}
				
				pGl.glMatrixMode(GL2.GL_MODELVIEW);
				pGl.glPopMatrix();
		
				pGl.glMatrixMode(pGl.GL_PROJECTION);
				pGl.glPopMatrix();
				pGl.glPopAttrib();
				
			}

			private void gluPerspective(final GL2 pGl, final float fovy, final float aspect, final float zNear, final float zFar)
				{
				   final float xmin, xmax, ymin, ymax;

				   ymax =  zNear * (float)Math.tan(fovy * Math.PI / 360.0f);
				   ymin = -ymax;
				   xmin = ymin * aspect;
				   xmax = ymax * aspect;

				   pGl.glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
				
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

	public void setMin(final double pMin)
	{
		mJCudaClearVolumeRenderer.setTransferRangeMin(pMin);
	}

	public void setMax(final double pMax)
	{
		mJCudaClearVolumeRenderer.setTransferRangeMax(pMax);
	}

}
