package clearcontrol.gui.jfx.custom.gridpane;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

/**
 * Custom grid pane
 *
 * @author royer
 */
public class CustomGridPane extends GridPane
{
  protected int mRow;

  /**
   * Standard custom grid pane gap
   */
  public static final int cStandardGap = 5;
  /**
   * Standard custom grid pane padding
   */
  public static final int cStandardPadding = 10;

  /**
   * Instanciates a custom grid pane
   */
  public CustomGridPane()
  {
    this(cStandardPadding, cStandardGap);
  }

  /**
   * Instanciates a custom grid pane with given padding and gaps
   * 
   * @param pPadding
   *          padding
   * @param pGaps
   *          gaps
   */
  public CustomGridPane(int pPadding, int pGaps)
  {
    super();
    setAlignment(Pos.CENTER);
    setGap(pGaps);
    setPadding(pPadding);
  }

  /**
   * Sets pading
   * 
   * @param pPadding
   *          padding
   */
  public void setPadding(double pPadding)
  {
    setPadding(new Insets(pPadding, pPadding, pPadding, pPadding));
  }

  /**
   * Sets gap
   * 
   * @param pGap
   *          gap
   */
  public void setGap(double pGap)
  {
    setHgap(pGap);
    setVgap(pGap);
  }

  public void addSeparator()
  {
    {
      Separator lSeparator = new Separator();
      lSeparator.setOrientation(Orientation.HORIZONTAL);
      GridPane.setColumnSpan(lSeparator, 4);
      add(lSeparator, 0, mRow);
      mRow++;
    }
  }


}
