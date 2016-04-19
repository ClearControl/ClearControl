package rtlib.device.position.gui.jfx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import rtlib.core.variable.Variable;
import rtlib.device.position.PositionDeviceInterface;

public class PositionDevicePanel extends GridPane
{

	public PositionDevicePanel(PositionDeviceInterface pPositionDeviceInterface)
	{
		super();

		setAlignment(Pos.CENTER);
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(25, 25, 25, 25));

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
