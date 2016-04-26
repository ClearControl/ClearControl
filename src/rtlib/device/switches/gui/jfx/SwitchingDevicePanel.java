package rtlib.device.switches.gui.jfx;

import eu.hansolo.enzo.common.SymbolType;
import rtlib.gui.jfx.iconswitch.IconSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import rtlib.core.variable.Variable;
import rtlib.device.switches.SwitchingDeviceInterface;
import rtlib.gui.variable.JFXPropertyVariable;

public class SwitchingDevicePanel extends GridPane
{

	public SwitchingDevicePanel(SwitchingDeviceInterface pSwitchingDeviceInterface)
	{
		super();

		setAlignment(Pos.CENTER);
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(25, 25, 25, 25));

		int lNumberOfSwitches = pSwitchingDeviceInterface.getNumberOfSwitches();

		for (int i = 0; i < lNumberOfSwitches; i++)
		{
			IconSwitch lOnSwitch = new IconSwitch();
			lOnSwitch.setSymbolType( SymbolType.POWER );
			lOnSwitch.setSymbolColor( Color.web( "#ffffff" ) );
			lOnSwitch.setSwitchColor( Color.web( "#34495e" ) );
			lOnSwitch.setThumbColor( Color.web( "#ff495e" ) );

			Label lSwitchName = new Label( pSwitchingDeviceInterface.getSwitchName(i) );
			lSwitchName.setFont( new Font( 16.0 ) );

			HBox lHBox = new HBox( lOnSwitch, lSwitchName );
			lHBox.setSpacing( 8 );
			lHBox.setAlignment( Pos.CENTER_LEFT );
			add( lHBox, 0, i );

			BooleanProperty lSelectedProperty = lOnSwitch.selectedProperty();

			JFXPropertyVariable<Boolean> lJFXPropertyVariable = new JFXPropertyVariable<Boolean>(	lSelectedProperty,
																																														"Switch" + i,
																																														false);

			Variable<Boolean> lSwitchVariable = pSwitchingDeviceInterface.getSwitchVariable(i);

			lJFXPropertyVariable.syncWith(lSwitchVariable);

		}
	}

}
