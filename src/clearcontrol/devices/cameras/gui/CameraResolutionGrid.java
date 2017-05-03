package clearcontrol.devices.cameras.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 *
 *
 * @author royer
 */
public class CameraResolutionGrid extends GridPane
{

  /**
   *
   *
   * @author royer
   */
  public interface ButtonEventHandler
  {
    /**
     * @param pWidth
     * @param pheight
     * @param x
     * @param y
     * @return
     */
    EventHandler<ActionEvent> getHandler(int pWidth, int pheight);
  }

  /**
   * @param pButtonEventHandler
   * @param pEventHandler
   */
  public CameraResolutionGrid(ButtonEventHandler pButtonEventHandler,
                              int pPowerMin,
                              int pPowerMax)
  {
    super();

    setGridLinesVisible(true);

    for (int x = pPowerMin; x < pPowerMax; x++)
    {
      for (int y = pPowerMin; y < pPowerMax; y++)
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

}
