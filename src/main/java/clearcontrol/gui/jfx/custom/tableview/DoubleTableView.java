package clearcontrol.gui.jfx.custom.tableview;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Table view of doubles
 *
 * @author royer
 */
public class DoubleTableView extends TableView<DoubleRow>
{

  private EventHandler<CellEditEvent<DoubleRow, Double>> mEventHandler;
  private int mTypicalColumnWidth;

  /**
   * Instantiates a table view of doubles
   * 
   * @param pTypicallColumnWidth
   *          typicall column width
   */
  public DoubleTableView(int pTypicallColumnWidth)
  {
    super();
    mTypicalColumnWidth = pTypicallColumnWidth;

    mEventHandler =
                  new EventHandler<TableColumn.CellEditEvent<DoubleRow, Double>>()
                  {
                    @Override
                    public void handle(TableColumn.CellEditEvent<DoubleRow, Double> t)
                    {
                      int lColumn = t.getTablePosition().getColumn();
                      double lNewValue = t.getNewValue();

                      t.getTableView()
                       .getItems()
                       .get(t.getTablePosition().getRow())
                       .setValue(lColumn, lNewValue);
                    }
                  };

    setEditable(true);

    getSelectionModel().setCellSelectionEnabled(true);
  }

  /**
   * Adds a column
   * 
   * @param pColumnHeader
   *          column header
   * @param pIsEditable
   *          true for editable columns
   * @param pIsRezisable
   *          true for resizable
   * @param pMenuItemSpecifications
   *          vararg list of contextual menu items specifications to associate
   *          to this column
   * @return column index
   */
  @SafeVarargs
  public final int addColumn(final String pColumnHeader,
                             final boolean pIsEditable,
                             final boolean pIsRezisable,
                             Pair<String, EditableTableCellHandler>... pMenuItemSpecifications)
  {
    TableColumn<DoubleRow, Double> lColumn =
                                           new TableColumn<>(pColumnHeader);
    lColumn.setPrefWidth(mTypicalColumnWidth);
    lColumn.setMinWidth(mTypicalColumnWidth / 1.3);
    lColumn.setMaxWidth(mTypicalColumnWidth * 1.3);
    lColumn.setResizable(pIsRezisable);

    final int lColumnIndex = getColumns().size();
    lColumn.setCellValueFactory((p) -> p.getValue()
                                        .getValue(lColumnIndex)
                                        .asObject());

    if (pIsEditable)
      lColumn.setCellFactory(new Callback<TableColumn<DoubleRow, Double>, TableCell<DoubleRow, Double>>()
      {
        @Override
        public TableCell<DoubleRow, Double> call(TableColumn<DoubleRow, Double> p)
        {

          return new EditableTableCell(DoubleTableView.this,
                                       lColumnIndex,
                                       mTypicalColumnWidth,
                                       pMenuItemSpecifications);
        }
      });

    if (pIsEditable)
      lColumn.setOnEditCommit(mEventHandler);

    getColumns().add(lColumn);

    return lColumnIndex;
  }

}
