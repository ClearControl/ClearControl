package clearcontrol.gui.video.video2d.videowindow;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

/**
 * Inner class encapsulating the MouseMotionListener and MouseWheelListener for
 * the interaction
 */
class MouseControl extends MouseAdapter implements MouseListener
{
  /**
   * 
   */
  private final VideoWindow mVideoWindow;

  /**
   * @param pJoglVolumeRenderer
   */
  MouseControl(final VideoWindow pVideoWindow)
  {
    mVideoWindow = pVideoWindow;
  }

  @Override
  public void mouseDragged(final MouseEvent pMouseEvent)
  {
    handleGammaMinMax(pMouseEvent);

    // mVideoWindow.repaint();

  }

  public void handleGammaMinMax(final MouseEvent pMouseEvent)
  {
    if (!pMouseEvent.isShiftDown() && pMouseEvent.isControlDown()
        && pMouseEvent.isButtonDown(1))
    {

      final double nx = getNormalizedX(pMouseEvent);
      final double ny = getNormalizedY(pMouseEvent);

      final double lMin = pow(nx, 3);
      final double lMax = lMin + pow(1 - ny, 3);

      mVideoWindow.setManualMinMax(true);
      mVideoWindow.setMinIntensity(lMin);
      mVideoWindow.setMaxIntensity(lMax);
    }

    if (pMouseEvent.isShiftDown() && !pMouseEvent.isControlDown()
        && pMouseEvent.isButtonDown(1))
    {
      final double nx = getNormalizedX(pMouseEvent);

      final double lGamma = Math.tan(0.5 * Math.PI * nx);
      mVideoWindow.setGamma(lGamma);
    }
  }

  private double getNormalizedX(final MouseEvent pMouseEvent)
  {
    final double lWindowWidth =
                              mVideoWindow.getEffectiveWindowWidth();
    final double lMouseX = max(0,
                               min(lWindowWidth, pMouseEvent.getX()));
    final double nx = lMouseX / lWindowWidth;
    return nx;
  }

  private double getNormalizedY(final MouseEvent pMouseEvent)
  {
    final double lWindowHeight =
                               mVideoWindow.getEffectiveWindowHeight();
    final double lMouseY =
                         max(0,
                             min(lWindowHeight, pMouseEvent.getY()));
    final double ny = lMouseY / lWindowHeight;
    return ny;
  }

  @Override
  public void mouseMoved(final MouseEvent pMouseEvent)
  {

  }

  @Override
  public void mouseWheelMoved(final MouseEvent pMouseEvent)
  {

  }

  private boolean isRightMouseButton(MouseEvent pMouseEvent)
  {
    return ((pMouseEvent.getModifiers()
             & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK);
  }

  @Override
  public void mouseClicked(final MouseEvent pMouseEvent)
  {
    if (pMouseEvent.getClickCount() == 2)
      mVideoWindow.toggleFullScreen();
  }
}