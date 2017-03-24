package clearcontrol.gui.jfx.var.onoffarray;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.custom.iconswitch.IconSwitch;
import eu.hansolo.enzo.common.SymbolType;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class OnOffArrayPane extends CustomGridPane
{

  private boolean mVertical = true;
  private boolean mFancyStyle = false;
  private int mCursor = 0;

  public OnOffArrayPane()
  {
    super(0, CustomGridPane.cStandardGap);
  }

  public void addSwitch(String pName, Variable<Boolean> pVariable)
  {
    addSwitch(pName, pVariable, true);
  }

  public void addSwitch(String pName,
                        Variable<Boolean> pVariable,
                        boolean pBidirectional)
  {
    Control lControl;
    BooleanProperty lSelectedProperty;

    if (isFancyStyle())
    {
      IconSwitch lIconSwitch = new IconSwitch();
      lIconSwitch.setSymbolType(SymbolType.POWER);
      lIconSwitch.setSymbolColor(Color.web("#ffffff"));
      lIconSwitch.setSwitchColor(Color.web("#34495e"));
      lIconSwitch.setThumbColor(Color.web("#ff495e"));

      lControl = lIconSwitch;
      lSelectedProperty = lIconSwitch.selectedProperty();
    }
    else
    {
      CheckBox lCheckBox = new CheckBox();
      lControl = lCheckBox;
      lSelectedProperty = lCheckBox.selectedProperty();
    }

    lControl.setOnMouseClicked((e) -> {
      boolean lValue = lSelectedProperty.get();
      if (lValue != pVariable.get())
        pVariable.setAsync(lValue);
    });

    Label lSwitchName = new Label(pName);
    lSwitchName.setFont(new Font(16.0));

    HBox lHBox = new HBox(lSwitchName, lControl);
    lHBox.setSpacing(8);
    if (isVertical())
      lHBox.setAlignment(Pos.CENTER);
    else
      lHBox.setAlignment(Pos.CENTER_LEFT);
    add(lHBox, mCursor++, 0);

    pVariable.addSetListener((o, n) -> {
      if (lSelectedProperty.get() != n && n != null)
        Platform.runLater(() -> {
          lSelectedProperty.set(n);
        });

    });

    Platform.runLater(() -> {
      lSelectedProperty.set(pVariable.get());
    });
  }

  public boolean isVertical()
  {
    return mVertical;
  }

  public void setVertical(boolean pVertical)
  {
    mVertical = pVertical;
  }

  public boolean isFancyStyle()
  {
    return mFancyStyle;
  }

  public void setFancyStyle(boolean pFancyStyle)
  {
    mFancyStyle = pFancyStyle;
  }

}
