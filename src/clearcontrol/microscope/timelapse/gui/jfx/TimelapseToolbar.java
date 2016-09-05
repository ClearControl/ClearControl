package clearcontrol.microscope.timelapse.gui.jfx;

import org.dockfx.DockNode;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.microscope.timelapse.TimelapseInterface;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class TimelapseToolbar extends DockNode
{
	private GridPane mGridPane;

	public TimelapseToolbar(TimelapseInterface pTimelapseInterface)
	{
		super(new CustomGridPane());
		mGridPane = (GridPane) getContents();

		mGridPane.setPrefSize(300, 200);

		setTitle("Timelapse");

		Button lStart2D = new Button("Start Timelapse");
		lStart2D.setAlignment(Pos.CENTER);
		lStart2D.setMaxWidth(Double.MAX_VALUE);
		lStart2D.setOnAction((e) -> {
			// pInteractiveAcquisition.start2DAcquisition();
		});
		GridPane.setColumnSpan(lStart2D, 2);
		mGridPane.add(lStart2D, 0, 1);

	}

}
