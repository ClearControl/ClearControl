package clearcontrol.gui.jfx.custom.labelgrid;

import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;

import org.apache.commons.lang3.tuple.Pair;

/**
 *
 *
 * @author royer
 */
public class LabelGrid extends CustomGridPane
{
  ConcurrentHashMap<Pair<Integer, Integer>, Label> mCellToLabelMap =
                                                                   new ConcurrentHashMap<>();

  /**
   * Instantiates a label grid
   */
  public LabelGrid()
  {
    super();
  }

  /**
   * Sets a given column name
   * 
   * @param pX
   *          column index
   * @param pColumnName
   *          column name
   * @return corresponding label
   */
  public Label setColumnName(int pX, String pColumnName)
  {
    Label lLabel = getLabelInternal(pX + 1, 0, false);
    lLabel.setText(pColumnName);
    return lLabel;
  }

  /**
   * Sets a given row name
   * 
   * @param pY
   *          row index
   * @param pRowName
   *          row name
   * @return corresponding row name
   */
  public Label setRowName(int pY, String pRowName)
  {
    Label lLabel = getLabelInternal(0, pY + 1, false);
    lLabel.setText(pRowName);
    return lLabel;
  }

  /**
   * Returns the label at the given coordinates
   * 
   * @param pX
   *          column index
   * @param pY
   *          row index
   * @return label
   */
  public Label getLabel(int pX, int pY)
  {
    Label lLabel = getLabelInternal(pX + 1, pY + 1, true);

    return lLabel;
  }

  /**
   * Removes all labels
   */
  public void clear()
  {
    Platform.runLater(() -> getChildren().clear());
  }

  protected Label getLabelInternal(int pX, int pY, boolean pBoxStyle)
  {
    Pair<Integer, Integer> lKey = Pair.of(pX, pY);
    Label lLabel = mCellToLabelMap.get(lKey);

    if (lLabel == null)
    {
      lLabel = new Label();
      if (pBoxStyle)
        lLabel.setStyle("-fx-border-color: lightgray;");
      mCellToLabelMap.put(lKey, lLabel);
      setLabelInternal(pX, pY, lLabel);
    }
    return lLabel;
  }

  protected void setLabelInternal(int pX, int pY, Label lLabel)
  {
    final Label lLabelFinal = lLabel;

    GridPane.setHgrow(lLabel, Priority.ALWAYS);
    GridPane.setVgrow(lLabel, Priority.ALWAYS);
    Platform.runLater(() -> add(lLabelFinal, pX, pY));
  }

}
