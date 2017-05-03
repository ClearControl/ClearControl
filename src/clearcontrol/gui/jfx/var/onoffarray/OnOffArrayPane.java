package clearcontrol.gui.jfx.var.onoffarray;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.custom.iconswitch.IconSwitch;
import eu.hansolo.enzo.common.SymbolType;

/**
 * Horizontal array of on/off switches
 *
 * @author royer
 */
public class OnOffArrayPane extends CustomGridPane
{

  private boolean mVertical = true;
  private boolean mFancyStyle = false;
  private int mCursor = 0;

  /**
   * Instantiates an On/Off array
   */
  public OnOffArrayPane()
  {
    super(0, CustomGridPane.cStandardGap);
  }

  /**
   * Adds a given switch.
   * 
   * @param pLabelText
   *          label text
   * @param pVariable
   *          boolean variable for switch
   */
  public void addSwitch(String pLabelText,
                        Variable<Boolean> pVariable)
  {
    addSwitch(pLabelText, pVariable, true);
  }

  /**
   * Adds a given switch.
   * 
   * @param pLabelText
   *          label text
   * @param pVariable
   *          boolean variable for switch
   * @param pBidirectional
   *          bidirectional synchronization
   */
  public void addSwitch(String pLabelText,
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

    Label lSwitchName = new Label(pLabelText);
    // lSwitchName.setFont(new Font(16.0));

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

  /**
   * Returns true if this array is vertical
   * 
   * @return true -> vertical
   */
  public boolean isVertical()
  {
    return mVertical;
  }

  /**
   * Sets whether this array should be vertical
   * 
   * @param pVertical
   *          true -> vertical
   */
  public void setVertical(boolean pVertical)
  {
    mVertical = pVertical;
  }

  /**
   * Returns true if this array has the 'fancy style'
   * 
   * @return true -> fancy style
   */
  public boolean isFancyStyle()
  {
    return mFancyStyle;
  }

  /**
   * Sets whether this array should be 'fancy'
   * 
   * @param pFancyStyle
   *          true -> fancy style
   */
  public void setFancyStyle(boolean pFancyStyle)
  {
    mFancyStyle = pFancyStyle;
  }

}
