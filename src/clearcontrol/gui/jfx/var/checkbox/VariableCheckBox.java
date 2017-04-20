package clearcontrol.gui.jfx.var.checkbox;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import clearcontrol.core.variable.Variable;

/**
 * Check box that is synced to a boolean variable
 *
 * @author royer
 */
public class VariableCheckBox extends HBox
{

  private final Label mLabel;
  private final CheckBox mCheckBox;
  private Variable<Boolean> mVariable;

  /**
   * Instanciates a checkbox
   * 
   * @param pCheckBoxLabel
   *          checkbox label
   * @param pVariable
   *          variable
   */
  public VariableCheckBox(String pCheckBoxLabel,
                          Variable<Boolean> pVariable)
  {
    super();
    mVariable = pVariable;

    mLabel = new Label(pCheckBoxLabel);
    mLabel.setAlignment(Pos.CENTER_LEFT);

    mCheckBox = new CheckBox();
    mCheckBox.setAlignment(Pos.CENTER);

    // getCheckBox().setPrefWidth(7 * 15);

    getChildren().add(getCheckBox());
    getChildren().add(getLabel());

    mVariable.addSetListener((o, n) -> {
      if (n != mCheckBox.isSelected() && n != null)
        Platform.runLater(() -> {
          mCheckBox.setSelected(n);
        });
    });

    mCheckBox.setOnAction((e) -> {
      if (mCheckBox.isSelected() != mVariable.get())
        mVariable.setAsync(mCheckBox.isSelected());
    });

    Platform.runLater(() -> {
      mCheckBox.setSelected(mVariable.get());
    });

  }

  /**
   * Returns label
   * 
   * @return internal label
   */
  public Label getLabel()
  {
    return mLabel;
  }

  /**
   * Returns checkbox itself
   * 
   * @return internal checkboxs
   */
  public CheckBox getCheckBox()
  {
    return mCheckBox;
  }

}
