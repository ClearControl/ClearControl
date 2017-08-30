package clearcontrol.gui.jfx.custom.tableview;

import javafx.event.ActionEvent;

/**
 * Editable table cell context menu handler
 *
 * @author royer
 */
public interface EditableTableCellHandler
{

  /**
   * Handles a context menu
   * 
   * @param pE
   *          event
   * @param pEditableTableCell
   *          editable table cell
   */
  public void handle(ActionEvent pE,
                     EditableTableCell pEditableTableCell);

}
