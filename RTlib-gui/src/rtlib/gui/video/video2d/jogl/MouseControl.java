package rtlib.gui.video.video2d.jogl;

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
		setTransfertFunctionRange(pMouseEvent);

		// mVideoWindow.repaint();

	}

	public void setTransfertFunctionRange(final MouseEvent pMouseEvent)
	{
		if (pMouseEvent.isShiftDown() && !pMouseEvent.isControlDown()
				&& pMouseEvent.isButtonDown(1))
		{

			final double nx = ((double) pMouseEvent.getX()) / mVideoWindow.getWindowWidth();

			mVideoWindow.setGamma(Math.tan(Math.PI * nx / 2));

		}

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
		return ((pMouseEvent.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK);
	}

	@Override
	public void mouseClicked(final MouseEvent pMouseEvent)
	{

	}
}