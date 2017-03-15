package clearcontrol.core.log.gui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

class LogWindow extends JFrame
{

  private static final long serialVersionUID = 1L;

  private final LogPanel mLogPanel;

  public LogWindow(String title, int width, int height)
  {
    super(title);
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    setSize(width, height);
    mLogPanel = new LogPanel();
    getContentPane().add(mLogPanel);
    setVisible(true);
  }

  public void append(String pMessage)
  {
    mLogPanel.append(pMessage);
  }
}
