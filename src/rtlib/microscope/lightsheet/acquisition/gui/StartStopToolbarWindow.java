package rtlib.microscope.lightsheet.acquisition.gui;

import org.dockfx.DockNode;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import rtlib.gui.jfx.gridpane.StandardGridPane;
import rtlib.microscope.lightsheet.acquisition.interactive.InteractiveAcquisition;

public class StartStopToolbarWindow extends DockNode
{
	private GridPane mGridPane;

	public StartStopToolbarWindow(InteractiveAcquisition pInteractiveAcquisition)
	{
		super(new StandardGridPane());
		mGridPane = (GridPane) getContents();
		
		
		Button lStart2D = new Button("Start 2D");
		lStart2D.setAlignment(Pos.CENTER);
		lStart2D.setOnAction((e) -> {pInteractiveAcquisition.start2DAcquisition();});
		mGridPane.add(lStart2D, 0, 0);
		
		Button lStart3D = new Button("Start 3D");
		lStart3D.setAlignment(Pos.CENTER);
		lStart3D.setOnAction((e) -> {pInteractiveAcquisition.start3DAcquisition();});
		mGridPane.add(lStart3D, 0, 1);
		
		Button lStop = new Button("Stop");
		lStop.setAlignment(Pos.CENTER);
		lStop.setOnAction((e) -> {pInteractiveAcquisition.stopAcquisition();});
		mGridPane.add(lStop, 0, 2);
		
	}
	

}
