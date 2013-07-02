package gui.video.video2d.jogl;

import java.awt.Font;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import thread.EnhancedThread;
import units.Units;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.awt.TextRenderer;

public class VideoWindow implements Closeable
{

	GLWindow mGLWindow;

	private int mBytesPerPixel, mVideoMaxWidth, mVideoMaxHeight,
			mVideoWidth, mVideoHeight, mMaxBufferLength;
	private int[] mPixelBufferIds;
	private GLU mGLU;
	private boolean mIsContextAvailable = false;

	private int mTextureId;
	private boolean mUsePBO = false; // seems to be faster without PBOs!!

	private ByteBuffer mSourceBuffer;

	private short[] mShortArray;
	private byte[] mByteArray;
	private ByteBuffer mConvertedSourceBuffer;

	private volatile boolean mIsUpToDate = false;
	private final boolean mReportErrors = false;

	private volatile long mFrameIndex = 0;
	private long mNanosecondsSinceLastFrame = EnhancedThread.getTimeInNanoseconds();
	private volatile double mFrameRate;

	private volatile boolean mDisplayFrameRate = true;
	private final TextRenderer mTextRenderer = new TextRenderer(new Font(	"Helvetica",
																																				Font.PLAIN,
																																				12));

	private volatile boolean mDisplayOn = true,
			mLinearInterpolation = false, mSyncToRefresh,
			mManualMinMax = false;

	private volatile double mMinIntensity, mMaxIntensity;

	private static final GLCapabilities cGLCapabilities = new GLCapabilities(GLProfile.getDefault());

	public VideoWindow() throws GLException
	{
		mGLWindow = GLWindow.create(cGLCapabilities);
		mGLWindow.setAutoSwapBufferMode(true);
	}

	public VideoWindow(	final int pBytesPerPixel,
											final int pVideoMaxWidth,
											final int pVideoMaxHeight) throws GLException
	{
		this();
		mBytesPerPixel = pBytesPerPixel;
		mVideoMaxWidth = pVideoMaxWidth;
		mVideoMaxHeight = pVideoMaxHeight;
		mVideoWidth = pVideoMaxWidth;
		mVideoHeight = pVideoMaxHeight;
		mMaxBufferLength = mVideoMaxWidth * mVideoMaxWidth
												* mBytesPerPixel;

		if (pVideoMaxWidth > 512 || pVideoMaxHeight > 512)
			mGLWindow.setSize(512, 512);
		else
			mGLWindow.setSize(pVideoMaxWidth, pVideoMaxHeight);

		mGLWindow.setTitle(VideoWindow.class.getSimpleName());

		mGLWindow.addGLEventListener(new GLEventListener()
		{

			@Override
			public void reshape(final GLAutoDrawable glautodrawable,
													final int x,
													final int y,
													final int pWidth,
													final int pHeight)
			{
				// System.out.println("reshape");
				final GL2 lGL2 = glautodrawable.getGL().getGL2();

				lGL2.glLoadIdentity();
				lGL2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
				lGL2.glLoadIdentity();
				lGL2.glOrtho(0, 1, 1, 0, 0, 2000);
				lGL2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
				lGL2.glViewport(0, 0, pWidth, pHeight);
			}

			@Override
			public void init(final GLAutoDrawable glautodrawable)
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
								mGLWindow.getWidth(),
								mGLWindow.getHeight());

				lGL2.glDisable(GL2.GL_CULL_FACE);
				lGL2.glDisable(GL2.GL_DEPTH_TEST);
				lGL2.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT,
										GL2.GL_NICEST);
				lGL2.glDisable(GLLightingFunc.GL_LIGHTING);
				lGL2.glDisable(GLLightingFunc.GL_COLOR_MATERIAL);

				lGL2.glEnable(GL2.GL_TEXTURE_2D);
				reportError(lGL2);

				final int[] tmp = new int[1];
				lGL2.glGenTextures(1, tmp, 0);
				mTextureId = tmp[0];
				lGL2.glBindTexture(GL2.GL_TEXTURE_2D, mTextureId);
				reportError(lGL2);

				lGL2.glTexParameteri(	GL2.GL_TEXTURE_2D,
															GL2.GL_TEXTURE_MIN_FILTER,
															mLinearInterpolation ? GL2.GL_LINEAR
																									: GL2.GL_NEAREST);
				lGL2.glTexParameteri(	GL2.GL_TEXTURE_2D,
															GL2.GL_TEXTURE_MAG_FILTER,
															mLinearInterpolation ? GL2.GL_LINEAR
																									: GL2.GL_NEAREST);
				lGL2.glTexParameteri(	GL2.GL_TEXTURE_2D,
															GL2.GL_TEXTURE_WRAP_S,
															GL2.GL_CLAMP);
				lGL2.glTexParameteri(	GL2.GL_TEXTURE_2D,
															GL2.GL_TEXTURE_WRAP_T,
															GL2.GL_CLAMP);
				reportError(lGL2);

				if (mSourceBuffer == null)
					mSourceBuffer = ByteBuffer.allocate(mMaxBufferLength);

				mSourceBuffer.rewind();
				lGL2.glTexImage2D(GL2.GL_TEXTURE_2D,
													0,
													GL2.GL_LUMINANCE,
													mVideoMaxWidth,
													mVideoMaxHeight,
													0,
													GL2.GL_LUMINANCE,
													GL2.GL_UNSIGNED_BYTE,
													mSourceBuffer);
				reportError(lGL2);

				// mGL2.glEnable(GL2.GL_PIXEL_UNPACK_BUFFER);
				// reportError();

				if (mUsePBO)
				{

					mPixelBufferIds = new int[2];
					lGL2.glGenBuffers(2, mPixelBufferIds, 0);
					reportError(lGL2);
					lGL2.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER,
														mPixelBufferIds[0]);
					reportError(lGL2);

					lGL2.glBufferData(GL2.GL_PIXEL_UNPACK_BUFFER,
														mMaxBufferLength,
														null,
														GL2.GL_STREAM_DRAW);
					reportError(lGL2);

					lGL2.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER,
														mPixelBufferIds[1]);
					reportError(lGL2);

