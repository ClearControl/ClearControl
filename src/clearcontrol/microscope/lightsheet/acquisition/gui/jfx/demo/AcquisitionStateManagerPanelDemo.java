package clearcontrol.microscope.lightsheet.acquisition.gui.jfx.demo;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.gui.jfx.recycler.RecyclerPanel;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.acquisition.gui.jfx.AcquisitionStateManagerPanel;
import clearcontrol.microscope.lightsheet.acquisition.gui.jfx.AcquisitionStatePanel;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.microscope.stacks.gui.jfx.StackRecyclerManagerPanel;
import clearcontrol.microscope.state.AcquisitionStateManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AcquisitionStateManagerPanelDemo extends Application	implements
																															AsynchronousExecutorServiceAccess
{

	
	@Override
	public void start(Stage stage)
	{
		

		AcquisitionStateManager lAcquisitionStateManager = new AcquisitionStateManager(null);
		AcquisitionStateManagerPanel lAcquisitionStateManagerPanel = new AcquisitionStateManagerPanel(lAcquisitionStateManager);
		
		
		InterpolatedAcquisitionState lState1 = new InterpolatedAcquisitionState("State1",2,4);
		InterpolatedAcquisitionState lState2 = new InterpolatedAcquisitionState("State2",2,4);
		
		lAcquisitionStateManager.addState(lState1);
		lAcquisitionStateManager.addState(lState2);
		

		Scene scene = new Scene(lAcquisitionStateManagerPanel, AcquisitionStatePanel.cPrefWidth, AcquisitionStatePanel.cPrefHeight);
		stage.setScene(scene);
		stage.setTitle("AcquisitionStateManagerPanel Demo");
	
		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
