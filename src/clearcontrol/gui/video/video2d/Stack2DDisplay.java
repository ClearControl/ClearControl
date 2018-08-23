package clearcontrol.gui.video.video2d;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import clearcontrol.core.concurrent.executors.AsynchronousSchedulerFeature;
import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.video.StackDisplayInterface;
import clearcontrol.gui.video.util.MinMaxControlDialog;
import clearcontrol.gui.video.video2d.videowindow.VideoWindow;
import clearcontrol.stack.EmptyStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.imglib2.ImageJStackDisplay;
import cleargl.ClearGLWindow;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;

import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import coremem.exceptions.FreedException;

/**
 * Stack 2D display
 *
 * @author royer
 */
public class Stack2DDisplay extends VirtualDevice implements
                            StackDisplayInterface,
                            AsynchronousSchedulerFeature
{
  private final VideoWindow mVideoWindow;

  private final Variable<StackInterface> mInputStackVariable;
  private boolean mFlipX = false;
  private Variable<StackInterface> mOutputStackVariable;

  private volatile StackInterface mReceivedStackCopy;

  private final Variable<Boolean> mDisplayOn;
  private final Variable<Boolean> mManualMinMaxIntensity;
  private final Variable<Double> mMinimumIntensity;
  private final Variable<Double> mMaximumIntensity;

  private final Variable<Double> mStackSliceNormalizedIndex;

  private AsynchronousProcessorBase<StackInterface, Object> mAsynchronousDisplayUpdater;

  private final ReentrantLock mDisplayLock = new ReentrantLock();

  /**
   * Instantiates a stack 2D display
   */
  public Stack2DDisplay()
  {
    this("2D Video Display", 512, 512, false, 1);
  }

  /**
   * Instantiates a stack 2D display
   * 
   * @param pVideoWidth
   *          window width
   * @param pVideoHeight
   *          window height
   */
  public Stack2DDisplay(final int pVideoWidth, final int pVideoHeight)
  {
    this("2D Video Display", pVideoWidth, pVideoHeight, false, 10);
  }

  /**
   * Instantiates a stack 2D display
   * 
   * @param pWindowName
   *          window name
   * @param pWindowWidth
   *          window width
   * @param pWindowHeight
   *          window height
   * @param pFlipX
   *          flip image horizontally
   */
  public Stack2DDisplay(final String pWindowName,
                        final int pWindowWidth,
                        final int pWindowHeight,
                        final boolean pFlipX)
  {
    this(pWindowName, pWindowWidth, pWindowHeight, pFlipX, 10);
  }

  /**
   * Instantiates a stack 2D display
   * 
   * @param pWindowName
   *          window name
   * @param pWindowWidth
   *          window width
   * @param pWindowHeight
   *          window height
   * @param pFlipX
   *          flip image horizontally
   * @param pUpdaterQueueLength
   *          updater queue length
   */
  public Stack2DDisplay(final String pWindowName,
                        final int pWindowWidth,
                        final int pWindowHeight,
                        final boolean pFlipX,
                        final int pUpdaterQueueLength)
  {
    super(pWindowName);

    mVideoWindow =
                 new VideoWindow(pWindowName,
                                 NativeTypeEnum.UnsignedShort,
                                 pWindowWidth,
                                 pWindowHeight,
                                 pFlipX);
    mFlipX = pFlipX;

    // mVideoWindow.setVisible(true);

    final MouseAdapter lMouseAdapter = new MouseAdapter()
    {
      @Override
      public void mouseDragged(MouseEvent pMouseEvent)
      {
        if (pMouseEvent.isAltDown() && pMouseEvent.isButtonDown(1))
        {
          final double nx = ((double) pMouseEvent.getX())
                            / mVideoWindow.getWindowWidth();
          mStackSliceNormalizedIndex.set(nx);
          displayStack(mReceivedStackCopy, true);
        }

        super.mouseDragged(pMouseEvent);
      }
    };

    mVideoWindow.getGLWindow().addMouseListener(lMouseAdapter);

    KeyListener lKeyAdapter = new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent pE)
      {
        switch (pE.getKeyCode())
        {
        case KeyEvent.VK_I:
          try
          {
            mDisplayLock.lock();
            ImageJStackDisplay.show(mReceivedStackCopy);
            mDisplayLock.unlock();
          }
          catch (Throwable e)
          {
            e.printStackTrace();
          }

          break;
        case KeyEvent.VK_V:
          openVisualisationOptionsDialog();
          break;
        }

      }
    };

    mVideoWindow.getGLWindow().addKeyListener(lKeyAdapter);

    mAsynchronousDisplayUpdater =
                                new AsynchronousProcessorBase<StackInterface, Object>("AsynchronousDisplayUpdater",
                                                                                      pUpdaterQueueLength)
                                {

                                  /**
                                   * Interface method implementation
                                   * 
                                   * @see clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorBase#process(java.lang.Object)
                                   */
                                  @Override
                                  public Object process(final StackInterface pStack)
                                  {
                                    if (pStack instanceof EmptyStack)
                                      return null;

                                    try
                                    {
                                      if (mVideoWindow.isVisible())
                                      {
                                        makeCopyOfReceivedStack(pStack);
                                        displayStack(mReceivedStackCopy,
                                                     false);
                                      }
                                      forwardStack(pStack);
                                    }
                                    catch (FreedException e)
                                    {
                                      System.err.println(this.getClass()
                                                             .getSimpleName()
                                                         + ": Underlying ressource has been freed while processing last stack");
                                    }

                                    return null;
                                  }

                                };

    mAsynchronousDisplayUpdater.start();

    mInputStackVariable = new Variable<StackInterface>(pWindowName
                                                       + "StackInput")
    {

      @Override
      public StackInterface setEventHook(final StackInterface pOldStack,
                                         final StackInterface pNewStack)
      {
        if (!mAsynchronousDisplayUpdater.passOrFail(pNewStack))
          forwardStack(pNewStack);
        return super.setEventHook(pOldStack, pNewStack);
      }

    };

    mDisplayOn = new Variable<Boolean>("DisplayOn", false)
    {
      @Override
      public Boolean setEventHook(final Boolean pOldValue,
                                  final Boolean pNewValue)
      {
        final boolean lDisplayOn = pNewValue;
        mVideoWindow.setDisplayOn(lDisplayOn);
        return super.setEventHook(pOldValue, pNewValue);
      }
    };

    mManualMinMaxIntensity =
                           new Variable<Boolean>("ManualMinMaxIntensity",
                                                 false)
                           {
                             @Override
                             public Boolean setEventHook(final Boolean pOldValue,
                                                         final Boolean pNewValue)
                             {
                               final boolean lManualMinMax =
                                                           pNewValue;
                               mVideoWindow.setManualMinMax(lManualMinMax);
                               return super.setEventHook(pOldValue,
                                                         pNewValue);
                             }
                           };

    mMinimumIntensity = new Variable<Double>("MinimumIntensity", 0.0)
    {
      @Override
      public Double setEventHook(final Double pOldValue,
                                 final Double pNewMinIntensity)
      {
        final double lMinIntensity = Math.pow(pNewMinIntensity, 6);
        mVideoWindow.setMinIntensity(lMinIntensity);
        return super.setEventHook(pOldValue, pNewMinIntensity);
      }
    };

    mMaximumIntensity = new Variable<Double>("MaximumIntensity", 1.0)
    {
      @Override
      public Double setEventHook(final Double pOldValue,
                                 final Double pNewMaxIntensity)
      {
        final double lMaxIntensity = Math.pow(pNewMaxIntensity, 6);
        mVideoWindow.setMaxIntensity(lMaxIntensity);
        return super.setEventHook(pOldValue, pNewMaxIntensity);
      }
    };

    mStackSliceNormalizedIndex =
                               new Variable<Double>("StackSliceNormalizedIndex",
                                                    Double.NaN);

    Runnable lAutoRescaleRunnable = () -> {
      boolean lTryLock = false;
      try
      {
        lTryLock = mDisplayLock.tryLock(1, TimeUnit.MILLISECONDS);
        if (lTryLock && mReceivedStackCopy != null)
        {
          int lStackZIndex =
                           getCurrentStackPlaneIndex(mReceivedStackCopy);
          ContiguousMemoryInterface lContiguousMemory =
                                                      mReceivedStackCopy.getContiguousMemory(lStackZIndex);

          if (mVideoWindow != null)
            mVideoWindow.fastMinMaxSampling(lContiguousMemory);

        }
      }
      catch (Throwable e)
      {
        e.printStackTrace();
      }
      finally
      {
        if (lTryLock)
          mDisplayLock.unlock();
      }

    };

    scheduleAtFixedRate(lAutoRescaleRunnable,
                        10,
                        TimeUnit.MILLISECONDS);
    /**/

  }

  private void openVisualisationOptionsDialog()
  {
    mVideoWindow.setManualMinMax(true);

    MinMaxControlDialog.showDialog(mVideoWindow);
  }

  private void makeCopyOfReceivedStack(final StackInterface pStack)
  {
    if (mReceivedStackCopy == null
        || mReceivedStackCopy.getWidth() != pStack.getWidth()
        || mReceivedStackCopy.getHeight() != pStack.getHeight()
        || mReceivedStackCopy.getDepth() != pStack.getDepth()
        || mReceivedStackCopy.getSizeInBytes() != pStack.getSizeInBytes())
    {
      if (mReceivedStackCopy != null)
      {
        mDisplayLock.lock();
        final StackInterface lStackToFree = mReceivedStackCopy;
        mReceivedStackCopy = pStack.allocateSameSize();
        lStackToFree.free();
        mDisplayLock.unlock();

      }
      else
        mReceivedStackCopy = pStack.allocateSameSize();
    }

    if (!mReceivedStackCopy.isFree())
    {
      mDisplayLock.lock();
      mReceivedStackCopy.getContiguousMemory()
                        .copyFrom(pStack.getContiguousMemory());
      mDisplayLock.unlock();

    }
  }

  private void displayStack(final StackInterface pStack,
                            boolean pNonBlockingLock)
  {
    boolean lTryLock = false;
    if (pNonBlockingLock)
      try
      {
        lTryLock = mDisplayLock.tryLock(0, TimeUnit.MILLISECONDS);
      }
      catch (InterruptedException e)
      {
      }
    else
    {
      mDisplayLock.lock();
      lTryLock = true;
    }

    if (lTryLock)
    {
      if (pStack != null)
      {

        final int lStackWidth = (int) pStack.getWidth();
        final int lStackHeight = (int) pStack.getHeight();
        final int lStackDepth = (int) pStack.getDepth();
        if (lStackDepth > 1)
        {

          int lStackZIndex = getCurrentStackPlaneIndex(pStack);

          final ContiguousMemoryInterface lContiguousMemory =
                                                            pStack.getContiguousMemory(lStackZIndex);
          mVideoWindow.sendBuffer(lContiguousMemory,
                                  lStackWidth,
                                  lStackHeight);
          mVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
        }
        else
        {
          final ContiguousMemoryInterface lContiguousMemory =
                                                            pStack.getContiguousMemory(0);
          mVideoWindow.sendBuffer(lContiguousMemory,
                                  lStackWidth,
                                  lStackHeight);
          mVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
        }
        mVideoWindow.setWidth(lStackWidth);
        mVideoWindow.setHeight(lStackHeight);
      }

      mDisplayLock.unlock();
    }

  }

  public int getCurrentStackPlaneIndex(StackInterface pStack)
  {
    long lStackDepth = pStack.getDepth();

    int lStackZIndex = (int) (mStackSliceNormalizedIndex.get()
                              * lStackDepth);
    if (lStackZIndex < 0)
      lStackZIndex = 0;
    else if (lStackZIndex >= lStackDepth)
      lStackZIndex = (int) (lStackDepth - 1);
    else if (Double.isNaN(lStackZIndex))
      lStackZIndex = (int) Math.round(lStackDepth / 2.0);
    return lStackZIndex;
  }

  private void forwardStack(final StackInterface pStack)
  {
    if (mOutputStackVariable == null && !pStack.isReleased())
    {
      pStack.release();
      return;
    }
    mOutputStackVariable.set(pStack);
  }

  @Override
  public Variable<StackInterface> getOutputStackVariable()
  {
    return mOutputStackVariable;
  }

  @Override
  public void setOutputStackVariable(Variable<StackInterface> pOutputStackVariable)
  {
    mOutputStackVariable = pOutputStackVariable;
  }

  public Variable<Boolean> getDisplayOnVariable()
  {
    return mDisplayOn;
  }

  public Variable<Boolean> getManualMinMaxIntensityOnVariable()
  {
    return mManualMinMaxIntensity;
  }

  public Variable<Double> getMinimumIntensityVariable()
  {
    return mMinimumIntensity;
  }

  public Variable<Double> getMaximumIntensityVariable()
  {
    return mMaximumIntensity;
  }

  @Override
  public Variable<StackInterface> getInputStackVariable()
  {
    return mInputStackVariable;
  }

  public VideoWindow getVideoWindow()
  {
    return mVideoWindow;
  }

  public void setVisible(final boolean pIsVisible)
  {
    mVideoWindow.setVisible(pIsVisible);
  }

  public boolean isVisible()
  {
    return mVideoWindow.isVisible();
  }

  public void requestFocus()
  {
    mVideoWindow.requestFocus();
  }

  @Override
  public boolean open()
  {
    mDisplayOn.set(true);
    mVideoWindow.start();
    return true;
  }

  @Override
  public boolean close()
  {
    setVisible(false);
    try
    {
      // if the mVideoWindow was never started, it crashes while stopping
      if (mDisplayOn.get())
      {
        mVideoWindow.stop();
        mDisplayOn.set(false);
      }
      mVideoWindow.setVisible(false);
      mVideoWindow.close();

      return true;
    }
    catch (final IOException e)
    {
      e.printStackTrace();
      return false;
    }
  }

  public void disableClose()
  {
    mVideoWindow.disableClose();
  }

  /**
   * Gets GL window for handling size and position.
   * 
   * @return the GL window
   */
  public ClearGLWindow getGLWindow()
  {
    return mVideoWindow.getGLWindow();
  }

  public StackInterface getLastViewedStack()
  {
    return mReceivedStackCopy;
  }

  public boolean isFlipX()
  {
    return mFlipX;
  }
}