					lGL2.glBufferData(GL2.GL_PIXEL_UNPACK_BUFFER,
														mMaxBufferLength,
														null,
														GL2.GL_STREAM_DRAW);
					reportError(lGL2);

					lGL2.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, 0);
					reportError(lGL2);
				}

				mIsContextAvailable = true;
			}

			@Override
			public void dispose(final GLAutoDrawable glautodrawable)
			{
			}

			@Override
			public void display(final GLAutoDrawable glautodrawable)
			{
				if (mSourceBuffer == null)
					return;

				final int lWidth = mVideoWidth;
				final int lHeight = mVideoHeight;

				final GL2 lGL2 = glautodrawable.getGL().getGL2();

				if (!mDisplayOn)
				{
					lGL2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
					lGL2.glClear(GL2.GL_COLOR_BUFFER_BIT);
					return;
				}/**/

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
				lGL2.glEnable(GL2.GL_TEXTURE_2D);
				lGL2.glBindTexture(GL2.GL_TEXTURE_2D, mTextureId);
				// lGL2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
				lGL2.glBegin(GL2.GL_QUADS);

				final double lRatioEffective2MaxWidth = ((double) mVideoWidth) / mVideoMaxWidth;
				final double lRatioEffective2MaxHeight = ((double) mVideoHeight) / mVideoMaxHeight;

				lGL2.glTexCoord2d(0.0, 0.0);
				lGL2.glVertex3d(0.0, 0.0, 0.0);

				lGL2.glTexCoord2d(lRatioEffective2MaxWidth, 0.0);
				lGL2.glVertex3d(1, 0.0, 0.0);

				lGL2.glTexCoord2d(lRatioEffective2MaxWidth,
													lRatioEffective2MaxHeight);
				lGL2.glVertex3d(1, 1, 0.0);

				lGL2.glTexCoord2d(0.0, lRatioEffective2MaxHeight);
				lGL2.glVertex3d(0.0, 1, 0.0);
				lGL2.glEnd();
				/**/

				if (mDisplayFrameRate)
				{
					mTextRenderer.beginRendering(	mGLWindow.getWidth(),
																				mGLWindow.getHeight());
					// optionally set the color
					mTextRenderer.setColor(1f, 1f, 1f, 0.5f);
					mTextRenderer.draw(	String.format("%.0f fps", mFrameRate),
															15,
															15);
					mTextRenderer.endRendering();

				}
			}
		});

	}

	public void setBytesPerPixel(final int pBytesPerPixel)
	{
		mBytesPerPixel = pBytesPerPixel;
	}

	public void setWidth(final int pVideoWidth)
	{
		mVideoWidth = pVideoWidth;
	}

	public void setHeight(final int pVideoHeight)
	{
		mVideoHeight = pVideoHeight;
	}

	public void setSourceBuffer(final ByteBuffer pSourceBuffer)
	{
		mSourceBuffer = pSourceBuffer;
	}

	private boolean updateVideoWithBuffer(final GL2 pGL2,
																				final ByteBuffer pNewContentBuffer)
	{
		if (mIsUpToDate || !mDisplayOn) //
			return true;

		if (!isContextAvailable())
			return false;

		final int lCurrentIndex = (int) (mFrameIndex % 2);
		final int lNextIndex = (int) ((mFrameIndex + 1) % 2);

		final ByteBuffer lConvertedBuffer = convertBuffer(pNewContentBuffer);

		boolean lResult;

		if (mUsePBO)
		{
			lResult = updateVideoWithBufferPBO(	pGL2,
																					lConvertedBuffer,
																					lCurrentIndex,
																					lNextIndex);
		}
		else
		{
			lResult = updateVideoWithBufferClassic(	pGL2,
																							lConvertedBuffer,
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
			final double lFrameRate = 1 / Units.nano2unit(lTimelapsedInNanoseconds);
			mFrameRate = 0.9 * mFrameRate + 0.1 * lFrameRate;
		}

		return lResult;
	}

	private boolean updateVideoWithBufferClassic(	final GL2 pGL2,
																								final ByteBuffer pNewContentBuffer,
																								final int pCurrentIndex,
																								final int pNextIndex)
	{
		pGL2.glBindTexture(GL2.GL_TEXTURE_2D, mTextureId);

		// System.out.println("pNewContentBuffer=" + pNewContentBuffer);
		pNewContentBuffer.rewind();
		pGL2.glTexSubImage2D(	GL2.GL_TEXTURE_2D,
													0,
													0,
													0,
													mVideoWidth,
													mVideoHeight,
													GL2.GL_LUMINANCE,
													GL2.GL_UNSIGNED_BYTE,
													pNewContentBuffer);
		reportError(pGL2);

		return true;
	}

	private boolean updateVideoWithBufferPBO(	final GL2 pGL2,
																						final ByteBuffer pNewContentBuffer,
																						final int pCurrentIndex,
																						final int pNextIndex)
	{

		// Bind texture:
		pGL2.glBindTexture(GL2.GL_TEXTURE_2D, mTextureId);
		reportError(pGL2);

		// Bind buffer for drawing
		pGL2.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER,
											mPixelBufferIds[pCurrentIndex]);
		reportError(pGL2);

		// copy pixels from PBO to texture object
		// Use offset instead of pointer.
		pGL2.glTexSubImage2D(	GL2.GL_TEXTURE_2D,
													0,
													0,
													0,
													mVideoMaxWidth,
													mVideoMaxHeight,
													GL2.GL_LUMINANCE,
													GL2.GL_UNSIGNED_BYTE,
													0);
		reportError(pGL2);

		// Bind buffer to update:
		pGL2.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER,
											mPixelBufferIds[pNextIndex]);
		reportError(pGL2);

		// Null existing data
		pGL2.glBufferData(GL2.GL_PIXEL_UNPACK_BUFFER,
											mVideoWidth * mVideoHeight * 4,
											null,
											GL2.GL_STREAM_DRAW);
		reportError(pGL2);

		// Map buffer. Returns pointer to buffer memory
		final ByteBuffer lTextureMappedBuffer = pGL2.glMapBuffer(	GL2.GL_PIXEL_UNPACK_BUFFER,
																															GL2.GL_WRITE_ONLY);

		reportError(pGL2);

		if (lTextureMappedBuffer == null)
			return false;

		lTextureMappedBuffer.clear();
		pNewContentBuffer.rewind();
		lTextureMappedBuffer.put(pNewContentBuffer);

		// Unmaps buffer, indicating we are done writing data to it
		pGL2.glUnmapBuffer(GL2.GL_PIXEL_UNPACK_BUFFER);
		reportError(pGL2);

		// Unbind buffer
		pGL2.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, 0);
		reportError(pGL2);

		return true;
	}

	private ByteBuffer convertBuffer(final ByteBuffer pNewContentBuffer)
	{
		if (mBytesPerPixel == 1)
		{
			return pNewContentBuffer;
		}
		else if (mBytesPerPixel == 2)
		{
			final int lByteBufferLength = pNewContentBuffer.capacity();
			final int lConvertedBuferLength = lByteBufferLength / 2;
			if (mConvertedSourceBuffer == null || mConvertedSourceBuffer.capacity() != lConvertedBuferLength)
			{
				mShortArray = new short[lConvertedBuferLength];
				mByteArray = new byte[lConvertedBuferLength];
				mConvertedSourceBuffer = ByteBuffer.wrap(mByteArray);
			}

			pNewContentBuffer.rewind();
			convertFromShortBuffer(pNewContentBuffer.asShortBuffer());

			return mConvertedSourceBuffer;

		}

		return null;
	}

	int[] mMinMax = new int[]
	{ Integer.MAX_VALUE, Integer.MIN_VALUE };

	private void convertFromShortBuffer(final ShortBuffer pShortBuffer)
	{
		pShortBuffer.rewind();
		pShortBuffer.get(mShortArray);
		if (mManualMinMax)
		{
			mMinMax[0] = (int) Math.round(65535 * mMinIntensity);
			mMinMax[1] = (int) Math.round(65535 * mMaxIntensity);
		}
		convert16to8bitRescaled(mShortArray,
														mByteArray,
														!mManualMinMax,
														mMinMax);
	}

	private static final void convert16to8bitRescaled(final short[] pShortArray,
																										final byte[] lByteArray,
																										final boolean pAutoRescale,
																										final int[] pMinMax)
	{
		final int length = pShortArray.length;

		if (pAutoRescale)
		{
			convert16to8bitRescaledAuto(pShortArray,
																	lByteArray,
																	pMinMax,
																	length);
		}
		else
		{
			convert16to8bitRescaledManual(pShortArray,
																		lByteArray,
																		pMinMax,
																		length);
		}

	}

	private static void convert16to8bitRescaledManual(final short[] pShortArray,
																										final byte[] lByteArray,
																										final int[] pMinMax,
																										final int length)
	{
		final int lCurrentMin = pMinMax[0];
		final int lCurrentMax = pMinMax[1];
		final int lCurrentWidth = lCurrentMax - lCurrentMin;

		for (int i = 0; i < length; i++)
		{
			final int lShortValue = pShortArray[i];
			byte lByteMappedValue = 0;
			if (lCurrentWidth > 0)
			{
				final int lIntegerMappedValue = ((255 * (lShortValue - lCurrentMin)) / lCurrentWidth);
				if (lIntegerMappedValue <= 0)
					lByteMappedValue = 0;
				else if (lIntegerMappedValue >= 255)
					lByteMappedValue = (byte) 255;
				else
					lByteMappedValue = (byte) (lIntegerMappedValue);
			}
			lByteArray[i] = lByteMappedValue;
		}
	}

	private static void convert16to8bitRescaledAuto(final short[] pShortArray,
																									final byte[] lByteArray,
																									final int[] pMinMax,
																									final int length)
	{
		final int lCurrentMin = pMinMax[0];
		final int lCurrentMax = pMinMax[1];
		final int lCurrentWidth = lCurrentMax - lCurrentMin;

		int lNewMin = Integer.MAX_VALUE;
		int lNewMax = Integer.MIN_VALUE;

		for (int i = 0; i < length; i++)
		{
			final int lShortValue = pShortArray[i];
			lNewMin = Math.min(lNewMin, lShortValue);
			lNewMax = Math.max(lNewMax, lShortValue);
			byte lByteMappedValue = 0;
			if (lCurrentWidth > 0)
				lByteMappedValue = (byte) ((255 * (lShortValue - lCurrentMin)) / lCurrentWidth);
			lByteArray[i] = lByteMappedValue;
		}

		pMinMax[0] = lNewMin;
		pMinMax[1] = lNewMax;
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

	public int getMaxBufferLength()
	{
		return mMaxBufferLength;
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

	public void setSyncToRefresh(final boolean syncToRefresh)
	{
		mSyncToRefresh = syncToRefresh;
	}

	public void display()
	{
		mGLWindow.display();
	}

	public void setVisible(final boolean pB)
	{
		mGLWindow.setVisible(pB);
	}

	public boolean isVisible()
	{
		return mGLWindow.isVisible();
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

	public void setMinIntensity(final double minIntensity)
	{
		mMinIntensity = minIntensity;
	}

	public double getMaxIntensity()
	{
		return mMaxIntensity;
	}

	public void setMaxIntensity(final double maxIntensity)
	{
		mMaxIntensity = maxIntensity;
	}

	public boolean isManualMinMax()
	{
		return mManualMinMax;
	}

	public void setManualMinMax(final boolean manualMinMax)
	{
		mManualMinMax = manualMinMax;
	}

}
