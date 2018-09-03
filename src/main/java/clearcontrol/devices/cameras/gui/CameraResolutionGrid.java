package clearcontrol.devices.cameras.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * Camera resolution grid
 *
 * @author royer
 */
public class CameraResolutionGrid extends GridPane
{

  /**
   * Camera resolution grid event button handler
   *
   * @author royer
   */
  public interface ButtonEventHandler
  {
    /**
     * Returns an event handler for the button of corresponding image width and
     * height
     * 
     * @param pWidth
     * @param pheight
     * @return handler
     */
    EventHandler<ActionEvent> getHandler(int pWidth, int pheight);
  }

  /**
   * Instantiates a camera resolution grid
   * 
   * @param pButtonEventHandler
   *          button handler
   * @param pMaxWidth
   *          max width
   * @param pMaxHeight
   *          max height
   */
  public CameraResolutionGrid(ButtonEventHandler pButtonEventHandler,
                              int pMaxWidth,
                              int pMaxHeight)
  {
    super();

    setGridLinesVisible(true);

    int lMaxPowerWidth = log2(pMaxWidth);
    int lMaxPowerHeight = log2(pMaxHeight);

    for (int x = 7; x < lMaxPowerWidth; x++)
    {
      for (int y = 7; y < lMaxPowerHeight; y++)
      {
        int width = 2 << x;
        int height = 2 << y;

        Button button = new Button(width + "\n" + height);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinWidth(45);
        button.setMinHeight(45);
        button.setOnAction(pButtonEventHandler.getHandler(width,
                                                          height));

        // Place the button on the GridPane
        add(button, x, y);
      }
    }
  }

  private static int log2(int n)
  {
    if (n <= 0)
      throw new IllegalArgumentException();
    return 31 - Integer.numberOfLeadingZeros(n);
  }

}
