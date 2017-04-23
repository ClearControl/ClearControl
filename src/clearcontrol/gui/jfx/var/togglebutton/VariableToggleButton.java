package clearcontrol.gui.jfx.var.togglebutton;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

import clearcontrol.core.variable.Variable;

/**
 * Toggle button syncs to a boolean variable.
 *
 * @author royer
 */
public class VariableToggleButton extends ToggleButton
{

  /**
   * Instantiates an ordinary toggle button.
   */
  public VariableToggleButton()
  {
    super();
    setStyle();
  }

  /**
   * Instantiates an ordinary toggle button.
   * 
   * @param pText
   *          button text
   * @param pGraphic
   *          button graphic
   */
  public VariableToggleButton(String pText, Node pGraphic)
  {
    super(pText, pGraphic);
    setStyle();
  }

  /**
   * Instantiates an ordinary toggle button.
   * 
   * @param pText
   *          button text
   */
  public VariableToggleButton(String pText)
  {
    super(pText);
    setStyle();
  }

  /**
   * Instantiates an ordinary toggle button.
   * 
   * @param pSelectedText
   *          selected text
   * @param pUnselectedText
   *          unselected text
   */
  public VariableToggleButton(String pSelectedText,
                              String pUnselectedText)
  {
    super(pUnselectedText);
    setStyle();

    selectedProperty().addListener((e) -> {
      if (selectedProperty().get())
      {
        setText(pSelectedText);
      }
      else
      {
        setText(pUnselectedText);
      }
    });
  }

  /**
   * Instantiates an ordinary toggle button.
   * 
   * @param pSelectedText
   *          selected text
   * @param pUnselectedText
   *          unselected text
   * @param pBooleanVariable
   *          boolean variable to sync with
   */
  public VariableToggleButton(String pSelectedText,
                              String pUnselectedText,
                              Variable<Boolean> pBooleanVariable)
  {
    this(pSelectedText, pUnselectedText);

    pBooleanVariable.addSetListener((o, n) -> {
      if (selectedProperty().get() != n && n != null)
      {
        Platform.runLater(() -> {
          selectedProperty().set(n);
        });
      }
    });

    selectedProperty().addListener((e) -> {
      pBooleanVariable.setAsync(selectedProperty().get());
    });

    Platform.runLater(() -> {
      if (pBooleanVariable.get() != null)
        selectedProperty().set(pBooleanVariable.get());
    });

  }

  private void setStyle()
  {
    getStylesheets().add(getClass().getResource("css/coloredbutton.css")
                                   .toExternalForm());

  }

}
