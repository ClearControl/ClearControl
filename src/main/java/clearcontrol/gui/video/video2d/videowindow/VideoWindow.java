package clearcontrol.gui.video.video2d.videowindow;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.random;
import static java.lang.Math.round;

import java.io.IOException;
import java.nio.Buffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import clearcontrol.gui.video.util.WindowControl;
import cleargl.ClearGLDefaultEventListener;
import cleargl.ClearGLWindow;

import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.opengl.GLException;

import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import coremem.offheap.OffHeapMemory;
import coremem.util.Size;

public class VideoWindow implements AutoCloseable
{

  static final double cEpsilon = 0.01;

  static final float cPercentageOfPixelsToSample = 0.01f;

  static final int cMipMapLevel = 3;

  NativeTypeEnum mType;
  ClearGLWindow mClearGLWindow;
  volatile int mEffectiveWindowWidth;

  volatile int mEffectiveWindowHeight;

  volatile int mVideoWidth;

  volatile int mVideoHeight;

  volatile ContiguousMemoryInterface mSourceBuffer;
  volatile CountDownLatch mNotifyBufferCopy;
  volatile int mSourceBufferWidth;

  volatile int mSourceBufferHeight;

  volatile ContiguousMemoryInterface mConversionBuffer;

  private volatile boolean mDisplayFrameRate = true;

  volatile boolean mDisplayOn = true;

  volatile boolean mManualMinMax = false;

  volatile boolean mMinMaxFixed = false;

  volatile boolean mIsDisplayLines = false;

  volatile double mMinIntensity = 0;

  volatile double mMaxIntensity = 1;

  volatile double mGamma = 1;

  volatile boolean mFlipX = false;

  final ReentrantLock mSendBufferLock = new ReentrantLock();

  double mSampledMinIntensity;

  double mSampledMaxIntensity;

  private ClearGLDefaultEventListener mClearGLDebugEventListener;

  // private GLPixelBufferObject mPixelBufferObject;

  public VideoWindow(final String pWindowName,
                     final NativeTypeEnum pType,
                     final int pWindowWidth,
                     final int pWindowHeight,
                     final boolean pFlipX) throws GLException
  {
    mType = pType;
    mVideoWidth = pWindowWidth;
    mVideoHeight = pWindowHeight;

    // this is a guess until we get the actual values:
    mEffectiveWindowWidth = pWindowWidth;
    mEffectiveWindowHeight = pWindowHeight;
    mFlipX = pFlipX;

    mClearGLDebugEventListener =
                               new ClearGLDebugEventListenerForVideoWindow(this,
                                                                           mFlipX);

    mClearGLWindow = new ClearGLWindow(pWindowName,
                                       pWindowWidth,
                                       pWindowHeight,
                                       mClearGLDebugEventListener);
    mClearGLDebugEventListener.setClearGLWindow(mClearGLWindow);
    mClearGLWindow.setFPS(30);

    final MouseControl lMouseControl = new MouseControl(this);
    mClearGLWindow.addMouseListener(lMouseControl);
    final KeyboardControl lKeyboardControl =
                                           new KeyboardControl(this);
    mClearGLWindow.addKeyListener(lKeyboardControl);

    WindowAdapter lWindowControl = new WindowControl(getGLWindow());
    mClearGLWindow.addWindowListener(lWindowControl);

    getGLWindow().setDefaultCloseOperation(WindowClosingMode.DO_NOTHING_ON_CLOSE);

  }

  public void setWindowSize(int pWindowWidth, int pWindowHeigth)
  {
    mClearGLWindow.setSize(pWindowWidth, pWindowWidth);
  }

  public int getWindowWidth()
  {
    return mClearGLWindow.getWindowWidth();
  }

  public int getWindowHeight()
  {
    return mClearGLWindow.getWindowHeight();
  }

  public void setWidth(final int pVideoWidth)
  {
    mVideoWidth = pVideoWidth;
  }

