package rtlib.hardware.stages.gui.jfx;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import rtlib.hardware.stages.StageDeviceInterface;

/**
 * StageDeviceGUI for Halcyon
 */
public class StageDeviceGUI extends BorderPane
{
	public final Stage3DControl mStage;

	public StageDeviceGUI(StageDeviceInterface stageDeviceInterface)
	{
		mStage = new Stage3DControl(this);
	}

	public void init()
	{
		mStage.init();
	}

	public void start(Stage stage)
	{
		mStage.start(stage);
	}

	public void stop()
	{
		mStage.stop();
	}
}
