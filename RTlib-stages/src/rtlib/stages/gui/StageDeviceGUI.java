package rtlib.stages.gui;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.component.RunnableFX;
import rtlib.stages.StageDeviceInterface;
import utils.RunFX;

/**
 * StageDeviceGUI for Halcyon
 */
public class StageDeviceGUI implements RunnableFX
{
	public final Stage3DControl mStage;

	public StageDeviceGUI( StageDeviceInterface stageDeviceInterface )
	{
		mStage = new Stage3DControl();
	}

	@Override public void init()
	{
		mStage.init();
	}

	@Override public void start(Stage stage) {

		mStage.start( stage );
	}

	@Override public void stop()
	{
		mStage.stop();
	}

	public Pane getPanel()
	{
		return mStage.getPanel();
	}

	public static void main(String[] args)
	{
		RunFX.start( new StageDeviceGUI( null ) );
	}
}
