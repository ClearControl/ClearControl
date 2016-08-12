package clearcontrol.gui.jfx.gridpane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class StandardGridPane extends GridPane
{

	public static final int cStandardGap = 5;
	public static final int cStandardPadding = 10;

	public StandardGridPane()
	{
		this(cStandardPadding, cStandardGap);
	}

	public StandardGridPane(int pAddPading, int pGaps)
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
