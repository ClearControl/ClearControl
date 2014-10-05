package rtlib.gui.video.video2d.jogl;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.random;
import static java.lang.Math.round;

import java.io.IOException;
import java.nio.Buffer;
import java.util.concurrent.locks.ReentrantLock;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GL;
import javax.media.opengl.GL4;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;

import org.bridj.Pointer;

import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import cleargl.ClearGLDebugEventListener;
import cleargl.ClearGLEventListener;
import cleargl.ClearGLWindow;
import cleargl.GLAttribute;
import cleargl.GLFloatArray;
import cleargl.GLProgram;
import cleargl.GLTexture;
import cleargl.GLUniform;
import cleargl.GLVertexArray;
import cleargl.GLVertexAttributeArray;

import com.jogamp.newt.opengl.GLWindow;

public class VideoWindow<T> extends ClearGLDebugEventListener	implements
																															AutoCloseable,
																															ClearGLEventListener
{

	private static final double cPercentageOfPixelsToSample = 0.001;

	private static final int cMipMapLevel = 2;

	private Class<T> mType;
	private ClearGLWindow mClearGLWindow;
	private volatile int mVideoWidth, mVideoHeight;

	private NDArrayTyped<T> mSourceBuffer;

	private volatile boolean mIsContextAvailable = false,
			mIsUpToDate = false, mDisplayFrameRate = true,
			mDisplayOn = true, mManualMinMax = false;

	private volatile double mMinIntensity = 0, mMaxIntensity = 1,
			mGamma = 1;

	private ReentrantLock mDisplayLock = new ReentrantLock();

	private GLProgram mGLProgram;
	private GLAttribute mPositionAttribute, mTexCoordAttribute;
	private GLUniform mTexUnit, mMinimumUniform, mMaximumUniform,
			mGammaUniform;
	private GLVertexArray mQuadVertexArray;
	private GLVertexAttributeArray mPositionAttributeArray,
			mTexCoordAttributeArray;
	private GLTexture<T> mTexture;

	private double mSampledMinIntensity, mSampledMaxIntensity;

	// private GLPixelBufferObject mPixelBufferObject;

	public VideoWindow(	final String pWindowName,
											final Class<T> pClass,
											final int pVideoWidth,
											final int pVideoHeight) throws GLException
	{
		this(	pWindowName,
					pClass,
					pVideoWidth,
					pVideoHeight,
					pVideoWidth,
					pVideoHeight);
	}

	public VideoWindow(	final String pWindowName,
											final Class<T> pClass,
											final int pVideoWidth,
											final int pVideoHeight,
											final int pWindowWidth,
											final int pWindowHeight) throws GLException
	{
		mType = pClass;
		mVideoWidth = pVideoWidth;
		mVideoHeight = pVideoHeight;

		mClearGLWindow = new ClearGLWindow(	pWindowName,
																				pWindowWidth,
																				pWindowHeight,
																				this);

		MouseControl lMouseControl = new MouseControl(this);
		mClearGLWindow.getGLWindow().addMouseListener(lMouseControl);
		KeyboardControl lKeyboardControl = new KeyboardControl(this);
		mClearGLWindow.getGLWindow().addKeyListener(lKeyboardControl);

	}

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

			mGLProgram = GLProgram.buildProgram(lGL4,
																					VideoWindow.class,
																					"shaders/vertex.glsl",
																					"shaders/fragment.glsl");

			mPositionAttribute = mGLProgram.getAtribute("position");
			mTexCoordAttribute = mGLProgram.getAtribute("texcoord");
			mTexUnit = mGLProgram.getUniform("texUnit");
			mTexUnit.set(0);

			mMinimumUniform = mGLProgram.getUniform("minimum");
			mMaximumUniform = mGLProgram.getUniform("maximum");
			mGammaUniform = mGLProgram.getUniform("gamma");

			mQuadVertexArray = new GLVertexArray(mGLProgram);
			mQuadVertexArray.bind();
			mPositionAttributeArray = new GLVertexAttributeArray(	mPositionAttribute,
																														4);

			GLFloatArray lVerticesFloatArray = new GLFloatArray(6, 4);
			lVerticesFloatArray.add(-1, -1, 0, 1);
			lVerticesFloatArray.add(1, -1, 0, 1);
			lVerticesFloatArray.add(1, 1, 0, 1);
			lVerticesFloatArray.add(-1, -1, 0, 1);
			lVerticesFloatArray.add(1, 1, 0, 1);
			lVerticesFloatArray.add(-1, 1, 0, 1);

			mQuadVertexArray.addVertexAttributeArray(	mPositionAttributeArray,
																								lVerticesFloatArray.getFloatBuffer());

			mTexCoordAttributeArray = new GLVertexAttributeArray(	mTexCoordAttribute,
																														2);

			GLFloatArray lTexCoordFloatArray = new GLFloatArray(6, 2);
			lTexCoordFloatArray.add(0, 0);
			lTexCoordFloatArray.add(1, 0);
			lTexCoordFloatArray.add(1, 1);
			lTexCoordFloatArray.add(0, 0);
			lTexCoordFloatArray.add(1, 1);
			lTexCoordFloatArray.add(0, 1);

			mQuadVertexArray.addVertexAttributeArray(	mTexCoordAttributeArray,
																								lTexCoordFloatArray.getFloatBuffer());

			initializeTexture();

			/*mPixelBufferObject = new GLPixelBufferObject(	mGLProgram,
																										mTexture.getWidth(),
																										mTexture.getHeight());

			mPixelBufferObject.copyFrom(null);/**/
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

		mTexture = new GLTexture<T>(mGLProgram,
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
		super.reshape(pGLAutoDrawable, x, y, pWindowWidth, pWindowHeight);
	}

	@Override
	public void display(final GLAutoDrawable pGLAutoDrawable)
	{

		super.display(pGLAutoDrawable);
		final GL4 lGL4 = pGLAutoDrawable.getGL().getGL4();

		lGL4.glClear(GL4.GL_COLOR_BUFFER_BIT);

		final int lBufferWidth = (int) mSourceBuffer.getWidth();
		final int lBufferHeight = (int) mSourceBuffer.getHeight();

		if (mVideoWidth != lBufferWidth || mVideoHeight != lBufferWidth)
		{
			mVideoWidth = lBufferWidth;
			mVideoHeight = lBufferHeight;
			initializeTexture();
		}

		if (!mIsUpToDate)
		{
			Buffer lBuffer = mSourceBuffer.getBridJPointer(mType)
																		.getBuffer();
			fastMinMaxSampling(mSourceBuffer);
			lBuffer.rewind();
			mTexture.copyFrom(lBuffer);
		}

		if (mManualMinMax)
		{
			final double lSampledIntensityWidth = mSampledMaxIntensity - mSampledMinIntensity;
			mMinimumUniform.set((float) (mMinIntensity * lSampledIntensityWidth + mSampledMinIntensity));
			mMaximumUniform.set((float) (mMaxIntensity * lSampledIntensityWidth + mSampledMinIntensity));
		}
		else
		{
			mMinimumUniform.set((float) mSampledMinIntensity);
			mMaximumUniform.set((float) mSampledMaxIntensity);
		}
		mGammaUniform.set((float) mGamma);

		mGLProgram.use(lGL4);
		mTexture.bind(mGLProgram);
		mQuadVertexArray.draw(GL.GL_TRIANGLES);
	}

	private void fastMinMaxSampling(NDArrayTyped<T> pSourceBuffer)
	{
		long lLength = pSourceBuffer.getVolume();
		int lStep = (int) round(cPercentageOfPixelsToSample * lLength);
		int lStartPixel = (int) round(random() * lStep);

		double lMin = Double.POSITIVE_INFINITY;
		double lMax = Double.NEGATIVE_INFINITY;

		if (mType == Byte.class)
			for (int i = lStartPixel; i < lLength; i += lStep)
			{
				byte lByteAligned = pSourceBuffer.getByteAligned(i);
				lMin = min(lMin, lByteAligned);
				lMax = max(lMax, lByteAligned);
			}
		else if (mType == Short.class)
			for (int i = lStartPixel; i < lLength; i += lStep)
			{
				short lShortAligned = pSourceBuffer.getShortAligned(i);
				lMin = min(lMin, lShortAligned);
				lMax = max(lMax, lShortAligned);
			}
		else if (mType == Character.class)
			for (int i = lStartPixel; i < lLength; i += lStep)
			{
				char lCharAligned = pSourceBuffer.getCharAligned(i);
				lMin = min(lMin, lCharAligned);
				lMax = max(lMax, lCharAligned);
			}
		else if (mType == Float.class)
			for (int i = lStartPixel; i < lLength; i += lStep)
			{
				float lFloatAligned = pSourceBuffer.getFloatAligned(i);
				lMin = min(lMin, lFloatAligned);
				lMax = max(lMax, lFloatAligned);
			}

		mSampledMinIntensity = 0.9 * mSampledMinIntensity + 0.1 * lMin;
		mSampledMaxIntensity = 0.9 * mSampledMaxIntensity + 0.1 * lMax;

	}

	@Override
	public void dispose(final GLAutoDrawable pGLAutoDrawable)
	{
		super.dispose(pGLAutoDrawable);
	}

	private void setWindowSize(int pWindowWidth, int pWindowHeigth)
	{
		getGLWindow().setSize(pWindowWidth, pWindowWidth);
	}

	public int getWindowWidth()
	{
		return getGLWindow().getWidth();
	}

	public int getWindowHeight()
	{
		return getGLWindow().getHeight();
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
			getGLWindow().display();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		mDisplayLock.unlock();
	}

	public void setVisible(final boolean pVisible)
	{
		getGLWindow().setVisible(pVisible);
	}

	public boolean isVisible()
	{
		return getGLWindow().isVisible();
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

	public boolean isDisplayFrameRate()
	{
		return mDisplayFrameRate;
	}

	public void setDisplayFrameRate(boolean pDisplayFrameRate)
	{
		mDisplayFrameRate = pDisplayFrameRate;
	}

	public void setManualMinMax(final boolean pManualMinMax)
	{
		mManualMinMax = pManualMinMax;
	}

	public void disableClose()
	{
		getGLWindow().setDefaultCloseOperation(WindowClosingMode.DO_NOTHING_ON_CLOSE);
	}

	public GLWindow getGLWindow()
	{
		return mClearGLWindow.getGLWindow();
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

}
