package rtlib.gui.video.video2d.jogl;

import java.io.Closeable;
import java.io.IOException;

import javax.media.nativewindow.WindowClosingProtocol.WindowClosingMode;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.bridj.Pointer;

import rtlib.core.memory.TypeId;
import rtlib.core.units.Magnitudes;
import rtlib.gui.video.video2d.BitDepthAutoRescaler;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;
import rtlib.kam.memory.ndarray.NDArrayTyped;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;

public class VideoWindow implements Closeable
{

	private final GLWindow mGLWindow;
	private final MouseControl mMouseControl;
	private final KeyListener mKeyboardControl;
	private String mWindowName;

	private volatile int mVideoMaxWidth, mVideoMaxHeight, mVideoWidth,
			mVideoHeight;
	private int[] mPixelBufferIds;
	private GLU mGLU;
	private boolean mIsContextAvailable = false;

	private int mTextureId;
	private boolean mUsePBO = false; // seems to be faster without PBOs!!

	private NDArrayTyped<?> mSourceBuffer;
	private BitDepthAutoRescaler mBitDepthAutoRescaler;

	private volatile boolean mIsUpToDate = false;
	private final boolean mReportErrors = false;

	private volatile long mFrameIndex = 0;
	private volatile long mNanosecondsSinceLastFrame = System.nanoTime();
	private volatile double mFrameRate;

	private volatile boolean mDisplayFrameRate = true;
	private volatile long mDisplayFrameRateLastDisplayTime = 0;

	private volatile boolean mDisplayOn = true,
			mLinearInterpolation = false, mSyncToRefresh = false,
			mManualMinMax = false;

	private volatile double mMinIntensity = 0, mMaxIntensity = 1;

	private static final GLCapabilities cGLCapabilities = new GLCapabilities(GLProfile.getDefault());

	private Object mDisplayLock = new Object();

	public VideoWindow() throws GLException
	{
		mGLWindow = GLWindow.create(cGLCapabilities);
		getGLWindow().setAutoSwapBufferMode(true);

		mMouseControl = new MouseControl(this);
		getGLWindow().addMouseListener(mMouseControl);
		mKeyboardControl = new KeyboardControl(this);
		getGLWindow().addKeyListener(mKeyboardControl);
	}

