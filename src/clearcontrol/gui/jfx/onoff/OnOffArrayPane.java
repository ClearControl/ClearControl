package clearcontrol.gui.jfx.onoff;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import clearcontrol.gui.jfx.iconswitch.IconSwitch;
import clearcontrol.gui.variable.JFXPropertyVariable;
import eu.hansolo.enzo.common.SymbolType;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class OnOffArrayPane extends StandardGridPane
{

	private boolean mVertical = true;
	private boolean mFancyStyle = false;
	private int mCursor = 0;



	public void addSwitch(String pName, Variable<Boolean> pVariable)
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

		Label lSwitchName = new Label(pName);
		lSwitchName.setFont(new Font(16.0));

		HBox lHBox = new HBox(lSwitchName, lControl);
		lHBox.setSpacing(8);
		if (isVertical())
			lHBox.setAlignment(Pos.CENTER);
		else
			lHBox.setAlignment(Pos.CENTER_LEFT);
		add(lHBox, mCursor++, 0);

		JFXPropertyVariable<Boolean> lJFXPropertyVariable = new JFXPropertyVariable<Boolean>(	lSelectedProperty,
																																													pVariable.getName(),
																																													false);

		lJFXPropertyVariable.syncWith(pVariable);
		lSelectedProperty.set(pVariable.get());

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
