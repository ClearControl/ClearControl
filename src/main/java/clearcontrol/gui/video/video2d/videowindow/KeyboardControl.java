package clearcontrol.gui.video.video2d.videowindow;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

/**
 * Inner class encapsulating the MouseMotionListener and MouseWheelListener for
 * the interaction
 */
class KeyboardControl extends KeyAdapter implements KeyListener
{
  /**
   * 
   */
  private final VideoWindow mVideoWindow;

  /**
   * @param pJoglVolumeRenderer
   */
  KeyboardControl(final VideoWindow pVideoWindow)
  {
    mVideoWindow = pVideoWindow;
  }

  @Override
  public void keyPressed(final KeyEvent pE)
  {
    final boolean lIsShiftPressed = pE.isShiftDown();

    switch (pE.getKeyCode())
    {
    case KeyEvent.VK_G:
      mVideoWindow.setGamma(1);
      break;
    case KeyEvent.VK_M:
      mVideoWindow.setManualMinMax(true);
      break;
    case KeyEvent.VK_A:
      mVideoWindow.setManualMinMax(false);
      break;
    case KeyEvent.VK_F:
      mVideoWindow.setMinMaxFixed(!mVideoWindow.isMinMaxFixed());
      break;
    case KeyEvent.VK_L:
      mVideoWindow.setDisplayLines(!mVideoWindow.isDisplayLines());
      break;
    case KeyEvent.VK_ESCAPE:
      if (mVideoWindow.isFullScreen())
        mVideoWindow.setFullScreen(false);
      break;
    }

  }

}