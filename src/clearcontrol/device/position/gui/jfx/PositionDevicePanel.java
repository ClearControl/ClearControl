package clearcontrol.device.position.gui.jfx;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.position.PositionDeviceInterface;
import clearcontrol.gui.jfx.gridpane.StandardGridPane;
import javafx.scene.control.Button;

public class PositionDevicePanel extends StandardGridPane
{

	public PositionDevicePanel(PositionDeviceInterface pPositionDeviceInterface)
	{
		super();

		int[] lValidPositions = pPositionDeviceInterface.getValidPositions();

		for (int i = 0; i < lValidPositions.length; i++)
		{
			String lPositionName = pPositionDeviceInterface.getPositionName(i);
			Button lButton = new Button(lPositionName);
			add(lButton, 0, i);

			Variable<Integer> lPositionVariable = pPositionDeviceInterface.getPositionVariable();

			final int fi = i;
			lButton.setOnAction((e) -> lPositionVariable.set(fi));
		}
	}

}
