package clearcontrol.gui.jfx.var.combo;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import clearcontrol.core.variable.Variable;

/**
 * Combo box that takes class elements
 *
 * @author royer
 */
public class ClassComboBoxVariable extends ComboBox<Class<?>>
{

  private Variable<Class<?>> mClassVariable;

  /**
   * Instantiates an enum combo box
   * 
   * @param pVariable
   *          variable to sync with
   * @param pArrayList
   *          classes to use
   * @param PrefWidth
   *          pref width for cells
   */
  @SuppressWarnings("unchecked")
  public ClassComboBoxVariable(Variable<?> pVariable,
                               ArrayList<?> pArrayList,
                               int PrefWidth)
  {
    super(FXCollections.observableArrayList(pArrayList.toArray(new Class[pArrayList.size()])));
    mClassVariable = (Variable<Class<?>>) pVariable;

    mClassVariable.addSetListener((o, n) -> {
      if (!n.equals(o) && n != null)
        Platform.runLater(() -> {
          if (n.equals(getSelectionModel().getSelectedItem()))
            return;
          getSelectionModel().select(n);
        });
    });

    if (mClassVariable.get() != null)
      getSelectionModel().select(mClassVariable.get());

    showingProperty().addListener((obs, o, n) -> {
      if (!n)
        if (mClassVariable.get() != getSelectionModel().getSelectedItem())
          mClassVariable.setAsync(getSelectionModel().getSelectedItem());
    });

    setCellFactory(new Callback<ListView<Class<?>>, ListCell<Class<?>>>()
    {
      @Override
      public ListCell<Class<?>> call(ListView<Class<?>> param)
      {
        final ListCell<Class<?>> cell = new ListCell<Class<?>>()
        {
          {
            super.setPrefWidth(PrefWidth);
          }

          @Override
          public void updateItem(Class<?> item, boolean empty)
          {
            super.updateItem(item, empty);
            if (item != null)
            {
              setText(item.getSimpleName());
            }
            else
            {
              setText(null);
            }
          }
        };
        return cell;
      }
    });

    setButtonCell(new ListCell<Class<?>>()
    {
      @Override
      protected void updateItem(Class<?> item, boolean bln)
      {
        super.updateItem(item, bln);
        if (bln)
        {
          setText("");
        }
        else
        {
          setText(item.getSimpleName());
        }
      }
    });

    /*valueProperty().addListener((obs, o, n) -> {
      if (n != o)
        mEnumVariable.setAsync(getSelectionModel().getSelectedItem());
    });/**/

  }

}
