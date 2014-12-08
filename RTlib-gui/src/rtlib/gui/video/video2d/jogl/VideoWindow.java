package rtlib.gui.video.video2d.jogl;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.random;
import static java.lang.Math.round;
import static java.lang.Math.toIntExact;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.concurrent.locks.ReentrantLock;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GL;
import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;

import org.bridj.Pointer;

import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import cleargl.ClearGLDefaultEventListener;
import cleargl.ClearGLWindow;
import cleargl.GLAttribute;
import cleargl.GLFloatArray;
import cleargl.GLProgram;
import cleargl.GLTexture;
import cleargl.GLUniform;
import cleargl.GLVertexArray;
import cleargl.GLVertexAttributeArray;

import com.jogamp.newt.opengl.GLWindow;

public class VideoWindow<T> implements AutoCloseable
{

	private static final double cEpsilon = 0.05;

	private static final float cPercentageOfPixelsToSample = 0.01f;

	private static final int cMipMapLevel = 3;

	private Class<T> mType;
	private ClearGLWindow mClearGLWindow;
	private volatile int mVideoWidth, mVideoHeight;

	private NDArrayTyped<T> mSourceBuffer;

	private volatile boolean mIsContextAvailable = false,
			mIsUpToDate = false, mDisplayFrameRate = true,
			mDisplayOn = true, mManualMinMax = false, mMinMaxFixed = false,
			mIsDisplayLines = false;

	private volatile double mMinIntensity = 0, mMaxIntensity = 1,
			mGamma = 1;

	private ReentrantLock mDisplayLock = new ReentrantLock();

	private GLProgram mGLProgramVideoRender;
	private GLAttribute mPositionAttribute, mTexCoordAttribute;
	private GLUniform mTexUnit, mMinimumUniform, mMaximumUniform,
			mGammaUniform;
	private GLVertexArray mQuadVertexArray;
	private GLVertexAttributeArray mPositionAttributeArray,
			mTexCoordAttributeArray;
	private GLTexture<T> mTexture;

	private GLProgram mGLProgramGuides;
	private GLAttribute mGuidesPositionAttribute;
	private GLVertexAttributeArray mXLinesPositionAttributeArray;
	private GLVertexAttributeArray mGridPositionAttributeArray;
	private GLVertexArray mXLinesGuidesVertexArray;
	private GLVertexArray mGridGuidesVertexArray;

	private double mSampledMinIntensity, mSampledMaxIntensity;

	private volatile FloatBuffer mTemporaryFloatBuffer;

	private ClearGLDefaultEventListener mClearGLDebugEventListener;

	// private GLPixelBufferObject mPixelBufferObject;

