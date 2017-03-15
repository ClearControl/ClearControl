package clearcontrol.gui.jfx.custom.gridpane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class CustomGridPane extends GridPane
{

  public static final int cStandardGap = 5;
  public static final int cStandardPadding = 10;

  public CustomGridPane()
  {
    this(cStandardPadding, cStandardGap);
  }

  public CustomGridPane(int pAddPading, int pGaps)
  {
    super();
    setAlignment(Pos.CENTER);
    setGap(pGaps);
    setPadding(pAddPading);
  }

  public void setPadding(double pAddPading)
  {
    setPadding(new Insets(pAddPading,
                          pAddPading,
                          pAddPading,
                          pAddPading));
  }

  public void setGap(double pGap)
  {
    setHgap(pGap);
    setVgap(pGap);
  }

}
