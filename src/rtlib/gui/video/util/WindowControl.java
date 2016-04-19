package rtlib.gui.video.util;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;

import cleargl.ClearGLWindow;

public class WindowControl extends WindowAdapter
{
	private ClearGLWindow mClearGLWindow;

	public WindowControl(ClearGLWindow pClearGLWindow)
	{
		mClearGLWindow = pClearGLWindow;
	}

	public void windowDestroyNotify(final WindowEvent e)
	{
		mClearGLWindow.setVisible(false);
	}
}
