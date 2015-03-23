package rtlib.gui.video.video2d.videowindow;

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
	private final VideoWindow<?> mVideoWindow;

	/**
	 * @param pJoglVolumeRenderer
	 */
	KeyboardControl(final VideoWindow<?> pVideoWindow)
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
			mVideoWindow.requestDisplay();
			break;
		case KeyEvent.VK_M:
			mVideoWindow.setManualMinMax(true);
			mVideoWindow.requestDisplay();
			break;
		case KeyEvent.VK_A:
			mVideoWindow.setManualMinMax(false);
			mVideoWindow.requestDisplay();
			break;
		case KeyEvent.VK_F:
			mVideoWindow.setMinMaxFixed(!mVideoWindow.isMinMaxFixed());
			mVideoWindow.requestDisplay();
			break;
		case KeyEvent.VK_L:
			mVideoWindow.setDisplayLines(!mVideoWindow.isDisplayLines());
			mVideoWindow.requestDisplay();
			break;
		}

	}

}