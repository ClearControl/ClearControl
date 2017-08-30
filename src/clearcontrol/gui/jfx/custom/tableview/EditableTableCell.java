package clearcontrol.gui.jfx.custom.tableview;

import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Editable table cell
 *
 * @author royer
 */
public class EditableTableCell extends TableCell<DoubleRow, Double>
{

  private TextField mTextField;
  private static volatile double sClipBoard;

  /**
   * Instantiates an editable table cell
   * 
   * @param pDoubleTableView
   *          parent table view
   * @param pColumnIndex
   *          column index
   * @param pMinColumnWidth
   *          min column width
   * @param pMenuItemSpecifications
   *          vararg list of context menu item specifications
   */
  @SafeVarargs
  public EditableTableCell(DoubleTableView pDoubleTableView,
                           int pColumnIndex,
                           int pMinColumnWidth,
                           Pair<String, EditableTableCellHandler>... pMenuItemSpecifications)
  {
    final ContextMenu lContextMenu = new ContextMenu();

    MenuItem lCopyCell = new MenuItem("Copy");
    lCopyCell.setOnAction((e) -> {
      sClipBoard = getItem();
    });

    MenuItem lPasteCell = new MenuItem("Paste");
    lPasteCell.setOnAction((e) -> {
      int lRowIndex = getTableRow().getIndex();

      pDoubleTableView.getItems()
                      .get(lRowIndex)
                      .setValue(pColumnIndex, sClipBoard);
    });

    MenuItem lSetColumn = new MenuItem("Set Column");
    lSetColumn.setOnAction((e) -> {
      for (DoubleRow lDoubleRow : pDoubleTableView.getItems())
        lDoubleRow.setValue(pColumnIndex, getItem());
    });

    lContextMenu.getItems().addAll(lCopyCell, lPasteCell, lSetColumn);

    for (Pair<String, EditableTableCellHandler> lMenuItemSpecification : pMenuItemSpecifications)
    {
      String lMenuText = lMenuItemSpecification.getKey();
      EditableTableCellHandler lHandler =
                                        lMenuItemSpecification.getValue();

      MenuItem lMenuItem = new MenuItem(lMenuText);
      lMenuItem.setOnAction((e) -> lHandler.handle(e, this));
      lContextMenu.getItems().add(lMenuItem);
      lMenuItem.setUserData(getItem());
    }
    setContextMenu(lContextMenu);

    setMinWidth(pMinColumnWidth);
    setMaxWidth(pMinColumnWidth);

  }

  @Override
  public void startEdit()
  {
    super.startEdit();

    if (mTextField == null)
    {
      createTextField();
    }

    setGraphic(mTextField);
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    mTextField.selectAll();
  }

  @Override
  public void cancelEdit()
  {
    super.cancelEdit();

    setText(String.valueOf(getItem()));
    setContentDisplay(ContentDisplay.TEXT_ONLY);
  }

  @Override
  public void updateItem(Double item, boolean empty)
  {
    super.updateItem(item, empty);

    if (empty)
    {
      setText(null);
      setGraphic(null);
    }
    else
    {
      if (isEditing())
      {
        if (mTextField != null)
        {
          mTextField.setText(getString());
        }
        setGraphic(mTextField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      }
      else
      {
        setText(getString());
        setContentDisplay(ContentDisplay.TEXT_ONLY);
      }
    }
  }

  private void createTextField()
  {
    mTextField = new TextField(getString());
    mTextField.setMinWidth(this.getWidth()
                           - this.getGraphicTextGap() * 2);
    mTextField.setOnKeyPressed(new EventHandler<KeyEvent>()
    {

      @Override
      public void handle(KeyEvent t)
      {
        if (t.getCode() == KeyCode.ENTER)
        {
          try
          {
            commitEdit(Double.parseDouble(mTextField.getText()));
          }
          catch (NumberFormatException e)
          {
          }
        }
        else if (t.getCode() == KeyCode.ESCAPE)
        {
          cancelEdit();
        }
      }
    });
  }

  private String getString()
  {
    return getItem() == null ? "" : getItem().toString();
  }
}