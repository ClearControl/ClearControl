package clearcontrol.gui.jfx.var.lcd;

import javafx.application.Platform;
import javafx.scene.layout.HBox;

import clearcontrol.core.variable.Variable;
import eu.hansolo.enzo.lcd.Lcd;

/**
 * This LCD class syncs a LCD panel with a number variable
 *
 * @author royer
 * @param <T>
 *          lcd number type
 */
public class VariableLCD<T extends Number> extends HBox
{

  /**
   * Instantiates a variable synced LCD panel, given an existing LCD panel and
   * number variable
   * 
   * @param pLCDDisplay
   *          existing LCD panel
   * @param pNumberVariable
   *          number variable to sync with
   */
  public VariableLCD(Lcd pLCDDisplay, Variable<T> pNumberVariable)
  {
    pNumberVariable.addSetListener((o, n) -> {
      if (!n.equals(o) && n != null)
        Platform.runLater(() -> {
          double lValue = pNumberVariable.get().doubleValue();
          pLCDDisplay.valueProperty().set(lValue);
        });
    });

    getChildren().add(pLCDDisplay);
  }

}