  public void setHeight(final int pVideoHeight)
  {
    mVideoHeight = pVideoHeight;
  }

  public void sendBuffer(ContiguousMemoryInterface pSourceDataObject,
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
    mClearGLWindow.close();
  }

  public void setVisible(final boolean pVisible)
  {
    mClearGLWindow.setVisible(pVisible);
  }

  public boolean isVisible()
  {
    return mClearGLWindow.isVisible();
  }

  public void requestFocus()
  {
    mClearGLWindow.requestFocus();
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

  public boolean isFullScreen()
  {
    return mClearGLWindow.isFullscreen();
  }

  public void setFullScreen(boolean pFullScreen)
  {
    mClearGLWindow.setFullscreen(pFullScreen);
  }

  public void toggleFullScreen()
  {
    mClearGLWindow.toggleFullScreen();
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

  public void fastMinMaxSampling(final ContiguousMemoryInterface pMemory)
  {
    if (pMemory.isFree())
      return;

    final long lLength =
                       min(this.mSourceBufferWidth
                           * this.mSourceBufferHeight,
                           pMemory.getSizeInBytes() / Size.of(mType));
    final int lStep =
                    1 + round(VideoWindow.cPercentageOfPixelsToSample
                              * lLength);
    final int lStartPixel = (int) round(random() * lStep);

    double lMin = Double.POSITIVE_INFINITY;
    double lMax = Double.NEGATIVE_INFINITY;

    if (this.mType == NativeTypeEnum.UnsignedByte)
      for (int i = lStartPixel; i < lLength; i += lStep)
      {
        final double lValue =
                            (0xFF & pMemory.getByteAligned(i)) / 255d;
        lMin = min(lMin, lValue);
        lMax = max(lMax, lValue);
      }
    else if (this.mType == NativeTypeEnum.UnsignedShort)
      for (int i = lStartPixel; i < lLength; i += lStep)
      {
        final double lValue = (0xFFFF & pMemory.getCharAligned(i))
                              / 65535d;
        lMin = min(lMin, lValue);
        lMax = max(lMax, lValue);
      }
    else if (this.mType == NativeTypeEnum.UnsignedInt)
      for (int i = lStartPixel; i < lLength; i += lStep)
      {
        final double lValue = (0xFFFFFFFF & pMemory.getIntAligned(i))
                              / 4294967296d;
        lMin = min(lMin, lValue);
        lMax = max(lMax, lValue);
      }
    else if (this.mType == NativeTypeEnum.Float)
      for (int i = lStartPixel; i < lLength; i += lStep)
      {
        final float lFloatAligned = pMemory.getFloatAligned(i);
        lMin = min(lMin, lFloatAligned);
        lMax = max(lMax, lFloatAligned);
      }
    else if (this.mType == NativeTypeEnum.Double)
      for (int i = lStartPixel; i < lLength; i += lStep)
      {
        final double lDoubleAligned = pMemory.getDoubleAligned(i);
        lMin = min(lMin, lDoubleAligned);
        lMax = max(lMax, lDoubleAligned);
      }

    if (!Double.isFinite(this.mSampledMinIntensity))
      this.mSampledMinIntensity = 0;

    if (!Double.isFinite(this.mSampledMaxIntensity))
      this.mSampledMaxIntensity = 1;

    this.mSampledMinIntensity = (1 - VideoWindow.cEpsilon)
                                * this.mSampledMinIntensity
                                + VideoWindow.cEpsilon * lMin;
    this.mSampledMaxIntensity = (1 - VideoWindow.cEpsilon)
                                * this.mSampledMaxIntensity
                                + VideoWindow.cEpsilon * lMax;

    // System.out.println("mSampledMinIntensity=" +
    // mSampledMinIntensity);
    // System.out.println("mSampledMaxIntensity=" +
    // mSampledMaxIntensity);
  }

}