	public VideoWindow(	final String pWindowName,
											final Class<T> pClass,
											final int pWindowWidth,
											final int pWindowHeight) throws GLException
	{
		mType = pClass;
		mVideoWidth = pWindowWidth;
		mVideoHeight = pWindowHeight;

		mClearGLDebugEventListener = new ClearGLDefaultEventListener()
		{

			@Override
			public void init(final GLAutoDrawable pGLAutoDrawable)
			{
				super.init(pGLAutoDrawable);
				try
				{
					final GL4 lGL4 = pGLAutoDrawable.getGL().getGL4();
					lGL4.setSwapInterval(0);
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

					GLFloatArray lQuadVerticesFloatArray = new GLFloatArray(6,
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

					GLFloatArray lTexCoordFloatArray = new GLFloatArray(6, 2);
					lTexCoordFloatArray.add(0, 0);
					lTexCoordFloatArray.add(1, 0);
					lTexCoordFloatArray.add(1, 1);
					lTexCoordFloatArray.add(0, 0);
					lTexCoordFloatArray.add(1, 1);
					lTexCoordFloatArray.add(0, 1);

					initializeTexture();

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

					GLFloatArray lXlinesGuidesVerticesFloatArray = new GLFloatArray(4,
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

					GLFloatArray lGridGuidesVerticesFloatArray = new GLFloatArray(12,
																																				4);
					float lRatio = 0.5f;

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
				catch (IOException e)
				{
					e.printStackTrace();
				}

			}

			private void initializeTexture()
			{
				if (mTexture != null)
					mTexture.close();

				mTexture = new GLTexture<T>(mGLProgramVideoRender,
																		mType,
																		1,
																		mVideoWidth,
																		mVideoHeight,
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
			}

			@Override
			public void display(final GLAutoDrawable pGLAutoDrawable)
			{
				super.display(pGLAutoDrawable);
				final GL4 lGL4 = pGLAutoDrawable.getGL().getGL4();

				if (mSourceBuffer == null)
					return;

				final int lBufferWidth = (int) mSourceBuffer.getWidth();
				final int lBufferHeight = (int) mSourceBuffer.getHeight();

				if (mVideoWidth != lBufferWidth || mVideoHeight != lBufferHeight
						|| mTexture.getWidth() != lBufferWidth
						|| mTexture.getHeight() != lBufferHeight)
				{
					mVideoWidth = lBufferWidth;
					mVideoHeight = lBufferHeight;
					initializeTexture();
				}

				if (!mIsUpToDate)
				{
					if (!mMinMaxFixed)
						fastMinMaxSampling(mSourceBuffer);
					Buffer lBuffer = convertBuffer();
					lBuffer.rewind();
					mTexture.copyFrom(lBuffer);
				}

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
				mQuadVertexArray.draw(GL.GL_TRIANGLES);

				if (isDisplayLines())
				{
					mGLProgramGuides.bind();
					// mXLinesGuidesVertexArray.draw(GL.GL_LINES);
					mGridGuidesVertexArray.draw(GL.GL_LINES);
				}
			}

			private Buffer convertBuffer()
			{
				if (mType == Byte.class)
					return mSourceBuffer.getBridJPointer(mType).getByteBuffer();
				if (mType == Short.class || mType == Character.class)
					return mSourceBuffer.getBridJPointer(mType)
															.getShortBuffer();
				if (mType == Integer.class)
					return mSourceBuffer.getBridJPointer(mType).getIntBuffer();
				if (mType == Float.class)
					return mSourceBuffer.getBridJPointer(mType)
															.getFloatBuffer();
				if (mType == Double.class)
				{
					int lLength = toIntExact(mSourceBuffer.getVolume());
					if (mTemporaryFloatBuffer == null || mTemporaryFloatBuffer.capacity() != mSourceBuffer.getVolume())
					{
						mTemporaryFloatBuffer = ByteBuffer.allocateDirect(lLength * Float.BYTES)
																							.order(ByteOrder.nativeOrder())
																							.asFloatBuffer();
					}

					DoubleBuffer lDoubleBuffer = mSourceBuffer.getBridJPointer(mType)
																										.getDoubleBuffer();

					lDoubleBuffer.clear();
					mTemporaryFloatBuffer.clear();
					for (int i = 0; i < lLength; i++)
					{
						double lValue = lDoubleBuffer.get();
						mTemporaryFloatBuffer.put((float) lValue);
					}

					mTemporaryFloatBuffer.clear();
					return mTemporaryFloatBuffer;
				}

				return null;
			}

			private void fastMinMaxSampling(NDArrayTyped<T> pSourceBuffer)
			{
				long lLength = pSourceBuffer.getVolume();
				int lStep = 1 + round(cPercentageOfPixelsToSample * lLength);
				int lStartPixel = (int) round(random() * lStep);

				double lMin = Double.POSITIVE_INFINITY;
				double lMax = Double.NEGATIVE_INFINITY;

				if (mType == Byte.class)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						double lValue = (0xFF & pSourceBuffer.getByteAligned(i)) / 255d;
						lMin = min(lMin, lValue);
						lMax = max(lMax, lValue);
					}
				else if (mType == Short.class)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						double lValue = (0xFFFF & pSourceBuffer.getShortAligned(i)) / (65535d);
						lMin = min(lMin, lValue);
						lMax = max(lMax, lValue);
					}
				else if (mType == Character.class)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						double lValue = (0xFFFF & pSourceBuffer.getCharAligned(i)) / 65535d;
						lMin = min(lMin, lValue);
						lMax = max(lMax, lValue);
					}
				else if (mType == Integer.class)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						double lValue = (0xFFFFFFFF & pSourceBuffer.getIntAligned(i)) / 4294967296d;
						lMin = min(lMin, lValue);
						lMax = max(lMax, lValue);
					}
				else if (mType == Float.class)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						float lFloatAligned = pSourceBuffer.getFloatAligned(i);
						lMin = min(lMin, lFloatAligned);
						lMax = max(lMax, lFloatAligned);
					}
				else if (mType == Double.class)
					for (int i = lStartPixel; i < lLength; i += lStep)
					{
						double lDoubleAligned = pSourceBuffer.getDoubleAligned(i);
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

		MouseControl lMouseControl = new MouseControl(this);
		mClearGLWindow.getGLWindow().addMouseListener(lMouseControl);
		KeyboardControl lKeyboardControl = new KeyboardControl(this);
		mClearGLWindow.getGLWindow().addKeyListener(lKeyboardControl);
	}

	public void setWindowSize(int pWindowWidth, int pWindowHeigth)
	{
		mClearGLWindow.getGLWindow().setSize(pWindowWidth, pWindowWidth);
	}

	public int getWindowWidth()
	{
		return mClearGLWindow.getGLWindow().getWidth();
	}

	public int getWindowHeight()
	{
		return mClearGLWindow.getGLWindow().getHeight();
	}

	public void setWidth(final int pVideoWidth)
	{
		mVideoWidth = pVideoWidth;
	}

	public void setHeight(final int pVideoHeight)
	{
		mVideoHeight = pVideoHeight;
	}

	public void setSourceBuffer(final java.nio.ByteBuffer pSourceBuffer,
															final int pVideoBytesPerPixel,
															final int pVideoWidth,
															final int pVideoHeight)
	{
		mDisplayLock.lock();
		try
		{
			Pointer<Byte> lPointerToBytes = Pointer.pointerToBytes(pSourceBuffer);
			long lNativeAddress = lPointerToBytes.getPeer();
			long lLengthInBytes = lPointerToBytes.getValidBytes();
			mSourceBuffer = NDArrayTypedDirect.wrapPointerTXYZ(	pSourceBuffer,
																													lNativeAddress,
																													lLengthInBytes,
																													mType,
																													pVideoWidth,
																													pVideoHeight,
																													1);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		mDisplayLock.unlock();
	}

	public void setSourceBuffer(NDArrayTyped<T> pSourceBuffer)
	{
		mDisplayLock.lock();
		mSourceBuffer = pSourceBuffer;
		mDisplayLock.unlock();
	}

	public void notifyNewFrame()
	{
		mIsUpToDate = false;
	}

	public boolean isContextAvailable()
	{
		return mIsContextAvailable;
	}

	@Override
	public void close() throws IOException
	{

	}

	public void requestDisplay()
	{
		boolean lLocked = mDisplayLock.tryLock();
		if (!lLocked)
			return;
		try
		{
			mClearGLWindow.getGLWindow().display();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		mDisplayLock.unlock();
	}

	public void setVisible(final boolean pVisible)
	{
		mClearGLWindow.getGLWindow().setVisible(pVisible);
	}

	public boolean isVisible()
	{
		return mClearGLWindow.getGLWindow().isVisible();
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
		mClearGLWindow.getGLWindow()
									.setDefaultCloseOperation(WindowClosingMode.DO_NOTHING_ON_CLOSE);
	}

	public GLWindow getGLWindow()
	{
		return mClearGLWindow.getGLWindow();
	}

}
