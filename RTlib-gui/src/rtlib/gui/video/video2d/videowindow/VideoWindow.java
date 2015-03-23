package rtlib.gui.video.video2d.videowindow;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.random;
import static java.lang.Math.round;

import java.io.IOException;
import java.nio.Buffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GL;
import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import cleargl.ClearGLDefaultEventListener;
import cleargl.ClearGLWindow;
import cleargl.GLAttribute;
import cleargl.GLFloatArray;
import cleargl.GLProgram;
import cleargl.GLTexture;
import cleargl.GLUniform;
import cleargl.GLVertexArray;
import cleargl.GLVertexAttributeArray;
import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;
import coremem.types.NativeTypeEnum;
import coremem.util.Size;

public class VideoWindow<T extends NativeType<T>> implements
																									AutoCloseable
{

	private static final double cEpsilon = 0.05;

	private static final float cPercentageOfPixelsToSample = 0.01f;

	private static final int cMipMapLevel = 3;

	private T mType;
	private ClearGLWindow mClearGLWindow;
	private volatile int mEffectiveWindowWidth, mEffectiveWindowHeight;

	private volatile int mVideoWidth, mVideoHeight;

	private volatile ContiguousMemoryInterface mSourceBuffer;
	private volatile CountDownLatch mNotifyBufferCopy;
	private volatile int mSourceBufferWidth, mSourceBufferHeight;

	private volatile ContiguousMemoryInterface mConversionBuffer;

	private volatile boolean
			mDisplayFrameRate = true, mDisplayOn = true,
			mManualMinMax = false, mMinMaxFixed = false,
			mIsDisplayLines = false;

	private volatile double mMinIntensity = 0, mMaxIntensity = 1,
			mGamma = 1;

	private final ReentrantLock mSendBufferLock = new ReentrantLock();

	private GLProgram mGLProgramVideoRender;
	private GLAttribute mPositionAttribute, mTexCoordAttribute;
	private GLUniform mTexUnit, mMinimumUniform, mMaximumUniform,
			mGammaUniform;
	private GLVertexArray mQuadVertexArray;
	private GLVertexAttributeArray mPositionAttributeArray,
			mTexCoordAttributeArray;
	private GLTexture mTexture;

	private GLProgram mGLProgramGuides;
	private GLAttribute mGuidesPositionAttribute;
	private GLVertexAttributeArray mXLinesPositionAttributeArray;
	private GLVertexAttributeArray mGridPositionAttributeArray;
	private GLVertexArray mXLinesGuidesVertexArray;
	private GLVertexArray mGridGuidesVertexArray;

	private double mSampledMinIntensity, mSampledMaxIntensity;

	private ClearGLDefaultEventListener mClearGLDebugEventListener;

	// private GLPixelBufferObject mPixelBufferObject;

	public VideoWindow(	final String pWindowName,
											final T pType,
											final int pWindowWidth,
											final int pWindowHeight) throws GLException
	{
		mType = pType;
		mVideoWidth = pWindowWidth;
		mVideoHeight = pWindowHeight;

		// this is a guess until we get the actual values:
		mEffectiveWindowWidth = pWindowWidth;
		mEffectiveWindowHeight = pWindowHeight;

		mClearGLDebugEventListener = new ClearGLDefaultEventListener()
		{

			@Override
			public void init(final GLAutoDrawable pGLAutoDrawable)
			{
				super.init(pGLAutoDrawable);
				try
				{
					final GL4 lGL4 = pGLAutoDrawable.getGL().getGL4();
					lGL4.setSwapInterval(1);
					lGL4.glDisable(GL4.GL_DEPTH_TEST);
					lGL4.glDisable(GL4.GL_STENCIL_TEST);
					lGL4.glEnable(GL4.GL_TEXTURE_2D);

					lGL4.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
					lGL4.glClear(GL4.GL_COLOR_BUFFER_BIT);

					getClearGLWindow().setOrthoProjectionMatrix(0,
																											pGLAutoDrawable.getSurfaceWidth(),
																											0,
																											pGLAutoDrawable.getSurfaceHeight(),
																											0,
																											1);

					mGLProgramVideoRender = GLProgram.buildProgram(	lGL4,
																													VideoWindow.class,
																													"shaders/video.vertex.glsl",
																													"shaders/video.fragment.glsl");

					mPositionAttribute = mGLProgramVideoRender.getAtribute("position");
					mTexCoordAttribute = mGLProgramVideoRender.getAtribute("texcoord");
					mTexUnit = mGLProgramVideoRender.getUniform("texUnit");
					mTexUnit.set(0);

					mMinimumUniform = mGLProgramVideoRender.getUniform("minimum");
					mMaximumUniform = mGLProgramVideoRender.getUniform("maximum");
					mGammaUniform = mGLProgramVideoRender.getUniform("gamma");

					mQuadVertexArray = new GLVertexArray(mGLProgramVideoRender);
					mQuadVertexArray.bind();
					mPositionAttributeArray = new GLVertexAttributeArray(	mPositionAttribute,
																																4);

					final GLFloatArray lQuadVerticesFloatArray = new GLFloatArray(6,
																																				4);
					lQuadVerticesFloatArray.add(-1, -1, 0, 1);
					lQuadVerticesFloatArray.add(1, -1, 0, 1);
					lQuadVerticesFloatArray.add(1, 1, 0, 1);
					lQuadVerticesFloatArray.add(-1, -1, 0, 1);
					lQuadVerticesFloatArray.add(1, 1, 0, 1);
					lQuadVerticesFloatArray.add(-1, 1, 0, 1);

					mQuadVertexArray.addVertexAttributeArray(	mPositionAttributeArray,
																										lQuadVerticesFloatArray.getFloatBuffer());

					mTexCoordAttributeArray = new GLVertexAttributeArray(	mTexCoordAttribute,
																																2);

					final GLFloatArray lTexCoordFloatArray = new GLFloatArray(6,
																																		2);
					lTexCoordFloatArray.add(0, 0);
					lTexCoordFloatArray.add(1, 0);
					lTexCoordFloatArray.add(1, 1);
					lTexCoordFloatArray.add(0, 0);
					lTexCoordFloatArray.add(1, 1);
					lTexCoordFloatArray.add(0, 1);

					initializeTexture(mVideoWidth, mVideoHeight);

					mQuadVertexArray.addVertexAttributeArray(	mTexCoordAttributeArray,
																										lTexCoordFloatArray.getFloatBuffer());

					mGLProgramGuides = GLProgram.buildProgram(lGL4,
																										VideoWindow.class,
																										"shaders/guides.vertex.glsl",
																										"shaders/guides.fragment.glsl");

					mGuidesPositionAttribute = mGLProgramGuides.getAtribute("position");

					mXLinesPositionAttributeArray = new GLVertexAttributeArray(	mGuidesPositionAttribute,
																																			4);
					mXLinesGuidesVertexArray = new GLVertexArray(mGLProgramGuides);
					mXLinesGuidesVertexArray.bind();

					final GLFloatArray lXlinesGuidesVerticesFloatArray = new GLFloatArray(4,
																																								4);
					lXlinesGuidesVerticesFloatArray.add(-1, -1, 0, 1);
					lXlinesGuidesVerticesFloatArray.add(+1, +1, 0, 1);
					lXlinesGuidesVerticesFloatArray.add(-1, +1, 0, 1);
					lXlinesGuidesVerticesFloatArray.add(+1, -1, 0, 1);

					mXLinesGuidesVertexArray.addVertexAttributeArray(	mXLinesPositionAttributeArray,
																														lXlinesGuidesVerticesFloatArray.getFloatBuffer());

					mGridPositionAttributeArray = new GLVertexAttributeArray(	mGuidesPositionAttribute,
																																		4);
					mGridGuidesVertexArray = new GLVertexArray(mGLProgramGuides);
					mGridGuidesVertexArray.bind();

					final GLFloatArray lGridGuidesVerticesFloatArray = new GLFloatArray(12,
																																							4);
					final float lRatio = 0.5f;

					lGridGuidesVerticesFloatArray.add(-1.0f, 0, 0, 1.0f);
					lGridGuidesVerticesFloatArray.add(+1.0f, 0, 0, 1.0f);

					lGridGuidesVerticesFloatArray.add(0, -1.0f, 0, 1.0f);
					lGridGuidesVerticesFloatArray.add(0, +1.0f, 0, 1.0f);

					lGridGuidesVerticesFloatArray.add(-1.0f,
																						-lRatio,
																						0.0f,
																						1.0f);
					lGridGuidesVerticesFloatArray.add(+1.0f,
																						-lRatio,
																						0.0f,
																						1.0f);
					lGridGuidesVerticesFloatArray.add(-1.0f,
																						+lRatio,
																						0.0f,
																						1.0f);
					lGridGuidesVerticesFloatArray.add(+1.0f,
																						+lRatio,
																						0.0f,
																						1.0f);
					lGridGuidesVerticesFloatArray.add(-lRatio,
																						-1.0f,
																						0.0f,
																						1.0f);
					lGridGuidesVerticesFloatArray.add(-lRatio,
																						+1.0f,
																						0.0f,
																						1.0f);
					lGridGuidesVerticesFloatArray.add(+lRatio,
																						-1.0f,
																						0.0f,
																						1.0f);
					lGridGuidesVerticesFloatArray.add(+lRatio,
																						+1.0f,
																						0.0f,
																						1.0f);

					mGridGuidesVertexArray.addVertexAttributeArray(	mGridPositionAttributeArray,
																													lGridGuidesVerticesFloatArray.getFloatBuffer());

				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}

			}

			private void initializeTexture(	int pTextureWidth,
																			int pTextureHeight)
			{
				if (mTexture != null)
					mTexture.close();

				NativeTypeEnum lGLType = null;

				if (mType instanceof ByteType)
					lGLType = NativeTypeEnum.Byte;
				else if (mType instanceof UnsignedByteType)
					lGLType = NativeTypeEnum.UnsignedByte;
				else if (mType instanceof ShortType)
					lGLType = NativeTypeEnum.Short;
				else if (mType instanceof UnsignedShortType)
					lGLType = NativeTypeEnum.UnsignedShort;
				else if (mType instanceof IntType)
					lGLType = NativeTypeEnum.Int;
				else if (mType instanceof UnsignedIntType)
					lGLType = NativeTypeEnum.UnsignedInt;
				else if (mType instanceof FloatType)
					lGLType = NativeTypeEnum.Float;
				else if (mType instanceof DoubleType)
					lGLType = NativeTypeEnum.Float;

				mTexture = new GLTexture(	mGLProgramVideoRender,
																	lGLType,
																	1,
																	pTextureWidth,
																	pTextureHeight,
																	1,
																	true,
																	cMipMapLevel);
			}

			@Override
			public void reshape(final GLAutoDrawable pGLAutoDrawable,
													final int x,
													final int y,
													final int pWindowWidth,
													final int pWindowHeight)
			{
				super.reshape(pGLAutoDrawable,
											x,
											y,
											pWindowWidth,
											pWindowHeight);
				mEffectiveWindowWidth = pWindowWidth;
				mEffectiveWindowHeight = pWindowHeight;

			}

			@Override
			public void display(final GLAutoDrawable pGLAutoDrawable)
			{
				super.display(pGLAutoDrawable);
				final GL4 lGL4 = pGLAutoDrawable.getGL().getGL4();

				// System.out.println("DISPLAY");
				if (!mDisplayOn)
					return;

				if (mSourceBuffer != null)
				{
					mSendBufferLock.lock();
					{
						final int lBufferWidth = mSourceBufferWidth;
						final int lBufferHeight = mSourceBufferHeight;
						final ContiguousMemoryInterface lSourceBuffer = mSourceBuffer;
						mSourceBuffer = null;

						if (mVideoWidth != lBufferWidth || mVideoHeight != lBufferHeight
								|| mTexture.getWidth() != lBufferWidth
								|| mTexture.getHeight() != lBufferHeight)
						{
							mVideoWidth = lBufferWidth;
							mVideoHeight = lBufferHeight;
							initializeTexture(mVideoWidth, mVideoHeight);
						}

						if (!mMinMaxFixed)
							fastMinMaxSampling(lSourceBuffer);
						final ContiguousMemoryInterface lConvertedBuffer = convertBuffer(	lSourceBuffer,
																																							lBufferWidth,
																																							lBufferHeight);

						mTexture.copyFrom(lConvertedBuffer);
						mNotifyBufferCopy.countDown();

					}
					mSendBufferLock.unlock();
				}



				{
					if (mManualMinMax)
					{
						mMinimumUniform.set((float) mMinIntensity);
						mMaximumUniform.set((float) mMaxIntensity);
					}
					else
					{
						mMinimumUniform.set((float) mSampledMinIntensity);
						mMaximumUniform.set((float) mSampledMaxIntensity);
					}
					mGammaUniform.set((float) mGamma);

					mGLProgramVideoRender.use(lGL4);
					mTexture.bind(mGLProgramVideoRender);
					// System.out.println("DRAW");
					mQuadVertexArray.draw(GL.GL_TRIANGLES);

					if (isDisplayLines())
					{
						mGLProgramGuides.bind();
						// mXLinesGuidesVertexArray.draw(GL.GL_LINES);
						mGridGuidesVertexArray.draw(GL.GL_LINES);
					}


				}

			}

			private ContiguousMemoryInterface convertBuffer(ContiguousMemoryInterface pSourceBuffer,
																											int pBufferWidth,
																											int pBufferHeight)
			{

				if (mType instanceof DoubleType)
				{
					final int lLengthInFloats = pBufferWidth * pBufferHeight;
					if (mConversionBuffer == null || mConversionBuffer.getSizeInBytes() != lLengthInFloats * Size.FLOAT)
					{
						if (mConversionBuffer != null)
							mConversionBuffer.free();

						mConversionBuffer = OffHeapMemory.allocateFloats(lLengthInFloats);
					}

					for (int i = 0; i < lLengthInFloats; i++)
					{
						final double lValue = pSourceBuffer.getDoubleAligned(i);
						mConversionBuffer.setFloatAligned(i, (float) lValue);
					}

					return mConversionBuffer;
				}

				return pSourceBuffer;
			}

			private void fastMinMaxSampling(final ContiguousMemoryInterface pSourceBuffer)
			{
				final long lLength = mSourceBufferWidth * mSourceBufferHeight;
				final int lStep = 1 + round(cPercentageOfPixelsToSample * lLength);
				final int lStartPixel = (int) round(random() * lStep);

				double lMin = Double.POSITIVE_INFINITY;
				double lMax = Double.NEGATIVE_INFINITY;

				if (mType instanceof UnsignedByteType)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						final double lValue = (0xFF & pSourceBuffer.getByteAligned(i)) / 255d;
						lMin = min(lMin, lValue);
						lMax = max(lMax, lValue);
					}
				else if (mType instanceof UnsignedShortType)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						final double lValue = (0xFFFF & pSourceBuffer.getCharAligned(i)) / 65535d;
						lMin = min(lMin, lValue);
						lMax = max(lMax, lValue);
					}
				else if (mType instanceof UnsignedIntType)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						final double lValue = (0xFFFFFFFF & pSourceBuffer.getIntAligned(i)) / 4294967296d;
						lMin = min(lMin, lValue);
						lMax = max(lMax, lValue);
					}
				else if (mType instanceof FloatType)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						final float lFloatAligned = pSourceBuffer.getFloatAligned(i);
						lMin = min(lMin, lFloatAligned);
						lMax = max(lMax, lFloatAligned);
					}
				else if (mType instanceof DoubleType)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						final double lDoubleAligned = pSourceBuffer.getDoubleAligned(i);
						lMin = min(lMin, lDoubleAligned);
						lMax = max(lMax, lDoubleAligned);
					}

				mSampledMinIntensity = (1 - cEpsilon) * mSampledMinIntensity
																+ cEpsilon
																* lMin;
				mSampledMaxIntensity = (1 - cEpsilon) * mSampledMaxIntensity
																+ cEpsilon
																* lMax;

				// System.out.println("mSampledMinIntensity=" + mSampledMinIntensity);
				// System.out.println("mSampledMaxIntensity=" + mSampledMaxIntensity);
			}

			@Override
			public void dispose(final GLAutoDrawable pGLAutoDrawable)
			{
				super.dispose(pGLAutoDrawable);
			}

			@Override
			public void setClearGLWindow(ClearGLWindow pClearGLWindow)
			{
				mClearGLWindow = pClearGLWindow;
			}

			@Override
			public ClearGLWindow getClearGLWindow()
			{
				return mClearGLWindow;
			}
		};

		mClearGLWindow = new ClearGLWindow(	pWindowName,
																				pWindowWidth,
																				pWindowHeight,
																				mClearGLDebugEventListener);
		mClearGLDebugEventListener.setClearGLWindow(mClearGLWindow);

		final MouseControl lMouseControl = new MouseControl(this);
		mClearGLWindow.addMouseListener(lMouseControl);
		final KeyboardControl lKeyboardControl = new KeyboardControl(this);
		mClearGLWindow.addKeyListener(lKeyboardControl);
	}

	public void setWindowSize(int pWindowWidth, int pWindowHeigth)
	{
		mClearGLWindow.setSize(pWindowWidth, pWindowWidth);
	}

	public int getWindowWidth()
	{
		return mClearGLWindow.getWidth();
	}

	public int getWindowHeight()
	{
		return mClearGLWindow.getHeight();
	}

	public void setWidth(final int pVideoWidth)
	{
		mVideoWidth = pVideoWidth;
	}

	public void setHeight(final int pVideoHeight)
	{
		mVideoHeight = pVideoHeight;
	}

	public void sendBuffer(	ContiguousMemoryInterface pSourceDataObject,
													int pWidth,
													int pHeight)
	{
		mSendBufferLock.lock();
		{

			mNotifyBufferCopy = new CountDownLatch(1);
			mSourceBufferWidth = pWidth;
			mSourceBufferHeight = pHeight;
			mSourceBuffer = pSourceDataObject;
		}
		mSendBufferLock.unlock();
	}

	public void sendBuffer(Buffer pBuffer, int pWidth, int pHeight)
	{
		mSendBufferLock.lock();
		{

			mNotifyBufferCopy = new CountDownLatch(1);
			mSourceBufferWidth = pWidth;
			mSourceBufferHeight = pHeight;
			mSourceBuffer = OffHeapMemory.wrapBuffer(pBuffer);
		}
		mSendBufferLock.unlock();
	}

	public boolean waitForBufferCopy(long pTimeOut, TimeUnit pTimeUnit)
	{
		try
		{
			return mNotifyBufferCopy.await(pTimeOut, pTimeUnit);
		}
		catch (final InterruptedException e)
		{
			e.printStackTrace();
			return waitForBufferCopy(pTimeOut, pTimeUnit);
		}

	}

	public void start()
	{
		mClearGLWindow.start();
	}

	public void stop()
	{
		mClearGLWindow.stop();
	}

	@Override
	public void close() throws IOException
	{

	}



	public void setVisible(final boolean pVisible)
	{
		mClearGLWindow.setVisible(pVisible);
	}

	public boolean isVisible()
	{
		return mClearGLWindow.isVisible();
	}

	public void setDisplayOn(final boolean pDisplayOn)
	{

		mDisplayOn = pDisplayOn;
	}

	public boolean getDisplayOn()
	{
		return mDisplayOn;
	}

	public double getMinIntensity()
	{
		return mMinIntensity;
	}

	public void setMinIntensity(final double pMinIntensity)
	{

		mMinIntensity = pMinIntensity;
	}

	public double getMaxIntensity()
	{
		return mMaxIntensity;
	}

	public void setMaxIntensity(final double pMaxIntensity)
	{

		mMaxIntensity = pMaxIntensity;
	}

	public void setGamma(double pGamma)
	{

		mGamma = pGamma;
	}

	public double getGamma()
	{
		return mGamma;
	}

	public boolean isManualMinMax()
	{
		return mManualMinMax;
	}

	public void setManualMinMax(final boolean pManualMinMax)
	{

		mManualMinMax = pManualMinMax;
	}

	public boolean isMinMaxFixed()
	{
		return mMinMaxFixed;
	}

	public void setMinMaxFixed(final boolean pMinMaxFixed)
	{
		mMinMaxFixed = pMinMaxFixed;
	}

	public boolean isDisplayFrameRate()
	{
		return mDisplayFrameRate;
	}

	public void setDisplayFrameRate(boolean pDisplayFrameRate)
	{

		mDisplayFrameRate = pDisplayFrameRate;
	}

	public boolean isDisplayLines()
	{
		return mIsDisplayLines;
	}

	public void setDisplayLines(boolean pIsDisplayLines)
	{

		mIsDisplayLines = pIsDisplayLines;
	}

	public void disableClose()
	{
		mClearGLWindow.setDefaultCloseOperation(WindowClosingMode.DO_NOTHING_ON_CLOSE);
	}

	public ClearGLWindow getGLWindow()
	{
		return mClearGLWindow;
	}

	public int getEffectiveWindowWidth()
	{
		return mEffectiveWindowWidth;
	}

	public int getEffectiveWindowHeight()
	{
		return mEffectiveWindowHeight;
	}

}