	public VideoWindow(	final String pWindowName,
											final int pVideoMaxWidth,
											final int pVideoMaxHeight) throws GLException
	{
		this();
		mWindowName = pWindowName;
		mVideoMaxWidth = pVideoMaxWidth;
		mVideoMaxHeight = pVideoMaxHeight;
		mVideoWidth = pVideoMaxWidth;
		mVideoHeight = pVideoMaxHeight;

		if (pVideoMaxWidth > 768 || pVideoMaxHeight > 768)
		{
			setWindowSize(768, 768);
		}
		else if (pVideoMaxWidth < 256 || pVideoMaxHeight < 256)
		{
			setWindowSize(512, 512);
		}

		getGLWindow().setTitle(mWindowName);

		getGLWindow().addGLEventListener(new GLEventListener()
		{

			@Override
			public void reshape(final GLAutoDrawable glautodrawable,
													final int x,
													final int y,
													final int pWindowWidth,
													final int pWindowHeight)
			{
				synchronized (mDisplayLock)
				{
					// System.out.println("reshape");
					final GL2 lGL2 = glautodrawable.getGL().getGL2();

					lGL2.glLoadIdentity();
					lGL2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
					lGL2.glLoadIdentity();

					// final double lOffsetWH = (lAspectRatioWH - 1) / 2;
					// final double lOffsetHW = (lAspectRatioHW - 1) / 2;

					lGL2.glOrtho(0, 1, 1, 0, 0, 2000);

					// if (lAspectRatioWH >= 1)
					/*	lGL2.glOrtho(	-lOffsetWH,
													lAspectRatioWH - lOffsetWH,
													1,
													0,
													0,
													2000);
					/*else if (lAspectRatioHW >= 1)
						lGL2.glOrtho(	0,
													1,
													lAspectRatioHW - lOffsetHW,
													-lOffsetHW,
													0,
													2000);/*/

					lGL2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
					lGL2.glViewport(0, 0, pWindowWidth, pWindowHeight);
					lGL2.glClearColor(0, 0, 0, 0);
					lGL2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
					lGL2.glFlush();
					lGL2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
					lGL2.glFlush();
				}

			}

			@Override
			public void init(final GLAutoDrawable glautodrawable)
			{
				synchronized (mDisplayLock)
				{
					final GL2 lGL2 = glautodrawable.getGL().getGL2();
					mGLU = new GLU();

					if (!lGL2.isExtensionAvailable("GL_ARB_pixel_buffer_object"))
					{
						System.out.println("Extension not available!");
						mUsePBO = false;
					}

					lGL2.setSwapInterval(mSyncToRefresh ? 1 : 0);

					reshape(glautodrawable,
									0,
									0,
									getGLWindow().getWidth(),
									getGLWindow().getHeight());

					lGL2.glDisable(GL.GL_CULL_FACE);
					lGL2.glDisable(GL.GL_DEPTH_TEST);
					lGL2.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT,
											GL.GL_NICEST);
					lGL2.glDisable(GLLightingFunc.GL_LIGHTING);
					lGL2.glDisable(GLLightingFunc.GL_COLOR_MATERIAL);

					lGL2.glEnable(GL.GL_TEXTURE_2D);
					reportError(lGL2);

					final int[] tmp = new int[1];
					lGL2.glGenTextures(1, tmp, 0);
					mTextureId = tmp[0];
					lGL2.glBindTexture(GL.GL_TEXTURE_2D, mTextureId);
					reportError(lGL2);

					lGL2.glTexParameteri(	GL.GL_TEXTURE_2D,
																GL.GL_TEXTURE_MIN_FILTER,
																mLinearInterpolation ? GL.GL_LINEAR
																										: GL.GL_NEAREST);
					lGL2.glTexParameteri(	GL.GL_TEXTURE_2D,
																GL.GL_TEXTURE_MAG_FILTER,
																mLinearInterpolation ? GL.GL_LINEAR
																										: GL.GL_NEAREST);
					lGL2.glTexParameteri(	GL.GL_TEXTURE_2D,
																GL.GL_TEXTURE_WRAP_S,
																GL2.GL_CLAMP);
					lGL2.glTexParameteri(	GL.GL_TEXTURE_2D,
																GL.GL_TEXTURE_WRAP_T,
																GL2.GL_CLAMP);
					reportError(lGL2);

					if (mSourceBuffer == null)
					{
						mSourceBuffer = NDArrayTypedDirect.allocateTXY(	Byte.class,
																														mVideoWidth,
																														mVideoHeight);
					}

					lGL2.glTexImage2D(GL.GL_TEXTURE_2D,
														0,
														GL.GL_LUMINANCE,
														(int) mVideoMaxWidth,
														(int) mVideoMaxHeight,
														0,
														GL.GL_LUMINANCE,
														GL.GL_UNSIGNED_BYTE,
														mSourceBuffer.getRAM()
																					.passNativePointerToByteBuffer(Character.class));
					reportError(lGL2);

					// mGL2.glEnable(GL2.GL_PIXEL_UNPACK_BUFFER);
					// reportError();

					if (mUsePBO)
					{

						mPixelBufferIds = new int[2];
						lGL2.glGenBuffers(2, mPixelBufferIds, 0);
						reportError(lGL2);
						lGL2.glBindBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER,
															mPixelBufferIds[0]);
						reportError(lGL2);

						lGL2.glBufferData(GL2GL3.GL_PIXEL_UNPACK_BUFFER,
															mSourceBuffer.getRAM().getSizeInBytes(),
															null,
															GL2ES2.GL_STREAM_DRAW);
						reportError(lGL2);

						lGL2.glBindBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER,
															mPixelBufferIds[1]);
						reportError(lGL2);

						lGL2.glBufferData(GL2GL3.GL_PIXEL_UNPACK_BUFFER,
															mSourceBuffer.getRAM().getSizeInBytes(),
															null,
															GL2ES2.GL_STREAM_DRAW);
						reportError(lGL2);

						lGL2.glBindBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER, 0);
						reportError(lGL2);
					}

					mIsContextAvailable = true;
				}
			}

			@Override
			public void dispose(final GLAutoDrawable glautodrawable)
			{
			}

			@Override
			public void display(final GLAutoDrawable glautodrawable)
			{
				synchronized (mDisplayLock)
				{
					if (mSourceBuffer == null)
					{
						return;
					}

					final int lWidth = (int) mVideoWidth;
					final int lHeight = (int) mVideoHeight;

					final GL2 lGL2 = glautodrawable.getGL().getGL2();

					lGL2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
					lGL2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
					if (!mDisplayOn)
					{
						return;
					}

					try
					{
						updateVideoWithBuffer(lGL2, mSourceBuffer);
					}
					catch (final Throwable e)
					{
						e.printStackTrace();
					}

					// mGL2.glLoadIdentity();

					lGL2.glColor4d(1, 1, 1, 1);
					lGL2.glEnable(GL.GL_TEXTURE_2D);
					lGL2.glBindTexture(GL.GL_TEXTURE_2D, mTextureId);
					// lGL2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
					lGL2.glBegin(GL2.GL_QUADS);

					final double lRatioEffective2MaxWidth = (double) lWidth / mVideoMaxWidth;
					final double lRatioEffective2MaxHeight = (double) lHeight / mVideoMaxHeight;

					double x, y, w, h;
					if (lWidth < lHeight)
					{
						w = (double) lWidth / lHeight;
						h = 1;
						x = (1 - w) / 2;
						y = 0;
					}
					else
					{
						w = 1;
						h = (double) lHeight / lWidth;
						x = 0;
						y = (1 - h) / 2;
					}

					lGL2.glTexCoord2d(0.0, 0.0);
					lGL2.glVertex3d(x, y, 0.0);

					lGL2.glTexCoord2d(lRatioEffective2MaxWidth, 0.0);
					lGL2.glVertex3d(x + w, y, 0.0);

					lGL2.glTexCoord2d(lRatioEffective2MaxWidth,
														lRatioEffective2MaxHeight);
					lGL2.glVertex3d(x + w, y + h, 0.0);

					lGL2.glTexCoord2d(0.0, lRatioEffective2MaxHeight);
					lGL2.glVertex3d(x, y + h, 0.0);
					lGL2.glEnd();
					/**/

					/*
					final long lTimeInNanoseconds = System.nanoTime();
					if (isDisplayFrameRate() && lTimeInNanoseconds > mDisplayFrameRateLastDisplayTime + 200 * 1000 * 1000)
					{
						mDisplayFrameRateLastDisplayTime = lTimeInNanoseconds;
						final String lTitleString = String.format("%s %.0f fps",
																											mWindowName,
																											mFrameRate);
						
						//TODO: this causes a crash when calling display too fast from multiple threads:
						// getGLWindow().setTitle(lTitleString);
						/*
						mTextRenderer.beginRendering(	mGLWindow.getWidth(),
																					mGLWindow.getHeight());
						// optionally set the color
						mTextRenderer.setColor(1f, 1f, 1f, 0.5f);
						mTextRenderer.draw(	,
																15,
																15);
						mTextRenderer.endRendering();

					}/**/
				}
			}
		});

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

	public <T> void setSourceBuffer(final java.nio.ByteBuffer pSourceBuffer,
																	Class<T> pType,
																	final int pVideoBytesPerPixel,
																	final int pVideoWidth,
																	final int pVideoHeight)
	{
		Pointer<Byte> lPointerToBytes = Pointer.pointerToBytes(pSourceBuffer);
		long lNativeAddress = lPointerToBytes.getPeer();
		long lLengthInBytes = lPointerToBytes.getValidBytes();
		mSourceBuffer = NDArrayTypedDirect.wrapPointerTXYZ(	pSourceBuffer,
																												lNativeAddress,
																												lLengthInBytes,
																												pType,
																												pVideoWidth,
																												pVideoHeight,
																												1);
	}

	public <T> void setSourceBuffer(NDArrayTyped<T> pSourceBuffer)
	{
		mSourceBuffer = pSourceBuffer;
	}

	private boolean updateVideoWithBuffer(final GL2 pGL2,
																				final NDArrayTyped<?> pNewContentBuffer)
	{
		if (mIsUpToDate || !mDisplayOn)
		{
			return true;
		}

		if (!isContextAvailable())
		{
			return false;
		}

		final int lCurrentIndex = (int) (mFrameIndex % 2);
		final int lNextIndex = (int) ((mFrameIndex + 1) % 2);

		boolean lIsFloatingPointType = TypeId.isFloatingPointType(pNewContentBuffer.getType());
		if (mBitDepthAutoRescaler == null || mBitDepthAutoRescaler.isFloat() != lIsFloatingPointType)
		{
			mBitDepthAutoRescaler = new BitDepthAutoRescaler(lIsFloatingPointType);
		}
		if (mManualMinMax)
		{
			mBitDepthAutoRescaler.setManualMinimum(mMinIntensity);
			mBitDepthAutoRescaler.setManualMaximum(mMaxIntensity);
		}
		mBitDepthAutoRescaler.setAutoRescale(!mManualMinMax);
		final NDArrayTyped<?> lConvertedBuffer = mBitDepthAutoRescaler.convertBuffer(pNewContentBuffer);

		if (lConvertedBuffer == null)
			throw new RuntimeException(this.getClass().getSimpleName() + ": new buffer is null or could not b converted to 8bit for display");

		boolean lResult;

		if (mUsePBO)
		{
			lResult = updateVideoWithBufferPBO(	pGL2,
																					lConvertedBuffer.getRAM()
																													.passNativePointerToByteBuffer(Character.class),
																					lCurrentIndex,
																					lNextIndex);
		}
		else
		{
			lResult = updateVideoWithBufferClassic(	pGL2,
																							lConvertedBuffer.getRAM()
																															.passNativePointerToByteBuffer(Character.class),
																							lCurrentIndex,
																							lNextIndex);
		}
		mIsUpToDate = true;
		mFrameIndex++;

		final long lCurrentTimeInNanoseconds = System.nanoTime();
		final long lTimelapsedInNanoseconds = lCurrentTimeInNanoseconds - mNanosecondsSinceLastFrame;
		mNanosecondsSinceLastFrame = lCurrentTimeInNanoseconds;

		if (lTimelapsedInNanoseconds > 0)
		{
			final double lFrameRate = 1 / Magnitudes.nano2unit(lTimelapsedInNanoseconds);
			mFrameRate = 0.9 * mFrameRate + 0.1 * lFrameRate;
		}

		return lResult;
	}

	private boolean updateVideoWithBufferClassic(	final GL2 pGL2,
																								final java.nio.ByteBuffer pNewContentBuffer,
																								final int pCurrentIndex,
																								final int pNextIndex)
	{
		pGL2.glBindTexture(GL.GL_TEXTURE_2D, mTextureId);

		// System.out.println("pNewContentBuffer=" + pNewContentBuffer);
		pNewContentBuffer.rewind();
		pGL2.glTexSubImage2D(	GL.GL_TEXTURE_2D,
													0,
													0,
													0,
													(int) mVideoWidth,
													(int) mVideoHeight,
													GL.GL_LUMINANCE,
													GL.GL_UNSIGNED_BYTE,
													pNewContentBuffer);
		reportError(pGL2);

		return true;
	}

	private boolean updateVideoWithBufferPBO(	final GL2 pGL2,
																						final java.nio.ByteBuffer pNewContentBuffer,
																						final int pCurrentIndex,
																						final int pNextIndex)
	{

		// Bind texture:
		pGL2.glBindTexture(GL.GL_TEXTURE_2D, mTextureId);
		reportError(pGL2);

		// Bind buffer for drawing
		pGL2.glBindBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER,
											mPixelBufferIds[pCurrentIndex]);
		reportError(pGL2);

		// copy pixels from PBO to texture object
		// Use offset instead of pointer.
		pGL2.glTexSubImage2D(	GL.GL_TEXTURE_2D,
													0,
													0,
													0,
													(int) mVideoMaxWidth,
													(int) mVideoMaxHeight,
													GL.GL_LUMINANCE,
													GL.GL_UNSIGNED_BYTE,
													0);
		reportError(pGL2);

		// Bind buffer to update:
		pGL2.glBindBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER,
											mPixelBufferIds[pNextIndex]);
		reportError(pGL2);

		// Null existing data
		pGL2.glBufferData(GL2GL3.GL_PIXEL_UNPACK_BUFFER,
											mVideoWidth * mVideoHeight * 4,
											null,
											GL2ES2.GL_STREAM_DRAW);
		reportError(pGL2);

		// Map buffer. Returns pointer to buffer memory
		final java.nio.ByteBuffer lTextureMappedBuffer = pGL2.glMapBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER,
																																			GL.GL_WRITE_ONLY);

		reportError(pGL2);

		if (lTextureMappedBuffer == null)
		{
			return false;
		}

		lTextureMappedBuffer.clear();
		pNewContentBuffer.rewind();
		lTextureMappedBuffer.put(pNewContentBuffer);

		// Unmaps buffer, indicating we are done writing data to it
		pGL2.glUnmapBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER);
		reportError(pGL2);

		// Unbind buffer
		pGL2.glBindBuffer(GL2GL3.GL_PIXEL_UNPACK_BUFFER, 0);
		reportError(pGL2);

		return true;
	}

	private void reportError(final GL2 pGL2)
	{
		if (mReportErrors)
		{
			final int errorCode = pGL2.glGetError();
			final String errorStr = mGLU.gluErrorString(errorCode);

			if (errorCode != 0)
			{
				System.out.println(errorStr);
				System.out.println(errorCode);
				System.out.println("ERROR!!");
			}
		}
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
		// TODO: we need a smart way to clean this up!!
		// mGL2.glDeleteBuffers(2, mPixelBufferIds, 0);
	}

	public boolean isLinearFiltering()
	{
		return mLinearInterpolation;
	}

	public void setLinearInterpolation(final boolean pLinearInterpolation)
	{
		mLinearInterpolation = pLinearInterpolation;
	}

	public boolean isSyncToRefresh()
	{
		return mSyncToRefresh;
	}

	public void setSyncToRefresh(final boolean pSyncToRefresh)
	{
		mSyncToRefresh = pSyncToRefresh;
	}

	public void display()
	{
		getGLWindow().display();
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

	public void setGamma(double pGamma)
	{
		mBitDepthAutoRescaler.setGamma(pGamma);
	}

	public GLWindow getGLWindow()
	{
		return mGLWindow;
	}

}
