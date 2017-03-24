package clearcontrol.gui.video.util;

import cleargl.ClearGLWindow;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;

public class WindowControl extends WindowAdapter
{
  private ClearGLWindow mClearGLWindow;

  public WindowControl(ClearGLWindow pClearGLWindow)
  {
    mClearGLWindow = pClearGLWindow;
  }

  @Override
  public void windowDestroyNotify(final WindowEvent e)
  {
    mClearGLWindow.setVisible(false);
  }
}
