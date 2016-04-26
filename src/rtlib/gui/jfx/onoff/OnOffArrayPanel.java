package rtlib.gui.jfx.onoff;

import eu.hansolo.enzo.common.SymbolType;
import eu.hansolo.enzo.onoffswitch.IconSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import rtlib.core.variable.Variable;
import rtlib.gui.variable.JFXPropertyVariable;

public class OnOffArrayPanel extends GridPane
{

	private boolean mVertical;
	private int mCursor = 0;

	public OnOffArrayPanel()
	{
		this(false);
	}

	public OnOffArrayPanel(boolean pVertical)
	{
		mVertical = pVertical;
		
		setAlignment(Pos.CENTER);
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(25, 25, 25, 25));
	}

	public void addSwitch(String pName, Variable<Boolean> pVariable)
	{
		IconSwitch lOnSwitch = new IconSwitch();
		lOnSwitch.setSymbolType(SymbolType.POWER);
		lOnSwitch.setSymbolColor(Color.web("#ffffff"));
		lOnSwitch.setSwitchColor(Color.web("#34495e"));
		lOnSwitch.setThumbColor(Color.web("#ff495e"));

		Label lSwitchName = new Label(pName);
		lSwitchName.setFont(new Font(16.0));

		
		if (mVertical)
		{
			Label lBlankSpace = new Label("   ");
			lBlankSpace.setFont(new Font(16.0));
			HBox lHBox = new HBox(lOnSwitch, lSwitchName, lBlankSpace);
			lHBox.setSpacing(8);
			lHBox.setAlignment(Pos.CENTER);
			add(lHBox, mCursor++, 0);
		}
		else
		{
			HBox lHBox = new HBox(lOnSwitch, lSwitchName);
			lHBox.setSpacing(8);
			lHBox.setAlignment(Pos.CENTER_LEFT);
			add(lHBox, 0, mCursor++);
		}

		BooleanProperty lSelectedProperty = lOnSwitch.selectedProperty();

		JFXPropertyVariable<Boolean> lJFXPropertyVariable = new JFXPropertyVariable<Boolean>(	lSelectedProperty,
																																													pVariable.getName(),
																																													false);

		lJFXPropertyVariable.syncWith(pVariable);
		lSelectedProperty.set(pVariable.get());

	}

}
