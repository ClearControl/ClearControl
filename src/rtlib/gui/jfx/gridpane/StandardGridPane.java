package rtlib.gui.jfx.gridpane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class StandardGridPane extends GridPane
{

	public StandardGridPane()
	{
		super();
		setAlignment(Pos.CENTER);
		setHgap(10);
		setVgap(10);
		setPadding(new Insets(25, 25, 25, 25));
	}

}
