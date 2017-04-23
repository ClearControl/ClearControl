package clearcontrol.gui.jfx.var.textfield;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

import clearcontrol.core.variable.Variable;

/**
 * Text field that is synced to a Number
 *
 * @author royer
 */
public class StringVariableTextField extends HBox
{

  private final Label mLabel;
  private final TextField mTextField;

  private Variable<String> mVariable;

  /**
   * Instantiates a number variable text field.
   * 
   * @param pTextFieldLabel
   *          text field
   * @param pVariable
   *          variable
   */
  public StringVariableTextField(String pTextFieldLabel,
                                 Variable<String> pVariable)
  {
    super();
    mVariable = pVariable;

    setAlignment(Pos.CENTER);
    // setPadding(new Insets(10, 10, 10, 10));

    mLabel = new Label(pTextFieldLabel);
    mLabel.setAlignment(Pos.CENTER);

    mTextField = new TextField();
    mTextField.setAlignment(Pos.CENTER);

    getTextField().setPrefWidth(7 * 15);

    mTextField.setText(mVariable.get());

    getTextField().textProperty().addListener((obs, o, n) -> {
      if (o != null && !o.equals(n))
        setUpdatedTextField();
    });

    getTextField().focusedProperty().addListener((obs, o, n) -> {
      if (!n)
        setVariableValueFromTextField();
    });

    getTextField().setOnKeyPressed((e) -> {
      if (e.getCode().equals(KeyCode.ENTER))
        setVariableValueFromTextField();
    });

    getChildren().add(getLabel());
    getChildren().add(getTextField());

    mVariable.addSetListener((o, n) -> {
      if (n != null && !n.equals(o))
        Platform.runLater(() -> {
          if (n.equals(getTextFieldValue()))
            return;
          mTextField.setText(n);
        });
    });

  }

  private void setVariableValue(String pNewValue)
  {
    if (pNewValue != null && !pNewValue.equals(mVariable.get()))
    {
      mVariable.setAsync(pNewValue);
    }
  }

  private void setUpdatedTextField()
  {
    getTextField().setStyle("-fx-text-fill: orange");
  }

  private void setVariableValueFromTextField()
  {
    try
    {
      setVariableValue(getTextFieldValue());
      getTextField().setStyle("-fx-text-fill: black");
    }
    catch (NumberFormatException e)
    {
      getTextField().setStyle("-fx-text-fill: red");
    }
  }

  private String getTextFieldValue()
  {
    return mTextField.getText();
  }

  /**
   * Returns the label
   * 
   * @return label
   */
  public Label getLabel()
  {
    return mLabel;
  }

  /**
   * Returns the text field
   * 
   * @return text field
   */
  public TextField getTextField()
  {
    return mTextField;
  }

}
