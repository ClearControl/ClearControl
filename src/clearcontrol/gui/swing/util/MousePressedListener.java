package clearcontrol.gui.swing.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface MousePressedListener extends MouseListener
{
  @Override
  public default void mouseClicked(MouseEvent e)
  {
  }

  @Override
  public default void mouseEntered(MouseEvent e)
  {
  }

  @Override
  public default void mouseExited(MouseEvent e)
  {
  }

  @Override
  public default void mouseReleased(MouseEvent e)
  {
  }
}
