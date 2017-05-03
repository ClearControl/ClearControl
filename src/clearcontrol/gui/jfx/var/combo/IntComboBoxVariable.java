package clearcontrol.gui.jfx.var.combo;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import clearcontrol.core.variable.Variable;

/**
 * Combo box that takes enum elements
 *
 * @author royer
 * 
 */
public class IntComboBoxVariable extends ComboBox<Integer>
{

  private Variable<Integer> mEnumVariable;

  /**
   * Instantiates an int combo box for int values with an interval [min,max[
   * 
   * @param pVariable
   *          variable to sync with
   * @param pMin
   *          min (inclusive)
   * @param pMax
   *          max (exclusive)
   * 
   */
  public IntComboBoxVariable(Variable<Integer> pVariable,
                             int pMin,
                             int pMax)
  {
    super(FXCollections.observableArrayList(getIntList(pMin, pMax)));
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

  }

  private static ArrayList<Integer> getIntList(int pMin, int pMax)
  {
    ArrayList<Integer> lIntList = new ArrayList<>();

    for (int i = pMin; i <= pMax; i++)
      lIntList.add(i);

    return lIntList;
  }

}
