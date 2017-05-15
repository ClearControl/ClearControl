package clearcontrol.gui.jfx.custom.labelgrid;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 *
 * @author royer
 */
public class LabelGrid extends CustomGridPane
{
  ConcurrentHashMap<Pair<Integer, Integer>, Label> mCellToLabelMap =
                                                                   new ConcurrentHashMap<>();

  public LabelGrid()
  {
    super();

  }

  public Label setColumnName(int pX, String pColumnName)
  {
    Label lLabel = new Label(pColumnName);
    getLabelInternal(pX + 1, 0, false).setText(pColumnName);
    return lLabel;
  }

  public Label setRowName(int pY, String pColumnName)
  {
    Label lLabel = new Label(pColumnName);
    getLabelInternal(0, pY + 1, false).setText(pColumnName);
    return lLabel;
  }

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
