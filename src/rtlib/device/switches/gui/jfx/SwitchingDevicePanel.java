package rtlib.device.switches.gui.jfx;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
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
			CheckBox lCheckBox = new CheckBox(pSwitchingDeviceInterface.getSwitchName(i));
			add(lCheckBox, 0, i);

			BooleanProperty lSelectedProperty = lCheckBox.selectedProperty();

			JFXPropertyVariable<Boolean> lJFXPropertyVariable = new JFXPropertyVariable<Boolean>(	lSelectedProperty,
																																														"Switch" + i,
																																														false);

			Variable<Boolean> lSwitchVariable = pSwitchingDeviceInterface.getSwitchVariable(i);

			lJFXPropertyVariable.syncWith(lSwitchVariable);

		}
	}

}
