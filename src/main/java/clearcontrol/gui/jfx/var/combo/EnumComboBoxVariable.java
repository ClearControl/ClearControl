package clearcontrol.gui.jfx.var.combo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import clearcontrol.core.variable.Variable;

/**
 * Combo box that takes enum elements
 *
 * @author royer
 * @param <T>
 *          selectable type
 */
public class EnumComboBoxVariable<T extends Enum<T>>
                                 extends ComboBox<T>
{

  private Variable<T> mEnumVariable;

  /**
   * Instanciates an enum combo box
   * 
   * @param pVariable
   *          variable to sync with
   * @param pEnumValues
   *          enum values to use
   */
  public EnumComboBoxVariable(Variable<T> pVariable, T[] pEnumValues)
  {
    super(FXCollections.observableArrayList(pEnumValues));
    mEnumVariable = pVariable;

    mEnumVariable.addSetListener((o, n) -> {
      if (!n.equals(o) && n != null)
        Platform.runLater(() -> {
          if (n.equals(getSelectionModel().getSelectedItem()))
            return;
          getSelectionModel().select(n);
        });
    });

    if (mEnumVariable.get() != null)
      getSelectionModel().select(mEnumVariable.get());

    showingProperty().addListener((obs, o, n) -> {
      if (!n)
        if (mEnumVariable.get() != getSelectionModel().getSelectedItem())
          mEnumVariable.setAsync(getSelectionModel().getSelectedItem());
    });

    /*valueProperty().addListener((obs, o, n) -> {
      if (n != o)
        mEnumVariable.setAsync(getSelectionModel().getSelectedItem());
    });/**/

  }

}
