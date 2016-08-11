package clearcontrol.microscope.stacks.gui.jfx.demo;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.gui.jfx.recycler.RecyclerPanel;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.microscope.stacks.gui.jfx.StackRecyclerManagerPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StackRecyclerManagerPanelDemo extends Application	implements
																															AsynchronousExecutorServiceAccess
{

	
	@Override
	public void start(Stage stage)
	{
		

		StackRecyclerManager lStackRecyclerManager = new StackRecyclerManager();
		StackRecyclerManagerPanel lStackRecyclerManagerPanel = new StackRecyclerManagerPanel(lStackRecyclerManager);
		
		lStackRecyclerManager.getRecycler("recycler1", 10, 11);
		
		lStackRecyclerManager.getRecycler("recycler2", 20, 12);
		
		lStackRecyclerManager.getRecycler("recycler3", 30, 13);
		
		lStackRecyclerManager.getRecycler("recycler4", 40, 14);
		

		Scene scene = new Scene(lStackRecyclerManagerPanel, RecyclerPanel.cPrefWidth, RecyclerPanel.cPrefHeight);
		stage.setScene(scene);
		stage.setTitle("RecyclerPane Demo");
	
		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
